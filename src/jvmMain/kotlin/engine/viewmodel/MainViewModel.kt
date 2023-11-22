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

    init {
        //test segments
        timelineModel.addSegment(TimelineSegment("D:\\My stuff\\Gym\\95kg squat.mp4", 39.5f, 42F))
        timelineModel.addSegment(TimelineSegment("D:\\My stuff\\Gym\\95kg squat.mp4", 50f, 52.5F))
        timelineModel.addSegment(TimelineSegment("D:\\My stuff\\Gym\\135kg deadlift.mp4", 10f, 13.5F))
        timelineModel.moveToSegment(0)
    }
    
    fun togglePlayerPause() {
        // dont unpause if we are already paused and at end of timeline
        if (playerModel.isPaused && timelineModel.atEndOfTimeline(playerModel.progressState.value.time)) {
            return
        }

        playerModel.isPaused = !playerModel.isPaused
    }
}