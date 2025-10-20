package com.pixelro.nenoonkiosk.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.pixelro.nenoonkiosk.R


val defaultFont =
    FontFamily(
        Font(R.font.koddi_regular, FontWeight.Normal),
        Font(R.font.koddi_bold, FontWeight.Bold),
        Font(R.font.koddi_extrabold, FontWeight.ExtraBold),
    )

val Typography =
    Typography(
        displayLarge =
            TextStyle(
                fontFamily = defaultFont,
                fontWeight = FontWeight.Normal,
                fontSize = 56.sp,
                letterSpacing = 0.5.sp,
            ),
        headlineLarge =
            TextStyle(
                fontFamily = defaultFont,
                fontWeight = FontWeight.Normal,
                fontSize = 40.sp,
                letterSpacing = 0.5.sp,
            ),
        headlineMedium =
            TextStyle(
                fontFamily = defaultFont,
                fontWeight = FontWeight.Normal,
                fontSize = 36.sp,
                letterSpacing = 0.5.sp,
            ),
        headlineSmall =
            TextStyle(
                fontFamily = defaultFont,
                fontWeight = FontWeight.Normal,
                fontSize = 32.sp,
                letterSpacing = 0.5.sp,
            ),
        titleLarge =
            TextStyle(
                fontFamily = defaultFont,
                fontWeight = FontWeight.Normal,
                fontSize = 28.sp,
                letterSpacing = 0.5.sp,
            ),
        titleMedium =
            TextStyle(
                fontFamily = defaultFont,
                fontWeight = FontWeight.Normal,
                fontSize = 24.sp,
                letterSpacing = 0.5.sp,
            ),
        titleSmall =
            TextStyle(
                fontFamily = defaultFont,
                fontWeight = FontWeight.Normal,
                fontSize = 20.sp,
                letterSpacing = 0.5.sp,
            ),
        bodyLarge =
            TextStyle(
                fontFamily = defaultFont,
                fontWeight = FontWeight.Normal,
                fontSize = 18.sp,
                letterSpacing = 0.5.sp,
            ),
        bodyMedium =
            TextStyle(
                fontFamily = defaultFont,
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
                letterSpacing = 0.5.sp,
            ),
        bodySmall =
            TextStyle(
                fontFamily = defaultFont,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                letterSpacing = 0.5.sp,
            ),
        labelLarge =
            TextStyle(
                fontFamily = defaultFont,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                letterSpacing = 0.5.sp,
            ),
        labelMedium =
            TextStyle(
                fontFamily = defaultFont,
                fontWeight = FontWeight.Normal,
                fontSize = 12.sp,
                letterSpacing = 0.5.sp,
            ),
        labelSmall =
            TextStyle(
                fontFamily = defaultFont,
                fontWeight = FontWeight.Normal,
                fontSize = 10.sp,
                letterSpacing = 0.5.sp,
            ),
    )


