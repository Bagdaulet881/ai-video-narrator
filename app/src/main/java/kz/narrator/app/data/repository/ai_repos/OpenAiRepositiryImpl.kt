package kz.narrator.app.data.repository.ai_repos

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.aallam.openai.api.chat.ChatCompletion
import com.aallam.openai.api.chat.ChatCompletionRequest
import kz.narrator.app.data.models.network.Resource
import kz.narrator.app.data.openai.OpenAiInstance
import kz.narrator.app.network.api.AiApi
import kz.narrator.app.utils.live_data.Event
import javax.inject.Inject
import android.content.Context
import kz.narrator.app.data.models.AudioRequestPayload
import kz.narrator.app.data.repository.error_handler.RepositoryErrorHandler
import okhttp3.ResponseBody
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class OpenAiRepositiryImpl
@Inject constructor(
    private val openAiInstance: OpenAiInstance,
    private val aiApi: AiApi,
    private val app: Application,
    private val errorHandler: RepositoryErrorHandler,
    ): OpenAiRepositiry {

    override fun getChatCompletions(chatCompletionRequest: ChatCompletionRequest): LiveData<Event<Resource<ChatCompletion>>> {
        return liveData {
            try {
                emit(
                    Event(Resource.loading(null))
                )
                val request = openAiInstance.get().chatCompletion(chatCompletionRequest)

                emit(
                    Event(Resource.success(request))
                )
            }catch (e: Exception){
                emit(
                    Event(Resource.error("TESTFLOW_ERROR", null, e))
                )
            }
        }
    }

    override fun generateAudio(text: AudioRequestPayload, voiceId: String): LiveData<Event<Resource<String>>> {
        return liveData {
            try {
                emit(
                    Event(Resource.loading(null))
                )
                val request = aiApi.generateAudio(voiceId = voiceId, textBody = text)
                with(request){
                    if(request.isSuccessful){
                        val filePath = body()?.let { saveAudioFile(it, fileName = "audioText.mp3") }

                        emit(
                            Event(Resource.success(filePath))
                        )
                    }else{
                        emit(
                            Event(errorHandler.getError(code(), errorBody()))
                        )
                    }
                }
            }catch (e: Exception){
                emit(
                    Event(errorHandler.getError(e))
                )
            }
        }
    }

    fun saveAudioFile(responseBody: ResponseBody, fileName: String): String {
        val context = app.applicationContext
        val audioFile = File(context.filesDir, fileName)

        var inputStream: InputStream? = null
        var outputStream: FileOutputStream? = null
        try {
            val fileReader = ByteArray(4096)
            val fileSize = responseBody.contentLength()
            var fileSizeDownloaded: Long = 0

            inputStream = responseBody.byteStream()
            outputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE)

            while (true) {
                val read = inputStream.read(fileReader)
                if (read == -1) {
                    break
                }
                outputStream.write(fileReader, 0, read)
                fileSizeDownloaded += read
            }

            outputStream.flush()
            return audioFile.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            inputStream?.close()
            outputStream?.close()
        }
        return ""
    }
}