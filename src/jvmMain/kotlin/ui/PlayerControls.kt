package ui

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import engine.viewmodel.MainViewModel
import moe.tlaster.precompose.viewmodel.viewModel
import moe.tlaster.precompose.viewmodel.viewModelScope
import ui.components.ControlButton
import ui.components.openSaveDialog
import util.exportVideoOutput
import util.handleExport
import util.rememberArrowTopRight

@Composable
fun PlayerControls(modifier: Modifier = Modifier) {
    val viewModel = viewModel() { MainViewModel() }
    // player and trimming controls
    Row(
        modifier.fillMaxWidth(),//.height(50.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        // split timeline button
        ControlButton(
            Modifier.size(50.dp),
            onClick = { viewModel.splitCurrentSegment() },
            enabled = viewModel.timelineModel.segments.size > 0,
            toolTipText = "Split timeline"
        )
        {
            Icon(
                imageVector = Icons.Default.ContentCut,
                contentDescription = null
            )
        }

        // play/pause button
        ControlButton(
            Modifier.size(50.dp),
            onClick = { viewModel.togglePlayerPause() },
            enabled = viewModel.timelineModel.segments.size > 0,
            toolTipText = "Play/pause"
        )
        {
            Icon(
                imageVector = if (viewModel.playerModel.isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                contentDescription = null
            )
        }


        // delete segment button
        ControlButton(
            Modifier.size(50.dp),
            onClick = { viewModel.deleteSelectedSegment() },
            enabled = viewModel.timelineModel.selectedSegmentIndex != null,
            toolTipText = "Delete selected segment"
        )
        {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = null
            )
        }

        // export button
        ControlButton(
            Modifier.size(50.dp),
            onClick = {
                handleExport(viewModel.timelineModel.segments.toList(), viewModel.viewModelScope)
            },
            enabled = viewModel.timelineModel.segments.size > 0,
            toolTipText = "Export to video"
        )
        {
            Icon(
                imageVector = Icons.Default.DoubleArrow,
                contentDescription = null
            )
        }

    }
}


