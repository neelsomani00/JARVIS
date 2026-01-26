package com.jarvis.core.brain

import android.util.Log
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

class AiOrchestrator {
    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    private val bridgeUrl = "https://neel-jarvis.vercel.app/api/evolve"

    fun processCommand(query: String, callback: (String) -> Unit) {
        Log.d("JARVIS_BRAIN", "Sending query: $query")
        
        val json = JSONObject().apply {
            put("command", query)
        }
        
        val body = json.toString().toRequestBody("application/json".toMediaType())
        val request = Request.Builder()
            .url(bridgeUrl)
            .post(body)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("JARVIS_BRAIN", "Connection Failed: ${e.message}")
                callback("Error: Connection Failed")
            }

            override fun onResponse(call: Call, response: Response) {
                val responseData = response.body?.string()
                Log.d("JARVIS_BRAIN", "Response: $responseData")
                val aiMessage = JSONObject(responseData ?: "{}").optString("reply", "System online.")
                callback(aiMessage)
            }
        })
    }
}
