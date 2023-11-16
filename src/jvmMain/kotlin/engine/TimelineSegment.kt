package engine

data class TimelineSegment(var videoUrl: String, var startTime: Double, var endTime: Double) {
    init {
        if (endTime <= startTime) throw Exception("Tried to create a TimelineSegment with endTime <= startTime")
    }

    fun getDuration(): Double {
        return endTime - startTime
    }
}