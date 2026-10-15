package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.ui.ZaironViewModel
import com.example.ui.components.AudioWaveBarVisualizer
import com.example.ui.components.LiquidGlassOrb
import com.example.ui.components.OrbState
import com.example.ui.components.TechBadge
import com.example.ui.theme.BorderGlass
import com.example.ui.theme.BorderGlassDim
import com.example.ui.theme.LocalAccentTheme
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceGlass
import com.example.ui.theme.TextBright
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VoidBlack

@Composable
fun VoiceAssistantScreen(
    viewModel: ZaironViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToChat: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val accent = LocalAccentTheme.current

    val orbState by viewModel.orbState.collectAsState()
    val isListening by viewModel.isListening.collectAsState()
    val isSpeaking by viewModel.isSpeaking.collectAsState()
    val liveTranscription by viewModel.liveTranscription.collectAsState()
    val audioRms by viewModel.audioRms.collectAsState()
    val isGenerating by viewModel.isGenerating.collectAsState()
    val currentMessages by viewModel.currentMessages.collectAsState()

    val lastZaironMsg = currentMessages.lastOrNull { it.sender == "zairon" }

    // Audio Permission Launcher
    val audioPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.startVoiceListening { errMsg ->
                Toast.makeText(context, errMsg, Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(
                context,
                "Microphone permission is required for voice conversation.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    fun toggleListening() {
        if (isListening) {
            viewModel.stopVoiceListening()
        } else {
            if (isSpeaking) {
                viewModel.stopSpeaking()
            }
            val hasPerm = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED

            if (hasPerm) {
                viewModel.startVoiceListening { errMsg ->
                    Toast.makeText(context, errMsg, Toast.LENGTH_SHORT).show()
                }
            } else {
                audioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(VoidBlack)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        viewModel.stopVoiceListening()
                        viewModel.stopSpeaking()
                        onNavigateBack()
                    },
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(SurfaceDark)
                        .border(1.dp, BorderGlassDim, RoundedCornerShape(12.dp))
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = TextBright
                    )
                }

                TechBadge(
                    text = when {
                        isListening -> "VOICE STREAM ACTIVE"
                        isGenerating -> "NEURAL SYNTHESIS"
                        isSpeaking -> "VOCAL HARMONICS"
                        else -> "JARVIS VOCAL LINK"
                    },
                    statusColor = when {
                        isListening -> Color(0xFF00E5FF)
                        isGenerating -> Color(0xFF00FF9D)
                        isSpeaking -> accent.primary
                        else -> TextMuted
                    }
                )

                IconButton(
                    onClick = {
                        viewModel.stopVoiceListening()
                        viewModel.stopSpeaking()
                        onNavigateToChat()
                    },
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(SurfaceDark)
                        .border(1.dp, BorderGlassDim, RoundedCornerShape(12.dp))
                ) {
                    Icon(
                        imageVector = Icons.Default.Keyboard,
                        contentDescription = "Switch to keyboard chat",
                        tint = accent.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // State title & Wave Visualizer
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = when {
                        isListening -> "LISTENING..."
                        isGenerating -> "PROCESSING INTENT..."
                        isSpeaking -> "SPEAKING..."
                        orbState == OrbState.ERROR -> "SYSTEM ERROR"
                        else -> "TAP MICROPHONE TO SPEAK"
                    },
                    color = when {
                        isListening -> Color(0xFF00E5FF)
                        isGenerating -> accent.primary
                        isSpeaking -> accent.primary
                        orbState == OrbState.ERROR -> Color(0xFFFF5252)
                        else -> TextSecondary
                    },
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                AudioWaveBarVisualizer(
                    isSpeakingOrListening = isListening || isSpeaking || isGenerating,
                    barCount = 18
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // The Giant Liquid-Glass Orb
            Box(
                modifier = Modifier
                    .size(240.dp)
                    .clickable { toggleListening() },
                contentAlignment = Alignment.Center
            ) {
                LiquidGlassOrb(
                    state = orbState,
                    audioLevel = audioRms,
                    size = 240.dp
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Transcription & Spoken Response Cards
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Live user transcription
                AnimatedVisibility(
                    visible = liveTranscription.isNotBlank() || isListening,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .border(1.dp, Color(0xFF00E5FF).copy(alpha = 0.35f), RoundedCornerShape(16.dp)),
                        color = SurfaceDark
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "YOU SAID:",
                                color = Color(0xFF00E5FF),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (liveTranscription.isNotBlank()) "\"$liveTranscription\"" else "Listening for speech input...",
                                color = TextBright,
                                fontSize = 14.sp,
                                fontStyle = if (liveTranscription.isBlank()) androidx.compose.ui.text.font.FontStyle.Italic else androidx.compose.ui.text.font.FontStyle.Normal
                            )
                        }
                    }
                }

                // Last Zairon response
                if (lastZaironMsg != null && !isListening) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .border(1.dp, BorderGlassDim, RoundedCornerShape(16.dp)),
                        color = SurfaceGlass
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "ZAIRON AI 2.0 RESPONSE:",
                                    color = accent.primary,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )

                                if (isSpeaking) {
                                    Row(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(accent.primary.copy(alpha = 0.15f))
                                            .clickable { viewModel.stopSpeaking() }
                                            .padding(horizontal = 6.dp, vertical = 2.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Stop,
                                            contentDescription = "Stop speaking",
                                            tint = accent.primary,
                                            modifier = Modifier.size(12.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "MUTE",
                                            color = accent.primary,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                } else {
                                    Row(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(SurfaceDark)
                                            .clickable { viewModel.speakMessage(lastZaironMsg.id, lastZaironMsg.content) }
                                            .padding(horizontal = 6.dp, vertical = 2.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.VolumeUp,
                                            contentDescription = "Replay audio",
                                            tint = TextSecondary,
                                            modifier = Modifier.size(12.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "REPLAY",
                                            color = TextSecondary,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = lastZaironMsg.content.take(280) + if (lastZaironMsg.content.length > 280) "..." else "",
                                color = TextBright,
                                fontSize = 13.5.sp,
                                lineHeight = 19.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Bottom Voice Controls
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 36.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Cancel / Stop button
                IconButton(
                    onClick = {
                        viewModel.stopVoiceListening()
                        viewModel.stopSpeaking()
                    },
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(SurfaceDark)
                        .border(1.dp, BorderGlassDim, CircleShape)
                        .testTag("voice_cancel_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Cancel voice session",
                        tint = TextSecondary,
                        modifier = Modifier.size(22.dp)
                    )
                }

                // Giant Glowing Mic Button
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    if (isListening) Color(0xFF00E5FF) else accent.primary,
                                    if (isListening) Color(0xFF00B4D8) else accent.secondary
                                )
                            )
                        )
                        .border(
                            2.dp,
                            Color.White.copy(alpha = 0.8f),
                            CircleShape
                        )
                        .clickable { toggleListening() }
                        .testTag("voice_mic_main_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isListening) Icons.Default.MicOff else Icons.Default.Mic,
                        contentDescription = if (isListening) "Stop listening" else "Start listening",
                        tint = VoidBlack,
                        modifier = Modifier.size(36.dp)
                    )
                }

                // Switch to Keyboard Chat
                IconButton(
                    onClick = {
                        viewModel.stopVoiceListening()
                        viewModel.stopSpeaking()
                        onNavigateToChat()
                    },
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(SurfaceDark)
                        .border(1.dp, BorderGlassDim, CircleShape)
                        .testTag("voice_keyboard_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Keyboard,
                        contentDescription = "Text chat mode",
                        tint = accent.primary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}
