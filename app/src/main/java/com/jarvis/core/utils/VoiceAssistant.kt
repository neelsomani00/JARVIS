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
import java.util.*

class VoiceAssistant(private val context: Context, private val onResult: (String, String) -> Unit) : TextToSpeech.OnInitListener {
    private var tts: TextToSpeech = TextToSpeech(context, this)
    private val handler = Handler(Looper.getMainLooper())
    private var isListeningForWakeWord = true

    override fun onInit(status: Int) { if (status == TextToSpeech.SUCCESS) tts.language = Locale.US }
    fun speak(text: String) { tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "") }

    fun start() {
        handler.post {
            val sr = SpeechRecognizer.createSpeechRecognizer(context)
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, context.packageName)
            }
            sr.setRecognitionListener(object : RecognitionListener {
                override fun onResults(results: Bundle?) {
                    val text = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.get(0) ?: ""
                    processChatOrCommand(text)
                }
                override fun onReadyForSpeech(p0: Bundle?) { onResult("System Listening", "I'm here, sir.") }
                override fun onError(p0: Int) { 
                    onResult("Silence", "Standing by.") 
                    if (isListeningForWakeWord) start() // Loop back to keep listening
                }
                override fun onBeginningOfSpeech() {}
                override fun onRmsChanged(p0: Float) {}
                override fun onBufferReceived(p0: ByteArray?) {}
                override fun onEndOfSpeech() {}
                override fun onPartialResults(p0: Bundle?) {}
                override fun onEvent(p0: Int, p1: Bundle?) {}
            })
            sr.startListening(intent)
        }
    }

    private fun processChatOrCommand(text: String) {
        val input = text.lowercase()
        
        // 1. SMART CHATBOT REASONING
        val chatResponse = when {
            input.contains("hello") || input.contains("hi jarvis") -> "Hello sir. Systems are at 100%. How can I assist?"
            input.contains("who are you") -> "I am J.A.R.V.I.S., your personalized neural network assistant."
            input.contains("your creator") || input.contains("made you") -> "I was developed as a Stark-inspired mobile intelligence."
            input.contains("how are you") -> "Functioning within normal parameters. My processors are running quite smoothly."
            else -> null
        }

        if (chatResponse != null) {
            speak(chatResponse)
            onResult(text, chatResponse)
            return
        }

        // 2. TASK ENGINE (If not chatting, then check for orders)
        when {
            input.contains("recent") || input.contains("tabs") -> {
                speak("Accessing recent apps.")
                onResult(text, "ACTION: SHOW_RECENTS")
            }
            input.contains("open") || input.contains("launch") -> {
                val appTarget = input.replace("open", "").replace("launch", "").replace("for me", "").trim()
                launchAppSmart(appTarget, text)
            }
            else -> {
                speak("I heard $text. I'm learning to understand complex thoughts like that.")
                onResult(text, "Thinking...")
            }
        }
    }

    private fun launchAppSmart(name: String, originalText: String) {
        val pm = context.packageManager
        val apps = pm.getInstalledApplications(0)
        val bestMatch = apps.find { pm.getApplicationLabel(it).toString().lowercase().contains(name) }
        
        if (bestMatch != null) {
            val intent = pm.getLaunchIntentForPackage(bestMatch.packageName)
            context.startActivity(intent?.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            speak("Launching ${pm.getApplicationLabel(bestMatch)}")
            onResult(originalText, "Success")
        } else {
            speak("I couldn't find $name in your local database.")
            onResult(originalText, "Not Found")
        }
    }
}
