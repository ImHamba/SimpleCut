package util

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import engine.model.TimelineSegment
import kotlinx.coroutines.*
import org.bytedeco.ffmpeg.ffmpeg
import org.bytedeco.ffmpeg.global.avutil.AV_LOG_WARNING
import org.bytedeco.javacpp.Loader
import org.bytedeco.javacv.FFmpegFrameFilter
import org.bytedeco.javacv.FFmpegFrameGrabber
import org.bytedeco.javacv.FFmpegLogCallback
import org.bytedeco.javacv.Java2DFrameConverter
import java.awt.image.BufferedImage
import java.io.File
import kotlin.math.roundToLong


data class Progress(val fraction: Float, val time: Float)

// fetches a frame from a video file
fun getFrameFromVideo(videoUrl: String, time: Float = 0f): ImageBitmap {
    val buffImage: BufferedImage

    // grab frame from video
    FFmpegFrameGrabber(videoUrl).use { grabber ->
        grabber.start()

        if (time > 0f)
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

fun getVideoDuration(videoUrl: String): Float {
    val duration: Float
    FFmpegFrameGrabber(videoUrl).use { grabber ->
        grabber.start()
        duration = grabber.lengthInTime / 1000000f
        grabber.stop()
    }
    return duration
}

suspend fun exportVideoOutput(segments: List<TimelineSegment>, outputPath: String) {
    FFmpegLogCallback.set()
    FFmpegLogCallback.setLevel(AV_LOG_WARNING)

    withContext(Dispatchers.IO) {
        // create a grabber for each segment and start them
        val grabbers = segments.map {
            FFmpegFrameGrabber(it.videoUrl).apply {
                start()
            }
        }

//        grabbers.forEach {
//            println(
//                "ac: ${it.audioCodec}\nvc: ${it.videoCodec}\nrot: ${it.displayRotation}\n" +
//                        "sample: ${it.sampleRate}\nwidth: ${it.imageWidth}\nheight: ${it.imageHeight}\nframerate: ${it.frameRate}"
//            )
//        }

        // check if video properties align to allow simple concat muxer
        val simpleConcatPossible = grabbers.all {
            it.audioCodec == grabbers[0].audioCodec
                    && it.videoCodec == grabbers[0].videoCodec
                    && it.sampleRate == grabbers[0].sampleRate
                    && it.imageWidth == grabbers[0].imageWidth
                    && it.imageHeight == grabbers[0].imageHeight
                    && it.frameRate in (0.995 * grabbers[0].frameRate..1.005 * grabbers[0].frameRate)
        }

        val ffmpeg: String = Loader.load(ffmpeg::class.java)

        val pb: ProcessBuilder
        if (simpleConcatPossible) {
            println("Simple concat")

            val concatString = segments.map { segment ->
                "file '${segment.videoUrl}'\ninpoint ${segment.startTime}\noutpoint ${segment.endTime}"
            }.joinToString("\n")

            val concatTxtPath = "temp/concat.txt"
            val concatTxtFile = File(concatTxtPath)
            concatTxtFile.writeText(concatString)

            pb = ProcessBuilder(
                ffmpeg, "-y", "-f", "concat", "-safe", "0",
                "-i", concatTxtFile.absolutePath,
                "-c", "copy", outputPath
            )

            pb.inheritIO().start().waitFor()
            concatTxtFile.delete()

        } else {
            println("filter_complex")

            pb = ProcessBuilder(
                ffmpeg, "-y", "-i", segments[0].videoUrl, "-i", segments[1].videoUrl,
                "-filter_complex", "\"[0:v] [0:a] [1:v] [1:a]concat=n=2:v=1:a=1 [v] [a]\"",
                "-map", "\"[v]\"", "-map", "\"[a]\"", outputPath
            )

            pb.inheritIO().start()
        }

    }
}