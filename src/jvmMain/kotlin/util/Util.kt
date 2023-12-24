package util

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputScope
import engine.model.TimelineSegment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import ui.components.openSaveDialog
import java.awt.Desktop
import java.io.File
import java.net.URI
import java.nio.file.Path
import java.nio.file.Paths
import java.text.DecimalFormat
import kotlin.io.path.absolutePathString
import kotlin.math.abs


/**
 * PointerInputScope extension function that detects a downclick without consuming the event
 * adapted from https://proandroiddev.com/android-touch-system-part-5-how-gestures-work-in-jetpack-compose-ef7e74703b6a
 */
suspend fun PointerInputScope.detectDownClickUncomsumed(
    onTap: ((Offset) -> Unit)
) {
    awaitEachGesture {
        val down = awaitFirstDown(requireUnconsumed = true)
        onTap(down.position)
    }
}

/**
 * PointerInputScope extension function that detects a downclick event and consumes it
 */
suspend fun PointerInputScope.detectDownClick(
    onTap: ((Offset) -> Unit)
) {
    awaitEachGesture {
        val down = awaitFirstDown(requireUnconsumed = true)
        down.consume()
        onTap(down.position)
    }
}

/**
 * converts a local file URI string to an absolute path string
 */
fun uriToAbsolutePath(fileUri: String): String {
    val uri = URI(fileUri)
    val path: Path = Paths.get(uri)

    // Convert the URI to a regular file path
    return path.absolutePathString()
}

/**
 * An extension function for List of float that returns the value in the list closest to the provided argument
 * taken from https://stackoverflow.com/a/59898110
 */
fun List<Float>.closest(value: Float) = minBy { abs(value - it) }


/**
 * converts a number of [seconds] to a formatted minutes/seconds string (in the format 00:00.00)
 */
fun formatTime(seconds: Float): String {
    val minutes = seconds.toInt() / 60
    val formattedMins = DecimalFormat("00").format(minutes)
    val formattedSeconds = DecimalFormat("00.00").format(seconds % 60)
    return "$formattedMins:$formattedSeconds"
}

/**
 * takes a list of timeline [segments], prompts for save location and outputs video file
 */
fun handleExport(segments: List<TimelineSegment>, scope: CoroutineScope): Boolean {
    val logTxtPath = "temp/log.txt"
    val logTxtFile = File(logTxtPath)
    logTxtFile.parentFile.mkdirs()

    val outputPath = openSaveDialog()
    var exportSuccess = false

//  run video export in a coroutine to avoid blocking ui thread
    scope.launch {
        outputPath?.let {
            try {
                exportSuccess = exportVideoOutput(segments, it)

                // open location of saved file after export
                Desktop.getDesktop().open(File(outputPath).parentFile)
            } catch (ex: Exception) {
                ex.printStackTrace()
                logTxtFile.writeText(ex.stackTraceToString())
            }
        }
    }

    return exportSuccess
}