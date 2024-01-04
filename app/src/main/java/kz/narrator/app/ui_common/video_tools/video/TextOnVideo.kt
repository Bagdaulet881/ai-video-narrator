package kz.narrator.app.ui_common.video_tools.video


import android.content.Context
import android.util.Log
import com.arthenica.mobileffmpeg.Config
import com.arthenica.mobileffmpeg.Config.RETURN_CODE_SUCCESS
import com.arthenica.mobileffmpeg.FFmpeg
import com.arthenica.mobileffmpeg.LogCallback
import kz.narrator.app.data.models.ai_data.MappedTimeStamps
import kz.narrator.app.data.models.network.Status
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
    private var timeStamps: List<MappedTimeStamps>? = null
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

    fun setTimestamps(list: List<MappedTimeStamps>?): TextOnVideo {
        this.timeStamps = list
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
        callback!!.onLogStatus("video path check", Status.SUCCESS)
        if (!video!!.canRead()) {
            callback!!.onFailure(IOException("Can't read the file. Missing permission?"))
            return
        }
        callback!!.onLogStatus("video readable", Status.SUCCESS)


        val outputLocation = VideoFileUtils.getConvertedFile(outputPath, outputFileName)
        Log.i("MSG", "FFMPEG_LOG: outputLocation ${outputLocation.exists()}")
        callback!!.onLogStatus("video output check", if(outputLocation.exists()) Status.SUCCESS else Status.ERROR)


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

        val newListedCmd = arrayOf(
            "-i", video!!.path, // Input video
            "-i", audio!!.path, // Input audio
            "-filter_complex",
            getTimeStampCommand(),
            "-map", "[v]", // Map the video output from the filtergraph
            "-map", "1:a", // Map audio from the second input
            "-c:v", "libx264", // Video codec
            "-c:a", "aac", // Audio codec
            "-strict", "experimental",
            "-shortest", // Finish encoding when the shortest input stream ends
            "-y", outputLocation.path // Output file
        )

//        Config.enableLogCallback(LogCallback { message ->
//            Log.d("FFMPEG_LOG", "LOGGING: ${message.text}")
//            callback!!.onProgress(message.text)
//        })

        val rc = FFmpeg.execute(newListedCmd)

        if (rc == RETURN_CODE_SUCCESS) {
            callback!!.onSuccess("")
            Log.i("MSG", "FFMPEG_LOG: RETURN_CODE_SUCCESS ${outputLocation}")
        } else {
            callback!!.onLogStatus("video create", Status.ERROR)
            Log.i("MSG", "FFMPEG_LOG: Handle failure ${rc}")
        }
    }

    fun getTimeStampCommand(): String {

        val initial = "[0:v]"
        val end = "[v]"
        val seperator = ","

        val cmdNew = StringBuilder()

        if (timeStamps != null){
            cmdNew.append(initial)
            timeStamps!!.forEachIndexed {index, it ->
                val text1 = it.word
                val st1 = it.startTime
                val end1 = it.endTime
                cmdNew.append("drawtext=fontfile=${font!!.path}:text='${text1}':fontcolor=$color:fontsize=$size$border:$position:enable='between(t,$st1,$end1)'")

                if(index != timeStamps!!.lastIndex){
                    cmdNew.append(seperator)
                }
            }
            cmdNew.append(end)
        }
//        val first = "drawtext=fontfile=${font!!.path}:text='${text1}':fontcolor=$color:fontsize=$size$border:$position:enable='between(t,$st1,5)'"
//        val second = "drawtext=fontfile=${font!!.path}:text='${text2}':fontcolor=$color:fontsize=$size$border:$position:enable='between(t,5,10)'"

        Log.i("MSG", "COMMAND881: ${cmdNew.toString()}")
        return cmdNew.toString()
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

    //val cmdTextOnVideo = arrayOf(
    //            "-i",
    //            video!!.path,
    //            "-vf",
    //            "drawtext=fontfile=" + font!!.path + ":text=" + text + ": fontcolor=" + color + ": fontsize=" + size + border + ": " + position,
    //            "-c:v",
    //            "libx264",
    //            "-c:a",
    //            "copy",
    //            "-y",
    //            outputLocation.path
    //        )
    //
    //        val cmdAudioOnVideo = arrayOf(
    //            "-i",
    //            video!!.path,
    //            "-i",
    //            audio!!.path,
    //            "-c:v",
    //            "copy",
    //            "-c:a",
    //            "aac",
    //            "-strict",
    //            "experimental",
    //            "-map",
    //            "0:v:0",
    //            "-map",
    //            "1:a:0",
    //            "-shortest",
    //            "-y",
    //            outputLocation.path
    //        )
}
