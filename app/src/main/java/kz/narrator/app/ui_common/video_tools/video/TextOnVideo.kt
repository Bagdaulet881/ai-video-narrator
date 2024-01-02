package kz.narrator.app.ui_common.video_tools.video


import android.content.Context
import android.util.Log
import com.arthenica.mobileffmpeg.Config
import com.arthenica.mobileffmpeg.Config.RETURN_CODE_SUCCESS
import com.arthenica.mobileffmpeg.FFmpeg
import com.arthenica.mobileffmpeg.LogCallback
import kz.narrator.app.ui_common.callbacks.FFMpegCallback
import kz.narrator.app.utils.file.VideoFileUtils
import java.io.File
import java.io.IOException


/**
 * Created by Umair_Adil on 19/09/2016.
 */

class TextOnVideo private constructor(private val context: Context) {

    private var video: File? = null
    private var audio: File? = null
    private var callback: FFMpegCallback? = null

    private var outputPath = ""
    private var outputFileName = ""
    private var font: File? = null
    private var text: String? = null
    private var position: String? = null
    private var color: String? = null
    private var size: String? = null
    private var border: String? = null
    private var addBorder: Boolean? = null

    //Border
    var BORDER_FILLED = ": box=1: boxcolor=black@0.5:boxborderw=5"
    var BORDER_EMPTY = ""

    fun setFile(originalFiles: File): TextOnVideo {
        this.video = originalFiles
        return this
    }

    fun setAudioFile(originalFiles: File): TextOnVideo {
        this.audio = originalFiles
        Log.i("MSG", "FFMPEG_LOG: setAudioFile ${originalFiles}")
        Log.i("MSG", "FFMPEG_LOG: setAudioFile ${originalFiles.exists()}")
        Log.i("MSG", "FFMPEG_LOG: setAudioFile ${originalFiles.path} ${originalFiles.name}")
        return this
    }

    fun setCallback(callback: FFMpegCallback): TextOnVideo {
        this.callback = callback
        return this
    }

    fun setOutputPath(output: String): TextOnVideo {
        this.outputPath = output
        return this
    }

    fun setOutputFileName(output: String): TextOnVideo {
        this.outputFileName = output
        return this
    }

    fun setFont(output: File): TextOnVideo {
        this.font = output
        return this
    }

    fun setText(output: String): TextOnVideo {
        this.text = output
        return this
    }

    fun setPosition(output: String): TextOnVideo {
        this.position = output
        return this
    }

    fun setColor(output: String): TextOnVideo {
        this.color = output
        return this
    }

    fun setSize(output: String): TextOnVideo {
        this.size = output
        return this
    }

    fun addBorder(output: Boolean): TextOnVideo {
        if (output)
            this.border = BORDER_FILLED
        else
            this.border = BORDER_EMPTY
        return this
    }

    fun draw() {

        if (video == null || !video!!.exists()) {
            callback!!.onFailure(IOException("File not exists"))
            return
        }
        if (!video!!.canRead()) {
            callback!!.onFailure(IOException("Can't read the file. Missing permission?"))
            return
        }

        val outputLocation = VideoFileUtils.getConvertedFile(outputPath, outputFileName)
        Log.i("MSG", "FFMPEG_LOG: outputLocation ${outputLocation.exists()}")

        val cmd = arrayOf(
            "-i",
            video!!.path,
            "-vf",
            "drawtext=fontfile=" + font!!.path + ":text=" + text + ": fontcolor=" + color + ": fontsize=" + size + border + ": " + position,
            "-c:v",
            "libx264",
            "-c:a",
            "copy",
            "-y",
            outputLocation.path
        )

        val cmdAudioOnVideo = arrayOf(
            "-i",
            video!!.path,
            "-i",
            audio!!.path,
            "-c:v",
            "copy",
            "-c:a",
            "aac",
            "-strict",
            "experimental",
            "-map",
            "0:v:0",
            "-map",
            "1:a:0",
            "-shortest",
            "-y",
            outputLocation.path
        )

        val combinedCmd = arrayOf(
            "-i", video!!.path, // Input video
            "-i", audio!!.path, // Input audio
            "-filter_complex",
            "[0:v]drawtext=fontfile=${font!!.path}:text='$text':fontcolor=$color:fontsize=$size$border:$position[v]", // Text overlay
            "-map", "[v]", // Map the video output from the filtergraph
            "-map", "1:a", // Map audio from the second input
            "-c:v", "libx264", // Video codec
            "-c:a", "aac", // Audio codec
            "-strict", "experimental",
            "-shortest", // Finish encoding when the shortest input stream ends
            "-y", outputLocation.path // Output file
        )

        Config.enableLogCallback(LogCallback { message ->
            Log.d("FFMPEG_LOG", "LOGGING: ${message.text}")
        })

        val rc = FFmpeg.execute(combinedCmd)

        if (rc == RETURN_CODE_SUCCESS) {
            Log.i("MSG", "FFMPEG_LOG: RETURN_CODE_SUCCESS ${outputLocation}")
            // Command execution successful
        } else {
            Log.i("MSG", "FFMPEG_LOG: Handle failure ${rc}")
            // Handle failure
        }

        /**
         * use like add string by forEach where wordList data is groupped by 7 word(max word) and replace 'group1 words' with 7 words and start_number is timeStart, end_number is when it dissappeared
         * ffmpeg -i input.mp4 -vf \
        "[0:v]drawtext=text='group1 words':start_number=group1_start:end_number=group1_end:fontfile=/path/to/font.ttf:fontsize=24:fontcolor=white:x=(w-text_w)/2:y=(h-text_h)/2, \
        drawtext=text='group2 words':start_number=group2_start:end_number=group2_end:fontfile=/path/to/font.ttf:fontsize=24:fontcolor=white:x=(w-text_w)/2:y=(h-text_h)/2, \
        ..." \
        -codec:a copy output.mp4

         */
    }

    companion object {

        val TAG = "TextOnVideo"

        fun with(context: Context): TextOnVideo {
            return TextOnVideo(context)
        }

        //Positions
        var POSITION_BOTTOM_RIGHT = "x=w-tw-10:y=h-th-10"
        var POSITION_TOP_RIGHT = "x=w-tw-10:y=10"
        var POSITION_TOP_LEFT = "x=10:y=10"
        var POSITION_BOTTOM_LEFT = "x=10:h-th-10"
        var POSITION_CENTER_BOTTOM = "x=(main_w/2-text_w/2):y=main_h-(text_h*2)"
        var POSITION_CENTER_ALLIGN = "x=(w-text_w)/2: y=(h-text_h)/3"

        var POSITION_NARRATING_TEXT = "x=(main_w/2-text_w/2):y=main_h-(text_h*6)"
    }
}
