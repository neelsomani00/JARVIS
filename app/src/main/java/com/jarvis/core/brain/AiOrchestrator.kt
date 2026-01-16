package com.jarvis.core.brain

import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody

class AiOrchestrator {
    private val client = OkHttpClient()
    fun processCommand(query: String, callback: (String) -> Unit) {
        callback("Acknowledged: $query")
    }
}
