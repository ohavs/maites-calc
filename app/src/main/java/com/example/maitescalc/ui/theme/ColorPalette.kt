package com.example.maitescalc.ui.theme

import androidx.compose.ui.graphics.Color

data class AppColorPalette(
    val id: String,
    val name: String,           // שם בעברית
    val emoji: String,          // אימוג'י לזיהוי
    val primary: Color,
    val onPrimary: Color,
    val primaryContainer: Color,
    val onPrimaryContainer: Color,
    val secondary: Color,
    val onSecondary: Color,
    val background: Color,
    val onBackground: Color,
    val surface: Color,
    val onSurface: Color,
    val surfaceVariant: Color,
    val onSurfaceVariant: Color,
    val outline: Color,
    val error: Color = Color(0xFFFF6B6B),
    val onError: Color = Color.White,
    val success: Color = Color(0xFF4CAF50)
)

object ColorPalettes {
    val Lime = AppColorPalette(
        id = "lime",
        name = "ליים",
        emoji = "🍋",
        primary = Color(0xFFC8E64A),
        onPrimary = Color(0xFF1A1A2E),
        primaryContainer = Color(0xFF3D5A00),
        onPrimaryContainer = Color(0xFFD4F34F),
        secondary = Color(0xFFA8C83A),
        onSecondary = Color(0xFF1A1A2E),
        background = Color(0xFF1A1A2E),
        onBackground = Color(0xFFFFFFFF),
        surface = Color(0xFF2D2D44),
        onSurface = Color(0xFFE8E8F0),
        surfaceVariant = Color(0xFF3D3D5C),
        onSurfaceVariant = Color(0xFFB0B0C0),
        outline = Color(0xFF4D4D6A)
    )

    val RosePastry = AppColorPalette(
        id = "rose",
        name = "ורוד מאפה",
        emoji = "🧁",
        primary = Color(0xFFFF8FAB),
        onPrimary = Color(0xFF1C1520),
        primaryContainer = Color(0xFF8C1D3F),
        onPrimaryContainer = Color(0xFFFFD9E2),
        secondary = Color(0xFFE86A8A),
        onSecondary = Color(0xFF1C1520),
        background = Color(0xFF1C1520),
        onBackground = Color(0xFFFFFFFF),
        surface = Color(0xFF2D2535),
        onSurface = Color(0xFFF0E8EC),
        surfaceVariant = Color(0xFF3D3548),
        onSurfaceVariant = Color(0xFFC0B0B8),
        outline = Color(0xFF5D4D58)
    )

    val Chocolate = AppColorPalette(
        id = "chocolate",
        name = "שוקולד",
        emoji = "🍫",
        primary = Color(0xFFD4A574),
        onPrimary = Color(0xFF1A1410),
        primaryContainer = Color(0xFF6D4C2C),
        onPrimaryContainer = Color(0xFFFFDCC0),
        secondary = Color(0xFFC49564),
        onSecondary = Color(0xFF1A1410),
        background = Color(0xFF1A1410),
        onBackground = Color(0xFFFFFFFF),
        surface = Color(0xFF2D2520),
        onSurface = Color(0xFFF0E8E0),
        surfaceVariant = Color(0xFF3D3530),
        onSurfaceVariant = Color(0xFFC0B8B0),
        outline = Color(0xFF5D5548)
    )

    val Ocean = AppColorPalette(
        id = "ocean",
        name = "תכלת",
        emoji = "🌊",
        primary = Color(0xFF64B5F6),
        onPrimary = Color(0xFF0D1B2A),
        primaryContainer = Color(0xFF1565C0),
        onPrimaryContainer = Color(0xFFBBDEFB),
        secondary = Color(0xFF42A5F5),
        onSecondary = Color(0xFF0D1B2A),
        background = Color(0xFF0D1B2A),
        onBackground = Color(0xFFFFFFFF),
        surface = Color(0xFF1B2838),
        onSurface = Color(0xFFE0E8F0),
        surfaceVariant = Color(0xFF283848),
        onSurfaceVariant = Color(0xFFA0B0C0),
        outline = Color(0xFF3D5060)
    )

    val Orange = AppColorPalette(
        id = "orange",
        name = "תפוז",
        emoji = "🍊",
        primary = Color(0xFFFFB74D),
        onPrimary = Color(0xFF1A1510),
        primaryContainer = Color(0xFFE65100),
        onPrimaryContainer = Color(0xFFFFE0B2),
        secondary = Color(0xFFFFA726),
        onSecondary = Color(0xFF1A1510),
        background = Color(0xFF1A1510),
        onBackground = Color(0xFFFFFFFF),
        surface = Color(0xFF2D2820),
        onSurface = Color(0xFFF0E8E0),
        surfaceVariant = Color(0xFF3D3830),
        onSurfaceVariant = Color(0xFFC0B8A0),
        outline = Color(0xFF5D5540)
    )

    val ClassicLight = AppColorPalette(
        id = "light",
        name = "לבן קלאסי",
        emoji = "☁️",
        primary = Color(0xFF6C63FF),
        onPrimary = Color(0xFFFFFFFF),
        primaryContainer = Color(0xFFE0DDFF),
        onPrimaryContainer = Color(0xFF1A0060),
        secondary = Color(0xFF5C56E0),
        onSecondary = Color(0xFFFFFFFF),
        background = Color(0xFFF5F5F7),
        onBackground = Color(0xFF1A1A2E),
        surface = Color(0xFFFFFFFF),
        onSurface = Color(0xFF2D2D44),
        surfaceVariant = Color(0xFFEEEEF2),
        onSurfaceVariant = Color(0xFF6D6D80),
        outline = Color(0xFFD0D0D8)
    )

    val all = listOf(Lime, RosePastry, Chocolate, Ocean, Orange, ClassicLight)

    fun getById(id: String): AppColorPalette {
        return all.find { it.id == id } ?: Lime
    }
}
