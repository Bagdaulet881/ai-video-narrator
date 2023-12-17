package kz.narrator.app.data.openai

import android.util.Log
import com.aallam.openai.api.http.Timeout
import com.aallam.openai.client.OpenAI
import com.aallam.openai.client.OpenAIConfig
import kz.narrator.app.data.preferences.Preferences
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Duration.Companion.seconds

class OpenAiInstance
@Inject constructor() {
    private val openAiKey = "***REMOVED***"

    val openAiConfig = OpenAIConfig(
        token = openAiKey,
        timeout = Timeout(socket = 15.seconds),
        // additional configurations...
    )

    val openAiInstance = OpenAI(openAiConfig)

    fun get(): OpenAI {
        Log.i("MSG", "SENDREQUEST: INSTANCE GENERATED: ${openAiInstance}")
        return openAiInstance
    }
}