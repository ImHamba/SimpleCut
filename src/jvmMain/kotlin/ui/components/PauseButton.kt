package ui.components

import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import engine.viewmodel.MainViewModel
import moe.tlaster.precompose.viewmodel.viewModel

@Composable
fun PauseButton(isPaused: Boolean) {
    val viewModel = viewModel() { MainViewModel() }

    val text = if (isPaused) "Play" else "Pause"

    Button(
        onClick = {
            viewModel.togglePlayerPause()
        },
        enabled = viewModel.timelineModel.segments.size > 0
    )
    {
        Text(text = text)
    }
}