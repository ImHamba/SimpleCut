package engine.viewmodel

import androidx.compose.runtime.*
import engine.model.PlayerModel
import engine.model.TimelineModel
import engine.model.TimelineSegment
import kotlinx.coroutines.delay
import moe.tlaster.precompose.viewmodel.ViewModel


class MainViewModel : ViewModel() {

    val timelineModel = TimelineModel()
    val playerModel = PlayerModel()

    var recomposeTrigger by mutableStateOf(false)

    init {
        //test segments
//        timelineModel.addSegment(TimelineSegment("D:\\My stuff\\Gym\\95kg squat.mp4", 39.5f, 42F))
//        timelineModel.addSegment(TimelineSegment("D:\\My stuff\\Gym\\95kg squat.mp4", 50f, 52.5F))
//        timelineModel.addSegment(TimelineSegment("D:\\My stuff\\Gym\\135kg deadlift.mp4", 10f, 13.5F))
        timelineModel.addSegment(
            TimelineSegment(
                "D:\\My stuff\\Coding\\Java\\VideoCutter\\src\\resources\\video\\testvid.mp4",
                20.5f,
                25f
            )
        )
        timelineModel.moveToSegment(0)
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
}