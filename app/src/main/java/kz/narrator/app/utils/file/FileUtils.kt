package kz.narrator.app.utils.file

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.text.TextUtils
import android.webkit.MimeTypeMap
import java.io.*
import java.text.SimpleDateFormat
import java.util.*


object FileUtils {

    fun getName(): String {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        return "name_${timeStamp}_"
    }

    @Throws(IOException::class)
    fun createImageFile(context: Context): File {

        // Create an image file name
        val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            getName(), /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        )

    }

    fun galleryAddPic(context: Context, filePath: String) {

        MediaStore.Images.Media.insertImage(context.contentResolver, filePath, getName(), null)
    }

//    fun downloadFile(context: Context, attachment: Attachment) {
//
//        val url = attachment.url
//        val finalUrl = StringBuilder()
//
//        if (!url.isNullOrEmpty()) {
//            val containsScheme = url.contains("http") || url.contains("https")
//
//            if (!containsScheme) {
//                finalUrl.append(BuildConfig.URL_HOST)
//            }
//
//            finalUrl.append(url)
//        }
//
//        val uri = Uri.parse(finalUrl.toString())
//        val request = DownloadManager.Request(uri)
//
//        request.setTitle(attachment.getInfo(SelectOptionEnums.SELECT_ATTACHMENT_NAME.id))
//        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
//        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
//        request.allowScanningByMediaScanner()
//        request.setVisibleInDownloadsUi(true)
//
//        request.setDestinationInExternalFilesDir(
//            context,
//            Environment.DIRECTORY_DOWNLOADS,
//            attachment.getInfo(SelectOptionEnums.SELECT_ATTACHMENT_NAME.id)
//        )
//
//        ToastUtils.show(context, context.getString(R.string.posts_file_download_message))
//        val manager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
//        manager.enqueue(request)
//        Logger.d(finalUrl.toString())
//    }

    fun getMimeType(context: Context, uri: Uri): String? {
        val extension: String?

        //Check uri format to avoid null
        if (uri.scheme == ContentResolver.SCHEME_CONTENT) {
            //If scheme is a content
            val mime = MimeTypeMap.getSingleton()
            extension = mime.getExtensionFromMimeType(context.contentResolver.getType(uri))
        } else {
            //If scheme is a File
            //This will replace white spaces with %20 and also other special characters. This will avoid returning null values on file name with spaces and special characters.
            extension =
                MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(File(uri.path!!)).toString())

        }

        return extension
    }

    fun isImage(extension: String): Boolean {
        val extensions =
            mutableListOf("jpg", "jpeg", "png")//, "bmp", "gif", "webp", "heic", "heif")
        return extensions.contains(extension.toLowerCase())
    }

    fun getPathFromUri(
        context: Context,
        uri: Uri
    ): String? {
        var path = getPathFromLocalUri(context, uri)
        if (path == null) {
            path = getPathFromRemoteUri(context, uri)
        }
        return path
    }

    @SuppressLint("NewApi")
    private fun getPathFromLocalUri(
        context: Context,
        uri: Uri
    ): String? {
        val isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            if (isExternalStorageDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":").toTypedArray()
                val type = split[0]
                if ("primary".equals(type, ignoreCase = true)) {
                    return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
                }
            } else if (isDownloadsDocument(uri)) {
                val id = DocumentsContract.getDocumentId(uri)
                if (!TextUtils.isEmpty(id)) {
                    return try {
                        val contentUri = ContentUris.withAppendedId(
                            Uri.parse(Environment.DIRECTORY_DOWNLOADS),
                            java.lang.Long.valueOf(id)
                        )
                        getDataColumn(context, contentUri, null, null)
                    } catch (e: NumberFormatException) {
                        null
                    }
                }
            } else if (isMediaDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":").toTypedArray()
                val type = split[0]
                var contentUri: Uri? = null
                if ("image" == type) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                } else if ("video" == type) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                } else if ("audio" == type) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                } else if ("document" == type) {
                    contentUri = MediaStore.Files.getContentUri("external")
                }
                val selection = "_id=?"
                val selectionArgs =
                    arrayOf(split[1])
                return getDataColumn(context, contentUri, selection, selectionArgs)
            }
        } else if ("content".equals(uri.scheme, ignoreCase = true)) {

            // Return the remote address
            return if (isGooglePhotosUri(uri)) {
                null
            } else getDataColumn(context, uri, null, null)
        } else if ("file".equals(uri.scheme, ignoreCase = true)) {
            return uri.path
        }
        return null
    }

    private fun getDataColumn(
        context: Context,
        uri: Uri?,
        selection: String?,
        selectionArgs: Array<String>?
    ): String? {
        var cursor: Cursor? = null
        val column = "_data"
        val projection = arrayOf(column)
        try {
            cursor =
                context.contentResolver.query(uri!!, projection, selection, selectionArgs, null)
            if (cursor != null && cursor.moveToFirst()) {
                val column_index: Int = cursor.getColumnIndex(column)

                //yandex.disk and dropbox do not have _data column
                return if (column_index == -1) {
                    null
                } else cursor.getString(column_index)
            }
        } finally {
            if (cursor != null) {
                cursor.close()
            }
        }
        return null
    }

    private fun getPathFromRemoteUri(
        context: Context,
        uri: Uri
    ): String? {
        // The code below is why Java now has try-with-resources and the Files utility.
        var file: File? = null
        var inputStream: InputStream? = null
        var outputStream: OutputStream? = null
        var success = false
        try {
            val extension = getImageExtension(uri)
            inputStream = context.contentResolver.openInputStream(uri)
            file = File.createTempFile("image_picker", extension, context.cacheDir)
            outputStream = FileOutputStream(file)
            if (inputStream != null) {
                copy(inputStream, outputStream)
                success = true
            }
        } catch (ignored: IOException) {
        } finally {
            try {
                inputStream?.close()
            } catch (ignored: IOException) {
            }
            try {
                outputStream?.close()
            } catch (ignored: IOException) {
                // If closing the output stream fails, we cannot be sure that the
                // target file was written in full. Flushing the stream merely moves
                // the bytes into the OS, not necessarily to the file.
                success = false
            }
        }
        return if (success) file!!.path else null
    }

    /**
     * ак  @return extension of image with dot, or default .jpg if it none.
     */
    private fun getImageExtension(uriImage: Uri): String {
        var extension: String? = null
        try {
            val imagePath = uriImage.path
            if (imagePath != null && imagePath.lastIndexOf(".") != -1) {
                extension = imagePath.substring(imagePath.lastIndexOf(".") + 1)
            }
        } catch (e: Exception) {
            extension = null
        }
        if (extension == null || extension.isEmpty()) {
            //default extension for matches the previous behavior of the plugin
            extension = "jpg"
        }
        return ".$extension"
    }

    @Throws(IOException::class)
    private fun copy(`in`: InputStream, out: OutputStream) {
        val buffer = ByteArray(4 * 1024)
        var bytesRead: Int
        while (`in`.read(buffer).also { bytesRead = it } != -1) {
            out.write(buffer, 0, bytesRead)
        }
        out.flush()
    }

    private fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }

    private fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
    }

    private fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }

    private fun isGooglePhotosUri(uri: Uri): Boolean {
        return "com.google.android.apps.photos.contentprovider" == uri.authority
    }

    fun getFileName(context: Context, uri: Uri): String? {
        var result: String? = null
        if (uri.scheme == "content") {
            val cursor: Cursor? =
                context.contentResolver.query(uri, null, null, null, null)
            cursor.use { cursor ->
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                }
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result!!.lastIndexOf('/')
            if (cut != -1) {
                result = result!!.substring(cut + 1)
            }
        }
        return result
    }



}