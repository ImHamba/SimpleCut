package ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun SplitpaneHandle(width: Dp, height: Dp, color: Color = Color.Gray) {
    Row(Modifier.width(width).fillMaxHeight()) {
        Box(
            Modifier.height(height).width(1.dp).background(color)
                .align(Alignment.CenterVertically)
        )
        Spacer(Modifier.weight(1f))
        Box(
            Modifier.height(height).width(1.dp).background(color)
                .align(Alignment.CenterVertically)
        )
    }
}