package com.example.maitescalc.ui.theme

import androidx.compose.ui.graphics.Color

data class AppColorPalette(
    val id: String,
    val name: String,           // שם בעברית
    val emoji: String,          // אימוג'י לזיהוי
    val isDark: Boolean = false,
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
    val error: Color = Color(0xFFE53935),
    val onError: Color = Color.White,
    val success: Color = Color(0xFF10B981)
)

object ColorPalettes {
    // 1. ברירת מחדל: שחור-לבן מינימליסטי יוקרתי (Expressive Monochrome)
    val Monochrome = AppColorPalette(
        id = "monochrome",
        name = "שחור-לבן מודרני",
        emoji = "🖤",
        isDark = false,
        primary = Color(0xFF0F172A),          // שחור פחם עמוק
        onPrimary = Color(0xFFFFFFFF),        // לבן
        primaryContainer = Color(0xFFF1F5F9), // אפור פנינה בהיר
        onPrimaryContainer = Color(0xFF0F172A),
        secondary = Color(0xFF334155),
        onSecondary = Color(0xFFFFFFFF),
        background = Color(0xFFF8FAFC),       // רקע בהיר מודרני נקי
        onBackground = Color(0xFF0F172A),     // טקסט כהה חד
        surface = Color(0xFFFFFFFF),          // כרטיסים לבנים מבריקים
        onSurface = Color(0xFF0F172A),
        surfaceVariant = Color(0xFFF1F5F9),
        onSurfaceVariant = Color(0xFF64748B),
        outline = Color(0xFFE2E8F0),          // קווי מתאר דקים ואלגנטיים
        error = Color(0xFFDC2626),
        success = Color(0xFF16A34A)
    )

    // 2. כהה OLED מינימליסטי (Deep Dark Minimal)
    val DarkMinimal = AppColorPalette(
        id = "dark_minimal",
        name = "שחור פחם OLED",
        emoji = "🌑",
        isDark = true,
        primary = Color(0xFFFFFFFF),          // לבן בוהק
        onPrimary = Color(0xFF09090B),        // שחור
        primaryContainer = Color(0xFF27272A),
        onPrimaryContainer = Color(0xFFFAFAFA),
        secondary = Color(0xFFA1A1AA),
        onSecondary = Color(0xFF09090B),
        background = Color(0xFF09090B),       // רקע שחור עמוק
        onBackground = Color(0xFFFAFAFA),     // טקסט לבן חד
        surface = Color(0xFF18181B),          // כרטיס שחור גרפיט
        onSurface = Color(0xFFFAFAFA),
        surfaceVariant = Color(0xFF27272A),
        onSurfaceVariant = Color(0xFFA1A1AA),
        outline = Color(0xFF3F3F46),
        error = Color(0xFFEF4444),
        success = Color(0xFF22C55E)
    )

    // 3. אספרסו וקרם (Warm Espresso & Cream)
    val Espresso = AppColorPalette(
        id = "espresso",
        name = "אספרסו וקרם",
        emoji = "☕",
        isDark = false,
        primary = Color(0xFF2E1C14),          // קפה קלוי כהה
        onPrimary = Color(0xFFFAF7F2),
        primaryContainer = Color(0xFFF2EBE3),
        onPrimaryContainer = Color(0xFF2E1C14),
        secondary = Color(0xFF634A3E),
        onSecondary = Color(0xFFFAF7F2),
        background = Color(0xFFFAF7F2),       // קרם בהיר חם
        onBackground = Color(0xFF2E1C14),
        surface = Color(0xFFFFFFFF),
        onSurface = Color(0xFF2E1C14),
        surfaceVariant = Color(0xFFF3EDE6),
        onSurfaceVariant = Color(0xFF7A685D),
        outline = Color(0xFFE4D9CE),
        error = Color(0xFFC2410C),
        success = Color(0xFF15803D)
    )

    // 4. מרווה נורדית (Modern Nordic Sage)
    val Sage = AppColorPalette(
        id = "sage",
        name = "מרווה אלגנטית",
        emoji = "🌿",
        isDark = false,
        primary = Color(0xFF1E3A2F),          // ירוק מרווה עמוק
        onPrimary = Color(0xFFFFFFFF),
        primaryContainer = Color(0xFFE2EBE6),
        onPrimaryContainer = Color(0xFF1E3A2F),
        secondary = Color(0xFF3C5E51),
        onSecondary = Color(0xFFFFFFFF),
        background = Color(0xFFF5F8F6),
        onBackground = Color(0xFF1E3A2F),
        surface = Color(0xFFFFFFFF),
        onSurface = Color(0xFF1E3A2F),
        surfaceVariant = Color(0xFFE8EFEA),
        onSurfaceVariant = Color(0xFF5E796F),
        outline = Color(0xFFD3E0D8),
        error = Color(0xFFDC2626),
        success = Color(0xFF16A34A)
    )

    // 5. נייבי נקי (Clean Slate Navy)
    val Slate = AppColorPalette(
        id = "slate",
        name = "כחול אטלנטי",
        emoji = "🌊",
        isDark = false,
        primary = Color(0xFF1E293B),
        onPrimary = Color(0xFFFFFFFF),
        primaryContainer = Color(0xFFE2E8F0),
        onPrimaryContainer = Color(0xFF0F172A),
        secondary = Color(0xFF475569),
        onSecondary = Color(0xFFFFFFFF),
        background = Color(0xFFF1F5F9),
        onBackground = Color(0xFF0F172A),
        surface = Color(0xFFFFFFFF),
        onSurface = Color(0xFF0F172A),
        surfaceVariant = Color(0xFFE2E8F0),
        onSurfaceVariant = Color(0xFF64748B),
        outline = Color(0xFFCBD5E1),
        error = Color(0xFFDC2626),
        success = Color(0xFF10B981)
    )

    // 6. ורוד פסטל עדין (Soft Rose Bakery)
    val SoftRose = AppColorPalette(
        id = "soft_rose",
        name = "רוז פסטל",
        emoji = "🌸",
        isDark = false,
        primary = Color(0xFF881337),          // בורדו עמוק
        onPrimary = Color(0xFFFFFFFF),
        primaryContainer = Color(0xFFFFE4E6), // ורוד פסטל בהיר
        onPrimaryContainer = Color(0xFF881337),
        secondary = Color(0xFF9F1239),
        onSecondary = Color(0xFFFFFFFF),
        background = Color(0xFFFFF1F2),
        onBackground = Color(0xFF4C0519),
        surface = Color(0xFFFFFFFF),
        onSurface = Color(0xFF4C0519),
        surfaceVariant = Color(0xFFFFE4E6),
        onSurfaceVariant = Color(0xFF9F1239),
        outline = Color(0xFFFECDD3),
        error = Color(0xFFE11D48),
        success = Color(0xFF059669)
    )

    val all = listOf(Monochrome, DarkMinimal, Espresso, Sage, Slate, SoftRose)

    fun getById(id: String): AppColorPalette {
        return all.find { it.id == id } ?: Monochrome
    }
}
