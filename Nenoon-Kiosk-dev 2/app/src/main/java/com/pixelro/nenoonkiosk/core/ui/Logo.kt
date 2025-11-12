package com.pixelro.nenoonkiosk.core.ui

import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.pixelro.nenoonkiosk.R

@Composable
fun Logo(
    white: Boolean = false,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    // SettingsScreen에서 설정한 언어를 AppCompatDelegate로부터 가져오기
    val savedLanguage = remember {
        val appLocale = AppCompatDelegate.getApplicationLocales()
        if (appLocale.isEmpty) {
            "ko"
        } else {
            appLocale[0]?.toLanguageTag() ?: "ko"
        }
    }

    Image(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 40.dp),
        painter =
            painterResource(
                id =
                    if (savedLanguage == "ko") {
                        if (white) {
                            R.drawable.nenoon_logo_v2_invisible_white
                        } else {
                            R.drawable.nenoon_logo_v2_invisible
                        }
                    } else {
                        if (white) {
                            R.drawable.nenoon_logo_v2_invisible_white_en
                        } else {
                            R.drawable.nenoon_logo_v2_invisible_en
                        }
                    },
            ),
        contentDescription = "",
    )
}
