package ui

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Slider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.*
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import engine.viewmodel.MainViewModel
import moe.tlaster.precompose.viewmodel.viewModel
import util.HorizontalDivider
import util.Triangle
import util.VerticalDivider
import kotlin.math.floor

val defaultSliderThumbWidth = 20.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Timeline() {
    val viewModel = viewModel() { MainViewModel() }
    val tlDuration = viewModel.timelineModel.getDuration()

    // clamp the player time to the start/end time of the current segment
    val clampedTime = viewModel.playerModel.progressState.value.time.coerceIn(
        viewModel.timelineModel.getCurrentSegment().startTime,
        viewModel.timelineModel.getCurrentSegment().endTime
    )

    // slider position as a fraction of its length
    val sliderPos by mutableStateOf(
        (viewModel.timelineModel.playerTimeToTimelineTime(clampedTime) / viewModel.timelineModel.getDuration())
    )

    // temporary position used while actively dragging slider
    var tempSliderPos by remember { mutableStateOf(0f) }
    var sliderEnabled by remember { mutableStateOf(false) }
    val rulerHeight = 20.dp

    BoxWithConstraints(Modifier.then(borderStyle(Color.Red))) {
        Slider(
            value = if (tempSliderPos == 0f) sliderPos else tempSliderPos,
            onValueChange = {
                if (!sliderEnabled) return@Slider

                // update temporary position while dragging
                tempSliderPos = it
            },
            onValueChangeFinished = {
                if (!sliderEnabled) return@Slider

                // once drag has completed, update the timeline model with the final value
                viewModel.timelineModel.moveToPositionOnTimeline(tempSliderPos * tlDuration)
                tempSliderPos = 0f
            },
            thumb = { Playhead() },
            track = {
                Track(
                    Modifier.pointerInput(Unit) {
                        // disable slider and select correct segment based on where click occurred
                        detectTapUnconsumed { offset ->
                            sliderEnabled = offset.y < rulerHeight.toPx()
                            if (!sliderEnabled) {
                                val clickPosFrac = offset.x / (maxWidth - defaultSliderThumbWidth).toPx()

                                // determine which segment was clicked on
                                val clickedSegment =
                                    viewModel.timelineModel.getSegmentAtPositionFraction(clickPosFrac)

                                // deselect segment if it is already selected, otherwise set it as selected
                                if (viewModel.timelineModel.selectedSegmentIndex == clickedSegment)
                                    viewModel.timelineModel.selectedSegmentIndex = null
                                else
                                    viewModel.timelineModel.selectedSegmentIndex = clickedSegment
                            }
                        }
                    },
                    rulerHeight
                )
            },
            modifier = Modifier
                .fillMaxHeight()

        )
    }
}

@Composable
private fun Playhead() {
    BoxWithConstraints(
        Modifier
            .width(20.dp)
            .fillMaxHeight()
            .graphicsLayer(clip = false)
    ) {
        Triangle(
            Modifier
                .align(Alignment.TopCenter)
                .height(5.dp)
                .width(10.dp),
            color = Color.Red
        )
        Spacer(
            Modifier
                .width(1.dp)
                .height(maxHeight * 2)
                .align(Alignment.BottomCenter)
                .background(Color.Red)
        )
    }
}

@Composable
private fun Track(modifier: Modifier = Modifier, rulerHeight: Dp) {
    val viewModel = viewModel() { MainViewModel() }
    Column(modifier) {
        // time ruler
        Row(Modifier.height(rulerHeight)) {
            VerticalDivider()

            val duration = viewModel.timelineModel.getDuration()
            val wholeSec = floor(duration).toInt()
            // add a section for each second
            for (i in 0 until wholeSec) {
                Row(Modifier.weight(1f)) {
                    Text(i.toString(), fontSize = 10.sp)
                    Spacer(Modifier.weight(1f))
                    VerticalDivider()
                }
            }

            val remainder = duration - wholeSec
            if (remainder > 0) {
                Row(Modifier.weight(remainder)) {
                    Text((wholeSec).toString(), fontSize = 10.sp)
                    Spacer(Modifier.weight(1f))
                    VerticalDivider()
                }
            }
        }

        HorizontalDivider()

        // segments
        Row(
            Modifier
                .fillMaxHeight()
        )
        {

            for ((index, segment) in viewModel.timelineModel.segments.withIndex()) {

                val widthFrac = segment.getDuration() / viewModel.timelineModel.getDuration()
                Row(
                    Modifier
                        .weight(widthFrac)
                        .fillMaxHeight()
                        .then(
                            when (index) {
                                viewModel.timelineModel.selectedSegmentIndex -> Modifier.background(Color.Green)
                                viewModel.timelineModel.currentSegmentIndex -> Modifier.background(Color.Yellow)
                                else -> Modifier
                            }
                        )
                ) {
                    if (index == 0) VerticalDivider()

//                    Image()
                    Spacer(Modifier.weight(1f))
                    VerticalDivider()
                }


            }
        }
    }
}

// detects a downclick without consuming the event
// adapted from https://proandroiddev.com/android-touch-system-part-5-how-gestures-work-in-jetpack-compose-ef7e74703b6a
suspend fun PointerInputScope.detectTapUnconsumed(
    onTap: ((Offset) -> Unit)
) {
    awaitEachGesture {
        val down = awaitFirstDown(requireUnconsumed = false)
        onTap(down.position)
    }
}
