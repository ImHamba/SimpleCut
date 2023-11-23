package util

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import org.bytedeco.javacv.FFmpegFrameFilter
import org.bytedeco.javacv.FFmpegFrameGrabber
import org.bytedeco.javacv.Java2DFrameConverter
import java.awt.image.BufferedImage
import kotlin.math.roundToLong

data class Progress(val fraction: Float, val time: Float)

// fetches a frame from a video file
fun getFrameFromVideo(videoUrl: String, time: Float = 0f): ImageBitmap {
    val buffImage: BufferedImage

    // grab frame from video
    FFmpegFrameGrabber(videoUrl).use { grabber ->
        grabber.start()

        println("frame rate: ${grabber.frameRate} ${grabber.videoFrameRate}, frame: ${(grabber.frameRate * time).toInt()}")

        grabber.timestamp = (time * 1000000).roundToLong()

        var imageFrame = grabber.grabImage()
        val videoRotation = grabber.displayRotation

        // rotate the image if the video has rotation
        if (videoRotation != 0.0) {
            // determine correct transformation based on the video rotation
            val transformation = when (videoRotation) {
                90.0 -> "transpose=cclock"
                -90.0 -> "transpose=clock"
                180.0 -> "tranpose=cclock,transpose=cclock"
                else -> null
            }

            // apply transformation to the image
            FFmpegFrameFilter(transformation, grabber.imageWidth, grabber.imageHeight).use { filter ->
                filter.start()
                filter.push(imageFrame)
                imageFrame = filter.pull()
                buffImage = Java2DFrameConverter().convert(imageFrame)
                filter.stop()
            }

        } else
            buffImage = Java2DFrameConverter().convert(imageFrame)

        grabber.stop()
    }

    return buffImage.toComposeImageBitmap()
}