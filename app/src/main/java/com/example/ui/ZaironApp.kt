package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.screens.ChatScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.MemoryScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.VoiceAssistantScreen
import com.example.ui.theme.BorderGlassDim
import com.example.ui.theme.LocalAccentTheme
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceElevated
import com.example.ui.theme.SurfaceGlass
import com.example.ui.theme.TextBright
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VoidBlack
import com.example.ui.theme.ZaironTheme
import kotlinx.coroutines.launch

enum class Screen {
    HOME,
    VOICE,
    CHAT,
    MEMORY,
    SETTINGS
}

@Composable
fun ZaironApp(viewModel: ZaironViewModel) {
    val accentTheme by viewModel.accentTheme.collectAsState()

    ZaironTheme(accentTheme = accentTheme) {
        val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
        val scope = rememberCoroutineScope()
        var currentScreen by remember { mutableStateOf(Screen.HOME) }

        val conversations by viewModel.conversations.collectAsState()
        val activeConvId by viewModel.currentConversationId.collectAsState()

        BackHandler(enabled = currentScreen != Screen.HOME) {
            currentScreen = Screen.HOME
        }

        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet(
                    drawerContainerColor = SurfaceDark,
                    modifier = Modifier.width(310.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp, vertical = 20.dp)
                            .statusBarsPadding()
                            .navigationBarsPadding(),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            // Drawer Header
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(bottom = 16.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(accentTheme.primary.copy(alpha = 0.2f))
                                        .border(1.5.dp, accentTheme.primary, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(14.dp)
                                            .clip(CircleShape)
                                            .background(accentTheme.primary)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "ZAIRON AI 2.0",
                                        color = TextBright,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        letterSpacing = 1.sp
                                    )
                                    Text(
                                        text = "Engineered by Azhan",
                                        color = accentTheme.primary,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }

                            HorizontalDivider(color = BorderGlassDim, thickness = 1.dp)

                            Spacer(modifier = Modifier.height(14.dp))

                            // New Objective Button
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .border(1.dp, accentTheme.primary.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                                    .clickable {
                                        viewModel.createNewConversation("New Objective")
                                        currentScreen = Screen.CHAT
                                        scope.launch { drawerState.close() }
                                    },
                                color = SurfaceElevated
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = null, tint = accentTheme.primary)
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text("New Dialogue", color = TextBright, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = "CONVERSATION ARCHIVES",
                                color = TextMuted,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp,
                                modifier = Modifier.padding(start = 4.dp, bottom = 6.dp)
                            )

                            // Conversations List
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxHeight(0.6f)
                                    .fillMaxWidth()
                            ) {
                                items(conversations, key = { it.id }) { conv ->
                                    val isSelected = conv.id == activeConvId
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 3.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (isSelected) accentTheme.primary.copy(alpha = 0.12f) else Color.Transparent)
                                            .clickable {
                                                viewModel.selectConversation(conv.id)
                                                currentScreen = Screen.CHAT
                                                scope.launch { drawerState.close() }
                                            }
                                            .padding(horizontal = 10.dp, vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.ChatBubbleOutline,
                                                contentDescription = null,
                                                tint = if (isSelected) accentTheme.primary else TextMuted,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = conv.title,
                                                color = if (isSelected) TextBright else TextSecondary,
                                                fontSize = 13.sp,
                                                maxLines = 1,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                            )
                                        }

                                        IconButton(
                                            onClick = { viewModel.deleteConversation(conv.id) },
                                            modifier = Modifier.size(24.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = "Delete conversation",
                                                tint = TextMuted,
                                                modifier = Modifier.size(14.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Bottom Navigation Links
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            HorizontalDivider(color = BorderGlassDim, thickness = 1.dp)
                            Spacer(modifier = Modifier.height(4.dp))

                            DrawerItem(
                                icon = Icons.Default.Home,
                                title = "Dashboard Home",
                                isSelected = currentScreen == Screen.HOME,
                                onClick = {
                                    currentScreen = Screen.HOME
                                    scope.launch { drawerState.close() }
                                }
                            )

                            DrawerItem(
                                icon = Icons.Default.Mic,
                                title = "Voice Assistant",
                                isSelected = currentScreen == Screen.VOICE,
                                onClick = {
                                    currentScreen = Screen.VOICE
                                    scope.launch { drawerState.close() }
                                }
                            )

                            DrawerItem(
                                icon = Icons.Default.Psychology,
                                title = "Personal Memory Core",
                                isSelected = currentScreen == Screen.MEMORY,
                                onClick = {
                                    currentScreen = Screen.MEMORY
                                    scope.launch { drawerState.close() }
                                }
                            )

                            DrawerItem(
                                icon = Icons.Default.Settings,
                                title = "Settings & Models",
                                isSelected = currentScreen == Screen.SETTINGS,
                                onClick = {
                                    currentScreen = Screen.SETTINGS
                                    scope.launch { drawerState.close() }
                                }
                            )
                        }
                    }
                }
            }
        ) {
            Scaffold(
                modifier = Modifier
                    .fillMaxSize()
                    .background(VoidBlack)
                    .statusBarsPadding()
                    .navigationBarsPadding(),
                containerColor = VoidBlack
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    when (currentScreen) {
                        Screen.HOME -> HomeScreen(
                            viewModel = viewModel,
                            onOpenDrawer = { scope.launch { drawerState.open() } },
                            onNavigateToChat = { currentScreen = Screen.CHAT },
                            onNavigateToVoice = { currentScreen = Screen.VOICE }
                        )
                        Screen.VOICE -> VoiceAssistantScreen(
                            viewModel = viewModel,
                            onNavigateBack = { currentScreen = Screen.HOME },
                            onNavigateToChat = { currentScreen = Screen.CHAT }
                        )
                        Screen.CHAT -> ChatScreen(
                            viewModel = viewModel,
                            onNavigateBack = { currentScreen = Screen.HOME },
                            onNavigateToVoice = { currentScreen = Screen.VOICE }
                        )
                        Screen.MEMORY -> MemoryScreen(
                            viewModel = viewModel,
                            onNavigateBack = { currentScreen = Screen.HOME }
                        )
                        Screen.SETTINGS -> SettingsScreen(
                            viewModel = viewModel,
                            onNavigateBack = { currentScreen = Screen.HOME },
                            onNavigateToMemory = { currentScreen = Screen.MEMORY }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DrawerItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val accent = LocalAccentTheme.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(if (isSelected) accent.primary.copy(alpha = 0.15f) else Color.Transparent)
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = if (isSelected) accent.primary else TextSecondary,
            modifier = Modifier.size(19.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = title,
            color = if (isSelected) TextBright else TextSecondary,
            fontSize = 13.5.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
        )
    }
}
