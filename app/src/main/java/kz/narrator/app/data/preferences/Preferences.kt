package kz.narrator.app.data.preferences

import kz.narrator.app.data.models.ai_data.ScriptAudioSettings

interface Preferences {

    /**
     *
     */
    fun setVoiceScripts(list: List<ScriptAudioSettings>?)
    fun getVoiceScripts(): List<ScriptAudioSettings>?

    fun setSelectedScript(item: ScriptAudioSettings?)
    fun getSelectedScript(): ScriptAudioSettings?

}
