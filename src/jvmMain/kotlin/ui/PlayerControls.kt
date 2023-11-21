package ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import engine.viewmodel.MainViewModel
import moe.tlaster.precompose.viewmodel.viewModel
import ui.components.PauseButton
import ui.components.TimeDisplay

@Composable
fun PlayerControls() {
    val viewModel = viewModel() { MainViewModel() }

    Column() {
        // timeline panel
        Row(
            Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .weight(1f)
        ) {
            Timeline()
        }

        Row(
            Modifier.fillMaxWidth()
                .fillMaxHeight()
                .weight(1f)
        ) {
            TimeDisplay()
        }

        // player and trimming controls
        Row(
            Modifier.fillMaxWidth()
                .fillMaxHeight()
                .weight(1f)
                .then(borderStyle())
        ) {
            Button(onClick = {
                viewModel.timelineModel.moveToSegment(0)
            })
            {
                Text("Restart")
            }

            PauseButton(viewModel.playerModel.isPaused)


        }
    }
}

