package engine.model

import androidx.compose.runtime.*
import uk.co.caprica.vlcj.player.base.ControlsApi
import uk.co.caprica.vlcj.player.base.StatusApi
import uk.co.caprica.vlcj.player.component.CallbackMediaPlayerComponent
import uk.co.caprica.vlcj.player.component.EmbeddedMediaPlayerComponent
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer
import util.Progress
import java.awt.Component
import java.util.*

/**
 * Class representing the state of the video player
 */
class PlayerModel {
    var isPaused by mutableStateOf(true)
    var volume: Float by mutableStateOf(1f)
    var speed: Float by mutableStateOf(0f)
    var progressState: MutableState<Progress> = mutableStateOf(Progress(0f, 0f))
}