package com.jarvis.core.services

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.Toast
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.jarvis.core.brain.AiOrchestrator
import kotlinx.coroutines.*

class JarvisAccessibilityService : AccessibilityService() {

    private val scope = CoroutineScope(Dispatchers.Main + Job())
    private val orchestrator = AiOrchestrator()
    private var lastAuthTime: Long = 0

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        val rootNode = rootInActiveWindow ?: return
        
        // FLEX: AI is always watching the screen context
        val screenText = getAllText(rootNode)
        
        // Example logic: If 'Power Off' menu is detected, trigger Biometric
        if (screenText.contains("Power off", ignoreCase = true)) {
            checkUserIdentity()
        }
    }

    private fun checkUserIdentity() {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastAuthTime > 900000) { // 15 minutes
            performGlobalAction(GLOBAL_ACTION_HOME)
            Toast.makeText(this, "Biometric Verification Required by JARVIS", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getAllText(node: AccessibilityNodeInfo): String {
        val sb = StringBuilder()
        sb.append(node.text ?: "")
        for (i in 0 until node.childCount) {
            val child = node.getChild(i)
            if (child != null) sb.append(getAllText(child))
        }
        return sb.toString()
    }

    override fun onInterrupt() {}
}