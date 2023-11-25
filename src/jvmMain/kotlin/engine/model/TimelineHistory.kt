package engine.model


class TimelineHistory {
    var currentSnapshot = TimelineSnapshot(listOf(), 0, 0f, null)

    fun addRecord(segments: List<TimelineSegment>, currentSegmentIndex: Int, currentSegmentTime: Float) {
        // link new snapshot back to current snapshot as its previous
        val newSnapshot = TimelineSnapshot(segments, currentSegmentIndex, currentSegmentTime, currentSnapshot)

        // link current to the new one as its next
        currentSnapshot.next = newSnapshot

        currentSnapshot = newSnapshot
    }

    fun undo(): List<TimelineSegment> {
        // if there is a previous snapshot (i.e. we havent gone back to the start of history)
        currentSnapshot.previous?.let {
            // the previous becomes the current
            currentSnapshot = it

            // return the previous's segments
            return currentSnapshot.segments
        }

        // return empty list if there is no previous snapshot
        return listOf()
    }

    fun redo(): List<TimelineSegment> {
        // if there is a next snapshot (i.e. we havent gone all the way to the end of history)
        currentSnapshot.next?.let {
            // the next becomes the current
            currentSnapshot = it

            // return the next's segments
            return currentSnapshot.segments
        }

        // return the current's segments if there is no next snapshot
        return currentSnapshot.segments
    }

    override fun toString(): String {
        var count = 0
        var snap = currentSnapshot
        var string = "History:"
        while (snap.previous != null) {
            string += "\n\t$snap"
            snap = snap.previous!!
            count++
        }
        return string

    }

}

// represent each snapshot as a doubly linked list node with the data holding the timeline segments for this snapshot
data class TimelineSnapshot(
    val segments: List<TimelineSegment>,
    val currentSegmentIndex: Int,
    val currentSegmentTime: Float,
    val previous: TimelineSnapshot?
) {
    var next: TimelineSnapshot? = null

    override fun toString(): String {
        return segments.withIndex().joinToString(", ") {
            val segment = it.value
            val index = it.index

            segment.videoUrl.substringAfterLast('\\') +
                    if (index == currentSegmentIndex) " $currentSegmentTime" else ""
        }
            // if resulting string is empty (i.e. no segments in this snapshot) return fallback string instead
            .takeUnless { it.isEmpty() } ?: "No segments"
    }
}