package ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import engine.viewmodel.MainViewModel
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
                .fillMaxHeight()
        ) {
            Row(Modifier.weight(3f)) {
                VideoPlayerImpl(
                    url = viewModel.timelineModel.getCurrentVideoUrl(),
                    isPaused = viewModel.playerModel.isPaused,
                    volume = viewModel.playerModel.volume,
                    speed = viewModel.playerModel.speed,
                    seekTime = viewModel.timelineModel.seekedTime,
                    isFullscreen = false,
                    progressState = viewModel.playerModel.progressState,
                    Modifier.fillMaxWidth().fillMaxHeight().then(borderStyle()),
                    onFinish = {}
                )
            }

//            Divider(color = Color.Black)

            Row(Modifier.weight(1f)) {
                PlayerControls()
            }
        }

        Spacer(Modifier.fillMaxHeight().width(1.dp).background(Color.Black))

        // video sources import panel and
        Column(Modifier.weight(1f).fillMaxHeight()) {
            SourcesPane()
        }
    }
}
