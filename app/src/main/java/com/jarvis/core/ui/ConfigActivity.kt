package com.jarvis.core.ui

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

class ConfigActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Surface(modifier = Modifier.fillMaxSize(), color = Color(0xFF001219)) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("J.A.R.V.I.S CONFIG", style = MaterialTheme.typography.headlineLarge, color = Color(0xFF00E5FF))
                    Spacer(modifier = Modifier.height(30.dp))
                    
                    Button(onClick = {
                        val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
                        intent.data = Uri.parse("package:$packageName")
                        startActivity(intent)
                    }) {
                        Text("Grant Overlay Permission")
                    }
                    
                    Spacer(modifier = Modifier.height(10.dp))
                    
                    Button(onClick = {
                        startService(Intent(this@ConfigActivity, com.jarvis.core.services.OverlayService::class.java))
                    }) {
                        Text("Initiate J.A.R.V.I.S HUD")
                    }
                }
            }
        }
    }
}