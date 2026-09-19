package com.example.maitescalc.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.sp
import com.example.maitescalc.R

val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs
)

val heeboFont = GoogleFont("Heebo")

val HeeboFontFamily = FontFamily(
    Font(googleFont = heeboFont, fontProvider = provider, weight = FontWeight.Light),
    Font(googleFont = heeboFont, fontProvider = provider, weight = FontWeight.Normal),
    Font(googleFont = heeboFont, fontProvider = provider, weight = FontWeight.Medium),
    Font(googleFont = heeboFont, fontProvider = provider, weight = FontWeight.SemiBold),
    Font(googleFont = heeboFont, fontProvider = provider, weight = FontWeight.Bold),
    Font(googleFont = heeboFont, fontProvider = provider, weight = FontWeight.ExtraBold),
)

val AppTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = HeeboFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 40.sp,
        lineHeight = 48.sp,
        textDirection = TextDirection.ContentOrRtl
    ),
    displayMedium = TextStyle(
        fontFamily = HeeboFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 34.sp,
        lineHeight = 42.sp,
        textDirection = TextDirection.ContentOrRtl
    ),
    displaySmall = TextStyle(
        fontFamily = HeeboFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 36.sp,
        textDirection = TextDirection.ContentOrRtl
    ),
    headlineLarge = TextStyle(
        fontFamily = HeeboFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 26.sp,
        lineHeight = 34.sp,
        textDirection = TextDirection.ContentOrRtl
    ),
    headlineMedium = TextStyle(
        fontFamily = HeeboFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp,
        lineHeight = 30.sp,
        textDirection = TextDirection.ContentOrRtl
    ),
    headlineSmall = TextStyle(
        fontFamily = HeeboFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        lineHeight = 28.sp,
        textDirection = TextDirection.ContentOrRtl
    ),
    titleLarge = TextStyle(
        fontFamily = HeeboFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        lineHeight = 28.sp,
        textDirection = TextDirection.ContentOrRtl
    ),
    titleMedium = TextStyle(
        fontFamily = HeeboFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 18.sp,
        lineHeight = 26.sp,
        textDirection = TextDirection.ContentOrRtl
    ),
    titleSmall = TextStyle(
        fontFamily = HeeboFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        textDirection = TextDirection.ContentOrRtl
    ),
    bodyLarge = TextStyle(
        fontFamily = HeeboFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        textDirection = TextDirection.ContentOrRtl
    ),
    bodyMedium = TextStyle(
        fontFamily = HeeboFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 22.sp,
        textDirection = TextDirection.ContentOrRtl
    ),
    bodySmall = TextStyle(
        fontFamily = HeeboFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 18.sp,
        textDirection = TextDirection.ContentOrRtl
    ),
    labelLarge = TextStyle(
        fontFamily = HeeboFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 22.sp,
        textDirection = TextDirection.ContentOrRtl
    ),
    labelMedium = TextStyle(
        fontFamily = HeeboFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        textDirection = TextDirection.ContentOrRtl
    ),
    labelSmall = TextStyle(
        fontFamily = HeeboFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 18.sp,
        textDirection = TextDirection.ContentOrRtl
    )
)
