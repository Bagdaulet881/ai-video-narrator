package kz.narrator.app.ui.main_page

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import kz.narrator.app.data.models.custom.Scan
import kz.narrator.app.data.preferences.Preferences
import kz.narrator.app.data.repository.appeal.AppealRepository
import kz.narrator.app.ui.attach_files.AttachFilesCallback
import javax.inject.Inject

class MainPageViewModel
@Inject constructor(
    private val app: Application,
    private val appealRepository: AppealRepository,
    private val preferences: Preferences
) : AndroidViewModel(app) {

    /**
     *
     */
    private lateinit var attachFilesCallback: AttachFilesCallback

    fun setAttachFilesCallback(callback: AttachFilesCallback) {
        this.attachFilesCallback = callback
    }

    fun onSendImageBtnClick(){
        if(attachFilesCallback.getAttachedFiles().isNotEmpty()){
            val test = getImageParams()
            Log.i("MSG", "SENDBTNCLICK: ${test.size}")
            Log.i("MSG", "SENDBTNCLICK: ${test.get(0).uri}")
//            chatRepository.sendMessage(
//                message = message,
//                attachedFiles = getImageParams()
//            )
        }
    }

    fun getImageParams(): MutableList<Scan> {
        val temp = mutableListOf<Scan>()
        attachFilesCallback.getAttachedFiles().forEach {
            if (it is Scan) {
                temp.add(it)
            }
        }

        return temp
    }

    fun onAttachFileBtnClick() {
        attachFilesCallback.onAttachFileBtnClick()
    }

}