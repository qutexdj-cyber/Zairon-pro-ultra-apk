package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ChatMessageEntity
import com.example.data.model.WebSource
import com.example.ui.theme.BorderGlass
import com.example.ui.theme.BorderGlassDim
import com.example.ui.theme.CodeBackground
import com.example.ui.theme.DeepObsidian
import com.example.ui.theme.LocalAccentTheme
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceElevated
import com.example.ui.theme.SurfaceGlass
import com.example.ui.theme.TextBright
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSecondary
import org.json.JSONArray

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MessageBubble(
    message: ChatMessageEntity,
    isSpeakingThis: Boolean,
    onReadAloud: (String) -> Unit,
    onStopSpeaking: () -> Unit,
    onRegenerate: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isUser = message.sender == "user"
    val context = LocalContext.current
    val accent = LocalAccentTheme.current

    val sources = parseSources(message.sourcesJson)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
    ) {
        // Sender Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 4.dp, start = 4.dp, end = 4.dp)
        ) {
            if (!isUser) {
                Box(
                    modifier = Modifier
                        .size(18.dp)
                        .clip(CircleShape)
                        .background(accent.primary.copy(alpha = 0.2f))
                        .border(1.dp, accent.primary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(accent.primary)
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "ZAIRON AI 2.0",
                    color = accent.primary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "• ${message.modelUsed}",
                    color = TextMuted,
                    fontSize = 10.sp
                )
            } else {
                Text(
                    text = "YOU",
                    color = TextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }
        }

        // Message Content Bubble
        Surface(
            modifier = Modifier
                .clip(
                    RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (isUser) 16.dp else 4.dp,
                        bottomEnd = if (isUser) 4.dp else 16.dp
                    )
                )
                .border(
                    1.dp,
                    if (isUser) accent.primary.copy(alpha = 0.25f) else BorderGlassDim,
                    RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (isUser) 16.dp else 4.dp,
                        bottomEnd = if (isUser) 4.dp else 16.dp
                    )
                ),
            color = if (isUser) SurfaceElevated else SurfaceDark.copy(alpha = 0.95f),
            tonalElevation = 2.dp
        ) {
            Column(
                modifier = Modifier
                    .background(
                        if (isUser) {
                            Brush.linearGradient(
                                colors = listOf(
                                    accent.primary.copy(alpha = 0.12f),
                                    SurfaceElevated
                                )
                            )
                        } else {
                            Brush.verticalGradient(
                                colors = listOf(
                                    SurfaceGlass,
                                    SurfaceDark
                                )
                            )
                        }
                    )
                    .padding(14.dp)
            ) {
                // Render Formatted Content (Headings, Code, Text)
                RenderFormattedMarkdown(content = message.content)

                // Render Citations / Sources if attached
                if (sources.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "LIVE RESEARCH CITATIONS",
                        color = accent.secondary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        sources.forEach { source ->
                            SourceChip(source = source)
                        }
                    }
                }
            }
        }

        // Action Toolbar for Zairon's responses
        if (!isUser) {
            Row(
                modifier = Modifier
                    .padding(top = 4.dp, start = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        clipboard.setPrimaryClip(ClipData.newPlainText("Zairon Message", message.content))
                        Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier
                        .size(32.dp)
                        .testTag("copy_message_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copy message",
                        tint = TextSecondary,
                        modifier = Modifier.size(15.dp)
                    )
                }

                IconButton(
                    onClick = {
                        if (isSpeakingThis) {
                            onStopSpeaking()
                        } else {
                            onReadAloud(message.content)
                        }
                    },
                    modifier = Modifier
                        .size(32.dp)
                        .testTag("speech_message_button")
                ) {
                    Icon(
                        imageVector = if (isSpeakingThis) Icons.Default.Stop else Icons.Default.PlayArrow,
                        contentDescription = if (isSpeakingThis) "Stop speaking" else "Read aloud",
                        tint = if (isSpeakingThis) accent.primary else TextSecondary,
                        modifier = Modifier.size(17.dp)
                    )
                }

                IconButton(
                    onClick = { onRegenerate() },
                    modifier = Modifier
                        .size(32.dp)
                        .testTag("regenerate_message_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Regenerate answer",
                        tint = TextSecondary,
                        modifier = Modifier.size(15.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun RenderFormattedMarkdown(content: String) {
    val sections = splitCodeAndText(content)

    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        for (section in sections) {
            when (section) {
                is MarkdownSection.CodeBlock -> {
                    CodeBlockView(
                        language = section.language,
                        code = section.code
                    )
                }
                is MarkdownSection.TextSection -> {
                    RenderTextBlock(text = section.text)
                }
            }
        }
    }
}

@Composable
fun RenderTextBlock(text: String) {
    val lines = text.split("\n")
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        for (line in lines) {
            val trimmed = line.trim()
            when {
                trimmed.startsWith("### ") -> {
                    Text(
                        text = trimmed.removePrefix("### "),
                        color = LocalAccentTheme.current.primary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 4.dp, bottom = 2.dp)
                    )
                }
                trimmed.startsWith("## ") -> {
                    Text(
                        text = trimmed.removePrefix("## "),
                        color = LocalAccentTheme.current.primary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 6.dp, bottom = 2.dp)
                    )
                }
                trimmed.startsWith("# ") -> {
                    Text(
                        text = trimmed.removePrefix("# "),
                        color = TextBright,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                    )
                }
                trimmed.startsWith("- ") || trimmed.startsWith("* ") -> {
                    Row(modifier = Modifier.padding(start = 6.dp)) {
                        Text(
                            text = "• ",
                            color = LocalAccentTheme.current.secondary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = cleanInlineMarkdown(trimmed.substring(2)),
                            color = TextBright,
                            fontSize = 14.sp,
                            lineHeight = 20.sp
                        )
                    }
                }
                trimmed.isNotBlank() -> {
                    Text(
                        text = cleanInlineMarkdown(trimmed),
                        color = TextBright,
                        fontSize = 14.sp,
                        lineHeight = 21.sp
                    )
                }
            }
        }
    }
}

@Composable
fun CodeBlockView(
    language: String,
    code: String
) {
    val context = LocalContext.current
    val accent = LocalAccentTheme.current

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .border(1.dp, BorderGlassDim, RoundedCornerShape(10.dp)),
        color = CodeBackground
    ) {
        Column {
            // Code block header with copy button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceDark)
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = language.ifBlank { "CODE" }.uppercase(),
                    color = accent.secondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )

                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .clickable {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("Code", code))
                            Toast.makeText(context, "Code copied", Toast.LENGTH_SHORT).show()
                        }
                        .padding(horizontal = 6.dp, vertical = 3.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copy code",
                        tint = TextSecondary,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "COPY",
                        color = TextSecondary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Scrollable Code Content
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(12.dp)
            ) {
                Text(
                    text = code,
                    color = Color(0xFF81C784),
                    fontSize = 12.5.sp,
                    fontFamily = FontFamily.Monospace,
                    lineHeight = 18.sp
                )
            }
        }
    }
}

@Composable
fun SourceChip(source: WebSource) {
    val context = LocalContext.current
    val accent = LocalAccentTheme.current

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(SurfaceDark)
            .border(1.dp, BorderGlassDim, RoundedCornerShape(8.dp))
            .clickable {
                try {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(source.url))
                    context.startActivity(intent)
                } catch (_: Exception) {}
            }
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.OpenInBrowser,
            contentDescription = "Open link",
            tint = accent.secondary,
            modifier = Modifier.size(12.dp)
        )
        Spacer(modifier = Modifier.width(5.dp))
        Text(
            text = source.title.take(24) + if (source.title.length > 24) "..." else "",
            color = TextSecondary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

private sealed class MarkdownSection {
    data class TextSection(val text: String) : MarkdownSection()
    data class CodeBlock(val language: String, val code: String) : MarkdownSection()
}

private fun splitCodeAndText(raw: String): List<MarkdownSection> {
    val result = mutableListOf<MarkdownSection>()
    val codeRegex = Regex("```(\\w*)\\n([\\s\\S]*?)```")
    var lastIndex = 0

    codeRegex.findAll(raw).forEach { match ->
        val textBefore = raw.substring(lastIndex, match.range.first)
        if (textBefore.isNotBlank()) {
            result.add(MarkdownSection.TextSection(textBefore.trim()))
        }
        val lang = match.groupValues[1]
        val code = match.groupValues[2]
        result.add(MarkdownSection.CodeBlock(lang, code.trimEnd()))
        lastIndex = match.range.last + 1
    }

    if (lastIndex < raw.length) {
        val remainder = raw.substring(lastIndex)
        if (remainder.isNotBlank()) {
            result.add(MarkdownSection.TextSection(remainder.trim()))
        }
    }

    return result.ifEmpty { listOf(MarkdownSection.TextSection(raw)) }
}

private fun cleanInlineMarkdown(text: String): String {
    return text
        .replace(Regex("\\*\\*([^*]+)\\*\\*"), "$1")
        .replace(Regex("\\*([^*]+)\\*"), "$1")
}

private fun parseSources(sourcesJson: String?): List<WebSource> {
    if (sourcesJson.isNullOrBlank()) return emptyList()
    val list = mutableListOf<WebSource>()
    try {
        val arr = JSONArray(sourcesJson)
        for (i in 0 until arr.length()) {
            val obj = arr.getJSONObject(i)
            list.add(
                WebSource(
                    title = obj.optString("title", "Source"),
                    snippet = obj.optString("snippet", ""),
                    url = obj.optString("url", "")
                )
            )
        }
    } catch (_: Exception) {}
    return list
}
