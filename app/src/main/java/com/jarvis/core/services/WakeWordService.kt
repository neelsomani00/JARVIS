package com.jarvis.core.services

import android.app.*
import android.content.Intent
import android.content.pm.ServiceInfo
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import ai.picovoice.porcupine.*

class WakeWordService : Service() {
    private var porcupineManager: PorcupineManager? = null
    private val toneGen = ToneGenerator(AudioManager.STREAM_SYSTEM, 100)

    override fun onCreate() {
        super.onCreate()
        startServiceAsForeground()
        initPorcupine()
    }

    private fun startServiceAsForeground() {
        val channelId = "jarvis_active"
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "JARVIS Background Service", NotificationManager.IMPORTANCE_LOW)
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("JARVIS is Listening")
            .setContentText("Wake word 'Jarvis' is active")
            .setSmallIcon(android.R.drawable.ic_btn_speak_now)
            .setOngoing(true)
            .build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(1, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_MICROPHONE)
        } else {
            startForeground(1, notification)
        }
    }

    private fun initPorcupine() {
        try {
            porcupineManager = PorcupineManager.Builder()
                .setAccessKey("73YlFlpVmGD7eA9fsG1dk1yURfVctg0udzu9K5EafYXCBakU1CIqHg==")
                .setKeyword(Porcupine.BuiltInKeyword.JARVIS)
                .build(applicationContext) { 
                    toneGen.startTone(ToneGenerator.TONE_PROP_ACK, 150)
                }
            porcupineManager?.start()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null
    
    override fun onDestroy() {
        porcupineManager?.stop()
        porcupineManager?.delete()
        super.onDestroy()
    }
}
