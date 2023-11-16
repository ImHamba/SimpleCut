package ui

import androidx.compose.foundation.border
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

fun borderStyle(color: Color = Color.Black): Modifier {
    return Modifier.border(1.dp, color)
}
