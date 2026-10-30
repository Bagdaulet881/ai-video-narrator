package kz.narrator.app.data.preferences

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kz.narrator.app.data.models.ai_data.ScriptAudioSettings

class PreferencesImpl(
    private val context: Context
) : Preferences {

    companion object {
        private const val VOICE_SCRIPTS = "voice_scripts"
        private const val SELECTED_VOICE_SCRIPTS = "selected_voice_scripts"
    }

    private lateinit var shPr: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    var gson = Gson()

    init {
        shPr = context.getSharedPreferences("PreferencesImpl", Context.MODE_PRIVATE)
        editor = shPr.edit()
    }

    /**
     *  VoiceId, Scripts
     */
    override fun setVoiceScripts(list: List<ScriptAudioSettings>?){
        val gson = Gson()
        val json = gson.toJson(list)
        editor.putString(VOICE_SCRIPTS, json).apply()
    }
    override fun getVoiceScripts(): List<ScriptAudioSettings>?{
        val json = shPr.getString(VOICE_SCRIPTS, null)
        val type = object : TypeToken<List<ScriptAudioSettings>>() {}.type
        return Gson().fromJson(json, type)
    }

    override fun setSelectedScript(item: ScriptAudioSettings?){
        val jsonString = gson.toJson(item)
        editor.putString(SELECTED_VOICE_SCRIPTS, jsonString)
        editor.commit()
    }
    override fun getSelectedScript(): ScriptAudioSettings?{
        return gson.fromJson(shPr.getString(SELECTED_VOICE_SCRIPTS, null), ScriptAudioSettings::class.java)
    }

}
