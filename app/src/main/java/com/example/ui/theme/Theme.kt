package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

enum class AccentTheme(val title: String, val primary: Color, val secondary: Color) {
    EMERALD("Emerald Pulse", EmeraldPrimary, CyanAccent),
    CYAN("Cyan Matrix", CyanAccent, EmeraldPrimary),
    MINT("Neon Mint", MintAccent, CyanGlow),
    AMBER("Solar Gold", AmberAccent, EmeraldPrimary),
    VIOLET("Cyber Violet", VioletAccent, CyanAccent)
}

val LocalAccentTheme = staticCompositionLocalOf { AccentTheme.EMERALD }

@Composable
fun ZaironTheme(
    accentTheme: AccentTheme = AccentTheme.EMERALD,
    content: @Composable () -> Unit
) {
    val colorScheme = darkColorScheme(
        primary = accentTheme.primary,
        onPrimary = VoidBlack,
        primaryContainer = SurfaceElevated,
        onPrimaryContainer = TextBright,
        secondary = accentTheme.secondary,
        onSecondary = VoidBlack,
        secondaryContainer = SurfaceDark,
        onSecondaryContainer = TextBright,
        tertiary = MintAccent,
        onTertiary = VoidBlack,
        background = VoidBlack,
        onBackground = TextBright,
        surface = DeepObsidian,
        onSurface = TextBright,
        surfaceVariant = SurfaceDark,
        onSurfaceVariant = TextSecondary,
        outline = BorderGlass,
        outlineVariant = BorderGlassDim
    )

    CompositionLocalProvider(LocalAccentTheme provides accentTheme) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
