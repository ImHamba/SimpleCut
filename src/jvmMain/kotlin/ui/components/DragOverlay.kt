package ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import engine.model.TimelineSegment
import engine.viewmodel.MainViewModel
import moe.tlaster.precompose.viewmodel.viewModel

@OptIn(ExperimentalComposeUiApi::class)
@Composable
// a wrapper to the entire app that allows drawing of elements over the top used for dragging of sources to timeline
fun SourceDragOverlay(content: @Composable () -> Unit) {
    val viewModel = viewModel { MainViewModel() }
    var cursorPos by remember { mutableStateOf(Offset(0f, 0f)) }

    // size of source icon when being dragged
    val iconSize = 80.dp

    Box(Modifier
        .fillMaxSize()

        // update cursor position when dragging
        .onPointerEvent(PointerEventType.Move) {
            if (viewModel.sourceBeingDragged)
                cursorPos = it.changes.first().position
        }
    ) {
        // draw regular app content
        content()

        // display source icon on cursor if its being dragged
        val source = viewModel.sourcesModel.selectedSource
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

            // create a new segment that exists for the current drag
            val newSegment = remember(viewModel.dragCounter) {
                // segment has the entire duration of this source and the source thumbnail
                TimelineSegment(source.videoUrl, startTime = 0f, endTime = source.duration).apply {
                    thumbnail = source.thumbnail
                }
            }

            // start/end x and y coords of timeline
            val tlx1 = viewModel.timelinePos.x
            val tlx2 = viewModel.timelinePos.x + viewModel.timelineDims.width
            val tly1 = viewModel.timelinePos.y
            val tly2 = viewModel.timelinePos.y + viewModel.timelineDims.height

            println("$tlx1 $tlx2 $tly1 $tly2 ${cursorPos.x} ${cursorPos.y}")

            // if drag goes over the timeline, add segment
            if (cursorPos.x in tlx1..tlx2 && cursorPos.y in tly1..tly2) {
                println("in")

                // find which segment index the cursor is hovering over
                val posFrac = (cursorPos.x - tlx1) / (tlx2 - tlx1)
                val hoveredSegmentIndex = viewModel.timelineModel.getSegmentIndexAtPositionFraction(posFrac)

                // if dragging over an existing segment, insert the new one in its place
                if (hoveredSegmentIndex != null && viewModel.timelineModel.segments[hoveredSegmentIndex] !== newSegment) {
                    // delete the new segment if its swapping from another place on the timeline
                    if (viewModel.timelineModel.segments.any { it === newSegment })
                        viewModel.timelineModel.deleteSegment(newSegment)
                    viewModel.timelineModel.addSegmentAt(newSegment, hoveredSegmentIndex)
                    viewModel.timelineModel.moveToSegment(hoveredSegmentIndex)
                }

                // if the timeline has no elements, just add it in
                else if (viewModel.timelineModel.segments.size == 0) {
                    println("add")
                    viewModel.timelineModel.addSegment(newSegment)
                    viewModel.timelineModel.moveToSegment(0)
                }
            }

            // if drag leaves the timeline, delete the new segment if it was inserted into the timeline
            else if (viewModel.timelineModel.segments.any { it === newSegment })
                viewModel.timelineModel.deleteSegment(newSegment)
        }
    }
}
