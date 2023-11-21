package ui

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.SwingPanel
import androidx.compose.ui.graphics.Color
import com.sun.jna.NativeLibrary
import engine.viewmodel.MainViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import moe.tlaster.precompose.viewmodel.viewModel
import uk.co.caprica.vlcj.binding.RuntimeUtil
import uk.co.caprica.vlcj.player.base.MediaPlayer
import uk.co.caprica.vlcj.player.base.MediaPlayerEventAdapter
import uk.co.caprica.vlcj.player.component.CallbackMediaPlayerComponent
import uk.co.caprica.vlcj.player.component.EmbeddedMediaPlayerComponent
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer
import util.Progress
import java.awt.Component
import java.util.*
import kotlin.math.roundToInt

@Composable
internal fun VideoPlayerImpl(
    url: String,
    isPaused: Boolean,
    volume: Float,
    speed: Float,
    seekTime: Float,
    isFullscreen: Boolean,
    progressState: MutableState<Progress>,
    modifier: Modifier,
    onFinish: (() -> Unit)?
) {
    println("recompose player")

    val viewModel = viewModel { MainViewModel() }

    val mediaPlayerComponent = remember { initializeMediaPlayerComponent() }
    val mediaPlayer = remember { mediaPlayerComponent.mediaPlayer() }
    mediaPlayer.emitProgressTo(progressState)
    mediaPlayer.checkTimelineProgress()
    mediaPlayer.setupVideoFinishHandler(onFinish)

    val factory = remember { { mediaPlayerComponent } }
    /* OR the following code and using SwingPanel(factory = { factory }, ...) */
    // val factory by rememberUpdatedState(mediaPlayerComponent)

    LaunchedEffect(url) {
        // if video is paused when url is changed, add a listener that will pause video immediately
        // using set-paused option only shows the video as a black screen initially until it is played again
        if (isPaused) {
            val listener = object : MediaPlayerEventAdapter() {
                override fun playing(mediaPlayer: MediaPlayer) {
                    mediaPlayer.controls().setPause(true)

                    // remove listener after use
                    mediaPlayer.events().removeMediaPlayerEventListener(this)
                }
            }
            mediaPlayer.events().addMediaPlayerEventListener(listener)
        }

        mediaPlayer.media().start(
            url,
            ":start-time=${viewModel.timelineModel.setSegmentTime}"
        )
    }

    LaunchedEffect(seekTime) { mediaPlayer.controls().setTime((seekTime * 1000).toLong()) }
    LaunchedEffect(speed) { mediaPlayer.controls().setRate(speed) }
    LaunchedEffect(volume) { mediaPlayer.audio().setVolume(volume.toPercentage()) }
    LaunchedEffect(isPaused) {
        println("Set pause to $isPaused")
        mediaPlayer.controls().setPause(isPaused)
    }
    LaunchedEffect(isFullscreen) {
        if (mediaPlayer is EmbeddedMediaPlayer) {
            /*
             * To be able to access window in the commented code below,
             * extend the player composable function from WindowScope.
             * See https://github.com/JetBrains/compose-jb/issues/176#issuecomment-812514936
             * and its subsequent comments.
             *
             * We could also just fullscreen the whole window:
             * `window.placement = WindowPlacement.Fullscreen`
             * See https://github.com/JetBrains/compose-multiplatform/issues/1489
             */
            // mediaPlayer.fullScreen().strategy(ExclusiveModeFullScreenStrategy(window))
            mediaPlayer.fullScreen().toggle()
        }
    }
    DisposableEffect(Unit) { onDispose(mediaPlayer::release) }
    SwingPanel(
        factory = factory,
        background = Color.Transparent,
        modifier = modifier
    )
}

private fun Float.toPercentage(): Int = (this * 100).roundToInt()

/**
 * See https://github.com/caprica/vlcj/issues/887#issuecomment-503288294
 * for why we're using CallbackMediaPlayerComponent for macOS.
 */
private fun initializeMediaPlayerComponent(): Component {
//    System.setProperty("jna.library.path", "src/resources/vlc");
    NativeLibrary.addSearchPath(RuntimeUtil.getLibVlcLibraryName(), "src/resources/vlc")
//    NativeDiscovery().discover()
    return if (isMacOS()) {
        CallbackMediaPlayerComponent()
    } else {
        EmbeddedMediaPlayerComponent()
    }
}

/**
 * We play the video again on finish (so the player is kind of idempotent),
 * unless the [onFinish] callback stops the playback.
 * Using `mediaPlayer.controls().repeat = true` did not work as expected.
 */
@Composable
private fun MediaPlayer.setupVideoFinishHandler(onFinish: (() -> Unit)?) {
    DisposableEffect(onFinish) {
        val listener = object : MediaPlayerEventAdapter() {
            override fun stopped(mediaPlayer: MediaPlayer) {
                onFinish?.invoke()
                mediaPlayer.controls().play()
            }
        }
        events().addMediaPlayerEventListener(listener)
        onDispose { events().removeMediaPlayerEventListener(listener) }
    }
}

/**
 * Checks for and emits video progress every 50 milliseconds.
 * Note that it seems vlcj updates the progress only every 250 milliseconds or so.
 *
 * Instead of using `Unit` as the `key1` for [LaunchedEffect],
 * we could use `media().info()?.mrl()` if it's needed to re-launch
 * the effect (for whatever reason) when the url (aka video) changes.
 */
@Composable
private fun MediaPlayer.emitProgressTo(state: MutableState<Progress>) {
    val viewModel = viewModel { MainViewModel() }

    LaunchedEffect(key1 = Unit) {
        var oldPlayerTime: Long = 0
        var timeOffset: Long = 0

        var wallTime = System.currentTimeMillis()
        var lastTime: Long

        while (isActive) {
            val fraction = status().position()
            val playerTime = status().time()

            lastTime = wallTime
            wallTime = System.currentTimeMillis()

            // if player time updated, reset the time offset to 0
            if (playerTime != oldPlayerTime) {
                oldPlayerTime = playerTime
                timeOffset = 0
            }

            // if player time hasn't updated (due to its built-in 500ms update interval) but is still playing, then update
            // the time offset
            else if (status().isPlaying) {
                timeOffset += wallTime - lastTime
            }

            val reportedPlayerTime = ((playerTime + timeOffset).toFloat() / 1000)
            state.value = Progress(fraction, reportedPlayerTime)

            delay(20)
        }
    }
}

@Composable
private fun MediaPlayer.checkTimelineProgress() {
    val viewModel = viewModel { MainViewModel() }

    var count = remember { 0 }

    LaunchedEffect(count) {
        count++
        while (isActive) {
            val reportedPlayerTime = viewModel.playerModel.progressState.value.time

            if (viewModel.timelineModel.atEndOfTimeline(reportedPlayerTime) && !viewModel.playerModel.isPaused) {
                println("Reached end of timeline")
                viewModel.playerModel.isPaused = true

                // no need to continue checking. any movement in the timeline will recompose the player and start a new
                // end of timeline check loop
//                break
            }

            // update timeline with new time
            viewModel.timelineModel.checkIfNextSegment(reportedPlayerTime)

            delay(100)
        }
    }
}

/**
 * Returns [MediaPlayer] from player components.
 * The method names are the same, but they don't share the same parent/interface.
 * That's why we need this method.
 */
private fun Component.mediaPlayer() = when (this) {
    is CallbackMediaPlayerComponent -> mediaPlayer()
    is EmbeddedMediaPlayerComponent -> mediaPlayer()
    else -> error("mediaPlayer() can only be called on vlcj player components")
}

private fun isMacOS(): Boolean {
    val os = System
        .getProperty("os.name", "generic")
        .lowercase(Locale.ENGLISH)
    return "mac" in os || "darwin" in os
}

