package com.jarvis.core.services

import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.IBinder
import android.view.Gravity
import android.view.WindowManager
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.lifecycle.*
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner

class OverlayService : Service(), SavedStateRegistryOwner {

    private lateinit var windowManager: WindowManager
    private var composeView: ComposeView? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.END
            x = 20
            y = 200
        }

        val lifecycleOwner = MyLifecycleOwner()
        lifecycleOwner.performRestore(null)
        lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)

        composeView = ComposeView(this).apply {
            setContent {
                JarvisArcReactor()
            }
        }
        
        // This is a workaround to use Compose in a Service
        composeView?.setViewTreeLifecycleOwner(lifecycleOwner)
        composeView?.setViewTreeSavedStateRegistryOwner(this)
        
        windowManager.addView(composeView, params)
    }

    @Composable
    fun JarvisArcReactor() {
        val infiniteTransition = rememberInfiniteTransition(label = "pulse")
        val scale by infiniteTransition.animateFloat(
            initialValue = 1f,
            targetValue = 1.2f,
            animationSpec = infiniteRepeatable(
                animation = tween(1000, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ), label = "scale"
        )

        Box(
            modifier = Modifier
                .size(60.dp)
                .scale(scale)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(Color(0xFF00E5FF), Color(0xFF006064))
                    )
                )
                .border(2.dp, Color.White.copy(alpha = 0.5f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            // Inner Core
            Box(modifier = Modifier.size(20.dp).clip(CircleShape).background(Color.White))
        }
    }

    // Lifecycle Boilerplate for Compose in Service
    private var mLifecycleRegistry = LifecycleRegistry(this)
    override val lifecycle: Lifecycle get() = mLifecycleRegistry
    private val mSavedStateRegistryController = SavedStateRegistryController.create(this)
    override val savedStateRegistry: SavedStateRegistry get() = mSavedStateRegistryController.savedStateRegistry
    internal class MyLifecycleOwner : LifecycleOwner {
        private val registry = LifecycleRegistry(this)
        override val lifecycle: Lifecycle get() = registry
        fun handleLifecycleEvent(event: Lifecycle.Event) = registry.handleLifecycleEvent(event)
        fun performRestore(bundle: android.os.Bundle?) {}
    }

    override fun onDestroy() {
        super.onDestroy()
        composeView?.let { windowManager.removeView(it) }
    }
}