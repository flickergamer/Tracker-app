package com.example.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.R

val InterFontFamily = FontFamily(
    Font(R.font.inter, FontWeight.Normal),
    Font(R.font.inter, FontWeight.Medium),
    Font(R.font.inter, FontWeight.SemiBold),
    Font(R.font.inter, FontWeight.Bold)
)

fun fontFamilyForTheme(@Suppress("UNUSED_PARAMETER") mode: AppThemeMode): FontFamily = InterFontFamily

fun typographyForTheme(mode: AppThemeMode): Typography {
    val family = fontFamilyForTheme(mode)
    return Typography(
        displayLarge = TextStyle(
            fontFamily = family,
            fontWeight = FontWeight.Bold,
            fontSize = 32.sp,
            lineHeight = 38.sp,
            letterSpacing = (-0.5).sp
        ),
        displayMedium = TextStyle(
            fontFamily = family,
            fontWeight = FontWeight.Bold,
            fontSize = 25.sp,
            lineHeight = 31.sp,
            letterSpacing = (-0.3).sp
        ),
        headlineLarge = TextStyle(
            fontFamily = family,
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp,
            lineHeight = 28.sp,
            letterSpacing = (-0.2).sp
        ),
        headlineMedium = TextStyle(
            fontFamily = family,
            fontWeight = FontWeight.Bold,
            fontSize = 19.sp,
            lineHeight = 25.sp
        ),
        titleLarge = TextStyle(
            fontFamily = family,
            fontWeight = FontWeight.Bold,
            fontSize = 17.sp,
            lineHeight = 23.sp
        ),
        titleMedium = TextStyle(
            fontFamily = family,
            fontWeight = FontWeight.SemiBold,
            fontSize = 15.sp,
            lineHeight = 20.sp
        ),
        titleSmall = TextStyle(
            fontFamily = family,
            fontWeight = FontWeight.SemiBold,
            fontSize = 13.sp,
            lineHeight = 18.sp
        ),
        bodyLarge = TextStyle(
            fontFamily = family,
            fontWeight = FontWeight.Normal,
            fontSize = 15.sp,
            lineHeight = 22.sp
        ),
        bodyMedium = TextStyle(
            fontFamily = family,
            fontWeight = FontWeight.Normal,
            fontSize = 13.sp,
            lineHeight = 19.sp
        ),
        bodySmall = TextStyle(
            fontFamily = family,
            fontWeight = FontWeight.Normal,
            fontSize = 12.sp,
            lineHeight = 16.sp
        ),
        labelLarge = TextStyle(
            fontFamily = family,
            fontWeight = FontWeight.SemiBold,
            fontSize = 13.sp,
            lineHeight = 18.sp,
            letterSpacing = 0.1.sp
        ),
        labelMedium = TextStyle(
            fontFamily = family,
            fontWeight = FontWeight.SemiBold,
            fontSize = 11.sp,
            lineHeight = 15.sp,
            letterSpacing = 0.2.sp
        ),
        labelSmall = TextStyle(
            fontFamily = family,
            fontWeight = FontWeight.Medium,
            fontSize = 10.sp,
            lineHeight = 14.sp,
            letterSpacing = 0.2.sp
        )
    )
}

val AfterglowTypography = typographyForTheme(AppThemeMode.NEON_GLOSSY)
