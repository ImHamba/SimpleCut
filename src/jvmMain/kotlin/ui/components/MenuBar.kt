package ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.window.FrameWindowScope
import androidx.compose.ui.window.MenuBar
import engine.viewmodel.MainViewModel
import moe.tlaster.precompose.viewmodel.viewModel
import util.openFileDialog
import java.io.File
import javax.swing.UIManager

@Composable
fun FrameWindowScope.FilledMenuBar() {
    val viewModel = viewModel { MainViewModel() }

    println(UIManager.getSystemLookAndFeelClassName())
    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
    MenuBar {
        Menu("File") {
            Item("New Project", onClick = { })
            Item("Open Project...", onClick = { })
            Item("Save Project", onClick = { })
            Item("Save Project As...", onClick = { })


            Item("Import Videos", onClick = { importVideoFiles(openFileDialog("Choose File")) })
            Item("Export", onClick = {})
        }

        Menu("Edit") {
            Item("Split Segment", onClick = { viewModel.splitCurrentSegment() })
            Item("Delete Segment", onClick = { })
        }

        Menu("Help") {}
    }
}

private fun importVideoFiles(files: Set<File>) {

}