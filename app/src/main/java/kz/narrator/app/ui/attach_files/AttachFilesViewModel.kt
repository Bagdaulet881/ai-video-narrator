package kz.narrator.app.ui.attach_files

import android.Manifest
import android.app.Application
import android.net.Uri
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kz.narrator.app.data.models.custom.Scan
import kz.narrator.app.permission.base.OnPermissionGrantedListener
import kz.narrator.app.utils.base64.Base64Utils
import kz.narrator.app.utils.file.FileUtils
import kz.narrator.app.utils.live_data.Event
import java.util.*
import javax.inject.Inject

class AttachFilesViewModel
@Inject constructor(
    private val app: Application
) : AndroidViewModel(app) {

    val MAX_BYTES = 2 * 1000 * 1000

    val REQUIRED_PERMISSIONS_FROM_MEDIA =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            arrayOf(
                Manifest.permission.READ_MEDIA_IMAGES
            )
        else if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q)
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        else arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

    private val images = mutableListOf<Scan>()
    private var allMedia = mutableListOf<Any>()

    private val _mediaSelected = MutableLiveData<Event<Unit>>()
    val mediaSelected: LiveData<Event<Unit>>
        get() = _mediaSelected

    /**
     * Permission
     */
    private val _requestStoragePermission = MutableLiveData<Event<Unit>>()
    val requestStoragePermission: LiveData<Event<Unit>>
        get() = _requestStoragePermission

    val storagePermissionCallback = object : OnPermissionGrantedListener {

        override fun onPermissionGranted() {
            _requestCameraPermission.postValue(Event(Unit))
        }

        override fun onPermissionNotGranted() {
            _requestStoragePermission.postValue(Event(Unit))
        }

        override fun onPermissionExit() {
            //do nothing
        }
    }

    //
    private val _requestCameraPermission = MutableLiveData<Event<Unit>>()
    val requestCameraPermission: LiveData<Event<Unit>>
        get() = _requestCameraPermission

    val cameraPermissionCallback = object : OnPermissionGrantedListener {

        override fun onPermissionGranted() {
            val temp = mutableListOf<Uri>()
            _permissionGranted.postValue(Event(temp))
        }

        override fun onPermissionNotGranted() {
            _requestCameraPermission.postValue(Event(Unit))
        }

        override fun onPermissionExit() {
            //do nothing
        }
    }

    private val _permissionGranted = MutableLiveData<Event<List<Uri>>>()
    val permissionGranted: LiveData<Event<List<Uri>>>
        get() = _permissionGranted

    /**
     *
     */
    val attachFilesCallback = object : AttachFilesCallback {

        override fun onAttachFileBtnClick() {
            _requestStoragePermission.postValue(Event(Unit))
        }

        override fun clearAttachedFiles() {
            images.clear()
//            _adapterImages.postValue(emptyList()) TODO 881 UI
            allMedia.clear()
        }

        override fun getAttachedFiles(): List<Any> {
            allMedia.addAll(images)
            return allMedia
        }

        override fun setOldAttachedFiles(list: List<Any>){
            allMedia.addAll(list)
        }

        override fun showNotSelectedError() {
//            scanNotChosenErrorVisibility.postValue(true)
        }

        override fun showError(error: String?) {
            //todo
//            scanNotChosenErrorVisibility.postValue(true)
        }
    }

    fun onImagesOrFilesPicked(uriList: List<Uri>) {
        val temp = mutableListOf<Any>()

        uriList.forEach {

            //check uri
            val extension = FileUtils.getMimeType(app, it)
            val isImage = FileUtils.isImage(extension ?: "")

            val base64 = if(isImage){
                "\"${Base64Utils.getBase64(app, it)}\""
            }else{
                Base64Utils.convertToBase64(
                    app.applicationContext,
                    it
                )?.replace("\"", "\\\"")?.replace("\\", "\\\\")?:""
            }
//            if (base64.length > MAX_BYTES) {
//                _showSizeMaxError.postValue(Event(Unit))
//                return
//            }

            //
            var imageUpload = getImageChat(it)

            if (imageUpload != null) {
                imageUpload.id = UUID.randomUUID().toString()
                temp.add(imageUpload)
            } else {
                val imageUpload = Scan(
                    id = UUID.randomUUID().toString(),
                    uri = it,
                    extension = Base64Utils.getMimeType(app, it),
                    name = "${Base64Utils.getFileNameFromUri(app.applicationContext, it)}"
                )

                temp.add(
                    imageUpload
                )
            }
        }

        temp.addAll(allMedia)
        allMedia.clear()

        temp.forEach {
            if (it is Scan) {
                allMedia.add(it)
            }
        }
        if(allMedia.isNotEmpty()) _mediaSelected.postValue(Event(Unit))
//        _adapterImages.postValue(temp)
    }

    private fun getImageChat(uri: Uri): Scan? {
        for (temp in allMedia) {
            if (temp is Scan && temp.uri == uri) {
                return temp
            }
        }

        return null
    }
}