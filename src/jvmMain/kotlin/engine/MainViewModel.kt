package engine

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.flow.MutableStateFlow
import moe.tlaster.precompose.stateholder.SavedStateHolder
import moe.tlaster.precompose.viewmodel.ViewModel


class MainViewModel : ViewModel() {
    var counter by mutableStateOf(0)
    var videoUrl by mutableStateOf("")
    var timeline by mutableStateOf(VideoTimeline())

    init {
        timeline.addSegment(TimelineSegment("abc", 0.0, 10.0))
        timeline.addSegment(TimelineSegment("def", 3.46246, 5.1234))
    }

    fun incrementCounter() {
        counter++
    }


}