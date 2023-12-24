package engine.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import util.getFrameFromVideo

/**
 * Class representing a segment of a video in the editor timeline
 */
data class TimelineSegment(var videoUrl: String, var startTime: Float, var endTime: Float) {
    var thumbnail: ImageBitmap? by mutableStateOf(null)

    init {
        if (endTime <= startTime) throw IllegalArgumentException("Tried to create a TimelineSegment with endTime <= startTime")
    }

    /**
     * loads the segment thumbnail based on the start time of the segment
     */
    suspend fun loadThumbnail() {
        withContext(Dispatchers.IO) {
            thumbnail = getFrameFromVideo(videoUrl, startTime)
        }
    }

    fun getDuration(): Float {
        return endTime - startTime
    }
}