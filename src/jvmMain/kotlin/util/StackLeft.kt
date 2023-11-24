package util

import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp


class StackLeft(private val colWidth: Dp) : GridCells {
    override fun Density.calculateCrossAxisCellSizes(
        availableSize: Int,
        spacing: Int
    ): List<Int> {
        val count = maxOf((availableSize + spacing) / (colWidth.roundToPx() + spacing), 1)
        return List(count) { colWidth.roundToPx() }
    }
}