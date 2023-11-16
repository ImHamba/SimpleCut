package ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import engine.MainViewModel
import moe.tlaster.precompose.viewmodel.viewModel

@Composable
fun PlayerControls() {
    val options = listOf("D:\\My stuff\\Gym\\95kg squat.mp4", "D:\\My stuff\\Gym\\135kg deadlift.mp4")
    var vidChoice by remember { mutableStateOf(0) }

    val viewModel = viewModel() { MainViewModel() }
    Column() {
        // timeline
        Row(
            Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .weight(1f)
                .then(borderStyle())
        ) {
            Text(text = "${viewModel.counter}")
        }

        // player and trimming controls
        Row(
            Modifier.fillMaxWidth()
                .fillMaxHeight()
                .weight(1f)
                .then(borderStyle())
        ) {
            Button(onClick = {
                viewModel.incrementCounter()
                viewModel.videoUrl = options[vidChoice]
                vidChoice = 1 - vidChoice
            })
            {
                Text("Change Video")
            }

            Button(onClick = {
                viewModel.incrementCounter()
            })
            {
                Text("Increment")
            }
        }
    }
}