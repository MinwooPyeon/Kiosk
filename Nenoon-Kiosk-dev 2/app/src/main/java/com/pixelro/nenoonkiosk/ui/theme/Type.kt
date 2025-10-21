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

// Custom text styles
val selectLargeTextStyle =
    TextStyle(
        fontFamily = defaultFont,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 60.sp,
        letterSpacing = 0.5.sp,
    )

val titleTextStyle =
    TextStyle(
        fontFamily = defaultFont,
        fontWeight = FontWeight.Bold,
        fontSize = 48.sp,
        letterSpacing = 0.5.sp,
    )

val bodyTextStyle =
    TextStyle(
        fontFamily = defaultFont,
        fontWeight = FontWeight.Bold,
        fontSize = 40.sp,
        letterSpacing = 0.5.sp,
    )

val buttonTextStyle =
    TextStyle(
        fontFamily = defaultFont,
        fontWeight = FontWeight.Bold,
        fontSize = 38.sp,
        letterSpacing = 0.5.sp,
    )

val inputTextStyle =
    TextStyle(
        fontFamily = defaultFont,
        fontWeight = FontWeight.Normal,
        fontSize = 36.sp,
        letterSpacing = 0.5.sp,
    )

val topBackButtonTextStyle =
    TextStyle(
        fontFamily = defaultFont,
        fontWeight = FontWeight.Normal,
        fontSize = 36.sp,
        letterSpacing = 0.5.sp,
    )

val Typography =
    Typography(
        displayLarge = selectLargeTextStyle,
        headlineLarge = titleTextStyle,
        headlineMedium = bodyTextStyle,
        titleLarge = buttonTextStyle,
        titleMedium = inputTextStyle,
        bodyLarge = inputTextStyle,
        bodyMedium = topBackButtonTextStyle,
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


