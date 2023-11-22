package engine.model

import androidx.compose.runtime.*
import java.lang.IllegalArgumentException
import java.lang.IndexOutOfBoundsException

class TimelineModel {
    var segments = mutableStateListOf<TimelineSegment>()
    var currentSegmentIndex by mutableStateOf(0)
    var selectedSegmentIndex: Int? by mutableStateOf(null)

    var seekedTime: Float by mutableStateOf(0f)

//    var currentSegmentTime: Double by mutableStateOf(0.0)
//    var currentTimelineTime: Double by mutableStateOf(0.0)

    fun addSegment(segment: TimelineSegment) {
        segments.add(segment)
    }

    fun deleteSegment(deleteIndex: Int) {
        segments.removeAt(deleteIndex)

        // if the last segment was deleted return early
        if (segments.size == 0) {
            currentSegmentIndex = 0
            return
        }

        // if the current segment was deleted and we're at segment 0, set the time to start of timeline
        // if we're at some other segment, set the time to the end of the preceding segment
        if (currentSegmentIndex == deleteIndex) {
            if (deleteIndex == 0)
                seekedTime = getCurrentSegment().startTime
            else {
                currentSegmentIndex = deleteIndex - 1
                seekedTime = getCurrentSegment().endTime
            }
        } else if (currentSegmentIndex > deleteIndex) {
            currentSegmentIndex -= 1
        }

    }

    fun getCurrentSegment(): TimelineSegment {
        return if (segments.size > 0)
            segments[currentSegmentIndex]
        else
        // if no segments, return an empty placeholder segment
            TimelineSegment("", 0f, 1f)
    }

    fun atLastSegment(): Boolean {
        return currentSegmentIndex == segments.size - 1
    }

    fun atEndOfTimeline(reportedPlayerTime: Float): Boolean {
//        println("at last: ${atLastSegment()}, player $reportedPlayerTime, end: ${segments.last().endTime}")
        return atLastSegment() && reportedPlayerTime >= segments.last().endTime
    }

    // adds a segment at a certain index and pushes the segment currently at that index, and everything after it,
    // further along the timeline
    fun addSegmentAt(segment: TimelineSegment, index: Int) {
        segments.add(index, segment)
    }

    fun getCurrentVideoUrl(): String {
        return getCurrentSegment().videoUrl
    }


    // splits a segment into two parts based on a time along that segment
    fun splitSegment(index: Int = currentSegmentIndex, splitTime: Float) {
        val segmentToSplit = segments[index]

        if (splitTime == segmentToSplit.startTime || splitTime == segmentToSplit.endTime)
            throw IllegalArgumentException("Tried to split timeline segment at its boundary")

        if (splitTime !in segmentToSplit.startTime..segmentToSplit.endTime)
            throw IllegalArgumentException("Tried to split timeline segment outside its time period")

        // create two new segments that make up the split segment
        val part1 = TimelineSegment(segmentToSplit.videoUrl, segmentToSplit.startTime, splitTime)
        val part2 = TimelineSegment(segmentToSplit.videoUrl, splitTime, segmentToSplit.endTime)

        // delete old segment and insert two new segments in its place
        segments.removeAt(index)
        addSegmentAt(part1, index)
        addSegmentAt(part2, index + 1)
        moveToSegment(index + 1)
    }

    // determines the segment and time within that segment based on a given time along the timeline
    fun getPositionOnTimeline(time: Float): TimelinePosition {
        if (time < 0) throw IllegalArgumentException("Time must be positive")

        // iterate through segments until we have found the one that the time lies within
        var cumuTime: Float = 0f
        for ((index, segment) in segments.withIndex()) {
            if (time - cumuTime <= segment.getDuration()) {
                return TimelinePosition(index, segment.startTime + (time - cumuTime))
            } else {
                cumuTime += segment.getDuration()
            }
        }

        // if reached end of timeline, then the given time was out of bounds
        throw IllegalArgumentException("Tried to access time on timeline beyond its end time")
    }

    // sets the current video url and time within that video based on a given time along the timeline
    fun moveToPositionOnTimeline(time: Float) {
        val (segmentIndex, segmentTime) = getPositionOnTimeline(time)
        currentSegmentIndex = segmentIndex
        seekedTime = segmentTime
    }

    // get total duration of timeline
    fun getDuration(): Float {
        return segments.map { it.getDuration() }.fold(0f) { acc, duration -> acc + duration }
    }

    fun getDurationUntilSegment(index: Int): Float {
        return segments.slice(0 until index).map { it.getDuration() }.fold(0f) { acc, duration -> acc + duration }
    }

    // updates the current video/time to the start of a given segment
    fun moveToSegment(index: Int) {
        if (index >= segments.size || index < 0) throw IndexOutOfBoundsException("Index is out of bounds for timeline segments list")

        currentSegmentIndex = index
        seekedTime = segments[index].startTime

//        println("index: $index, url: ${getCurrentVideoUrl()}, time: $currentSegmentTime")
    }

    // checks if the current segment should be updated based on the real time passed in the video player
    // returns true if next segment is started, or false otherwise
    fun checkIfNextSegment(playerTime: Float): Boolean {

        // check if player time has passed the end of the current segment, and we are not at the last segment
        if (playerTime >= getCurrentSegment().endTime && !atLastSegment()) {
            startNextSegment()
            return true
        }
        return false
    }

    fun startNextSegment() {
        currentSegmentIndex++
        seekedTime = getCurrentSegment().startTime

        println("Next segment: ${getCurrentSegment()}")
    }

    fun playerTimeToTimelineTime(reportedPlayerTime: Float): Float {
        return getDurationUntilSegment(currentSegmentIndex) + (reportedPlayerTime - getCurrentSegment().startTime)
    }

    // converts a position along the timeline as a number between 0 and 1 to the correpsonding segment index
    fun getSegmentAtPositionFraction(posFraction: Float): Int? {
        if (posFraction !in 0f..1f) return null

        var cumuDuration = 0f
        val tlDuration = getDuration()

        // iterate through segments until the fraction is within the start and end of the segment
        for ((index, segment) in segments.withIndex()) {
//            println("frac: $posFraction, start: ${(cumuDuration / tlDuration)}, end: ${(cumuDuration + segment.getDuration() / tlDuration)}")
            if (posFraction in (cumuDuration / tlDuration)..(cumuDuration + segment.getDuration() / tlDuration)) {
                return index
            }

            cumuDuration += segment.getDuration() / tlDuration
        }

        return null
    }

}

data class TimelinePosition(val segmentIndex: Int, val time: Float)
