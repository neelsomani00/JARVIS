package com.jarvis.core.services

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.Toast

class JarvisAccessibilityService : AccessibilityService() {

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // Listening for UI changes to "learn"
    }

    // This is the function the AI will call to "click" buttons for you
    fun clickButtonByText(text: String): Boolean {
        val rootNode = rootInActiveWindow ?: return false
        val nodes = rootNode.findAccessibilityNodeInfosByText(text)
        for (node in nodes) {
            if (node.isClickable) {
                node.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                Toast.makeText(this, "JARVIS: Clicking $text", Toast.LENGTH_SHORT).show()
                return true
            }
        }
        return false
    }

    override fun onInterrupt() {}
}
