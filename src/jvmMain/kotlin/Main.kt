import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import org.bytedeco.ffmpeg.global.avutil
import ui.App


fun main() {
    // silence javacv logging
    avutil.av_log_set_level(avutil.AV_LOG_QUIET)

    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "SimpleCut",
            state = WindowState(width = 1200.dp, height = 850.dp)
        ) {
            App()
        }
    }
}

