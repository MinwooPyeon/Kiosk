package com.pixelro.nenoonkiosk.feature.components

import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.constants.NavConstants
import com.pixelro.nenoonkiosk.util.StringProvider
import com.pixelro.nenoonkiosk.feature.theme.nanumSquareNeoFamily


const val LARGE_SCALE = 1.5f

@Composable
fun TestSelectionButton(
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
    val savedLanguage =
        sharedPreferences.getString("language", "defaultLanguage")
    val boxTimeTextSize = if (savedLanguage == "ru" || savedLanguage == "en") 16.sp else 20.sp
    val approximateTextSize = if (savedLanguage == "ru" || savedLanguage == "en") 24.sp else 30.sp
    Card(
        elevation = CardDefaults.cardElevation(0.dp),
        modifier = modifier
            .padding(start = 40.dp, end = 40.dp)
            .fillMaxWidth()
            .border(
                border = BorderStroke(1.dp, Color(0xffc3c3c3)),
                shape = RoundedCornerShape(8.dp)
            ),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFFFFFF),
        ),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(
                    enabled = enabled
                ) {
                    onClickMethod()
                },
            contentAlignment = alignment
        ) {
            Text(
                text = buildAnnotatedString {
                    /**
                     * 제목 1
                     * 검은색
                     */
                    withStyle(
                        style = SpanStyle(color = Color(if (enabled) 0xff000000 else 0xffaaaaaa))
                    ) {
                        append(title1)
                    }

                    /**
                     * 제목 2
                     * 파란색
                     */
                    withStyle(
                        style = SpanStyle(color = Color(0xff1d71e1))
                    ) {
                        append(title2)
                    }
                },
                modifier = Modifier
                    .align(
                        if (icon != null) Alignment.CenterEnd
                        else Alignment.CenterStart
                    )
                    .padding(
                        start =
                            if (time != 0) 28.dp
                            else 0.dp,
                        end =
                            if (icon != null) 40.dp
                            else 0.dp
                    ),
                fontSize = when (isSenior) {
                    true ->
                        if (savedLanguage == "ru" || savedLanguage == "es" || savedLanguage == "en")
                            if (time != 0) 35.sp
                            else 25.sp
                        else 50.sp * (if (large) LARGE_SCALE else 1f)

                    false ->
                        if (savedLanguage == "ru" || savedLanguage == "es" || savedLanguage == "en")
                            if (time != 0) 35.sp
                            else 25.sp
                        else 40.sp * (if (large) LARGE_SCALE else 1f)
                },
                fontWeight = FontWeight.ExtraBold,
                color = when (isDone) {
                    true -> Color(0xff999999)
                    false -> Color(0xff000000)
                },
                fontFamily = nanumSquareNeoFamily,
            )

            if (icon != null) {
                Image(
                    modifier = Modifier
                        .width(200.dp)
                        .padding(vertical = 10.dp)
                        .align(Alignment.CenterStart),
                    painter = painterResource(id = icon),
                    contentDescription = null
                )
            }

            if (time != 0) {
                Image(
                    modifier = Modifier
                        .padding(end = 40.dp)
                        .rotate(180f)
                        .align(Alignment.CenterEnd),
                    painter = painterResource(id = R.drawable.icon_back_black),
                    contentDescription = ""
                )
            }
            when (time) {
                0 -> {}
                else -> Column(
                    modifier = Modifier
                        .padding(end = 120.dp)
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        modifier = Modifier
                            .width(150.dp),
                        text = StringProvider.getString(
                            R.string.box_time_required),
                        fontSize = boxTimeTextSize * (if (large) LARGE_SCALE else 1f),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        modifier = Modifier
                            .width(150.dp),
                        text = StringProvider.getString(
                            R.string.box_approximate) + " ${time} " + StringProvider.getString(
                            R.string.box_minute),
                        fontSize = approximateTextSize * (if (large) LARGE_SCALE else 1f),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

        }
    }
}