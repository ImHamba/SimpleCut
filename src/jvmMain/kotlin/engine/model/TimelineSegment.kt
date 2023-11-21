package engine.model

data class TimelineSegment(var videoUrl: String, var startTime: Float, var endTime: Float) {
    init {
        if (endTime <= startTime) throw Exception("Tried to create a TimelineSegment with endTime <= startTime")
    }

    fun getDuration(): Float {
        return endTime - startTime
    }
}