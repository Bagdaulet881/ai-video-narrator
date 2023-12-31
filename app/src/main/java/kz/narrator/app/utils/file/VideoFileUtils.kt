package kz.narrator.app.utils.file

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import java.io.*

object Constants{
    val APP_FOLDER = "NarratorMediaFiles"
}

object VideoFileUtils {

    val outputPath: String
        get() {
            val path = Environment.getExternalStorageDirectory().toString() + File.separator + Constants.APP_FOLDER + File.separator
            Log.i("MSG", "FILE_SYSTEM_CHECK: path ${path}")

            val folder = File(path)
            if (!folder.exists()){
                folder.mkdirs()
                Log.i("MSG", "FILE_SYSTEM_CHECK: folder.exists() false")
            }else{
                Log.i("MSG", "FILE_SYSTEM_CHECK: folder.exists() ok ${folder.exists()}")
            }

            return path
        }

    fun copyTTFFileToExternalStorage(resourceId: Int, resourceName: String, context: Context): File {
        val pathSDCard = outputPath + resourceName
        val outputFile = File(pathSDCard)

        try {
            if (!outputFile.parentFile.exists()) {
                Log.i("MSG", "FILE_SYSTEM_CHECK: outputFile.parentFile")
                outputFile.parentFile.mkdirs() // Create parent directories if they don't exist
            }

            context.resources.openRawResource(resourceId).use { inputStream ->
                FileOutputStream(outputFile).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            Log.i("MSG", "FILE_SYSTEM_CHECK: FileNotFoundException")
            return outputFile
        } catch (e: IOException) {
            e.printStackTrace()
            Log.i("MSG", "FILE_SYSTEM_CHECK: IOException")
            return outputFile
        }

        return outputFile
    }

    fun testcopyTTFFileToExternalStorage(resourceId: Int, resourceName: String, context: Context): Uri? {
        val mimeType = "application/x-font-ttf"

        // Use the generic MediaStore.Files collection for non-media file types
        val collection = MediaStore.Files.getContentUri("external")

        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, resourceName)
            put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
        }

        return try {
            context.contentResolver.insert(collection, contentValues)?.also { uri ->
                context.contentResolver.openOutputStream(uri).use { outputStream ->
                    context.resources.openRawResource(resourceId).use { inputStream ->
                        inputStream.copyTo(outputStream!!)
                    }
                }
            }
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            null
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }



    fun InputStream.toFile(path: String) {
        File(path).outputStream().use { this.copyTo(it) }
    }

    fun getConvertedFile(folder: String, fileName: String): File {
        val directory = File(folder)

        if (!directory.exists())
            directory.mkdirs()

        val file = File(directory.path + File.separator + fileName) //TODO 881 31.12 check bug

        if (!file.exists()) {
            try {
                file.createNewFile()
            } catch (e: IOException) {
                // Handle the exception
                e.printStackTrace()
            }
        }

        return file
    }

    fun refreshGallery(path: String, context: Context) {

        val file = File(path)
        try {
            val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
            val contentUri = Uri.fromFile(file)
            mediaScanIntent.data = contentUri
            context.sendBroadcast(mediaScanIntent)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun milliSecondsToTimer(milliseconds: Long): String {
        var finalTimerString = ""
        var secondsString = ""

        // Convert total duration into time
        val hours = (milliseconds / (1000 * 60 * 60)).toInt()
        val minutes = (milliseconds % (1000 * 60 * 60)).toInt() / (1000 * 60)
        val seconds = (milliseconds % (1000 * 60 * 60) % (1000 * 60) / 1000).toInt()
        // Add hours if there
        if (hours > 0) {
            finalTimerString = hours.toString() + ":"
        }

        // Prepending 0 to seconds if it is one digit
        if (seconds < 10) {
            secondsString = "0" + seconds
        } else {
            secondsString = "" + seconds
        }

        finalTimerString = finalTimerString + minutes + ":" + secondsString

        // return timer string
        return finalTimerString
    }
}