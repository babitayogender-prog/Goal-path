package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.PrimaryPurple
import com.example.ui.theme.PrimaryPurpleGlow
import com.example.ui.theme.SecondaryViolet

@Composable
fun ProgressRing(
    progressPercentage: Int,
    modifier: Modifier = Modifier,
    size: Dp = 110.dp,
    strokeWidth: Dp = 10.dp,
    label: String = "Total Progress"
) {
    val animatedProgress by animateFloatAsState(
        targetValue = (progressPercentage.coerceIn(0, 100) / 100f),
        animationSpec = tween(durationMillis = 1000),
        label = "ring_progress"
    )

    val trackColor = MaterialTheme.colorScheme.surfaceVariant
    val textColor = MaterialTheme.colorScheme.onSurface
    val labelColor = MaterialTheme.colorScheme.onSurfaceVariant

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(size)
            .testTag("progress_ring_container")
    ) {
        // Subtle outer glow effect
        Box(
            modifier = Modifier
                .size(size - 8.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(PrimaryPurple.copy(alpha = 0.15f), Color.Transparent)
                    ),
                    shape = CircleShape
                )
                .blur(16.dp)
        )

        // Canvas for arc drawing
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokePx = strokeWidth.toPx()
            val diameter = size.toPx() - strokePx
            val topLeft = strokePx / 2f

            // Track background arc
            drawArc(
                color = trackColor,
                startAngle = 140f,
                sweepAngle = 260f,
                useCenter = false,
                style = Stroke(width = strokePx, cap = StrokeCap.Round),
                topLeft = androidx.compose.ui.geometry.Offset(topLeft, topLeft),
                size = androidx.compose.ui.geometry.Size(diameter, diameter)
            )

            // Progress active arc
            if (animatedProgress > 0f) {
                drawArc(
                    brush = Brush.linearGradient(
                        colors = listOf(SecondaryViolet, PrimaryPurple, PrimaryPurpleGlow)
                    ),
                    startAngle = 140f,
                    sweepAngle = 260f * animatedProgress,
                    useCenter = false,
                    style = Stroke(width = strokePx, cap = StrokeCap.Round),
                    topLeft = androidx.compose.ui.geometry.Offset(topLeft, topLeft),
                    size = androidx.compose.ui.geometry.Size(diameter, diameter)
                )
            }
        }

        // Percentage Text Display
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "$progressPercentage%",
                color = textColor,
                fontSize = (size.value * 0.22f).sp,
                fontWeight = FontWeight.ExtraBold
            )
            if (label.isNotEmpty()) {
                Text(
                    text = label,
                    color = labelColor,
                    fontSize = (size.value * 0.09f).sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
