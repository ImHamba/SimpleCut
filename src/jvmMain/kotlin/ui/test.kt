/*
 * This file is part of VLCJ.
 *
 * VLCJ is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * VLCJ is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with VLCJ.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright 2009-2019 Caprica Software Limited.
 */
package uk.co.caprica.vlcj.test.component

import uk.co.caprica.vlcj.player.component.EmbeddedMediaListPlayerComponent
import javax.swing.JFrame
import javax.swing.SwingUtilities

/**
 * Test demonstrating the [EmbeddedMediaListPlayerComponent].
 *
 *
 * Leaving aside the standard Swing initialisation code, there are only *three* lines of vlcj
 * code required to create a media player, add a media item to the play list, and play the media.
 */
class BasicEmbeddedMediaListPlayerComponentTest private constructor() {
    /**
     * Media player component.
     */
    private val mediaListPlayerComponent: EmbeddedMediaListPlayerComponent

    /**
     * Create a new test.
     */
    init {
        val frame = JFrame("vlcj Media Player Component Test")
        mediaListPlayerComponent = EmbeddedMediaListPlayerComponent()
        frame.contentPane = mediaListPlayerComponent
        frame.setLocation(100, 100)
        frame.setSize(1050, 600)
        frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        frame.isVisible = true
    }

    /**
     * Start playing a movie.
     *
     * @param mrl mrl
     */
    private fun start(mrl: String) {
        // One line of vlcj code to add the media to the play-list...
        mediaListPlayerComponent.mediaListPlayer().list().media().add(mrl)
        // Another line of vlcj code to play the media...
        mediaListPlayerComponent.mediaListPlayer().controls().play()
    }

    companion object {
        /**
         * Application entry point.
         *
         * @param args
         */
        @JvmStatic
        fun main(args: Array<String>) {
            SwingUtilities.invokeLater { BasicEmbeddedMediaListPlayerComponentTest().start("D:\\My stuff\\Gym\\95kg squat.mp4") }
        }
    }
}