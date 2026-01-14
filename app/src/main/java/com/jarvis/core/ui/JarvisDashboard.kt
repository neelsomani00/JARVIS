package com.jarvis.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.draw.clip

@Composable
fun JarvisDashboard(transcript: String, response: String) {
    var sensitivity by remember { mutableFloatStateOf(0.5f) }
    
    Column(
        modifier = Modifier
            .width(320.dp)
            .padding(bottom = 12.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(Color.Black.copy(alpha = 0.9f))
            .border(1.dp, Color(0xFF00E5FF).copy(alpha = 0.5f), RoundedCornerShape(24.dp))
            .padding(20.dp)
    ) {
        Text("JARVIS COMMAND CENTER", color = Color(0xFF00E5FF), fontSize = 12.sp, style = MaterialTheme.typography.labelSmall)
        Divider(modifier = Modifier.padding(vertical = 8.dp), color = Color(0xFF00E5FF).copy(alpha = 0.3f))
        
        // Chat History Area
        Column(modifier = Modifier.height(100.dp)) {
            Text("YOU: $transcript", color = Color.White.copy(alpha = 0.7f), fontSize = 14.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text("JARVIS: $response", color = Color(0xFF00E5FF), fontSize = 16.sp)
        }
        
        Divider(modifier = Modifier.padding(vertical = 8.dp), color = Color(0xFF00E5FF).copy(alpha = 0.3f))
        
        // Settings Toggles
        Text("VOICE SENSITIVITY", color = Color.White, fontSize = 10.sp)
        Slider(
            value = sensitivity,
            onValueChange = { sensitivity = it },
            colors = SliderDefaults.colors(thumbColor = Color(0xFF00E5FF), activeTrackColor = Color(0xFF00E5FF))
        )
        
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("WAKE WORD", color = Color.White, fontSize = 12.sp)
            Switch(checked = true, onCheckedChange = {}, colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF00E5FF)))
        }
    }
}
