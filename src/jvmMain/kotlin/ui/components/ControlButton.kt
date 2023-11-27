package ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.TooltipArea
import androidx.compose.foundation.TooltipPlacement
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ui.borderStyle

@OptIn(ExperimentalComposeUiApi::class, ExperimentalFoundationApi::class)
@Composable
fun ControlButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    enabled: Boolean = true,
    toolTipText: String,
    content: @Composable () -> Unit
) {
//    val baseColor = Color.Transparent
//    var color by remember { mutableStateOf(baseColor) }

    TooltipArea(
        tooltip = {
            Surface(
                modifier = Modifier.shadow(4.dp),
                color = Color(255, 255, 245),
                shape = RoundedCornerShape(1.dp)
            ) {
                Text(
                    text = toolTipText,
                    modifier = Modifier.then(borderStyle()).padding(horizontal = 5.dp, vertical = 3.dp),
                    fontSize = 15.sp
                )
            }
        },
        tooltipPlacement = TooltipPlacement.ComponentRect(alignment = Alignment.BottomEnd)
    ) {
        IconButton(
            onClick = onClick,
            modifier = modifier,
            enabled = enabled
        ) {
            content()
        }
    }
}