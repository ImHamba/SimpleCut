package ui

import androidx.compose.desktop.ui.tooling.preview.Preview
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import engine.model.VideoSource
import engine.viewmodel.MainViewModel
import moe.tlaster.precompose.viewmodel.viewModel

@Composable
fun SourcesPane(colWidth: Dp, spacing: Dp) {
    val viewModel = viewModel() { MainViewModel() }

    val sources = viewModel.sourcesModel.sources
    BoxWithConstraints(Modifier.fillMaxWidth().fillMaxHeight()) {
        val ncols = (maxWidth / (colWidth + 2 * spacing)).toInt()

        LazyVerticalGrid(
            columns = GridCells.Fixed(ncols),
            horizontalArrangement = Arrangement.spacedBy(spacing),
            verticalArrangement = Arrangement.spacedBy(spacing),
            modifier = Modifier.padding(10.dp).width(ncols * colWidth + (ncols + 1) * spacing)
        ) {
            items(sources.size) { i ->
                SourceIcon(sources[i])
            }
        }

    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
@Preview
private fun SourceIcon(source: VideoSource) {
    val viewModel = viewModel() { MainViewModel() }

    Column(Modifier
        // set source as selected source on click
        .onClick {
            viewModel.sourcesModel.selectedSource = source
        }
    ) {
        val selectColor = Color(205, 231, 252)
        val borderColor = Color(171, 204, 229)

        Column(
            Modifier
                .clip(RoundedCornerShape(3.dp))
                .then(
                    if (viewModel.sourcesModel.selectedSource == source)
                        Modifier.background(selectColor).border(Dp.Hairline, borderColor)
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
