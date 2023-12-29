package kz.narrator.app.utils.base64

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Base64
import android.util.Log
import android.webkit.MimeTypeMap
import kz.narrator.app.data.enums.app.DatePatternEnums
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

object Base64Utils {

    fun getName(): String {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        return "name_${timeStamp}_"
    }

    fun getAudioFileName(): String {
        val timeStamp = SimpleDateFormat(DatePatternEnums.yyyyMMdd_HHmmss.id, Locale.getDefault()).format(Date())
        return "audio_name_${timeStamp}"
    }

    fun getBase64(context: Context, uri: Uri): String? {
        return try {
//            val bitmap = BitmapFactory.decodeStream(context.contentResolver.openInputStream(uri))
//            val resizedBitmap = Bitmap.createScaledBitmap(bitmap, 500, 500, false)

            val bitmap = getSmallBitmap(context, uri)
            convertToBase64(bitmap)

        } catch (e: FileNotFoundException) {
            null
        }
    }

    fun getRawBase64(context: Context, uri: Uri): String {
        return "\"${getBase64(context, uri)}\"".replace(
            "\n",
            ""
        )
    }

    fun getFileNameFromUri(context: Context, uri: Uri): String? {
        val contentResolver = context.contentResolver
        val cursor = contentResolver.query(uri, null, null, null, null)

        return try {
            if (cursor != null && cursor.moveToFirst()) {
                val displayNameColumnIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                cursor.getString(displayNameColumnIndex)
            } else {
                null
            }
        } finally {
            cursor?.close()
        }
    }

    fun getMimeType(context: Context, uri: Uri): String {
        //Check uri format to avoid null
        return if (uri.scheme == ContentResolver.SCHEME_CONTENT) {
            //If scheme is a content
            val mime = MimeTypeMap.getSingleton()
            mime.getExtensionFromMimeType(context.contentResolver.getType(uri))!!
        } else {
            //If scheme is a File
            //This will replace white spaces with %20 and also other special characters. This will avoid returning null values on file name with spaces and special characters.
            MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(File(uri.path)).toString())
        }
    }

    fun getFileName(context: Context, uri: Uri): String? {
        var result: String? = null
        if (uri.scheme == ContentResolver.SCHEME_CONTENT) {
            val cursor = context.contentResolver.query(uri, null, null, null, null)
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                }
            } finally {
                cursor?.close()
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result!!.lastIndexOf('/')
            if (cut != -1) {
                result = result.substring(cut + 1)
            }
        }
        return result //?: "${getName()}.${getMimeType(context, uri)}"
    }

    fun convertToBase64(attachment: File): String {
        return Base64.encodeToString(attachment.readBytes(), Base64.NO_WRAP)
    }

    fun convertToBase64(bitmap: Bitmap): String {
        var encodedImage = ""

        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream)
        try {
            encodedImage =
//                URLEncoder.encode(
                Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT)//,
//                    "UTF-8"
//                )
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }

        return encodedImage
    }

    fun convertToBase64(context: Context, fileUri: Uri): String? {
        var inputStream: InputStream? = null
        var outputStream: ByteArrayOutputStream? = null
        var base64: String? = null
        try {
            inputStream = context.contentResolver.openInputStream(fileUri)
            outputStream = ByteArrayOutputStream()
            val buffer = ByteArray(1024)
            var len: Int
            while (inputStream?.read(buffer).also { len = it ?:0 } != -1) {
                outputStream.write(buffer, 0, len)
            }
            base64 = Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP)
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                inputStream?.close()
                outputStream?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return base64
//        return Base64.encodeToString(attachment.readBytes(), Base64.NO_WRAP)
    }

    @Throws(FileNotFoundException::class)
    fun getSmallBitmap(context: Context, uri: Uri): Bitmap {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        options.inSampleSize = calculateInSampleSize(options, 480)
        options.inJustDecodeBounds = false

        return BitmapFactory.decodeStream(
            context.contentResolver.openInputStream(uri),
            null,
            options
        )!!
    }

    private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int): Int {
        val width = options.outWidth
        var inSampleSize = 1

        if (width > reqWidth) {

            val halfWidth = width / 2

            while (halfWidth / inSampleSize > reqWidth) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }

}