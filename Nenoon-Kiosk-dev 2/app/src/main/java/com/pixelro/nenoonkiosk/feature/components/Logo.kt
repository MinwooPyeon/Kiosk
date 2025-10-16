package com.pixelro.nenoonkiosk.feature.components

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.constants.NavConstants

@Composable
fun Logo(
    white: Boolean = false,
    modifier: Modifier = Modifier,
) {

    val context = LocalContext.current
    val sharedPreferences =
        remember { context.getSharedPreferences(NavConstants.PREFERENCE_NAME, Context.MODE_PRIVATE) }
    val savedLanguage =
        sharedPreferences.getString("language", "defaultLanguage")

    Image(
        modifier = modifier.width(600.dp),
        painter = painterResource(id =
            if (savedLanguage == "ko") {
                if (white) R.drawable.nenoon_logo_v2_invisible_white
                else R.drawable.nenoon_logo_v2_invisible
            } else {
                if (white) R.drawable.nenoon_logo_v2_invisible_white_en
                else R.drawable.nenoon_logo_v2_invisible_en
            }
        ),
        contentDescription = ""
    )
}