package ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Slider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import engine.model.TimelineSegment
import engine.viewmodel.MainViewModel
import moe.tlaster.precompose.viewmodel.viewModel
import util.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun Timeline() {
    val viewModel = viewModel() { MainViewModel() }

    val tlDuration = viewModel.timelineModel.getDuration()

    // temporary position used while actively dragging slider
    // this prevents many seeks being sent to the player until the slider is released at a final chosen time
    var tempSliderPos by remember { mutableStateOf(0f) }

    // flag to enable or disable the slider based on where its being clicked, so clicks on the segments dont seek but
    // clicks on the time ruler do
    var sliderEnabled by remember { mutableStateOf(false) }

    val rulerHeight = 25.dp

    val playerTime = viewModel.playerModel.progressState.value.time
    val seekedTime = viewModel.timelineModel.seekedTime

    // if seeking just occurred, and the player time has now updated to the seeked time, then set justSeeked to false
    var justSeeked by remember { mutableStateOf(false) }
    if (playerTime in (0.995 * seekedTime)..(1.005 * seekedTime))
        justSeeked = false

    // determine appropriate time to use for slider position
    val sliderTime =
        // slider stays at left side if no segments
        if (viewModel.timelineModel.segments.size == 0) 0f

        // if seeking just occurred, set slider to seeked time
        // this prevents the slider snapping back to the old player time for a short time before the player has
        // responded to the change in seeked time
        else if (justSeeked) viewModel.timelineModel.seekedTime

        // otherwise use the player time by default
        else playerTime

    val sliderPos by mutableStateOf(viewModel.timelineModel.playerTimeToTimelineTime(sliderTime) / viewModel.timelineModel.getDuration())

    Slider(
        value = if (tempSliderPos == 0f) sliderPos else tempSliderPos,
        onValueChange = {
            // ignore value change if slider is disabled
            if (!sliderEnabled) return@Slider

            // update temporary position while dragging
            tempSliderPos = it
        },
        onValueChangeFinished = {
            // ignore value change if slider is disabled
            if (!sliderEnabled) return@Slider

            // once drag has completed, update the timeline model with the final value
            viewModel.timelineModel.moveToPositionOnTimeline(tempSliderPos * tlDuration)
            justSeeked = true
            tempSliderPos = 0f
        },
        thumb = { Playhead(rulerHeight) },
        track = {
            Track(
                Modifier.pointerInput(Unit) {
                    // disable slider and select correct segment based on where click occurred
                    // (only allow clicks on time ruler section of timeline)
                    detectDownClickUncomsumed { offset ->
                        sliderEnabled = offset.y < rulerHeight.toPx()
                        if (!sliderEnabled) {
                            val clickPosFrac = offset.x / size.width

                            // determine which segment was clicked on
                            val clickedSegment =
                                viewModel.timelineModel.getSegmentIndexAtPositionFraction(clickPosFrac)

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

@Composable
private fun Playhead(timeRulerHeight: Dp) {
    var height: Int by remember { mutableStateOf(0) }
    Column(
        Modifier
            .width(20.dp)
            .fillMaxHeight()
            .graphicsLayer(clip = false)
            .onGloballyPositioned { height = it.size.height }
    ) {
        PlayheadTop(
            Modifier
                .align(Alignment.CenterHorizontally)
                .offset(y = timeRulerHeight - 12.dp)
                .height(12.dp)
                .width(9.dp),
            color = Color.Red
        )
        Spacer(Modifier.weight(1f))
        Spacer(
            Modifier
                .width(1.dp)
                .height(with(LocalDensity.current) { height.toDp() - 20.dp })
                .align(Alignment.CenterHorizontally)
                .background(Color.Red)
        )
    }
}

@Composable
private fun Track(modifier: Modifier = Modifier, rulerHeight: Dp) {
    val viewModel = viewModel() { MainViewModel() }
    Column(modifier.fillMaxSize()) {
        TimeRuler(Modifier.height(rulerHeight), viewModel.timelineModel.getDuration())

        Spacer(Modifier.height(2.dp))

        // segments
        Row(
            Modifier
                .fillMaxSize()
                .border(Dp.Hairline, Color.Black, RoundedCornerShape(5.dp))

                // update the timeline position and dimensions to allow detection of dragging sources onto timeline
                .onGloballyPositioned {
                    viewModel.timelinePos = it.positionInRoot()
                    viewModel.timelineDims = it.size
                }
        )
        {

            for ((index, segment) in viewModel.timelineModel.segments.withIndex()) {
                val widthFrac = segment.getDuration() / viewModel.timelineModel.getDuration()
                TimelineSegment(
                    widthFrac,
                    segment.videoUrl.substringAfterLast('\\'),
                    segment,
                    index == viewModel.timelineModel.selectedSegmentIndex,
                    5.dp
                )
            }

        }
    }
}

@Composable
private fun TimeRuler(modifier: Modifier = Modifier, duration: Float) {
    val approxMarkingSpacing = 200.dp

    // possible marking spacings in terms of seconds
    val validMarkingTimes =
        listOf(0.1f, 0.2f, 0.5f, 1f, 2f, 3f, 5f, 10f, 15f, 30f, 45f, 60f, 2 * 60f, 3 * 60f, 5 * 60f, 10 * 60f)

    BoxWithConstraints(modifier.fillMaxWidth()) {
        // determine a nicely spaced number of markings and the corresponding time per marking
        val approxNumMarkings = maxWidth / approxMarkingSpacing
        val approxMarkingTime = duration / approxNumMarkings
        val markingTime = validMarkingTimes.closest(approxMarkingTime)
        val numMarkings = duration / markingTime

        val wholeMarkings = numMarkings.toInt()

        Row {

            // add a section for each time marking
            for (i in 0..wholeMarkings) {
                // determine length of section - evenly spaced up until the last section
                val weight: Float
                if (i < wholeMarkings) {
                    weight = 1f
                } else {
                    val remainder = numMarkings - wholeMarkings
                    if (remainder > 0)
                        weight = remainder

                    // if there is no remainder, skip this last section
                    else
                        break
                }

                Row(Modifier.weight(weight)) {
                    // left most main time marking
                    VerticalDivider()
                    Column {
                        // time marking text
                        Text(
                            formatTime(i * markingTime),
                            fontSize = 10.sp,
                            modifier = Modifier.padding(start = 3.dp),
                            maxLines = 1,
                            overflow = TextOverflow.Clip
                        )

                        Spacer(Modifier.weight(1f))

                        // lines/spacers for minor markings in between main markings
                        Row(Modifier.height(7.dp)) {
                            val numSmallMarkings = weight / 0.2f
                            val numWholeSmallMarkings = numSmallMarkings.toInt()
                            for (j in 0 until numWholeSmallMarkings) {
                                Spacer(Modifier.weight(1f))

                                // dont display marking at end of last segment
                                if (j < 4)
                                    VerticalDivider()
                            }

                            // if there is a partial minor spacing left, set its weight manually proportional
                            // to the remaining length left to fill
                            val smallRemainder = numSmallMarkings - (numWholeSmallMarkings)
                            if (smallRemainder > 0)
                                Spacer(Modifier.weight(smallRemainder))
                        }
                    }

                }

            }

            // line at far right of time ruler
            if (duration > 0) VerticalDivider()
        }
    }
}

@Composable
private fun RowScope.TimelineSegment(
    widthFrac: Float,
    label: String,
    segment: TimelineSegment,
    isSelected: Boolean,
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