package engine.viewmodel

import androidx.compose.runtime.*
import androidx.compose.ui.geometry.Offset
import engine.model.*
import moe.tlaster.precompose.viewmodel.ViewModel


enum class UiSelection() {
    SOURCE, SEGMENT
}

class MainViewModel : ViewModel() {
    val timelineModel = TimelineModel()
    val playerModel = PlayerModel()
    val sourcesModel = SourcesModel()

    var recomposeTrigger by mutableStateOf(false)

    private var lastSelectType: UiSelection? = null
    var sourceBeingDragged by mutableStateOf(false)

    init {
        //test segments
        timelineModel.addSegment(TimelineSegment("D:\\My stuff\\Gym\\95kg squat.mp4", 39.5f, 42F))
        timelineModel.addSegment(TimelineSegment("D:\\My stuff\\Gym\\95kg squat.mp4", 50f, 52.5F))
        timelineModel.addSegment(TimelineSegment("D:\\My stuff\\Gym\\135kg deadlift.mp4", 10f, 13.5F))
        timelineModel.addSegment(
            TimelineSegment(
                "D:\\My stuff\\Coding\\Java\\VideoCutter\\src\\resources\\video\\testvid.mp4",
                20.5f,
                25f
            )
        )
        timelineModel.moveToSegment(0)

        sourcesModel.addSources(
            setOf(
                "D:\\My stuff\\Gym\\95kg squat.mp4",
                "D:\\My stuff\\Gym\\135kg deadlift.mp4",
                "D:\\My stuff\\Coding\\Java\\VideoCutter\\src\\resources\\video\\testvid.mp4"
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

    fun triggerRecompose() {
        recomposeTrigger = !recomposeTrigger
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
                // delete selected segment if its not null
                timelineModel.selectedSegmentIndex?.let {
                    timelineModel.deleteSegment(it)
                    timelineModel.selectedSegmentIndex = null
                }
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
}