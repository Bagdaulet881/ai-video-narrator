package kz.narrator.app.ui.ai_processor.speech_to_text

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.speech.v1.*
import com.google.protobuf.ByteString
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kz.narrator.app.R
import kz.narrator.app.data.models.ai_data.MappedTimeStamps
import kz.narrator.app.data.models.network.Status
import kz.narrator.app.ui_common.callbacks.UILoggingCallback
import kz.narrator.app.utils.file.FileUtils
import kz.narrator.app.utils.splitIntoSublists

class SpeechWithTimestamp private constructor(
    private val activity: Activity,
    private val requireContext: Context
) {

    private val speechClient: SpeechClient by lazy {
        activity.applicationContext.resources?.openRawResource(R.raw.google_cloud_credentials)
            .use {
                SpeechClient.create(
                    SpeechSettings.newBuilder()
                        .setCredentialsProvider { GoogleCredentials.fromStream(it) }
                        .build())
            }
    }

    private lateinit var resultLoggingCallback: UILoggingCallback

    fun setUILoggingCallback(callback: UILoggingCallback): SpeechWithTimestamp {
        this.resultLoggingCallback = callback
        return this
    }

    fun getTimeStamps(fileName: String) {
        GlobalScope.launch {
            analyze(
                ByteString.copyFrom(
                    FileUtils.getAudioFile(
                        requireContext,
                        fileName
                    ).readBytes()
                )
            )
        }
    }

    private fun analyze(fileByteString: ByteString) {
        resultLoggingCallback.onLogState("GoogleAI: initializing", Status.LOADING)
        val req = RecognizeRequest.newBuilder()
            .setConfig(
                RecognitionConfig.newBuilder()
                    .setEncoding(RecognitionConfig.AudioEncoding.MP3)
                    .setEnableWordTimeOffsets(true)
                    .setLanguageCode("en-US")
                    .setSampleRateHertz(16000)
                    .build()
            )
            .setAudio(
                RecognitionAudio.newBuilder()
                    .setContent(fileByteString)
                    .build()
            )
            .build()

        resultLoggingCallback.onLogState("GoogleAI: request", Status.LOADING)
        val response = speechClient.recognize(req)
        resultLoggingCallback.onLogState("GoogleAI: response cnt=${response.resultsCount}!", Status.SUCCESS)

        Log.d("MSG", "Response, count ${response.resultsCount}")
        val results = response.resultsList
        val alternative = results.get(0).getAlternativesList().get(0)

        val sntcs = mapTimeStamps(alternative.wordsList)
        resultLoggingCallback.onLogState("GoogleAI: response sentences=${sntcs.size}!", Status.SUCCESS)
        resultLoggingCallback.onTimeStampSuccess(sntcs)
    }

    private fun mapTimeStamps(wordsList: MutableList<WordInfo>): List<MappedTimeStamps> {
        val MAX_CHAR_PER_LINE = 23
        val timestamps = mutableListOf<MappedTimeStamps>()

        val sentences = wordsList.splitIntoSublists(12)

        for (sentence in sentences) {
            val mappedSentence = MappedTimeStamps()
            val word = StringBuilder()

            sentence.forEachIndexed { index, wordInfo ->
                val wordTxt = addLineBreaks(wordInfo.word.replace("'",""), MAX_CHAR_PER_LINE)
                val startTime = wordInfo.startTime
                val endTime = wordInfo.endTime

                if (index == 0) {
                    mappedSentence.startTime = startTime.seconds
                    mappedSentence.startTimeNanos = startTime.nanos
                }

                word.append(wordTxt).append(" ")

                if (index == sentence.lastIndex) {
                    mappedSentence.endTime = endTime.seconds
                    mappedSentence.endTimeNanos = endTime.nanos
                }
            }

            mappedSentence.word = word.toString()
            timestamps.add(mappedSentence)
            Log.d(
                "MSG",
                "Word: ${mappedSentence.word} (start: ${mappedSentence.startTime}.${mappedSentence.startTimeNanos}, end: ${mappedSentence.endTime}.${mappedSentence.endTimeNanos})"
            )
        }

        return timestamps.toList()
    }

    fun addLineBreaks(text: String, maxChars: Int): String {
        val words = text.split(" ")
        var currentLine = ""
        var processedText = ""

        for (word in words) {
            if (currentLine.length + word.length > maxChars) {
                processedText += "$currentLine\n"
                currentLine = "$word "
            } else {
                currentLine += "$word "
            }
        }

        processedText += currentLine // Add the last line
        return processedText.trim()
    }

    companion object {

        fun with(activity: Activity, requireContext: Context): SpeechWithTimestamp {
            return SpeechWithTimestamp(activity, requireContext)
        }

    }
}

