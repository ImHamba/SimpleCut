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

    val tlTime = viewModel.timelineModel.playerTimeToTimelineTime(viewModel.playerModel.progressState.value.time)
    var oldTlTime by remember { mutableStateOf(tlTime) }
    var displayTime: Float

    if (tlTime < 0) displayTime = oldTlTime
    else {
        displayTime = tlTime
        oldTlTime = tlTime
    }

    displayTime = displayTime.coerceIn(0f, viewModel.timelineModel.getDuration())

    val minutes = displayTime.toInt() / 60
    val formattedSeconds = DecimalFormat("00.000").format(displayTime % 60)
    Text(text = "$minutes:$formattedSeconds", modifier = modifier)
}