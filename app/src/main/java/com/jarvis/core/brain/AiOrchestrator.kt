package com.jarvis.core.brain

import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

class AiOrchestrator {
    private val client = OkHttpClient()
    // REPLACE THIS with your actual Vercel URL
    private val bridgeUrl = "https://your-project.vercel.app/api/evolve"

    fun processCommand(query: String, callback: (String) -> Unit) {
        val json = JSONObject().apply {
            put("command", query)
        }
        
        val body = json.toString().toRequestBody("application/json".toMediaType())
        val request = Request.Builder().url(bridgeUrl).post(body).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback("Error: Connection Failed")
            }

            override fun onResponse(call: Call, response: Response) {
                val responseData = response.body?.string()
                val aiMessage = JSONObject(responseData ?: "{}").optString("reply", "I'm processing...")
                callback(aiMessage)
            }
        })
    }
}
