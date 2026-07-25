package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val GoalPathColorScheme = darkColorScheme(
    primary = PrimaryPurpleGlow,
    onPrimary = Color.White,
    primaryContainer = HeroCardContainer,
    onPrimaryContainer = DarkPurpleText,
    secondary = SecondaryViolet,
    onSecondary = Color(0xFF1D192B),
    secondaryContainer = SecondaryPillBg,
    onSecondaryContainer = TextPrimary,
    tertiary = AccentPink,
    background = DarkBackground,
    onBackground = TextPrimary,
    surface = SurfaceCard,
    onSurface = TextPrimary,
    surfaceVariant = SecondaryPillBg,
    onSurfaceVariant = TextSecondary,
    outline = SurfaceCardBorder,
    outlineVariant = TextMuted
)

@Composable
fun GoalPathTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = GoalPathColorScheme,
        typography = Typography,
        content = content
    )
}

