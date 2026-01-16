package com.jarvis.core.services

import android.app.*
import android.content.Intent
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.IBinder
import androidx.core.app.NotificationCompat
import ai.picovoice.porcupine.*

class WakeWordService : Service() {
    private var porcupineManager: PorcupineManager? = null
    private val toneGen = ToneGenerator(AudioManager.STREAM_SYSTEM, 100)

    override fun onCreate() {
        super.onCreate()
        startForeground(1, createNotification())
        initPorcupine()
    }

    private fun initPorcupine() {
        porcupineManager = PorcupineManager.Builder()
            .setAccessKey("73YlFlpVmGD7eA9fsG1dk1yURfVctg0udzu9K5EafYXCBakU1CIqHg==")
            .setKeyword(Porcupine.BuiltInKeyword.JARVIS)
            .build(applicationContext) { 
                toneGen.startTone(ToneGenerator.TONE_PROP_ACK, 150)
            }
        porcupineManager?.start()
    }

    private fun createNotification(): Notification {
        val channelId = "jarvis_active"
        (getSystemService(NOTIFICATION_SERVICE) as NotificationManager)
            .createNotificationChannel(NotificationChannel(channelId, "JARVIS", NotificationManager.IMPORTANCE_LOW))
        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("JARVIS Online")
            .setSmallIcon(android.R.drawable.ic_btn_speak_now)
            .build()
    }

    override fun onBind(intent: Intent?): IBinder? = null
    override fun onDestroy() {
        porcupineManager?.stop()
        super.onDestroy()
    }
}
