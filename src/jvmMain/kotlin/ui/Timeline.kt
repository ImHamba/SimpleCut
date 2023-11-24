package ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.LocalMinimumInteractiveComponentEnforcement
import androidx.compose.material.Text
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Slider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.*
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import engine.model.TimelineSegment
import engine.viewmodel.MainViewModel
import kotlinx.coroutines.currentCoroutineContext
import moe.tlaster.precompose.viewmodel.viewModel
import util.Triangle
import util.VerticalDivider
import util.detectDownClickUncomsumed
import kotlin.math.floor

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
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
        if (viewModel.timelineModel.segments.size == 0) 0f
        else (viewModel.timelineModel.playerTimeToTimelineTime(clampedTime) / viewModel.timelineModel.getDuration())
    )

    // temporary position used while actively dragging slider
    var tempSliderPos by remember { mutableStateOf(0f) }
    var sliderEnabled by remember { mutableStateOf(false) }
    val rulerHeight = 13.dp

    CompositionLocalProvider(LocalMinimumInteractiveComponentEnforcement provides false) {
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
                        detectDownClickUncomsumed { offset ->
                            sliderEnabled = offset.y < rulerHeight.toPx()
                            if (!sliderEnabled) {
                                val clickPosFrac = offset.x / size.width

                                // determine which segment was clicked on
                                val clickedSegment =
                                    viewModel.timelineModel.getSegmentAtPositionFraction(clickPosFrac)

                                clickedSegment?.let {
                                    viewModel.selectSegment(it)
                                }
                            }
                        }
                    },
                    rulerHeight
                )
            },
            enabled = viewModel.timelineModel.segments.size > 0,
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
        TimeRuler(rulerHeight, viewModel.timelineModel.getDuration())

        Spacer(Modifier.height(2.dp))

        // segments
        Row(Modifier.fillMaxHeight().border(Dp.Hairline, Color.Black, RoundedCornerShape(5.dp)))
        {

            for ((index, segment) in viewModel.timelineModel.segments.withIndex()) {
                val widthFrac = segment.getDuration() / viewModel.timelineModel.getDuration()
                TimelineSegment(
                    widthFrac,
                    segment.videoUrl.substringAfterLast('\\'),
                    segment,
                    index == viewModel.timelineModel.selectedSegmentIndex,
                    index == viewModel.timelineModel.currentSegmentIndex,
                    5.dp
                )
            }

        }
    }
}

@Composable
private fun TimeRuler(rulerHeight: Dp, duration: Float) {
    Row(Modifier.height(rulerHeight)) {
        VerticalDivider()

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
}

@Composable
private fun RowScope.TimelineSegment(
    widthFrac: Float,
    label: String,
//    image: ImageBitmap?,
    segment: TimelineSegment,
    isSelected: Boolean,
    isCurrent: Boolean,
    cornerRadius: Dp
) {
    Column(
        Modifier
            .weight(widthFrac)
            .fillMaxHeight()
            .clip(RoundedCornerShape(cornerRadius))

            .background(Color.White)
            .then(
                // colour based on selection or if it is current
                if (isSelected) Modifier.border(1.dp, Color.Red, RoundedCornerShape(cornerRadius))
//                else if (isCurrent) Modifier.background(Color.Yellow)
                else Modifier.border(Dp.Hairline, Color.Black, RoundedCornerShape(cornerRadius))
            )
            .padding(start = cornerRadius, bottom = 5.dp)
    ) {
        Text(
            label,
            fontSize = 10.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        val image = segment.thumbnail

        // if the thumbnail from this segment is null, trigger the segment to load the thumbnail
        if (image == null) {
            LaunchedEffect(segment) {
                segment.loadThumbnail()
            }
        } else {
            Image(
                bitmap = image,
                contentDescription = null,
                modifier = Modifier.fillMaxHeight()
                    .padding(top = 2.dp)
                    .wrapContentWidth(unbounded = true, align = Alignment.Start) // allow to overflow end of segment
                    .clip(RoundedCornerShape(5.dp))
            )
        }
    }
}