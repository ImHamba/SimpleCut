package ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.*
import androidx.compose.ui.window.WindowScope
import engine.model.VideoSource
import engine.viewmodel.MainViewModel
import moe.tlaster.precompose.viewmodel.viewModel
import util.StackLeft
import java.awt.datatransfer.DataFlavor
import java.awt.dnd.*
import java.io.File
import javax.swing.Box

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun WindowScope.SourcesPane(colWidth: Dp, spacing: Dp) {
    val viewModel = viewModel() { MainViewModel() }

    val sources = viewModel.sourcesModel.sources
    var previouslySelectedSource: VideoSource? by remember { mutableStateOf(null) }

    Box(
        Modifier
            .fillMaxWidth()
            .onClick {
                viewModel.sourcesModel.selectedSource = null
            }
    ) {
        val state = rememberLazyGridState()
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

        VerticalScrollbar(adapter = rememberScrollbarAdapter(state), modifier = Modifier.align(Alignment.CenterEnd))

        var dndPanelVisible by remember { mutableStateOf(false) }

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
                Text(
                    text = "Drop Files Here to Import",
                    modifier = Modifier.align(Alignment.Center),
                    textAlign = TextAlign.Center
                )
            }


        // initialise drag and dropping files onto window
        if (window.dropTarget == null) {
            window.dropTarget = initialiseDropTarget(
                dropAction = { droppedFiles ->
                    dndPanelVisible = false
                    for (file in droppedFiles)
                        viewModel.sourcesModel.addSource((file as File).absolutePath)
                },
                dragEnter = { dndPanelVisible = true },
                dragExit = { dndPanelVisible = false }
            )
        }
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun SourceIcon(
    modifier: Modifier = Modifier,
    source: VideoSource,
    isSelected: Boolean = false,
    isPreviouslySelected: Boolean = false,
    onClick: () -> Unit = {}
) {
    Column(modifier
        // set source as selected source on click
        .onClick {
            onClick()
        }
    ) {
        val selectColor = Color(205, 231, 252)
        val borderColor = Color(171, 204, 229)

        val highlightRounding = 2.dp

        Column(
            Modifier
                .clip(RoundedCornerShape(highlightRounding))
                // apply background and border colour based on whether this icon is selected
                .then(
                    if (isSelected)
                        Modifier.background(selectColor)
                    else Modifier
                )
                .then(
                    if (isPreviouslySelected)
                        Modifier.border(Dp.Hairline, borderColor, RoundedCornerShape(highlightRounding))
                    else Modifier
                )
                .padding(5.dp)
        ) {
            Box(Modifier.padding(start = 7.dp, top = 5.dp, end = 7.dp).background(color = Color.Black)) {
                Image(
                    bitmap = source.thumbnail,
                    contentDescription = null,
                    Modifier
                        .align(Alignment.Center)
                        .aspectRatio(16 / 9f)
                )
            }


            Spacer(Modifier.height(2.dp))

            Text(
                source.videoUrl.substringAfterLast('\\').replace(' ', '_'),
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(horizontal = 3.dp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontSize = 14.sp
            )
        }
    }
}

// adapted from https://dev.to/tkuenneth/from-swing-to-jetpack-compose-desktop-2-4a4h
private fun initialiseDropTarget(
    dropAction: (droppedFiles: List<*>) -> Unit,
    dragEnter: () -> Unit = {},
    dragExit: () -> Unit = {}
): DropTarget {
    return object : DropTarget() {
        @Synchronized
        override fun dragEnter(dtde: DropTargetDragEvent?) {
//            super.dragEnter(dtde)
            dragEnter()
        }

        @Synchronized
        override fun dragExit(dte: DropTargetEvent?) {
//            super.dragExit(dte)
            dragExit()
        }

        @Synchronized
        override fun drop(evt: DropTargetDropEvent) {
            try {
                evt.acceptDrop(DnDConstants.ACTION_REFERENCE)
                val droppedFiles = evt
                    .transferable.getTransferData(
                        DataFlavor.javaFileListFlavor
                    ) as List<*>

                dropAction(droppedFiles)
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }
}
