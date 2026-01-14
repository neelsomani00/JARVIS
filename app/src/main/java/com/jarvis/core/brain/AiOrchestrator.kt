package com.jarvis.core.brain

import android.util.Log
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import java.io.IOException
import org.json.JSONObject

class AiOrchestrator {
    private val client = OkHttpClient()

    fun sendCommand(command: String) {
        val json = JSONObject()
        json.put("command", command)
        
        val mediaType = "application/json; charset=utf-8".toMediaType()
        val body = json.toString().toRequestBody(mediaType)
        
        val request = Request.Builder()
            .url("https://api.openai.com/v1/chat/completions") // Example URL
            .post(body)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("Jarvis", "Network Failed", e)
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) throw IOException("Unexpected code $response")
                    Log.d("Jarvis", "Response: ${response.body?.string()}")
                }
            }
        })
    }
}
