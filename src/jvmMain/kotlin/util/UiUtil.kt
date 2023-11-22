package util

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.width
import androidx.compose.material.Divider
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.dp

@Composable
fun Triangle(modifier: Modifier = Modifier, color: Color) {
    var height by remember { mutableStateOf(0f) }
    var width by remember { mutableStateOf(0f) }
    Canvas(
        modifier = modifier.onGloballyPositioned {
            width = it.size.width.toFloat()
            height = it.size.height.toFloat()
        },
        onDraw = {
            val trianglePath = Path().apply {
                moveTo(0f, 0f)
                lineTo(width, 0f)
                lineTo(width / 2, height)
            }
            drawPath(color = color, path = trianglePath)
        })
}

@Composable
fun VerticalDivider() {
    Divider(Modifier.fillMaxHeight().width(1.dp), color = Color.Black)
}

@Composable
fun HorizontalDivider() {
    Divider(color = Color.Black)
}

// Box element that requests focus if it ever loses focus
// adapted from https://stackoverflow.com/questions/70838476/onkeyevent-without-focus-in-jetpack-compose
@Composable
fun AlwaysFocusedBox(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    val focusRequester = remember { FocusRequester() }
    var hasFocus by remember { mutableStateOf(false) }

    if (!hasFocus) {
        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
        }
    }

    Box(modifier
        .focusRequester(focusRequester)
        .onFocusChanged { hasFocus = it.hasFocus }
        .focusable()
    ) {
        content()
    }
}