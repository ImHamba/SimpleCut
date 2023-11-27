package ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import engine.model.VideoSource
import engine.viewmodel.MainViewModel
import moe.tlaster.precompose.viewmodel.viewModel
import ui.components.SourceIcon
import ui.components.openFileDialog
import util.StackLeft
import util.uriToAbsolutePath

@OptIn(ExperimentalFoundationApi::class, ExperimentalComposeUiApi::class)
@Composable
fun SourcesPane(colWidth: Dp, spacing: Dp) {
    val viewModel = viewModel { MainViewModel() }

    val sources = viewModel.sourcesModel.sources
    var previouslySelectedSource: VideoSource? by remember { mutableStateOf(null) }

    var dndPanelVisible by remember { mutableStateOf(false) }

    Box(
        Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .onClick {
                viewModel.sourcesModel.selectedSource = null

                // open file import picker
                if (viewModel.sourcesModel.sources.size == 0)
                    viewModel.sourcesModel.addSources(openFileDialog())
            }
            .onExternalDrag(
                onDrop = { dragValue ->
                    // only allow drop if files were dropped
                    if (dragValue.dragData is DragData.FilesList) {
                        dndPanelVisible = false
                        for (fileUri in (dragValue.dragData as DragData.FilesList).readFiles()) {
                            try {
                                viewModel.sourcesModel.addSource(uriToAbsolutePath(fileUri))
                            } catch (ex: Exception) {
                                ex.printStackTrace()
                            }
                        }
                    }

                },
                onDragStart = { dndPanelVisible = true },
                onDragExit = { dndPanelVisible = false }
            )
    ) {
        val state = rememberLazyGridState()

        // show text instruction if the sources panel is empty
        if (viewModel.sourcesModel.sources.size == 0) {
            Column(
                modifier = Modifier.align(Alignment.Center).padding(10.dp)
            ) {
                Icon(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    imageVector = Icons.Default.Download,
                    contentDescription = null
                )
                Text(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    text = "Drop video files here or click to import",
                    textAlign = TextAlign.Center
                )
            }
        } else {
            // show grid of imported sources
            LazyVerticalGrid(
                columns = StackLeft(colWidth),
                horizontalArrangement = Arrangement.spacedBy(spacing),
                verticalArrangement = Arrangement.spacedBy(spacing),
                state = state
            ) {
                items(sources.size) { i ->
                    val source = sources[i]
                    SourceIcon(source = source,
                        isSelected = source == viewModel.sourcesModel.selectedSource,
                        isPreviouslySelected = source == previouslySelectedSource,
                        onClick = {
                            viewModel.selectSource(source)
                            previouslySelectedSource = source
                        })
                }
            }

            // scroll bar for lazy grid
            VerticalScrollbar(adapter = rememberScrollbarAdapter(state), modifier = Modifier.align(Alignment.CenterEnd))
        }

        // pop up over sources panel when dragging files over to import
        if (dndPanelVisible)
            Box(
                Modifier.fillMaxWidth().fillMaxHeight()
                    .clip(RoundedCornerShape(5.dp))
                    .background(Color.White)
                    .padding(5.dp)
                    .drawBehind {
                        drawRoundRect(
                            color = Color.Gray,
                            style = dashedStroke,
                            cornerRadius = CornerRadius(3.dp.toPx())
                        )
                    }.padding(10.dp),
            ) {
                Column(
                    modifier = Modifier.align(Alignment.Center).padding(10.dp)
                ) {
                    Icon(
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        imageVector = Icons.Default.Download,
                        contentDescription = null
                    )
                    Text(
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        text = "Drop video files here to import",
                        textAlign = TextAlign.Center
                    )
                }
            }
    }
}


