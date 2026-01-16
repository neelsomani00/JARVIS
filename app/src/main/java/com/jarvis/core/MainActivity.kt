package com.jarvis.core

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var isCalibrated by remember { mutableStateOf(false) }
            
            Surface(modifier = Modifier.fillMaxSize(), color = Color.Black) {
                if (!isCalibrated) {
                    CalibrationForm { isCalibrated = true }
                } else {
                    DashboardHome()
                }
            }
        }
    }
}

@Composable
fun CalibrationForm(onComplete: () -> Unit) {
    var name by remember { mutableStateOf("") }
    Column(
        modifier = Modifier.fillMaxSize().padding(30.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("STARK INDUSTRIES", color = Color(0xFF00E5FF), fontSize = 12.sp, letterSpacing = 2.sp)
        Spacer(modifier = Modifier.height(20.dp))
        Text("CALIBRATION PROTOCOL", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(40.dp))
        
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Identify yourself, Sir", color = Color.Gray) },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF00E5FF),
                unfocusedBorderColor = Color.Gray,
                cursorColor = Color(0xFF00E5FF)
            )
        )
        
        Spacer(modifier = Modifier.height(30.dp))
        Button(
            onClick = onComplete,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E5FF)),
            shape = MaterialTheme.shapes.medium
        ) {
            Text("INITIALIZE JARVIS", color = Color.Black, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun DashboardHome() {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text("JARVIS HUB", color = Color(0xFF00E5FF), fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
        Spacer(modifier = Modifier.height(10.dp))
        Divider(color = Color(0xFF00E5FF).copy(alpha = 0.3f))
        Spacer(modifier = Modifier.height(20.dp))
        
        Text("SYSTEM STATUS: ONLINE", color = Color.Green, fontSize = 12.sp)
        Spacer(modifier = Modifier.height(16.dp))
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF121212))
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("NEURAL BRAIN: GEMINI 1.5 FLASH", color = Color.White, fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.height(4.dp))
                Text("MODE: ACTION-MODEL-AS-A-SERVICE", color = Color.Gray, fontSize = 11.sp)
            }
        }
    }
}
