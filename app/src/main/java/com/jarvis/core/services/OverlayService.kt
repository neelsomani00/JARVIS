package com.jarvis.core.services

import android.accessibilityservice.AccessibilityService
import android.graphics.PixelFormat
import android.view.Gravity
import android.view.WindowManager
import android.view.accessibility.AccessibilityEvent
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.*
import androidx.savedstate.*
import com.jarvis.core.ui.JarvisFloatingCore
import com.jarvis.core.ui.JarvisDashboard
import com.jarvis.core.utils.VoiceAssistant

class OverlayService : AccessibilityService(), LifecycleOwner, ViewModelStoreOwner, SavedStateRegistryOwner {
    private val lifecycleRegistry = LifecycleRegistry(this)
    private val savedStateRegistryController = SavedStateRegistryController.create(this)
    private val store = ViewModelStore()
    override val lifecycle: Lifecycle get() = lifecycleRegistry
    override val savedStateRegistry: SavedStateRegistry get() = savedStateRegistryController.savedStateRegistry
    override val viewModelStore: ViewModelStore get() = store

    private var transcript by mutableStateOf("Ready")
    private var jarvisResponse by mutableStateOf("Online")
    private var isDashboardVisible by mutableStateOf(false)

    override fun onCreate() {
        super.onCreate()
        savedStateRegistryController.performRestore(null)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
    }

    override fun onServiceConnected() {
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
        
        val wm = getSystemService(WINDOW_SERVICE) as WindowManager
        val voiceAssistant = VoiceAssistant(this) { userText, aiText ->
            transcript = userText
            jarvisResponse = aiText
            when (aiText) {
                "ACTION: GLOBAL_HOME" -> performGlobalAction(GLOBAL_ACTION_HOME)
                "ACTION: SHOW_RECENTS" -> performGlobalAction(GLOBAL_ACTION_RECENTS)
            }
        }

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
            PixelFormat.TRANSLUCENT
        ).apply { gravity = Gravity.TOP or Gravity.START; x = 0; y = 400 }

        val overlayView = ComposeView(this).apply {
            setViewTreeLifecycleOwner(this@OverlayService)
            setViewTreeViewModelStoreOwner(this@OverlayService)
            setViewTreeSavedStateRegistryOwner(this@OverlayService)
            setContent {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    if (isDashboardVisible) JarvisDashboard(transcript, jarvisResponse)
                    JarvisFloatingCore(onMove = { dx, dy -> params.x += dx; params.y += dy; wm.updateViewLayout(this@apply, params) },
                                       onClick = { isDashboardVisible = !isDashboardVisible; if (isDashboardVisible) voiceAssistant.start() })
                }
            }
        }
        wm.addView(overlayView, params)
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {}
    override fun onInterrupt() {}
}
