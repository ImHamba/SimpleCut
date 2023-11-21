package ui.components

import androidx.compose.material.Text
import androidx.compose.runtime.*
import engine.viewmodel.MainViewModel
import moe.tlaster.precompose.viewmodel.viewModel
import java.text.DecimalFormat

@Composable
fun TimeDisplay() {
    val viewModel = viewModel() { MainViewModel() }

    val tlTime = viewModel.timelineModel.playerTimeToTimelineTime(viewModel.playerModel.progressState.value.time)
    var oldTlTime by remember { mutableStateOf(tlTime) }
    var displayTime: Float

    if (tlTime < 0) displayTime = oldTlTime
    else {
        displayTime = tlTime
        oldTlTime = tlTime
    }

    displayTime = displayTime.coerceIn(0f, viewModel.timelineModel.getDuration())
//            println(
//                "segment: ${viewModel.timeline.currentSegmentIndex}, " +
//                        "period: ${viewModel.timeline.getCurrentSegment().startTime} - ${viewModel.timeline.getCurrentSegment().endTime}, " +
//                        "current: ${viewModel.reportedPlayerTime}"
//            )
//            println("tlTime: $tlTime, oldTlTime: $oldTlTime, displayTime: $displayTime")

    val minutes = displayTime.toInt() / 60
    val formattedSeconds = DecimalFormat("00.000").format(displayTime % 60)
    Text(text = "$minutes:$formattedSeconds")
}