package kz.narrator.app.ui.attach_files

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import gun0912.tedbottompicker.TedBottomPicker
import kz.narrator.app.R
import kz.narrator.app.data.enums.app.PermissionRequestCodeEnums
import kz.narrator.app.data.enums.app.RequestCodeEnums
import kz.narrator.app.permission.camera.CameraPermission
import kz.narrator.app.permission.storage.StoragePermission
import kz.narrator.app.utils.showConfirmDialog


class AttachFilesUtils(
    val context: Context,
    val activity: FragmentActivity,
    val fragment: Fragment,
    val lifecycleOwner: LifecycleOwner,
    val attachFilesViewModel: AttachFilesViewModel
) {

    private var storagePermission: StoragePermission = StoragePermission(
        context!!,
        activity!!,
        fragment,
        attachFilesViewModel.storagePermissionCallback,
        false
    )
    private var cameraPermission: CameraPermission = CameraPermission(
        context!!,
        activity!!,
        fragment,
        attachFilesViewModel.cameraPermissionCallback,
        false
    )

    init {
        observeAttachFilesViewModel()
    }

    private fun observeAttachFilesViewModel() {
//        attachFilesViewModel.showConfirmDelete.observe( //TODO 881 Add DELETE logic
//            lifecycleOwner,
//            Observer {
//                it.getContentIfNotHandled()?.let {
//                    showConfirmDialog(
//                        context!!,
//                        R.string.dialog_delete_message,
//                        {
//                            attachFilesViewModel.onDeleteConfirmed(it)
//                        },
//                        positiveBtnText = R.string.btn_yes,
//                        negativeBtnText = R.string.btn_no
//                    )
//                }
//            }
//        )

        attachFilesViewModel.requestStoragePermission.observe(
            lifecycleOwner,
            Observer {
                it.getContentIfNotHandled()?.let {
                    if (allPermissionsGranted()) {
                        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
                            attachFilesViewModel.storagePermissionCallback.onPermissionGranted()
                        } else storagePermission.checkPermission()
                    } else {
                        fragment.requestPermissions(
                            attachFilesViewModel.REQUIRED_PERMISSIONS_FROM_MEDIA,
                            PermissionRequestCodeEnums.STORAGE.id
                        )
                    }
                }
            }
        )

        attachFilesViewModel.requestCameraPermission.observe(
            lifecycleOwner,
            Observer {
                it.getContentIfNotHandled()?.let {
                    cameraPermission.checkPermission()
                }
            }
        )

        attachFilesViewModel.permissionGranted.observe(
            lifecycleOwner,
            Observer {
                it.getContentIfNotHandled()?.let {
                    openImageOrFilePicker()
                }
            }
        )

//        attachFilesViewModel.showToast.observe(
//            lifecycleOwner,
//            Observer {
//                it.getContentIfNotHandled()?.let {
//                    ToastUtils.show(context, context.getString(it))
//                }
//            }
//        )
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        storagePermission.onActivityResult(requestCode, resultCode, data)
        cameraPermission.onActivityResult(requestCode, resultCode, data)

        if(resultCode == Activity.RESULT_OK){
            when(requestCode){
                RequestCodeEnums.PICK_IMAGE_OR_FILE.id -> {
                    data?.data?.let {
                        val contentResolver = context.contentResolver
                        val mime = contentResolver.getType(it)
                        attachFilesViewModel.onImagesOrFilesPicked(listOf(it))

//                        if (isValidFileType(mime)) {
//                        attachFilesViewModel.onImagesPicked(listOf(it))
//                        } else {
//                            showNewUIDialog(
//                                title = "R.string.chat_attachment_type_error_title",
//                                messageRes = "context.getString(R.string.chat_attachment_type_error_body)",
//                                mainBtnClickListener = {},
//                                positiveBtnText = R.string.permission_ok,
//                                negativeBtnText = null,
//                                isDelete = false
//                            )
//                        }
                    }
                }
            }
        }
    }

    fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    ): Boolean {
        return if (requestCode == PermissionRequestCodeEnums.STORAGE.id) {
            storagePermission.onRequestPermissionsResult(requestCode, permissions, grantResults)
            true

        } else if (requestCode == PermissionRequestCodeEnums.CAMERA.id) {
            cameraPermission.onRequestPermissionsResult(requestCode, permissions, grantResults)
            true

        } else {
            false
        }
    }

    private fun allPermissionsGranted() =
        attachFilesViewModel.REQUIRED_PERMISSIONS_FROM_MEDIA.all {
            ContextCompat.checkSelfPermission(
                context, it
            ) == PackageManager.PERMISSION_GRANTED
        }

    fun openImageOrFilePicker() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "video/*" // Allow all types of files

        /** TODO add
         * image/jpeg", "image/png", "image/bmp

        "application/pdf",
        "application/msword",
        "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
        "application/vnd.oasis.opendocument.text",
        "application/vnd.ms-excel",
        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
        "application/vnd.oasis.opendocument.spreadsheet",
        "application/vnd.ms-powerpoint",
        "application/vnd.oasis.opendocument.presentation",
        "application/vnd.openxmlformats-officedocument.presentationml.presentation"
         */

        try {
            fragment.startActivityForResult(Intent.createChooser(intent, "Select Videos"), RequestCodeEnums.PICK_IMAGE_OR_FILE.id)
        } catch (e: ActivityNotFoundException) {
            // Handle the exception if no suitable activity is found to handle the intent.
            // For example, display an error message or fallback to a different file picker method.
        }
    }
}