package ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.FrameWindowScope
import androidx.compose.ui.window.MenuBar
import engine.viewmodel.MainViewModel
import moe.tlaster.precompose.viewmodel.viewModel
import org.lwjgl.system.MemoryStack
import org.lwjgl.util.tinyfd.TinyFileDialogs
import javax.swing.UIManager

@Composable
fun FrameWindowScope.FilledMenuBar() {
    val viewModel = viewModel { MainViewModel() }

    println(UIManager.getSystemLookAndFeelClassName())
    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
    MenuBar {
        Menu("File") {
            Item("Import Videos", onClick = { viewModel.sourcesModel.addSources(openFilePicker()) })
            Item("Export", onClick = {})
        }

        Menu("Edit") {
            Item("Split Segment", onClick = { viewModel.splitCurrentSegment() })
            Item("Delete Segment", onClick = { })
        }

        Menu("Help") {
            Item("User Guide", onClick = { })
        }
    }
}

// uses tinyfiledialogs to bring up file picker
// adapted from https://github.com/Wavesonics/compose-multiplatform-file-picker/blob/master/mpfilepicker/src/jvmMain/kotlin/com/darkrockstudios/libraries/mpfilepicker/FileChooser.kt
fun openFilePicker(
    initialDirectory: String = System.getProperty("user.home"),
    fileExtension: String = ""
): Set<String> =
    MemoryStack.stackPush().use { stack ->
        val filters = if (fileExtension.isNotEmpty()) fileExtension.split(",") else emptyList()
        val aFilterPatterns = stack.mallocPointer(filters.size)
        filters.forEach {
            aFilterPatterns.put(stack.UTF8("*.$it"))
        }
        aFilterPatterns.flip()

        // select multiple files from file dialog
        TinyFileDialogs.tinyfd_openFileDialog(
            "Choose File",
            initialDirectory,
            aFilterPatterns,
            null,
            true
        )?.split('|')?.toSet()

        // if the output string was null, return an empty set
            ?: emptySet()
    }

