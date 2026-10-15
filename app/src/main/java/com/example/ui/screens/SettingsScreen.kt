package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.ZaironViewModel
import com.example.ui.theme.AccentTheme
import com.example.ui.theme.BorderGlass
import com.example.ui.theme.BorderGlassDim
import com.example.ui.theme.LocalAccentTheme
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceElevated
import com.example.ui.theme.SurfaceGlass
import com.example.ui.theme.TextBright
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VoidBlack

@Composable
fun SettingsScreen(
    viewModel: ZaironViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToMemory: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val accent = LocalAccentTheme.current

    val currentTheme by viewModel.accentTheme.collectAsState()
    val selectedModel by viewModel.selectedModel.collectAsState()
    val conversations by viewModel.conversations.collectAsState()
    val memories by viewModel.memories.collectAsState()

    var pitchSlider by remember { mutableFloatStateOf(1.0f) }
    var speedSlider by remember { mutableFloatStateOf(1.05f) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(VoidBlack)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top App Bar
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, BorderGlassDim),
                color = SurfaceDark
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag("settings_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextBright
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "SETTINGS & SYSTEM CONFIGURATION",
                        color = TextBright,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                }
            }

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Section 1: Accent Color Themes
                item {
                    SettingsCard(title = "ACCENT THEME PALETTE", icon = Icons.Default.ColorLens) {
                        Text(
                            text = "Select your preferred futuristic HUD holographic matrix accent:",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            AccentTheme.entries.forEach { theme ->
                                val isSelected = currentTheme == theme
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.clickable {
                                        viewModel.setAccentTheme(theme)
                                        Toast.makeText(context, "${theme.title} Activated", Toast.LENGTH_SHORT).show()
                                    }
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(44.dp)
                                            .clip(CircleShape)
                                            .background(
                                                Brush.sweepGradient(
                                                    listOf(theme.primary, theme.secondary, theme.primary)
                                                )
                                            )
                                            .border(
                                                if (isSelected) 2.5.dp else 1.dp,
                                                if (isSelected) Color.White else BorderGlassDim,
                                                CircleShape
                                            )
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = theme.title.split(" ").first(),
                                        color = if (isSelected) theme.primary else TextMuted,
                                        fontSize = 10.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            }
                        }
                    }
                }

                // Section 2: Speech & Vocal Harmonics
                item {
                    SettingsCard(title = "VOCAL SYNTHESIS CONTROLS", icon = Icons.Default.RecordVoiceOver) {
                        Text(
                            text = "Tune the JARVIS pitch and speech cadence for auditory playback:",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Pitch Slider
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Vocal Pitch", color = TextBright, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            Text(String.format("%.2fx", pitchSlider), color = accent.primary, fontSize = 12.sp)
                        }
                        Slider(
                            value = pitchSlider,
                            onValueChange = {
                                pitchSlider = it
                                viewModel.setPitch(it)
                            },
                            valueRange = 0.6f..1.6f,
                            colors = SliderDefaults.colors(
                                thumbColor = accent.primary,
                                activeTrackColor = accent.primary,
                                inactiveTrackColor = SurfaceElevated
                            )
                        )

                        // Rate Slider
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Cadence Speed", color = TextBright, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            Text(String.format("%.2fx", speedSlider), color = accent.secondary, fontSize = 12.sp)
                        }
                        Slider(
                            value = speedSlider,
                            onValueChange = {
                                speedSlider = it
                                viewModel.setSpeechRate(it)
                            },
                            valueRange = 0.7f..1.7f,
                            colors = SliderDefaults.colors(
                                thumbColor = accent.secondary,
                                activeTrackColor = accent.secondary,
                                inactiveTrackColor = SurfaceElevated
                            )
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = {
                                viewModel.voiceManager.speak("Good day. Vocal synthesizer operational at your preferred cadence.")
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = SurfaceElevated),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("test_speech_button")
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null, tint = accent.primary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("TEST VOCAL OUTPUT", color = accent.primary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Section 3: Model & AI Engine
                item {
                    SettingsCard(title = "NEURAL MODEL SELECTION", icon = Icons.Default.AutoAwesome) {
                        Text(
                            text = "Choose the Gemini model architecture powering Zairon's reasoning:",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        listOf(
                            "gemini-3.5-flash" to "Gemini 3.5 Flash (Ultra-fast, optimized for mobile Q&A & coding)",
                            "gemini-3.1-pro-preview" to "Gemini 3.1 Pro Preview (Heavy STEM reasoning, math & architecture)"
                        ).forEach { (modelKey, label) ->
                            val isSelected = selectedModel == modelKey
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .border(
                                        1.dp,
                                        if (isSelected) accent.primary else BorderGlassDim,
                                        RoundedCornerShape(10.dp)
                                    )
                                    .clickable { viewModel.setSelectedModel(modelKey) },
                                color = if (isSelected) SurfaceElevated else SurfaceDark
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(16.dp)
                                            .clip(CircleShape)
                                            .background(if (isSelected) accent.primary else SurfaceDark)
                                            .border(1.dp, accent.primary, CircleShape)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = label,
                                        color = if (isSelected) TextBright else TextSecondary,
                                        fontSize = 12.sp,
                                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                                    )
                                }
                            }
                        }
                    }
                }

                // Section 4: Diagnostics & Storage
                item {
                    SettingsCard(title = "SYSTEM DIAGNOSTICS & TELEMETRY", icon = Icons.Default.Storage) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Stored Sessions", color = TextMuted, fontSize = 11.sp)
                                Text("${conversations.size} Dialogues", color = TextBright, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            }
                            Column {
                                Text("Memory Nodes", color = TextMuted, fontSize = 11.sp)
                                Text("${memories.size} Entries", color = accent.primary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            }
                            Column {
                                Text("Storage Engine", color = TextMuted, fontSize = 11.sp)
                                Text("Room (SQLite)", color = accent.secondary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = onNavigateToMemory,
                            colors = ButtonDefaults.buttonColors(containerColor = SurfaceElevated),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Psychology, contentDescription = null, tint = accent.primary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("MANAGE MEMORY NODES", color = accent.primary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Section 5: Creator & About Zairon AI 2.0
                item {
                    SettingsCard(title = "ABOUT ZAIRON AI 2.0", icon = Icons.Default.Info) {
                        Text(
                            text = "ZAIRON AI 2.0 — Quantum Neural Assistant",
                            color = TextBright,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Engineered and created by Azhan.",
                            color = accent.primary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Inspired by sophisticated futuristic assistant systems, Zairon combines on-device Room persistence, Gemini multi-turn reasoning, encyclopedic live research, and high-fidelity vocal interactions.",
                            color = TextSecondary,
                            fontSize = 12.sp,
                            lineHeight = 17.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SettingsCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    content: @Composable () -> Unit
) {
    val accent = LocalAccentTheme.current

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, BorderGlassDim, RoundedCornerShape(16.dp)),
        color = SurfaceDark
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = icon, contentDescription = null, tint = accent.primary, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    color = accent.primary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            content()
        }
    }
}
