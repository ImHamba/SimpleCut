package engine.model

/**
 * Class representing the history of states the video editor timeline has been in to facilitate undo/redo functionality.
 * The timeline is structured as a doubly linked list that can be stepped along or forked off from based on changes to
 * the timeline.
 */
class TimelineHistory {
    /**
     * initial record of the timeline that is empty
     */
    var currentRecord = TimelineRecord(listOf(), 0, 0f, null)

    /**
     * adds a record to the timeline history
     */
    fun addRecord(segments: List<TimelineSegment>, currentSegmentIndex: Int, currentSegmentTime: Float) {
        // link new record back to current record as its previous
        val newRecord = TimelineRecord(segments, currentSegmentIndex, currentSegmentTime, currentRecord)

        // link current to the new one as its next
        currentRecord.next = newRecord

        currentRecord = newRecord
    }

    /**
     * returns the previous timeline record and moves the history back 1 step
     */
    fun undo(): List<TimelineSegment> {
        // if there is a previous record (i.e. we havent gone back to the start of history)
        currentRecord.previous?.let {
            // the previous becomes the current
            currentRecord = it

            // return the previous record's segments
            return currentRecord.segments
        }

        // return empty list if there is no previous record
        return listOf()
    }

    /**
     * returns the next timeline record (if we have previously undo'd) and move the history forward 1 step
     */
    fun redo(): List<TimelineSegment> {
        // if there is a next record (i.e. we havent gone all the way to the end of history)
        currentRecord.next?.let {
            // the next becomes the current
            currentRecord = it

            // return the next's segments
            return currentRecord.segments
        }

        // return the current record's segments if there is no next record
        return currentRecord.segments
    }

    override fun toString(): String {
        var count = 0
        var snap = currentRecord
        var string = "History:"
        while (snap.previous != null) {
            string += "\n\t$snap"
            snap = snap.previous!!
            count++
        }
        return string

    }

}

/**
 * A class that represents a record in the timeline history. Each record is a doubly linked list node with the data
 * holding the state of the timeline.
 */
data class TimelineRecord(
    val segments: List<TimelineSegment>,
    val currentSegmentIndex: Int,
    val currentSegmentTime: Float,
    val previous: TimelineRecord?
) {
    var next: TimelineRecord? = null

    override fun toString(): String {
        // join filenames of each segment with comma delimiter
        return segments.withIndex().joinToString(", ") {
            val segment = it.value
            val index = it.index

            // transform each segment path to just the file name
            // if the segment is the current segment, also append the current player time in the segment's string
            segment.videoUrl.substringAfterLast('\\') +
                    if (index == currentSegmentIndex) " $currentSegmentTime" else ""
        }
            // if resulting string is empty (i.e. no segments in this record) return fallback string instead
            .takeUnless { it.isEmpty() } ?: "No segments"
    }
}