package ui

import androidx.compose.runtime.Composable
import engine.viewmodel.MainViewModel
import moe.tlaster.precompose.viewmodel.viewModel

@Composable
fun SourcesPane() {
    val viewModel = viewModel() { MainViewModel() }
//    for (segment in viewModel.timelineModel.segments) {
//        Text(text = segment.toString())
//    }

}
