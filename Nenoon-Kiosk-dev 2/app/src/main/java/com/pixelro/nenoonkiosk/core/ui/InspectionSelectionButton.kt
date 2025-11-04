package com.pixelro.nenoonkiosk.core.ui

import android.content.Context
import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
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
        remember {
            context.getSharedPreferences(
                NavConstants.PREFERENCE_NAME,
                Context.MODE_PRIVATE
            )
        }
    val savedLanguage = sharedPreferences.getString("language", "defaultLanguage")

    val isLandscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE

    val boxTimeTextSize = if (savedLanguage in listOf("ru", "en")) 16.sp else 20.sp
    val approximateTextSize = if (savedLanguage in listOf("ru", "en")) 24.sp else 30.sp
    val textSize = getDynamicFontSize(isSenior, large, savedLanguage, time).sp

    // 그라데이션: isDone일 때만 적용
    val gradientColors = if (isDone) {
        listOf(Color(0xFFFAFAFA), Color(0xFFF0F0F0))
    } else {
        listOf(Color(0xFFFFFFFF), Color(0xFFFFFFFF))
    }

    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        modifier = modifier
            .padding(horizontal = if (isLandscape) 0.dp else 40.dp)
            .fillMaxWidth()
            .border(
                border = BorderStroke(1.dp, LightGray),
                shape = RoundedCornerShape(8.dp),
            )
            .shadow(
                elevation = 3.dp,
                shape = RoundedCornerShape(8.dp),
                ambientColor = Color.Black.copy(alpha = 0.25f),
                spotColor = Color.Black.copy(alpha = 0.2f)
            ),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(colors = gradientColors),
                    shape = RoundedCornerShape(8.dp)
                )
        ) {
            if (isLandscape) {
                // 가로 모드: 왼쪽 텍스트, 오른쪽 시간 정보
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp, vertical = 8.dp)
                        .clickable(enabled = enabled) { onClickMethod() },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // 왼쪽: 제목
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxSize(),
                        horizontalAlignment = Alignment.Start,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = title1,
                            fontFamily = defaultFont,
                            fontSize = textSize * 0.75f,
                            fontWeight = FontWeight.Bold,
                            color = if (enabled) Color.Black else LightGray,
                            textAlign = TextAlign.Start
                        )
                        if (title2.isNotEmpty()) {
                            Text(
                                text = title2,
                                fontFamily = defaultFont,
                                fontSize = textSize * 0.65f,
                                fontWeight = FontWeight.Medium,
                                color = Color.Black,
                                textAlign = TextAlign.Start
                            )
                        }
                    }

                    // 오른쪽: 시간 정보
                    if (time != 0) {
                        Row(
                            modifier = Modifier
                                .weight(0.8f)
                                .fillMaxSize(),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                horizontalAlignment = Alignment.End,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = stringResource(R.string.box_time_required),
                                    fontFamily = defaultFont,
                                    fontSize = boxTimeTextSize * 0.75f,
                                    textAlign = TextAlign.Center,
                                    fontWeight = FontWeight.Medium,
                                )
                                Text(
                                    text = stringResource(R.string.box_approximate) +
                                            " $time " + stringResource(R.string.box_minute),
                                    fontFamily = defaultFont,
                                    fontSize = approximateTextSize * 0.75f,
                                    textAlign = TextAlign.Center,
                                    fontWeight = FontWeight.Bold,
                                )
                            }

                            Image(
                                modifier = Modifier
                                    .padding(start = 12.dp)
                                    .rotate(180f)
                                    .size(24.dp),
                                painter = painterResource(id = R.drawable.icon_back_black),
                                contentDescription = null
                            )
                        }
                    }
                }
            } else {
                // 세로 모드: 원본 스타일 유지
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
    }
}

@Preview(
    showBackground = true,
    name = "Inspection Button - Title1 & Title2 / 세로모드",
    widthDp = 850,
    heightDp = 140
)
@Composable
fun InspectionSelectionButtonPreview_Portrait() {
    NenoonKioskTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(neNoon_blue)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            InspectionSelectionButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                large = false,
                alignment = Alignment.CenterStart,
                title1 = "근거리 시력",
                title2 = "약 2분",
                onClickMethod = {},
                isDone = false,
                isSenior = false,
                time = 2,
                icon = null,
                enabled = true
            )
        }
    }
}

@Preview(
    showBackground = true,
    name = "Inspection Button - Title1 & Title2 / 가로모드",
    widthDp = 280,
    heightDp = 90
)
@Composable
fun InspectionSelectionButtonPreview_Landscape() {
    NenoonKioskTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(neNoon_blue)
                .padding(4.dp),
            contentAlignment = Alignment.Center
        ) {
            InspectionSelectionButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp),
                large = false,
                alignment = Alignment.CenterStart,
                title1 = "근거리 시력",
                title2 = "약 2분",
                onClickMethod = {},
                isDone = false,
                isSenior = false,
                time = 2,
                icon = null,
                enabled = true
            )
        }
    }
}

@Preview(
    showBackground = true,
    name = "Inspection Button - isDone True / 가로모드",
    widthDp = 280,
    heightDp = 90
)
@Composable
fun InspectionSelectionButtonPreview_Landscape_Done() {
    NenoonKioskTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(neNoon_blue)
                .padding(4.dp),
            contentAlignment = Alignment.Center
        ) {
            InspectionSelectionButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp),
                large = false,
                alignment = Alignment.CenterStart,
                title1 = "근거리 시력",
                title2 = "약 2분",
                onClickMethod = {},
                isDone = true,
                isSenior = false,
                time = 2,
                icon = null,
                enabled = true
            )
        }
    }
}
