package com.example.ui.screens

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.ShortText
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
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
import com.example.ui.components.GlassCard
import com.example.ui.components.LiquidGlassOrb
import com.example.ui.components.QuickActionCard
import com.example.ui.components.TechBadge
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
import java.util.Calendar

@Composable
fun HomeScreen(
    viewModel: ZaironViewModel,
    onOpenDrawer: () -> Unit,
    onNavigateToChat: () -> Unit,
    onNavigateToVoice: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val accent = LocalAccentTheme.current

    val conversations by viewModel.conversations.collectAsState()
    val orbState by viewModel.orbState.collectAsState()
    val webSearchEnabled by viewModel.webSearchEnabled.collectAsState()

    var inputText by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    // Android Zero-permission photo picker per Google Play policy
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        selectedImageUri = uri
    }

    val greetingText = remember {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        when (hour) {
            in 5..11 -> "Good morning, Sir."
            in 12..16 -> "Good afternoon, Sir."
            in 17..22 -> "Good evening, Sir."
            else -> "Standing by, Sir."
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(VoidBlack)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 100.dp, top = 8.dp)
        ) {
            // Header Top Bar
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onOpenDrawer,
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(SurfaceDark)
                            .border(1.dp, BorderGlassDim, RoundedCornerShape(12.dp))
                            .testTag("menu_drawer_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Open navigation menu",
                            tint = accent.primary
                        )
                    }

                    TechBadge(
                        text = "CORE V2.0 • ALL SYSTEMS ONLINE",
                        statusColor = accent.primary
                    )

                    IconButton(
                        onClick = {
                            viewModel.createNewConversation("New Objective")
                            onNavigateToChat()
                        },
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(SurfaceDark)
                            .border(1.dp, BorderGlassDim, RoundedCornerShape(12.dp))
                            .testTag("new_chat_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "New Conversation",
                            tint = accent.primary
                        )
                    }
                }
            }

            // Hero Liquid-Glass Orb & Greeting Banner
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(170.dp)
                            .clickable { onNavigateToVoice() },
                        contentAlignment = Alignment.Center
                    ) {
                        LiquidGlassOrb(
                            state = orbState,
                            size = 170.dp
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = greetingText,
                        color = TextBright,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = (-0.5).sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Zairon AI is primed and ready. What shall we accomplish today?",
                        color = TextSecondary,
                        fontSize = 13.5.sp,
                        lineHeight = 18.sp,
                        modifier = Modifier.padding(horizontal = 16.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }

            // Quick Actions Section
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "COMMAND MODULES",
                            color = accent.primary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "ENGINEERED BY AZHAN",
                            color = TextMuted,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        QuickActionCard(
                            title = "Ask AI",
                            subtitle = "General reasoning & queries",
                            icon = Icons.Default.AutoAwesome,
                            tag = "JARVIS",
                            modifier = Modifier.weight(1f),
                            onClick = {
                                viewModel.sendMessage("Hello Zairon, give me an overview of your capabilities.")
                                onNavigateToChat()
                            }
                        )
                        QuickActionCard(
                            title = "Solve Maths",
                            subtitle = "Step-by-step logic",
                            icon = Icons.Default.Calculate,
                            tag = "CALCULUS",
                            modifier = Modifier.weight(1f),
                            onClick = {
                                viewModel.sendMessage("Solve the quadratic formula and explain the discriminant.")
                                onNavigateToChat()
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        QuickActionCard(
                            title = "Coding Help",
                            subtitle = "Kotlin, Python & architecture",
                            icon = Icons.Default.Code,
                            tag = "SYNTAX",
                            modifier = Modifier.weight(1f),
                            onClick = {
                                viewModel.sendMessage("Write an idiomatic Kotlin StateFlow debounce search pattern.")
                                onNavigateToChat()
                            }
                        )
                        QuickActionCard(
                            title = "Explain a Topic",
                            subtitle = "Quantum physics & science",
                            icon = Icons.Default.Psychology,
                            tag = "DEEP DIVE",
                            modifier = Modifier.weight(1f),
                            onClick = {
                                viewModel.sendMessage("Explain quantum entanglement and Einstein's spooky action at a distance.")
                                onNavigateToChat()
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        QuickActionCard(
                            title = "Summarize",
                            subtitle = "Distill text & reports",
                            icon = Icons.Default.ShortText,
                            tag = "SUMMARY",
                            modifier = Modifier.weight(1f),
                            onClick = {
                                viewModel.sendMessage("Provide a concise executive summary template for technical proposals.")
                                onNavigateToChat()
                            }
                        )
                        QuickActionCard(
                            title = "Analyze Image",
                            subtitle = "Multimodal vision inspect",
                            icon = Icons.Default.Image,
                            tag = "VISION",
                            modifier = Modifier.weight(1f),
                            onClick = {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            }
                        )
                    }
                }
            }

            // Recent Conversations Carousel
            if (conversations.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                        Text(
                            text = "RECENT CONVERSATIONS",
                            color = accent.primary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(conversations.take(6)) { conv ->
                            Surface(
                                modifier = Modifier
                                    .width(180.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .border(1.dp, BorderGlassDim, RoundedCornerShape(12.dp))
                                    .clickable {
                                        viewModel.selectConversation(conv.id)
                                        onNavigateToChat()
                                    },
                                color = SurfaceDark
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        text = conv.title,
                                        color = TextBright,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        maxLines = 1
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Tap to reopen session",
                                        color = TextMuted,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Bottom Integrated Message Composer
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(12.dp)
                .clip(RoundedCornerShape(22.dp))
                .border(1.dp, BorderGlass, RoundedCornerShape(22.dp)),
            color = SurfaceDark.copy(alpha = 0.96f),
            tonalElevation = 8.dp
        ) {
            Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                // If image selected, show small preview tag
                if (selectedImageUri != null) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Image attached",
                            color = accent.secondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "[Remove]",
                            color = Color(0xFFFF5252),
                            fontSize = 11.sp,
                            modifier = Modifier.clickable { selectedImageUri = null }
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Attachment button
                    IconButton(
                        onClick = {
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Image,
                            contentDescription = "Attach image",
                            tint = if (selectedImageUri != null) accent.secondary else TextSecondary,
                            modifier = Modifier.size(19.dp)
                        )
                    }

                    // Web search toggle
                    IconButton(
                        onClick = { viewModel.toggleWebSearch() },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Language,
                            contentDescription = "Toggle web research",
                            tint = if (webSearchEnabled) accent.primary else TextMuted,
                            modifier = Modifier.size(19.dp)
                        )
                    }

                    // Input Text Field
                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        placeholder = {
                            Text(
                                "Ask Zairon anything...",
                                color = TextMuted,
                                fontSize = 13.sp
                            )
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("home_message_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                            focusedTextColor = TextBright,
                            unfocusedTextColor = TextBright,
                            cursorColor = accent.primary
                        ),
                        singleLine = true
                    )

                    // Voice trigger
                    IconButton(
                        onClick = onNavigateToVoice,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(SurfaceElevated)
                            .testTag("home_voice_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = "Voice Assistant",
                            tint = accent.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    // Send Button
                    IconButton(
                        onClick = {
                            if (inputText.isNotBlank() || selectedImageUri != null) {
                                val bitmap = selectedImageUri?.let { uri ->
                                    try {
                                        context.contentResolver.openInputStream(uri)?.use { stream ->
                                            BitmapFactory.decodeStream(stream)
                                        }
                                    } catch (_: Exception) {
                                        null
                                    }
                                }
                                viewModel.sendMessage(inputText, bitmap)
                                inputText = ""
                                selectedImageUri = null
                                onNavigateToChat()
                            }
                        },
                        enabled = inputText.isNotBlank() || selectedImageUri != null,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(if (inputText.isNotBlank()) accent.primary else SurfaceElevated)
                            .testTag("home_send_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = "Send prompt",
                            tint = if (inputText.isNotBlank()) VoidBlack else TextMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}
