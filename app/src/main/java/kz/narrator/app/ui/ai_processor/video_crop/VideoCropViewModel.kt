package kz.narrator.app.ui.ai_processor.video_crop

import android.app.Application
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import kz.narrator.app.data.models.network.Status
import kz.narrator.app.ui_common.callbacks.MainMediaCallback
import kz.narrator.app.ui_common.callbacks.UILoggingCallback
import javax.inject.Inject

class VideoCropViewModel
@Inject constructor(
    private val app: Application
) : AndroidViewModel(app) {

    /**
     *
     */
    private lateinit var mainCallback: MainMediaCallback

    fun setMainMediaCallback(callback: MainMediaCallback) {
        this.mainCallback = callback
    }

    private lateinit var resultLoggingCallback: UILoggingCallback

    fun setUILoggingCallback(callback: UILoggingCallback) {
        this.resultLoggingCallback = callback
    }

    fun onRun(){
        Log.i("MSG", "BIG_LOGGER: VC_VM onRun")
        resultLoggingCallback.onLogState("BestFrames: initializing", Status.SUCCESS)

        val listOfMedia = mainCallback.getSelectedMediaInfo()

        if(listOfMedia.isNotEmpty()){
            val firstMedia = listOfMedia.get(0)
            Log.i("MSG", "BIG_LOGGER: VC_VM frame cropping")
            resultLoggingCallback.onLogState("BestFrames: listOfMedia ${listOfMedia.size}", Status.SUCCESS)
            getFrame(firstMedia.mediaFilePath, firstMedia.frameBestShot?:10000000)
        }else{
            resultLoggingCallback.onLogState("BestFrames: listOfMedia isEmpty", Status.ERROR)
        }
    }

    fun getFrame(videoFilePath: Uri, timeInMicroSecond: Long){
        resultLoggingCallback.onLogState("BestFrames: frame cropping", Status.LOADING)

        val retriever = MediaMetadataRetriever()
        try {
            // Set the video source (can be a file path or a content URI)
            retriever.setDataSource(app, videoFilePath) // or setDataSource(context, uri)

            // Retrieve a frame at a specific time in microseconds
            val bitmap = retriever.getFrameAtTime(timeInMicroSecond, MediaMetadataRetriever.OPTION_CLOSEST_SYNC)
            bitmap?.let {
                resultLoggingCallback.onLogState("BestFrames: frame cropping", Status.SUCCESS)
                mainCallback.onImageCropped(listOf(bitmap))
            }
            // Use this bitmap as needed
        } catch (e: IllegalArgumentException) {
            resultLoggingCallback.onLogState("BestFrames: frame cropping - error ${e.message}", Status.ERROR)
            // Handle the exception
        } finally {
            // Make sure to release the retriever
            resultLoggingCallback.onLogState("BestFrames: frame cropping - released", Status.ERROR)
            retriever.release()
        }
    }
}