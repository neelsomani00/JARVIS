package com.jarvis.core.utils

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import com.jarvis.core.brain.AiOrchestrator
import kotlinx.coroutines.*
import java.util.*

class VoiceAssistant(private val context: Context, private val onResult: (String, String) -> Unit) : TextToSpeech.OnInitListener {
    
    private var tts: TextToSpeech = TextToSpeech(context, this)
    private val scope = CoroutineScope(Dispatchers.Main + Job())
    private var speechRecognizer: SpeechRecognizer? = null
    
    // CONNECTING TO THE VERCEL BRAIN
    private val brain = AiOrchestrator()

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts.language = Locale.US
        }
    }

    fun start() {
        // Ensure we are on main thread for SpeechRecognizer
        Handler(Looper.getMainLooper()).post {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            }
            
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
            speechRecognizer?.setRecognitionListener(object : RecognitionListener {
                override fun onResults(bundle: Bundle?) {
                    val text = bundle?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.get(0) ?: ""
                    if (text.isNotEmpty()) {
                        processWithBrain(text)
                    }
                }
                
                override fun onError(i: Int) { 
                    // Verify if error is "No Match" (7) or "Speech Timeout" (6) and handle gracefully
                }
                override fun onReadyForSpeech(b: Bundle?) {}
                override fun onBeginningOfSpeech() {}
                override fun onRmsChanged(f: Float) {}
                override fun onBufferReceived(b: ByteArray?) {}
                override fun onEndOfSpeech() {}
                override fun onPartialResults(b: Bundle?) {}
                override fun onEvent(i: Int, b: Bundle?) {}
            })
            speechRecognizer?.startListening(intent)
        }
    }

    private fun processWithBrain(text: String) {
        // Speak "Thinking..." or play a sound here if you want
        
        brain.processCommand(text) { response ->
            scope.launch {
                onResult(text, response)
                speak(response)
            }
        }
    }

    private fun speak(text: String) {
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "ID")
    }

    fun shutdown() {
        tts.stop()
        tts.shutdown()
        speechRecognizer?.destroy()
        scope.cancel()
    }
}
