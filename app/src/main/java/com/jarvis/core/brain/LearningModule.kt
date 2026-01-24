package com.jarvis.core.brain

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

class LearningModule(val context: Context) {
    private val feedbackFile = File(context.filesDir, "interaction_log.json")

    fun recordCorrection(prompt: String, aiAction: String, userCorrection: String) {
        val data = if (feedbackFile.exists()) feedbackFile.readText() else "[]"
        val jsonArray = JSONArray(data)
        
        val entry = JSONObject().apply {
            put("timestamp", System.currentTimeMillis())
            put("prompt", prompt)
            put("failed_action", aiAction)
            put("correction", userCorrection)
        }
        
        jsonArray.put(entry)
        feedbackFile.writeText(jsonArray.toString())
    }
}
