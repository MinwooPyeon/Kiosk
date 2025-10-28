package com.pixelro.nenoonkiosk.core.ui

import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.constants.NavConstants
import com.pixelro.nenoonkiosk.ui.theme.LightGray
import com.pixelro.nenoonkiosk.ui.theme.NenoonKioskTheme
import com.pixelro.nenoonkiosk.ui.theme.White
import com.pixelro.nenoonkiosk.ui.theme.defaultFont
import com.pixelro.nenoonkiosk.ui.theme.neNoon_blue

const val LARGE_SCALE = 1.5f

private fun getDynamicFontSize(
    isSenior: Boolean,
    large: Boolean,
    savedLanguage: String?,
    time: Int
): Float {
    val base = when {
        savedLanguage in listOf("ru", "es", "en") -> if (time != 0) 35f else 25f
        isSenior -> 50f
        else -> 40f
    }
    return base * if (large && savedLanguage !in listOf("ru", "es", "en")) LARGE_SCALE else 1f
}

@Composable
fun InspectionSelectionButton(
    modifier: Modifier,
    large: Boolean = false,
    alignment: Alignment,
    title1: String,
    title2: String,
    onClickMethod: () -> Unit,
    isDone: Boolean,
    isSenior: Boolean,
    time: Int,
    icon: Int? = null,
    enabled: Boolean = true,
) {
    val context = LocalContext.current
    val sharedPreferences =
        remember { context.getSharedPreferences(NavConstants.PREFERENCE_NAME, Context.MODE_PRIVATE) }
    val savedLanguage = sharedPreferences.getString("language", "defaultLanguage")

    val boxTimeTextSize = if (savedLanguage in listOf("ru", "en")) 16.sp else 20.sp
    val approximateTextSize = if (savedLanguage in listOf("ru", "en")) 24.sp else 30.sp
    val textSize = getDynamicFontSize(isSenior, large, savedLanguage, time).sp

    Card(
        elevation = CardDefaults.cardElevation(0.dp),
        modifier = modifier
            .padding(horizontal = 40.dp)
            .fillMaxWidth()
            .border(
                border = BorderStroke(1.dp, LightGray),
                shape = RoundedCornerShape(8.dp),
            ),
        colors = CardDefaults.cardColors(containerColor = White),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 40.dp)
                .clickable(enabled = enabled) { onClickMethod() },
            contentAlignment = alignment,
        ) {
            Text(
                text = buildString {
                    append(title1)
                    append(title2)
                },
                fontFamily = defaultFont,
                fontSize = textSize,
                fontWeight = FontWeight.Bold,
                color = if (enabled) Color.Black else LightGray,
                textAlign = TextAlign.Center,
                modifier = Modifier.align(
                    if (icon != null) Alignment.CenterEnd else Alignment.CenterStart
                )
            )

            icon?.let {
                Image(
                    modifier = Modifier
                        .size(80.dp)
                        .align(Alignment.CenterStart),
                    painter = painterResource(id = it),
                    contentDescription = null,
                    contentScale = ContentScale.Crop
                )
            }

            if (time != 0) {
                Image(
                    modifier = Modifier
                        .padding(end = 40.dp)
                        .rotate(180f)
                        .align(Alignment.CenterEnd),
                    painter = painterResource(id = R.drawable.icon_back_black),
                    contentDescription = null
                )

                Column(
                    modifier = Modifier
                        .padding(end = 120.dp)
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        modifier = Modifier.width(150.dp),
                        text = stringResource(R.string.box_time_required),
                        fontFamily = defaultFont,
                        fontSize = boxTimeTextSize * (if (large) LARGE_SCALE else 1f),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Medium,
                    )
                    Text(
                        modifier = Modifier.width(150.dp),
                        text = stringResource(R.string.box_approximate) +
                                " $time " + stringResource(R.string.box_minute),
                        fontFamily = defaultFont,
                        fontSize = approximateTextSize * (if (large) LARGE_SCALE else 1f),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "Inspection Button - Icon only / title2 없음", widthDp = 850, heightDp = 140)
@Composable
fun InspectionSelectionButtonPreview_IconOnly() {
    NenoonKioskTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(neNoon_blue) // 전체 배경 파란색
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            InspectionSelectionButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                large = false,
                alignment = Alignment.CenterStart,
                title1 = stringResource(R.string.eye_test), // 예시 텍스트
                title2 = "", // 🔹 title2 없음
                onClickMethod = {},
                isDone = false,
                isSenior = false,
                time = 0,
                icon = R.drawable.eye_test_icon, // 🔹 아이콘 표시
                enabled = true
            )
        }
    }
}
