package engine.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import util.getFrameFromVideo

data class TimelineSegment(var videoUrl: String, var startTime: Float, var endTime: Float) {
    var thumbnail: ImageBitmap? by mutableStateOf(null)
        private set

    init {
        if (endTime <= startTime) throw IllegalArgumentException("Tried to create a TimelineSegment with endTime <= startTime")
    }

    suspend fun loadThumbnail() {
        withContext(Dispatchers.IO) {
            thumbnail = getFrameFromVideo(videoUrl, startTime)
        }
    }

    fun getDuration(): Float {
        return endTime - startTime
    }
}