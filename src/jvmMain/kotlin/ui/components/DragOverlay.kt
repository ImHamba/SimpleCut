package ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import engine.viewmodel.MainViewModel
import moe.tlaster.precompose.viewmodel.viewModel
import ui.borderStyle

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun DragOverlay(content: @Composable () -> Unit) {

    val viewModel = viewModel { MainViewModel() }
    var cursorPos by remember { mutableStateOf(Offset(0f, 0f)) }


    val iconSize = 80.dp

    Box(Modifier
        .fillMaxHeight().fillMaxWidth()
        .onPointerEvent(PointerEventType.Move) {
            if (viewModel.sourceBeingDragged)
                cursorPos = it.changes.first().position
        }
    ) {
        content()

        val source = viewModel.sourcesModel.selectedSource

        // display source icon on cursor if its being dragged
        if (viewModel.sourceBeingDragged && source != null) {
            val x: Dp
            val y: Dp
            with(LocalDensity.current) {
                x = cursorPos.x.toDp() - iconSize / 2
                y = cursorPos.y.toDp() - iconSize / 4
            }
            SourceIcon(
                Modifier
                    .size(iconSize)
                    .offset(x, y),
                source = source,
                isSelected = true,
                isPreviouslySelected = true,
                transparency = 0x88
            )


        }
    }
}