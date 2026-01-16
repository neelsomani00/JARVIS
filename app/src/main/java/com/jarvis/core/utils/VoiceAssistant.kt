package com.jarvis.core.utils

import android.content.*
import android.os.*
import android.speech.*
import android.speech.tts.*
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.*
import java.util.*

class VoiceAssistant(private val context: Context, private val onResult: (String, String) -> Unit) : TextToSpeech.OnInitListener {
    private var tts: TextToSpeech = TextToSpeech(context, this)
    private val scope = CoroutineScope(Dispatchers.Main + Job())
    private var silenceRetries = 0
    private val geminiKey = "AIzaSyBfK3WWZLUQ4Vdh8DHNpvBIOmK5yGHuw6o"
    private val model = GenerativeModel(modelName = "gemini-1.5-flash", apiKey = geminiKey)

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts.language = Locale.US
            tts.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(s: String?) {}
                override fun onError(s: String?) {}
                override fun onDone(s: String?) {
                    Handler(Looper.getMainLooper()).post { start() }
                }
            })
        }
    }

    fun start() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        }
        val sr = SpeechRecognizer.createSpeechRecognizer(context)
        sr.setRecognitionListener(object : RecognitionListener {
            override fun onResults(bundle: Bundle?) {
                val text = bundle?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.get(0) ?: ""
                scope.launch {
                    try {
                        val response = model.generateContent(text).text ?: ""
                        tts.speak(response, TextToSpeech.QUEUE_FLUSH, null, "ID")
                        onResult(text, response)
                    } catch (e: Exception) {
                        onResult(text, "Error: ${e.message}")
                    }
                }
            }
            override fun onError(i: Int) {
                if (silenceRetries < 1) {
                    silenceRetries++
                    start()
                } else {
                    silenceRetries = 0
                    onResult("Status", "Session Ended")
                }
            }
            override fun onReadyForSpeech(b: Bundle?) {}
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(f: Float) {}
            override fun onBufferReceived(b: ByteArray?) {}
            override fun onEndOfSpeech() {}
            override fun onPartialResults(b: Bundle?) {}
            override fun onEvent(i: Int, b: Bundle?) {}
        })
        sr.startListening(intent)
    }

    fun shutdown() {
        tts.stop()
        tts.shutdown()
        scope.cancel()
    }
}
