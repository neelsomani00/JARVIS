package com.jarvis.core.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

@Composable
fun JarvisFloatingCore(onMove: (Int, Int) -> Unit, onClick: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "breathing")
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.8f, targetValue = 1.2f,
        animationSpec = infiniteRepeatable(tween(1500), RepeatMode.Reverse), label = "pulse"
    )

    Box(
        modifier = Modifier
            .size(70.dp)
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    onMove(dragAmount.x.roundToInt(), dragAmount.y.roundToInt())
                }
            }
            .clickable { onClick() }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val centerX = size.width / 2f
            val centerY = size.height / 2f
            val center = Offset(centerX, centerY)
            
            // Outer Glow
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFF00E5FF).copy(alpha = 0.6f), Color.Transparent),
                    center = center, radius = 35.dp.toPx() * pulse
                ),
                radius = 35.dp.toPx() * pulse
            )

            // Inner Ring
            drawCircle(
                color = Color(0xFF00E5FF),
                radius = 22.dp.toPx(),
                style = Stroke(width = 3.dp.toPx())
            )

            // The "V" Logo
            val vPath = Path().apply {
                moveTo(centerX - 10.dp.toPx(), centerY - 8.dp.toPx())
                lineTo(centerX, centerY + 10.dp.toPx())
                lineTo(centerX + 10.dp.toPx(), centerY - 8.dp.toPx())
            }
            drawPath(vPath, color = Color.White, style = Stroke(width = 4.dp.toPx()))
        }
    }
}
