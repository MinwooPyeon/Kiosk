package com.pixelro.nenoonkiosk.feature.screen.undeveloped

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.pixelro.nenoonkiosk.R

//미개발
@Composable
fun VideoTelephonyScreen(
    toContactScreen: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Image(
            modifier = Modifier
                .fillMaxSize(),
            painter = painterResource(id = R.drawable.jun),
            contentDescription = ""
        )
        Image(
            modifier = Modifier
                .height(150.dp)
                .width(150.dp)
                .offset(200.dp, 1120.dp),
            painter = painterResource(id = R.drawable.mute_icon),
            contentDescription = ""
        )
        Image(
            modifier = Modifier
                .height(150.dp)
                .width(150.dp)
                .offset(450.dp, 1120.dp)
                .clickable(
                    onClick = { toContactScreen() }
                ),
            painter = painterResource(id = R.drawable.end_icon),
            contentDescription = ""
        )
    }
}