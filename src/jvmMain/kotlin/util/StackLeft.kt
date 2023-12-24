package util

import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp

/**
 * A column layout function for LazyVerticalGrid that stacks as many columns on the left of the grid as possible according
 * to the given column width and grid width.
 * e.g. with a grid width of 11 and column width of 3, there would be 3 columns, each of width 3, on the left, and empty space
 * of width 2 on the right
 */
class StackLeft(private val colWidth: Dp) : GridCells {
    override fun Density.calculateCrossAxisCellSizes(
        availableSize: Int,
        spacing: Int
    ): List<Int> {
        val count = maxOf((availableSize + spacing) / (colWidth.roundToPx() + spacing), 1)
        return List(count) { colWidth.roundToPx() }
    }
}