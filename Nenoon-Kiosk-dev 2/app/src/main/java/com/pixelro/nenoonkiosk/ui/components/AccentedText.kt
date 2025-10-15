package com.pixelro.nenoonkiosk.ui.components

import android.content.Context
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.constants.NavConstants

enum class AccentStyle {
    Blue,
    Red,
}

const val ACCENT_SCALE = 1.0f

@Composable
fun AccentedText (
    prefix: String,
    accent: String,
    suffix: String,
    style: TextStyle = TextStyle.Message,
    accentStyle: AccentStyle = AccentStyle.Red,
    textAlign: TextAlign = TextAlign.Center,
    fontWeight: FontWeight? = null,
    modifier: Modifier = Modifier,
) {

    val context = LocalContext.current
    val sharedPreferences = remember { context.getSharedPreferences(NavConstants.PREFERENCE_NAME, Context.MODE_PRIVATE) }
    val savedLanguage = sharedPreferences.getString("language", "defaultLanguage")

    val fontSize =
        when (style) {
            TextStyle.BigNumber -> 256.sp
            TextStyle.Title -> 64.sp
            TextStyle.Hint -> 36.sp
            TextStyle.InputError -> 24.sp
            else -> 42.sp
        } * if (savedLanguage == "en") 0.8f else 1f

    val defaultStyle = SpanStyle(
        fontSize = fontSize,
        fontWeight =
            fontWeight ?:
            when (style) {
                TextStyle.Title -> FontWeight.SemiBold
                TextStyle.Success, TextStyle.Error, TextStyle.BigNumber -> FontWeight.Bold
                else -> FontWeight.Normal
            },
        color =
            when (style) {
                TextStyle.Hint -> colorResource(R.color.gray2)
                TextStyle.Success, TextStyle.BigNumber -> colorResource(R.color.main)
                TextStyle.Error, TextStyle.InputError -> colorResource(R.color.error)
                else -> colorResource(R.color.black)
            },
    )

    Text(
        text = buildAnnotatedString {
            withStyle(defaultStyle) {
                append(prefix)
            }
            withStyle(SpanStyle(
                fontSize = defaultStyle.fontSize * ACCENT_SCALE,
                fontWeight = FontWeight.Bold,
                color =
                    when (accentStyle) {
                        AccentStyle.Red -> colorResource(R.color.error)
                        AccentStyle.Blue -> colorResource(R.color.main)
                    }
            )) {
                append(accent)
            }
            withStyle(defaultStyle) {
                append(suffix)
            }
        },
        textAlign = textAlign,
        modifier = modifier
            .padding(
                vertical = when (style) {
                    TextStyle.Title -> 40.dp
                    TextStyle.InputError -> 2.dp
                    else -> 0.dp
                }
            )
    )
}