package ui

import androidx.compose.animation.core.animateIntOffsetAsState
import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.Slider
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import engine.viewmodel.MainViewModel
import moe.tlaster.precompose.viewmodel.viewModel
import util.toPx

@Composable
fun Timeline() {
    val viewModel = viewModel() { MainViewModel() }
//    var sliderPosition by remember { mutableFloatStateOf(0f) }

    val tlDuration = viewModel.timelineModel.getDuration()

    val clampedTime = viewModel.playerModel.progressState.value.time.coerceIn(
        viewModel.timelineModel.getCurrentSegment().startTime,
        viewModel.timelineModel.getCurrentSegment().endTime
    )

    val posFraction by mutableStateOf(
        (viewModel.timelineModel.playerTimeToTimelineTime(clampedTime) / viewModel.timelineModel.getDuration())
    )

    var tempPosition by remember { mutableStateOf(0f) }

    Slider(
        value = if (tempPosition == 0f) posFraction else tempPosition,
        onValueChange = {
            // update temporary position while dragging
            tempPosition = it
        },
        onValueChangeFinished = {
            // once drag has completed, update the timeline model with the final value
            viewModel.timelineModel.moveToPositionOnTimeline(tempPosition * tlDuration)
            tempPosition = 0f
        }
    )
}

//@Composable
//fun RowScope.Timeline1() {
//    val viewModel = viewModel() { MainViewModel() }
//
//
//
//    Box {
//        Row(
//            Modifier
//                .fillMaxHeight()
//                .pointerInput(Unit) {
//                    detectTapGestures { offset ->
//                        // click position as a fraction of the width along the element
//                        val clickPosition = (offset.x / size.width)
//                        viewModel.timeline.moveToPositionOnTimeline(clickPosition * tlDuration)
//                        viewModel.triggerRecompose()
//                    }
//                }
//        ) {
//
//            for ((segmentIndex, segment) in viewModel.timeline.segments.withIndex()) {
//                val length = (segment.getDuration() / tlDuration).toFloat()
//                Row(
//                    Modifier
//                        .weight(length)
//                        .fillMaxHeight()
//                ) {
//                    Text(text = "$segmentIndex")
//                }
//            }
//        }
//
//        TimelinePlayhead()
//    }
//}

@Composable
private fun TimelinePlayhead() {

    val viewModel = viewModel() { MainViewModel() }

    var posFraction by mutableStateOf(
        (viewModel.timelineModel.playerTimeToTimelineTime(viewModel.playerModel.progressState.value.time) / viewModel.timelineModel.getDuration())
            .coerceIn(0.001F, 0.999F)
    )



    BoxWithConstraints {
        val offset by animateIntOffsetAsState(
            targetValue = IntOffset(x = (maxWidth.toPx() * posFraction).toInt(), y = 0)
        )

        Divider(
            Modifier
                .width(3.dp)
                .fillMaxHeight()
                .offset { offset },
            color = Color.Red
        )
    }

}

