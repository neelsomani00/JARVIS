package com.jarvis.core

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.jarvis.core.services.WakeWordService

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Surface(modifier = Modifier.fillMaxSize(), color = Color(0xFF121212)) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("JARVIS Terminal", style = MaterialTheme.typography.headlineMedium, color = Color.Cyan)
                    Spacer(modifier = Modifier.height(20.dp))
                    
                    Button(
                        onClick = { checkPermissions() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Setup Permissions")
                    }
                    
                    Spacer(modifier = Modifier.height(10.dp))
                    
                    Button(
                        onClick = { 
                            startService(Intent(this@MainActivity, WakeWordService::class.java))
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF004D40))
                    ) {
                        Text("Initialize Jarvis")
                    }
                }
            }
        }
    }

    private fun checkPermissions() {
        if (!Settings.canDrawOverlays(this)) {
            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
            startActivity(intent)
        }
        // Redirect to Accessibility Settings
        val accIntent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
        startActivity(accIntent)
    }
}
