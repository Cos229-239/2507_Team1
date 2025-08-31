package com.example.mainui.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import com.example.mainui.R

// Global dark-mode flag (provided in MyApp)
val LocalAppDarkMode = staticCompositionLocalOf { false }

// Brand fonts
val Inter = FontFamily(Font(R.font.inter_regular))
val InterBold = FontFamily(Font(R.font.inter_bold))

// Accent teal for outlines/links
val TranquilBlue: Color
    @Composable get() = if (LocalAppDarkMode.current) Color(0xFF4FA8B8) else Color(0xFF1693B2)

// Body text
val TranquilText: Color
    @Composable get() = if (LocalAppDarkMode.current) Color(0xFFE5F6F8) else Color(0xFF022328)

// Accent purple for light mode (quotes, dividers, etc.)
val AccentPurple: Color
    @Composable get() = if (LocalAppDarkMode.current) TranquilBlue else Color(0xFF46127A)

// Light surface (cards) used in light theme
val TranquilSurface = Color(0xFFE7F2F4)

// Dark-mode plum panel + light-mode teal panel
private val DarkPlumBase = Color(0xFF2B1835) // deep plum
private val LightTealBase = Color(0xFFB2EBF2) // soft frosted teal

val PanelSurface: Color
    @Composable get() = if (LocalAppDarkMode.current) {
        DarkPlumBase.copy(alpha = 0.40f)
    } else {
        LightTealBase.copy(alpha = 0.30f)
    }

// Subtle borders/dividers
val PanelStroke: Color
    @Composable get() = if (LocalAppDarkMode.current) {
        Color(0x66B18CCF) // muted lavender border in dark
    } else {
        Color(0x661693B2) // subtle teal border in light
    }

// Gradient (muted in dark, vibrant in light)
@Composable
fun NeonAccent(): Brush =
    if (LocalAppDarkMode.current) {
        Brush.linearGradient(
            listOf(Color(0xFF3D2B4C), Color(0xFF2C5A63)),
            tileMode = TileMode.Clamp
        )
    } else {
        Brush.linearGradient(
            listOf(Color(0xFFAA7DFF), Color(0xFF45E3E5)),
            tileMode = TileMode.Clamp
        )
    }