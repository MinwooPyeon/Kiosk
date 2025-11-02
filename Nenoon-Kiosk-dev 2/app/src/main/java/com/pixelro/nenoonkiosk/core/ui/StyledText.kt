package com.pixelro.nenoonkiosk.core.ui

import android.content.Context
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.constants.NavConstants

enum class TextStyle {
    Title,
    Message,
    Success,
    BigNumber,
    Error,
    InputError,
    Hint,
}

@Composable
fun StyledText(
    text: String,
    style: TextStyle = TextStyle.Message,
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

    Text(
        text = text,
        fontSize = fontSize,
        textAlign = textAlign,
        fontWeight =
            fontWeight
                ?: when (style) {
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
        modifier =
            modifier
                .padding(
                    vertical =
                        when (style) {
                            TextStyle.Title -> 40.dp
                            TextStyle.InputError -> 2.dp
                            else -> 0.dp
                        },
                ),
    )
}
