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

class MainViewModel : ViewModel() {
    val timelineModel = TimelineModel()
    val playerModel = PlayerModel()
    val sourcesModel = SourcesModel()

    init {
        viewModelScope.launch {
            snapshotFlow { timelineModel.segments.toList() }
                .collect { currentSegments ->
                    // Update history whenever segments change
                    if (currentSegments != timelineModel.history.currentSnapshot.segments) {
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

    fun togglePlayerPause() {
        // dont unpause if we are already paused and at end of timeline
        if (playerModel.isPaused && timelineModel.atEndOfTimeline(playerModel.progressState.value.time)) {
            return
        }

        playerModel.isPaused = !playerModel.isPaused
    }

    fun splitCurrentSegment() {
        val splitTime = playerModel.progressState.value.time

        // only split if not at boundary of segment
        if (splitTime != timelineModel.getCurrentSegment().startTime
            && splitTime != timelineModel.getCurrentSegment().endTime
        )
            timelineModel.splitSegment(splitTime = splitTime)
    }

    fun selectSegment(selectedSegmentIndex: Int) {
        // deselect segment if it is already selected, otherwise set it as selected
        if (timelineModel.selectedSegmentIndex == selectedSegmentIndex) {
            timelineModel.selectedSegmentIndex = null
            lastSelectType = null

        } else {
            timelineModel.selectedSegmentIndex = selectedSegmentIndex
            lastSelectType = UiSelection.SEGMENT
        }

        // deselect the Source when interacting with sources panel
        sourcesModel.selectedSource = null
    }

    fun selectSource(source: VideoSource) {
        sourcesModel.selectedSource = source
        lastSelectType = UiSelection.SOURCE

        // deselect the timeline segment when interacting with sources panel
        timelineModel.selectedSegmentIndex = null
    }

    fun deleteUiSelection() {
        when (lastSelectType) {
            UiSelection.SEGMENT -> {
                deleteSelectedSegment()
            }

            UiSelection.SOURCE -> {
                // delete selected source if its not null
                sourcesModel.selectedSource?.let {
                    sourcesModel.removeSource(it)
                    sourcesModel.selectedSource = null
                }
            }

            null -> {
                // do nothing
            }
        }
    }

    fun deleteSelectedSegment() {
        // delete selected segment if its not null
        timelineModel.selectedSegmentIndex?.let {
            timelineModel.deleteSegment(it)
            timelineModel.selectedSegmentIndex = null
        }
    }
}