package kz.narrator.app.ui.ai_processor.video_crop

import android.app.Application
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kz.narrator.app.data.models.custom.Scan
import kz.narrator.app.ui.attach_files.AttachFilesCallback
import kz.narrator.app.utils.live_data.Event
import javax.inject.Inject

class VideoCropViewModel
@Inject constructor(
    private val app: Application
) : AndroidViewModel(app) {


    private val _selectedMediaFrame = MutableLiveData<Event<Bitmap>>()
    val selectedMediaFrame: LiveData<Event<Bitmap>>
        get() = _selectedMediaFrame

    private val _selectedMediaUri = MutableLiveData<Event<Uri>>()
    val selectedMediaUri: LiveData<Event<Uri>>
        get() = _selectedMediaUri

    /**
     *
     */
    private lateinit var attachFilesCallback: AttachFilesCallback

    fun setAttachFilesCallback(callback: AttachFilesCallback) {
        this.attachFilesCallback = callback
    }

    fun mediaSelected(){
        val listOfMedia = attachFilesCallback.getAttachedFiles()
        if(listOfMedia.isNotEmpty()){
            val mediaUri = (listOfMedia.get(0) as Scan).uri
            getFrame(mediaUri)
            _selectedMediaUri.postValue(Event(mediaUri))
        }
    }

    fun getFrame(videoFilePath: Uri){
        val retriever = MediaMetadataRetriever()
        try {
            // Set the video source (can be a file path or a content URI)
            retriever.setDataSource(app, videoFilePath) // or setDataSource(context, uri)

            // Retrieve a frame at a specific time in microseconds
            val timeInMicroSecond = 10000000 // For example, at 1 second
            val bitmap = retriever.getFrameAtTime(timeInMicroSecond.toLong(), MediaMetadataRetriever.OPTION_CLOSEST_SYNC)
            bitmap?.let {
                _selectedMediaFrame.postValue(Event(bitmap))
            }
            // Use this bitmap as needed
        } catch (e: IllegalArgumentException) {
            // Handle the exception
        } finally {
            // Make sure to release the retriever
            retriever.release()
        }
    }
}