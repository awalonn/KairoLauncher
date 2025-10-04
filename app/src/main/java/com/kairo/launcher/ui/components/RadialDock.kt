package com.kairo.launcher.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import kotlin.math.atan2
import kotlin.math.PI

@Composable
fun RadialDock(onSlot: (Int) -> Unit) {
    var center by remember { mutableStateOf(Offset.Zero) }
    Canvas(Modifier
        .fillMaxWidth()
        .height(120.dp)
        .pointerInput(Unit) {
            detectTapGestures { pos ->
                if (center == Offset.Zero) return@detectTapGestures
                val angle = ((atan2(center.y - pos.y, pos.x - center.x) * 180 / PI) + 360) % 360
                val quadrant = when {
                    angle in 315.0..360.0 || angle in 0.0..45.0 -> 0
                    angle in 45.0..135.0 -> 1
                    angle in 135.0..225.0 -> 2
                    else -> 3
                }
                onSlot(quadrant)
            }
        }
    ) {
        center = Offset(size.width/2f, size.height)
        drawArc(
            color = androidx.compose.ui.graphics.Color(0x337C5CFF),
            startAngle = 200f,
            sweepAngle = 140f,
            useCenter = false,
            topLeft = Offset(0f, size.height/3f),
            size = size
        )
    }
}