package engine.viewmodel

import androidx.compose.runtime.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntSize
import engine.model.*
import kotlinx.coroutines.launch
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope


enum class UiSelection {
    SOURCE, SEGMENT
}

/**
 * View model for the main video editor view
 */
class MainViewModel : ViewModel() {
    val timelineModel = TimelineModel()
    val playerModel = PlayerModel()
    val sourcesModel = SourcesModel()

    // count of restarts of the timeline
    var restartCount = 0

    // indicates the last type of ui element that was interacted with to determine what some keypresses do
    // e.g. whether delete deletes selected source or segment
    private var lastSelectType: UiSelection? = null

    // indicates whether the selected source is currently being dragged
    var sourceBeingDragged by mutableStateOf(false)
    var dragCounter by mutableStateOf(0)

    // stores position and size of timeline for detection of dragging source over it
    var timelinePos by mutableStateOf(Offset(0f, 0f))
    var timelineDims by mutableStateOf(IntSize(0, 0))


    init {
        // Set up automatic updates of timeline history whenever segments change
        viewModelScope.launch {
            snapshotFlow { timelineModel.segments.toList() }
                .collect { currentSegments ->
                    if (currentSegments != timelineModel.history.currentRecord.segments) {
                        timelineModel.history.addRecord(
                            currentSegments,
                            timelineModel.currentSegmentIndex,
                            playerModel.progressState.value.time
                        )
                        println("New history ${timelineModel.history}")
                    }
                }
        }
    }


    init {
        //test segments
//        timelineModel.addSegment(TimelineSegment("D:\\My stuff\\Gym\\95kg squat.mp4", 10f, 30f))
//        timelineModel.addSegment(TimelineSegment("D:\\My stuff\\Gym\\95kg squat.mp4", 50f, 52.5F))
//        timelineModel.addSegment(TimelineSegment("D:\\My stuff\\Gym\\135kg deadlift.mp4", 10f, 30f))
//        timelineModel.addSegment(
//            TimelineSegment(
//                "D:\\My stuff\\Coding\\Java\\VideoCutter\\src\\resources\\video\\testvid.mp4",
//                20.5f,
//                25f
//            )
//        )
//        timelineModel.moveToSegment(0)

        sourcesModel.addSources(
            setOf(
                "D:\\My stuff\\Gym\\95kg squat.mp4",
                "D:\\My stuff\\Gym\\135kg deadlift.mp4"
            )
        )
    }

    /**
     * Toggles the pause state of the player. Only allows playing if not at the end of the timeline.
     */
    fun togglePlayerPause() {
        // dont unpause if we are already paused and at end of timeline
        if (playerModel.isPaused && timelineModel.atEndOfTimeline(playerModel.progressState.value.time)) {
            return
        }

        playerModel.isPaused = !playerModel.isPaused
    }

    /**
     * Splits the current segment into two, at the current position on the timeline
     */
    fun splitCurrentSegment() {
        val splitTime = playerModel.progressState.value.time

        // only split if not at boundary of segment
        if (splitTime != timelineModel.getCurrentSegment().startTime
            && splitTime != timelineModel.getCurrentSegment().endTime
        )
            timelineModel.splitSegment(splitTime = splitTime)
    }

    /**
     * Toggles select of a given segment in the timeline
     */
    fun toggleSegmentSelect(selectedSegmentIndex: Int) {
        // deselect segment if it is already selected, otherwise set it as selected
        if (timelineModel.selectedSegmentIndex == selectedSegmentIndex) {
            timelineModel.selectedSegmentIndex = null
            lastSelectType = null

        } else {
            timelineModel.selectedSegmentIndex = selectedSegmentIndex

            // set the last selected UI element as a segment
            lastSelectType = UiSelection.SEGMENT
        }

        // deselect any possibly selected Source
        sourcesModel.selectedSource = null
    }

    /**
     * Selects a given video source in the sources panel
     */
    fun selectSource(source: VideoSource) {
        sourcesModel.selectedSource = source

        // set the last selected UI element as a source
        lastSelectType = UiSelection.SOURCE

        // deselect the timeline segment when interacting with sources panel
        timelineModel.selectedSegmentIndex = null
    }

    /**
     * Deletes the currently selected UI element based on which was last interacted with
     */
    fun deleteUiSelection() {
        when (lastSelectType) {
            UiSelection.SEGMENT -> {
                timelineModel.deleteSelectedSegment()
            }

            UiSelection.SOURCE -> {
                sourcesModel.deleteSelectedSource()
            }

            null -> {
                // do nothing
            }
        }
    }


}