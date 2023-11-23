package engine.model

import androidx.compose.ui.graphics.ImageBitmap
import util.getFrameFromVideo

data class TimelineSegment(var videoUrl: String, var startTime: Float, var endTime: Float) {
    val image: ImageBitmap

    init {
        if (endTime <= startTime) throw Exception("Tried to create a TimelineSegment with endTime <= startTime")

        image = getFrameFromVideo(videoUrl, startTime)
    }

    fun getDuration(): Float {
        return endTime - startTime
    }
}