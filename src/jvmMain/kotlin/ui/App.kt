package ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.FrameWindowScope
import engine.viewmodel.MainViewModel
import moe.tlaster.precompose.PreComposeApp
import moe.tlaster.precompose.viewmodel.viewModel
import ui.components.FilledMenuBar
import ui.components.TimeDisplay
import util.AlwaysFocusedBox
import util.HorizontalDivider
import util.VerticalDivider


@Composable
fun FrameWindowScope.App() {

    MaterialTheme {
        PreComposeApp {
            // always keep focused so keypress handler is always active
            // this avoids losing focus when clicking on the vlc panel
            AlwaysFocusedBox(Modifier.then(keypressHandler())) {
                FilledMenuBar()
                MainPanel()
            }
        }
    }
}


@Composable
fun MainPanel() {
    val viewModel = viewModel() { MainViewModel() }

    Column {

        Row(Modifier.weight(1f)) {
            // video sources import panel and
            Column(Modifier.weight(1f).fillMaxHeight().then(panelStyle(bottom = 5.dp, end = 5.dp))) {
                SourcesPane(100.dp, 10.dp)
            }

//            VerticalDivider()

            // player and player controls
            Column(
                Modifier
                    .weight(3f)
                    .fillMaxHeight()
                    .then(panelStyle(bottom = 5.dp, start = 5.dp, internalPadding = 0.dp))
            ) {
                // video display
                VideoPlayerImpl(
                    url = viewModel.timelineModel.getCurrentVideoUrl(),
                    isPaused = viewModel.playerModel.isPaused,
                    volume = viewModel.playerModel.volume,
                    speed = viewModel.playerModel.speed,
                    seekTime = viewModel.timelineModel.seekedTime,
                    isFullscreen = false,
                    progressState = viewModel.playerModel.progressState,
                    Modifier.fillMaxWidth().fillMaxHeight().weight(1f),
                    onFinish = {},
                    recomposeTrigger = viewModel.recomposeTrigger
                )

                PlayerControls(Modifier.height(50.dp))

            }
        }

//        HorizontalDivider()

        // all player controls - timeline, buttons etc
        Row(Modifier.height(120.dp).then(panelStyle(top = 5.dp))) {
            TimeDisplay(Modifier.align(Alignment.CenterVertically))
            Timeline()
        }
    }
}


@Composable
private fun keypressHandler(): Modifier {
    val viewModel = viewModel { MainViewModel() }

    return Modifier.onPreviewKeyEvent {
        // pause on spacebar down
        if (it.key == Key.Spacebar && it.type == KeyEventType.KeyDown)
            viewModel.togglePlayerPause()

        // delete selected segment on delete up
        else if ((it.key == Key.Delete || it.key == Key.Backspace) && it.type == KeyEventType.KeyUp) {
            val selected = viewModel.timelineModel.selectedSegmentIndex
            if (selected != null) {
                viewModel.timelineModel.deleteSegment(selected)
                viewModel.timelineModel.selectedSegmentIndex = null
            }
        }

        // unselect segment on esc down
        if (it.key == Key.Escape && it.type == KeyEventType.KeyDown)
            viewModel.timelineModel.selectedSegmentIndex = null

        false
    }
}
