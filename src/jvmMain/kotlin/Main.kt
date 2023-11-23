import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import org.bytedeco.ffmpeg.global.avutil
import ui.App
import ui.components.FilledMenuBar


fun main() {
    // silence javacv logging
    avutil.av_log_set_level(avutil.AV_LOG_QUIET)

    application {
        Window(onCloseRequest = ::exitApplication) {

            App()

        }
    }
}

