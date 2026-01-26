package com.jarvis.core.utils

import android.content.Context
import android.speech.tts.TextToSpeech
import com.jarvis.core.brain.AiOrchestrator
import java.util.*

class VoiceAssistant(context: Context) : TextToSpeech.OnInitListener {
    private var tts: TextToSpeech = TextToSpeech(context, this)
    private val orchestrator = AiOrchestrator()

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts.language = Locale.US
        }
    }

    fun speak(text: String) {
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
    }

    fun processAndSpeak(query: String) {
        orchestrator.processCommand(query) { response ->
            speak(response)
        }
    }

    fun shutdown() {
        tts.stop()
        tts.shutdown()
    }
}
