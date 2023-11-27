package ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import engine.viewmodel.MainViewModel
import moe.tlaster.precompose.viewmodel.viewModel
import ui.components.PauseButton
import ui.components.TimeDisplay
import util.exportVideoOutput

@Composable
fun PlayerControls(modifier: Modifier = Modifier) {
    val viewModel = viewModel() { MainViewModel() }
    // player and trimming controls
    Row(
        modifier.fillMaxWidth()
            .height(50.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Button(
            onClick = {
                viewModel.timelineModel.moveToSegment(0)
                viewModel.triggerRecompose()
            },
            enabled = viewModel.timelineModel.segments.size > 0
        )
        {
            Text("Restart")
        }

        PauseButton(viewModel.playerModel.isPaused)

        Button(
            onClick = {
                viewModel.splitCurrentSegment()
            },
            enabled = viewModel.timelineModel.segments.size > 0
        )
        {
            Text("Cut")
        }

        Button(
            onClick = {
                val selected = viewModel.timelineModel.selectedSegmentIndex
                if (selected != null) {
                    viewModel.timelineModel.deleteSegment(selected)
                    viewModel.timelineModel.selectedSegmentIndex = null
                }

            },
            enabled = viewModel.timelineModel.selectedSegmentIndex != null
        )
        {
            Text("Delete")
        }

        Button(
            onClick = {
                val outputPath = "C:\\Users\\DR\\Downloads\\Untitled.mp4"//openSaveDialog()
                try {
                    outputPath?.let {
                        exportVideoOutput(viewModel.timelineModel.segments.toList(), it)
                    }
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
            }
        )
        {
            Text("Export")
        }

    }
}


