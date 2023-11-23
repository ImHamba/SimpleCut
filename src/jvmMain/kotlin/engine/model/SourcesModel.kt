package engine.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap
import util.getFrameFromVideo

class SourcesModel {
    var sources = mutableStateListOf<VideoSource>()
    var selectedSource: VideoSource? by mutableStateOf(null)

    fun addSource(filePath: String) {
        // add source as long as its not already in sources
        if (filePath !in sources.map { it.videoUrl })
            sources.add(VideoSource(filePath))
    }

    fun addSources(filePaths: Set<String>) {
        for (path in filePaths)
            addSource(path)
    }

    fun removeSource(videoSource: VideoSource) {
        sources.remove(videoSource)
    }
}

data class VideoSource(var videoUrl: String) {
    val thumbnail: ImageBitmap

    init {
        // get thumbnail image upon creation of VideoSource object
        thumbnail = getFrameFromVideo(videoUrl)
    }
}