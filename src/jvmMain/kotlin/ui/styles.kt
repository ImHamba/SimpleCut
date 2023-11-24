package ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

fun borderStyle(color: Color = Color.Black): Modifier {
    return Modifier.border(Dp.Hairline, color)
}

fun panelStyle(
    top: Dp = 10.dp,
    bottom: Dp = 10.dp,
    start: Dp = 10.dp,
    end: Dp = 10.dp,
    backColor: Color = Color(0xFFEEEEEE),
    internalPadding: Dp = 10.dp
): Modifier {
    return Modifier
        .padding(start, top, end, bottom)
        .clip(RoundedCornerShape(5.dp))
        .background(backColor)
        .padding(internalPadding)
}

val dashedStroke = Stroke(
    width = 2f,
    pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 15f), 0f)
)