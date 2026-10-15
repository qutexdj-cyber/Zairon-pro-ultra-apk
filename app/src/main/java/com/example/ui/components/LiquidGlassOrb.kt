package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.LocalAccentTheme
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

enum class OrbState {
    IDLE,
    LISTENING,
    THINKING,
    SPEAKING,
    ERROR
}

@Composable
fun LiquidGlassOrb(
    state: OrbState = OrbState.IDLE,
    audioLevel: Float = 0f, // 0f to 1f
    modifier: Modifier = Modifier,
    size: Dp = 220.dp
) {
    val accent = LocalAccentTheme.current

    val primaryColor = when (state) {
        OrbState.ERROR -> Color(0xFFFF3B30)
        OrbState.LISTENING -> Color(0xFF00E5FF)
        OrbState.THINKING -> Color(0xFF00FF9D)
        OrbState.SPEAKING -> accent.primary
        OrbState.IDLE -> accent.primary
    }

    val secondaryColor = when (state) {
        OrbState.ERROR -> Color(0xFFFF9500)
        OrbState.LISTENING -> Color(0xFF00FF9D)
        OrbState.THINKING -> Color(0xFF00E5FF)
        OrbState.SPEAKING -> accent.secondary
        OrbState.IDLE -> accent.secondary
    }

    val infiniteTransition = rememberInfiniteTransition(label = "OrbTransition")

    // Rotation angle
    val rotationDuration = when (state) {
        OrbState.THINKING -> 2000
        OrbState.LISTENING -> 3500
        OrbState.SPEAKING -> 4000
        else -> 7000
    }
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(rotationDuration, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "OrbRotation"
    )

    // Breathing pulse scale
    val pulseDuration = when (state) {
        OrbState.THINKING -> 800
        OrbState.LISTENING -> 1200
        OrbState.SPEAKING -> 1400
        else -> 2600
    }
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.94f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(pulseDuration, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "OrbPulse"
    )

    // Inner fluid turbulence phase
    val fluidPhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(if (state == OrbState.THINKING) 1500 else 4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "FluidPhase"
    )

    // Floating vertical oscillation
    val floatOffset by infiniteTransition.animateFloat(
        initialValue = -6f,
        targetValue = 6f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "FloatOffset"
    )

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(this.size.width / 2f, this.size.height / 2f + floatOffset)
            val baseRadius = (this.size.minDimension / 2f) * 0.72f
            val reactiveScale = if (state == OrbState.LISTENING || state == OrbState.SPEAKING) {
                1f + (audioLevel * 0.15f)
            } else {
                1f
            }
            val radius = baseRadius * pulse * reactiveScale

            // 1. Ambient Bloom Glow
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        primaryColor.copy(alpha = if (state == OrbState.THINKING) 0.45f else 0.32f),
                        secondaryColor.copy(alpha = 0.12f),
                        Color.Transparent
                    ),
                    center = center,
                    radius = radius * 1.55f
                ),
                radius = radius * 1.55f,
                center = center
            )

            // 2. Quantum Orbiting Rings
            drawOrbitalRings(
                center = center,
                radius = radius * 1.22f,
                rotation = rotation,
                color = primaryColor,
                secondaryColor = secondaryColor,
                state = state
            )

            // 3. Liquid-Glass Orb Body
            drawLiquidGlassSphere(
                center = center,
                radius = radius,
                primaryColor = primaryColor,
                secondaryColor = secondaryColor,
                fluidPhase = fluidPhase,
                state = state
            )

            // 4. Glossy Glass Specular Highlights (Realistic 3D Liquid Reflection)
            drawGlassHighlights(
                center = center,
                radius = radius
            )
        }
    }
}

private fun DrawScope.drawOrbitalRings(
    center: Offset,
    radius: Float,
    rotation: Float,
    color: Color,
    secondaryColor: Color,
    state: OrbState
) {
    // Outer dashed arc ring
    val strokeWidth = 1.6f
    drawArc(
        color = color.copy(alpha = 0.45f),
        startAngle = rotation,
        sweepAngle = 140f,
        useCenter = false,
        topLeft = Offset(center.x - radius, center.y - radius),
        size = Size(radius * 2, radius * 2),
        style = Stroke(
            width = strokeWidth,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 8f), 0f),
            cap = StrokeCap.Round
        )
    )

    drawArc(
        color = secondaryColor.copy(alpha = 0.5f),
        startAngle = rotation + 180f,
        sweepAngle = 120f,
        useCenter = false,
        topLeft = Offset(center.x - radius, center.y - radius),
        size = Size(radius * 2, radius * 2),
        style = Stroke(
            width = strokeWidth,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(16f, 10f), 0f),
            cap = StrokeCap.Round
        )
    )

    // Orbiting energy satellite beacons
    val rad1 = (rotation * PI / 180).toFloat()
    val orbX1 = center.x + radius * cos(rad1)
    val orbY1 = center.y + radius * sin(rad1)
    drawCircle(color = color, radius = 3.5f, center = Offset(orbX1, orbY1))
    drawCircle(color = Color.White, radius = 1.8f, center = Offset(orbX1, orbY1))

    val rad2 = ((rotation + 180f) * PI / 180).toFloat()
    val orbX2 = center.x + radius * cos(rad2)
    val orbY2 = center.y + radius * sin(rad2)
    drawCircle(color = secondaryColor, radius = 3.2f, center = Offset(orbX2, orbY2))

    // Inner energetic sonic ripples when listening or speaking
    if (state == OrbState.LISTENING || state == OrbState.SPEAKING) {
        drawCircle(
            color = color.copy(alpha = 0.25f),
            radius = radius * 1.35f,
            center = center,
            style = Stroke(width = 1.2f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f)))
        )
    }
}

private fun DrawScope.drawLiquidGlassSphere(
    center: Offset,
    radius: Float,
    primaryColor: Color,
    secondaryColor: Color,
    fluidPhase: Float,
    state: OrbState
) {
    // A. Dark sphere base (deep translucent obsidian-emerald)
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                secondaryColor.copy(alpha = 0.4f),
                primaryColor.copy(alpha = 0.22f),
                Color(0xFF030A06).copy(alpha = 0.92f)
            ),
            center = Offset(center.x - radius * 0.25f, center.y - radius * 0.25f),
            radius = radius
        ),
        radius = radius,
        center = center
    )

    // B. Inner Fluid Waves (Liquid Turbulence)
    val wavePath = Path()
    val waveYBase = center.y + radius * 0.12f
    val waveAmplitude = radius * (if (state == OrbState.THINKING) 0.22f else 0.14f)

    wavePath.moveTo(center.x - radius, waveYBase)
    val steps = 30
    for (i in 0..steps) {
        val x = center.x - radius + (2 * radius * i / steps)
        val angle = fluidPhase + (i * 0.35f)
        val y = waveYBase + sin(angle) * waveAmplitude
        wavePath.lineTo(x, y)
    }
    wavePath.lineTo(center.x + radius, center.y + radius)
    wavePath.lineTo(center.x - radius, center.y + radius)
    wavePath.close()

    drawPath(
        path = wavePath,
        brush = Brush.verticalGradient(
            colors = listOf(
                primaryColor.copy(alpha = 0.55f),
                secondaryColor.copy(alpha = 0.25f),
                Color.Transparent
            ),
            startY = center.y - radius * 0.2f,
            endY = center.y + radius
        )
    )

    // C. Central glowing Quantum Core
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                Color.White.copy(alpha = 0.95f),
                primaryColor.copy(alpha = 0.85f),
                secondaryColor.copy(alpha = 0.3f),
                Color.Transparent
            ),
            center = center,
            radius = radius * 0.48f
        ),
        radius = radius * 0.48f,
        center = center
    )

    // D. Outer translucent glass rim outline
    drawCircle(
        brush = Brush.sweepGradient(
            colors = listOf(
                Color.White.copy(alpha = 0.75f),
                primaryColor.copy(alpha = 0.6f),
                secondaryColor.copy(alpha = 0.2f),
                Color.White.copy(alpha = 0.75f)
            ),
            center = center
        ),
        radius = radius,
        center = center,
        style = Stroke(width = 1.8f)
    )
}

private fun DrawScope.drawGlassHighlights(
    center: Offset,
    radius: Float
) {
    // Upper specular curved glass glare
    val specularCenter = Offset(center.x - radius * 0.32f, center.y - radius * 0.4f)
    val specularWidth = radius * 0.65f
    val specularHeight = radius * 0.35f

    drawOval(
        brush = Brush.radialGradient(
            colors = listOf(
                Color.White.copy(alpha = 0.82f),
                Color.White.copy(alpha = 0.25f),
                Color.Transparent
            ),
            center = specularCenter,
            radius = specularWidth * 0.8f
        ),
        topLeft = Offset(specularCenter.x - specularWidth / 2f, specularCenter.y - specularHeight / 2f),
        size = Size(specularWidth, specularHeight)
    )

    // Lower subtle rim reflection
    val bottomHighlight = Offset(center.x + radius * 0.25f, center.y + radius * 0.45f)
    drawOval(
        brush = Brush.radialGradient(
            colors = listOf(
                Color.White.copy(alpha = 0.35f),
                Color.Transparent
            ),
            center = bottomHighlight,
            radius = radius * 0.4f
        ),
        topLeft = Offset(bottomHighlight.x - radius * 0.3f, bottomHighlight.y - radius * 0.15f),
        size = Size(radius * 0.6f, radius * 0.3f)
    )
}
