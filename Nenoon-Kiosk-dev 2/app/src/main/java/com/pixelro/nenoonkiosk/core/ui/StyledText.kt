package com.pixelro.nenoonkiosk.core.ui

import android.content.Context
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pixelro.nenoonkiosk.core.constants.NavConstants
import com.pixelro.nenoonkiosk.ui.theme.Black
import com.pixelro.nenoonkiosk.ui.theme.Gray
import com.pixelro.nenoonkiosk.ui.theme.Red
import com.pixelro.nenoonkiosk.ui.theme.defaultFont
import com.pixelro.nenoonkiosk.ui.theme.neNoon_blue

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
    modifier: Modifier = Modifier,
    text: String,
    style: TextStyle = TextStyle.Message,
    textAlign: TextAlign = TextAlign.Center,
    fontWeight: FontWeight? = null,
    textDecoration: TextDecoration? = null,
    color: Color? = null,
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
            else -> 36.sp
        } * if (savedLanguage == "en") 0.8f else 1f

    val fontFamily = defaultFont

    Text(
        text = text,
        fontSize = fontSize,
        textAlign = textAlign,
        fontFamily = fontFamily,
        fontWeight =
            fontWeight
                ?: when (style) {
                    TextStyle.Title -> FontWeight.Bold
                    TextStyle.Success, TextStyle.Error, TextStyle.BigNumber -> FontWeight.Bold
                    else -> FontWeight.Normal
                },
        color =
            color ?: when (style) {
                TextStyle.Hint -> Gray
                TextStyle.Success, TextStyle.BigNumber -> neNoon_blue
                TextStyle.Error, TextStyle.InputError -> Red
                else -> Black
            },
        textDecoration = textDecoration,
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
