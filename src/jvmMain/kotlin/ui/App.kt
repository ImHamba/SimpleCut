package ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.key.*
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.FrameWindowScope
import androidx.compose.ui.window.WindowScope
import engine.viewmodel.MainViewModel
import moe.tlaster.precompose.PreComposeApp
import moe.tlaster.precompose.viewmodel.viewModel
import org.jetbrains.compose.splitpane.ExperimentalSplitPaneApi
import org.jetbrains.compose.splitpane.HorizontalSplitPane
import org.jetbrains.compose.splitpane.VerticalSplitPane
import org.jetbrains.compose.splitpane.rememberSplitPaneState
import ui.components.FilledMenuBar
import ui.components.SplitpaneHandle
import ui.components.TimeDisplay
import util.AlwaysFocusedBox
import util.HorizontalDivider
import util.VerticalDivider
import java.awt.Cursor


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


@OptIn(ExperimentalSplitPaneApi::class)
@Composable
fun WindowScope.MainPanel() {
    val viewModel = viewModel() { MainViewModel() }



    Column {
        Row(Modifier.weight(1f)) {

            val splitterState = rememberSplitPaneState()
            HorizontalSplitPane(splitPaneState = splitterState) {
                first(160.dp) {
                    // video sources import panel and
                    Column(Modifier.weight(1f).fillMaxHeight().then(panelStyle(bottom = 5.dp, end = 5.dp))) {
                        SourcesPane(100.dp, 10.dp)
                    }
                }

                second(250.dp) {
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
                            modifier = Modifier.offset(x = 1.dp).fillMaxWidth().fillMaxHeight().weight(1f),
                            onFinish = {},
                            recomposeTrigger = viewModel.recomposeTrigger
                        )

                        PlayerControls(Modifier.height(50.dp))

                    }
                }

                splitter {
                    visiblePart {
                        SplitpaneHandle(4.dp, 15.dp)
                    }
                    handle {
                        Box(
                            Modifier
                                .markAsHandle()
                                .cursorForHorizontalResize()
                                .width(9.dp)
                                .fillMaxHeight()
                        )
                    }
                }
            }


        }

        // all player controls - timeline, buttons etc
        Row(Modifier.height(120.dp).then(panelStyle(top = 5.dp))) {
            TimeDisplay(Modifier.align(Alignment.CenterVertically))
            Timeline()
        }


    }
}

private fun Modifier.cursorForHorizontalResize(): Modifier =
    pointerHoverIcon(PointerIcon(Cursor(Cursor.E_RESIZE_CURSOR)))


@Composable
private fun keypressHandler(): Modifier {
    val viewModel = viewModel { MainViewModel() }

    return Modifier.onPreviewKeyEvent {
        // pause on spacebar down
        if (it.key == Key.Spacebar && it.type == KeyEventType.KeyDown)
            viewModel.togglePlayerPause()

        // delete selected ui element on delete up
        else if ((it.key == Key.Delete || it.key == Key.Backspace) && it.type == KeyEventType.KeyUp) {
            viewModel.deleteUiSelection()
        }

        // unselect segment on esc down
        if (it.key == Key.Escape && it.type == KeyEventType.KeyDown)
            viewModel.timelineModel.selectedSegmentIndex = null

        false
    }
}
