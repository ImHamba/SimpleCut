package util

import java.awt.FileDialog
import java.awt.Frame
import java.io.File

fun openFileDialog(
    title: String,
    allowMultiSelection: Boolean = true
): Set<File> {
    return FileDialog(null as Frame?, title, FileDialog.LOAD).apply {
        isMultipleMode = allowMultiSelection
        isVisible = true
    }.files.toSet()
}