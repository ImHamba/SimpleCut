package util

import androidx.compose.material.icons.materialIcon
import androidx.compose.material.icons.materialPath
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

@Composable
fun rememberArrowTopRight(): ImageVector {
    return remember {
        ImageVector.Builder(
            name = "arrow_top_right",
            defaultWidth = 40.0.dp,
            defaultHeight = 40.0.dp,
            viewportWidth = 40.0f,
            viewportHeight = 40.0f
        ).apply {
            path(
                fill = SolidColor(Color.Black),
                fillAlpha = 1f,
                stroke = null,
                strokeAlpha = 1f,
                strokeLineWidth = 1.0f,
                strokeLineCap = StrokeCap.Butt,
                strokeLineJoin = StrokeJoin.Miter,
                strokeLineMiter = 1f,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(0f, 0f)
                quadToRelative(0f, 13.333f, -9.283f, 22.416f)
                quadToRelative(-9.284f, 9.084f, -22.667f, 9.084f)
                quadToRelative(-13.383f, 0f, -22.383f, -9.084f)
                quadToRelative(-9f, -9.083f, -9f, -22.416f)
                verticalLineTo(487.5f)
                quadToRelative(0f, -26.188f, 18.417f, -44.76f)
                quadToRelative(18.416f, -18.573f, 44.916f, -18.573f)
                horizontalLineToRelative(411.168f)
                lineTo(547.167f, 294f)
                quadToRelative(-8.833f, -9.667f, -9.083f, -22.222f)
                quadToRelative(-0.25f, -12.556f, 9.25f, -22.278f)
                quadToRelative(9.666f, -9.333f, 22.25f, -9.333f)
                quadToRelative(12.583f, 0f, 22.249f, 9.333f)
                lineToRelative(183.5f, 184f)
                quadToRelative(9.5f, 9.773f, 9.5f, 22.47f)
                reflectiveQuadToRelative(-9.5f, 22.217f)
                lineTo(591.833f, 662f)
                quadToRelative(-9.833f, 9.5f, -22.452f, 9.241f)
                quadToRelative(-12.619f, -0.259f, -22.594f, -9.969f)
                quadToRelative(-9.102f, -9.711f, -9.028f, -22.325f)
                quadToRelative(0.075f, -12.614f, 9.575f, -22.447f)
                lineToRelative(129f, -129f)
                close()
            }
        }.build()
    }
}