package engine

import androidx.compose.runtime.*

class VideoTimeline {
    var segments = mutableStateListOf<TimelineSegment>()

    // add a new segment to the end of the timeline
    fun addSegment(segment: TimelineSegment) {
        segments.add(segment)
    }

    fun addSegmentAt(segment: TimelineSegment, index: Int) {
        segments.add(index, segment)
    }

    fun deleteSegment(index: Int) {
        segments.removeAt(index)
    }

    fun splitSegment(index: Int, splitTime: Double) {
        val segment = segments[index]

        if (splitTime >= segment.getDuration()) throw Exception("Tried to split timeline segment beyond its duration")

        // create two new segments that make up the split segment
        val middleTime = segment.startTime + splitTime
        val part1 = TimelineSegment(segment.videoUrl, segment.startTime, middleTime)
        val part2 = TimelineSegment(segment.videoUrl, middleTime, segment.endTime)

        // delete old segment and insert two new segments in its place
        deleteSegment(index)
        addSegmentAt(part1, index)
        addSegmentAt(part2, index + 1)
    }
}

