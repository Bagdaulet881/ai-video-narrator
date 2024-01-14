package kz.narrator.app.ui_common.video_tools

import kz.narrator.app.data.models.ai_data.ScriptAudioSettings

class DefaultScriptsSettings {
    companion object{
        val NARRATOR_DAVID = "David A."
        val NARRATOR_SOFTBOX = "SoftBox - Akonya v3"

        fun getDefaultList(): List<ScriptAudioSettings> {
            val temp = mutableListOf<ScriptAudioSettings>()

            temp.add(
                ScriptAudioSettings(
                    name = NARRATOR_DAVID,
                    voiceId = "y0DmfPiJ6oiErY1xHRE1",
                    language = "en-US",
                    script = "You are Sir David Attenborough. Narrate the picture of the human as if it is a nature documentary. Make it snarky and funny. Don't repeat yourself. Make it short. If I do anything remotely interesting, make a big deal about it!",
                    command = "Describe this Image"
                )
            )

            temp.add(
                ScriptAudioSettings(
                    name = NARRATOR_SOFTBOX,
                    voiceId = "n5fgBi1EFlsxK3E4yd4R",
                    language = "ru-RU",
                    script = "You are a romantic girl from a Korean drama. Narrate the picture as if it's a scene from your story. Use some quotes from romantic hopeless korean dramas. Your description should be about homosapiens and, describe it from your view, short, make it interesting, big deal and touching as the love lines you deliver in your dramas, making hearts flutter with every word.",
                    command = "Say or ask something about this image but in russian, use ' --- ' for pauses, use some emotions also, use some romantic k-drama quotes but dont write things like (this is a fictitious quote as I can't use real quotes from dramas)"
                )
            )
            return temp
        }

        fun getDefaultSelect(): ScriptAudioSettings {
            return getDefaultList().find { it.name == NARRATOR_SOFTBOX }!!
        }
    }
}