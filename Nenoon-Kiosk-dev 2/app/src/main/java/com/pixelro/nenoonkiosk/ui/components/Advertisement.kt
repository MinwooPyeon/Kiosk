package com.pixelro.nenoonkiosk.ui.components

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.constants.NavConstants

/**
 * 광고 내용
 */
@Composable
fun Advertisement(
    idx: Int
) {

    val context = LocalContext.current
    val sharedPreferences =
        remember { context.getSharedPreferences(NavConstants.PREFERENCE_NAME, Context.MODE_PRIVATE) }
    val savedLanguage =
        sharedPreferences.getString("language", "defaultLanguage")

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp),
        elevation = CardDefaults.cardElevation(0.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xffffffff)
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Image(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(
                        shape = RoundedCornerShape(8.dp)
                    ),
                painter = painterResource(id =
                    if (savedLanguage == "ko") {
                        when (idx % 2) {
                            0 -> R.drawable.ad_lens
//                    1 -> R.drawable.ad_0
                            else -> R.drawable.ad_hades
                        }
                    } else {
                        R.drawable.ad_hades_en
                    }),
                contentScale = ContentScale.FillWidth,
                contentDescription = ""
            )
        }
    }
}
