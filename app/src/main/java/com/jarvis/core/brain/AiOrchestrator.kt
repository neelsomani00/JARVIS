package com.jarvis.core.brain

import com.google.ai.client.generativeai.GenerativeModel
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import okio.ByteString

class AiOrchestrator {
    private val geminiKey = "AIzaSyBfK3WWZLUQ4Vdh8DHNpvBIOmK5yGHuw6o"
    private val deepgramKey = "2345596ac993296fe43b0396187216b52fb5b9ad"
    private val elevenLabsKey = "sk_8ce28158ecc07db9553818f8b5aa63b3209b1aa19f8c57be"

    private val generativeModel = GenerativeModel(
        modelName = "gemini-2.0-flash",
        apiKey = geminiKey
    )

    suspend fun think(userInput: String, screenContext: String): String? {
        val prompt = "You are J.A.R.V.I.S. Context: $screenContext. User says: $userInput"
        return generativeModel.generateContent(prompt).text
    }

    fun speak(text: String, callback: (ByteString) -> Unit) {
        val client = OkHttpClient()
        val body = """{"text":"$text", "model_id": "eleven_multilingual_v2"}""".toRequestBody("application/json".toMediaType())
        val request = Request.Builder()
            .url("https://api.elevenlabs.io/v1/text-to-speech/pNInz6obpgDQGcFmaJgB")
            .addHeader("xi-api-key", elevenLabsKey)
            .post(body).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: java.io.IOException) {}
            override fun onResponse(call: Call, response: Response) {
                response.body?.byteString()?.let { callback(it) }
            }
        })
    }
}