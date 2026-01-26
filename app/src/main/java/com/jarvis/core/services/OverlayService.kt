package com.jarvis.core.services

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.WindowManager
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.*
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.jarvis.core.ui.JarvisFloatingCore
import com.jarvis.core.utils.VoiceAssistant

class OverlayService : Service(), LifecycleOwner, ViewModelStoreOwner, SavedStateRegistryOwner {
    
    private lateinit var windowManager: WindowManager
    private var params: WindowManager.LayoutParams? = null
    private val lifecycleRegistry = LifecycleRegistry(this)
    private val savedStateRegistryController = SavedStateRegistryController.create(this)
    
    // FIX 1: Correctly implement ViewModelStoreOwner
    override val viewModelStore = ViewModelStore()
    
    private lateinit var voiceAssistant: VoiceAssistant

    private val wakeWordReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == "com.jarvis.ACTION_WAKE_WORD_DETECTED") {
                println("JARVIS: Wake Word Detected! Starting Listening...")
                voiceAssistant.start()
            }
        }
    }

    override val lifecycle: Lifecycle get() = lifecycleRegistry
    override val savedStateRegistry: SavedStateRegistry get() = savedStateRegistryController.savedStateRegistry

    override fun onCreate() {
        super.onCreate()
        savedStateRegistryController.performRestore(null)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
        
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        
        voiceAssistant = VoiceAssistant(this) { userQuery, aiResponse ->
            // Handle response
        }

        val filter = IntentFilter("com.jarvis.ACTION_WAKE_WORD_DETECTED")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            registerReceiver(wakeWordReceiver, filter, Context.RECEIVER_EXPORTED)
        } else {
            registerReceiver(wakeWordReceiver, filter)
        }

        showFloatingBall()
    }

    private fun showFloatingBall() {
        val composeView = ComposeView(this)
        
        params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = 100
            y = 100
        }

        composeView.setContent {
            JarvisFloatingCore(
                onMove = { dx, dy ->
                    params?.let {
                        it.x += dx
                        it.y += dy
                        windowManager.updateViewLayout(composeView, it)
                    }
                },
                onClick = { voiceAssistant.start() }
            )
        }

        val lifecycleOwner = this
        composeView.setViewTreeLifecycleOwner(lifecycleOwner)
        composeView.setViewTreeViewModelStoreOwner(this)
        composeView.setViewTreeSavedStateRegistryOwner(this)

        windowManager.addView(composeView, params)
    }

    override fun onBind(intent: Intent?): IBinder? = null
    
    override fun onDestroy() {
        unregisterReceiver(wakeWordReceiver)
        voiceAssistant.shutdown()
        // FIX 2: Clear ViewModelStore to prevent memory leaks
        viewModelStore.clear()
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        super.onDestroy()
    }
}
