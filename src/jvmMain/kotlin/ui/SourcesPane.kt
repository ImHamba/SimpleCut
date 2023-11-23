package ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import engine.viewmodel.MainViewModel
import moe.tlaster.precompose.viewmodel.viewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SourcesPane(colWidth: Dp, spacing: Dp) {
    val viewModel = viewModel() { MainViewModel() }

    val sources = viewModel.sourcesModel.sources
    BoxWithConstraints(Modifier.fillMaxWidth().fillMaxHeight()) {
        val ncols = (maxWidth / (colWidth + 2 * spacing)).toInt()

        println("panel width: $maxWidth, ncols: $ncols")

        LazyVerticalGrid(
            columns = GridCells.Fixed(ncols),
            horizontalArrangement = Arrangement.spacedBy(spacing),
            verticalArrangement = Arrangement.spacedBy(spacing),
            modifier = Modifier.padding(10.dp).width(ncols * colWidth + (ncols + 1) * spacing)
        ) {
            items(sources.size) { i ->
                val source = sources[i]
                Column(Modifier
                    // set source as selected source on click
                    .onClick { viewModel.sourcesModel.selectedSource = source }
                ) {
                    BoxWithConstraints(
                        Modifier
                            .width(colWidth)
                            .aspectRatio(16 / 9f)
                            .clip(RoundedCornerShape(5.dp))
                            .border(Dp.Hairline, Color.Red, RoundedCornerShape(5.dp))
                            .background(Color.Black)
                    ) {
                        println("real width $maxWidth")
                        Image(
                            bitmap = source.thumbnail,
                            contentDescription = null,
                            Modifier.align(Alignment.Center)
                        )
                    }

                    Text(
                        source.videoUrl.substringAfterLast('\\'),
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .then(
                                if (viewModel.sourcesModel.selectedSource == source)
                                    Modifier.background(Color.Cyan)
                                else Modifier
                            )
                    )
                }

            }
        }

    }
}
