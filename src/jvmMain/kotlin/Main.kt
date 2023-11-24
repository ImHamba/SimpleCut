import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import org.bytedeco.ffmpeg.global.avutil
import ui.App
import ui.components.FilledMenuBar
import java.awt.datatransfer.DataFlavor
import java.awt.dnd.DnDConstants
import java.awt.dnd.DropTarget
import java.awt.dnd.DropTargetDropEvent
import java.io.File


fun main() {
    // silence javacv logging
    avutil.av_log_set_level(avutil.AV_LOG_QUIET)

    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "SimpleCut",
            state = WindowState(width = 1200.dp, height = 720.dp)
        ) {
            App()
        }
    }
}

