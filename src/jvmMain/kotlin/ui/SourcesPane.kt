package ui

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import engine.MainViewModel
import moe.tlaster.precompose.viewmodel.viewModel

@Composable
fun SourcesPane() {
    val viewModel = viewModel() { MainViewModel() }
    for (segment in viewModel.timeline.segments) {
        Text(text = segment.toString())
    }
}