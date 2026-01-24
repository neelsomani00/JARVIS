package com.jarvis.core.services

import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.IBinder
import android.view.Gravity
import android.view.WindowManager
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.*
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.jarvis.core.ui.JarvisFloatingCore

class OverlayService : Service(), LifecycleOwner {
    private lateinit var windowManager: WindowManager
    private var params: WindowManager.LayoutParams? = null
    private val lifecycleRegistry = LifecycleRegistry(this)

    override fun onCreate() {
        super.onCreate()
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
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
                onClick = {
                    // Logic to open dashboard
                }
            )
        }

        // Required for Compose in a Service
        val viewModelStore = ViewModelStore()
        val lifecycleOwner = this
        composeView.setViewTreeLifecycleOwner(lifecycleOwner)
        composeView.setViewTreeViewModelStoreOwner(object : ViewModelStoreOwner {
            override val viewModelStore: ViewModelStore = viewModelStore
        })
        composeView.setViewTreeSavedStateRegistryOwner(object : androidx.savedstate.SavedStateRegistryOwner {
            override val lifecycle: Lifecycle = lifecycleOwner.lifecycle
            override val savedStateRegistry = androidx.savedstate.SavedStateRegistryController.create(this).apply {
                performRestore(null)
            }.savedStateRegistry
        })

        windowManager.addView(composeView, params)
    }

    override fun onBind(intent: Intent?): IBinder? = null
    override val lifecycle: Lifecycle get() = lifecycleRegistry
}
