package util

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputScope
import java.awt.FileDialog
import java.awt.Frame
import java.io.File
import java.net.URI
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.absolutePathString


// detects a downclick without consuming the event
// adapted from https://proandroiddev.com/android-touch-system-part-5-how-gestures-work-in-jetpack-compose-ef7e74703b6a
suspend fun PointerInputScope.detectDownClickUncomsumed(
    onTap: ((Offset) -> Unit)
) {
    awaitEachGesture {
        val down = awaitFirstDown(requireUnconsumed = true)
        onTap(down.position)
    }
}

// detects a downclick
suspend fun PointerInputScope.detectDownClick(
    onTap: ((Offset) -> Unit)
) {
    awaitEachGesture {
        val down = awaitFirstDown(requireUnconsumed = true)
        down.consume()
        onTap(down.position)
    }
}

fun uriToAbsolutePath(fileUri: String): String {
    val uri = URI(fileUri)
    val path: Path = Paths.get(uri)

    // Convert the URI to a regular file path
    return path.absolutePathString()
}