package ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import engine.model.VideoSource
import engine.viewmodel.MainViewModel
import moe.tlaster.precompose.viewmodel.viewModel
import util.detectDownClick


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SourceIcon(
    modifier: Modifier = Modifier,
    source: VideoSource,
    isSelected: Boolean = false,
    isPreviouslySelected: Boolean = false,
    transparency: Int = 0xFF,
    onClick: () -> Unit = {}
) {
    val viewModel = viewModel { MainViewModel() }

    Column(modifier
        // set source as selected source on click
        .pointerInput(source) { detectDownClick { onClick() } }
        .pointerInput(source) {
            detectDragGestures(
                onDragStart = {
                    println("drag start")
                    viewModel.sourceBeingDragged = true
                },
                onDrag = { _ -> },
                onDragEnd = {
                    println("drag end")
                    viewModel.sourceBeingDragged = false
                }
            )
        }
    ) {
        val selectColor = Color(205, 231, 252, transparency)
        val borderColor = Color(171, 204, 229, transparency)

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
            // video thumbnail
            Box(
                Modifier.padding(start = 7.dp, top = 5.dp, end = 7.dp).background(color = Color(0, 0, 0, transparency))
            ) {
                Image(
                    bitmap = source.thumbnail,
                    contentDescription = null,
                    Modifier
                        .align(Alignment.Center)
                        .aspectRatio(16 / 9f),
                    alpha = transparency.toFloat() / 0xFF
                )
            }

            Spacer(Modifier.height(2.dp))

            // file name text
            Text(
                source.videoUrl.substringAfterLast('\\').replace(' ', '_'),
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(horizontal = 3.dp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontSize = 14.sp,
                color = Color(0, 0, 0, transparency)
            )
        }
    }
}