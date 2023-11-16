package ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import engine.MainViewModel
import moe.tlaster.precompose.PreComposeApp
import moe.tlaster.precompose.viewmodel.viewModel


@Composable
fun App() {
    PreComposeApp {
        MaterialTheme {
            MainPanel()
        }
    }
}

@Composable
fun MainPanel() {
    val viewModel = viewModel() { MainViewModel() }

    Row {
        // player and player controls
        Column(
            Modifier
                .weight(3f)
                .then(borderStyle())
                .fillMaxHeight()
        ) {
            Row(Modifier.weight(4f).then(borderStyle(Color.Red))) {
                VideoPlayerImpl(
                    modifier = Modifier.fillMaxWidth().fillMaxHeight(),
                    url = viewModel.videoUrl
                )
            }

            Row(Modifier.weight(1f).then(borderStyle())) {
                PlayerControls()
            }
        }

        // video sources import panel and
        Column(Modifier.weight(1f).fillMaxHeight().then(borderStyle())) {
            SourcesPane()
        }
    }
}
