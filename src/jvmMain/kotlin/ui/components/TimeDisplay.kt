package ui.components

import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import engine.viewmodel.MainViewModel
import moe.tlaster.precompose.viewmodel.viewModel
import java.text.DecimalFormat

@Composable
fun TimeDisplay(modifier: Modifier = Modifier) {
    val viewModel = viewModel() { MainViewModel() }

    // get the total time along the timeline up to the current reported player time in the current segment
    val tlTime = viewModel.timelineModel.playerTimeToTimelineTime(viewModel.playerModel.progressState.value.time)
    var oldTlTime by remember { mutableStateOf(tlTime) }
    var displayTime: Float

    // dont display wacky values when vlc loads a new media
    if (tlTime < 0)
        displayTime = oldTlTime
    else {
        displayTime = tlTime
        oldTlTime = tlTime
    }

    // dont display wacky values outside of the timeline duration
    displayTime = displayTime.coerceIn(0f, viewModel.timelineModel.getDuration())

    // format time nicely
    val minutes = displayTime.toInt() / 60
    val formattedMins = DecimalFormat("00").format(minutes)
    val formattedSeconds = DecimalFormat("00.00").format(displayTime % 60)
    Text(text = "$formattedMins:$formattedSeconds", modifier = modifier)
}