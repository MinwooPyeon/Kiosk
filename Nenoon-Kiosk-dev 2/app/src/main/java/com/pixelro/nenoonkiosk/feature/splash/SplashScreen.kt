package com.pixelro.nenoonkiosk.feature.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.constants.AppConstants
import com.pixelro.nenoonkiosk.core.ui.Logo
import com.pixelro.nenoonkiosk.core.util.StringProvider

// 처음에 스플래시 화면
@Composable
fun SplashScreen() {
    val systemUiController = rememberSystemUiController()

    DisposableEffect(true) {
        systemUiController.systemBarsDarkContentEnabled = false
        onDispose {
            systemUiController.systemBarsDarkContentEnabled = true
        }
    }

    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(
                    color = Color(0xff1d71e1),
                ),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text =
                    StringProvider.getString(
                        R.string.splash_description,
                    ),
                color = Color(0xffffffff),
                fontSize = 28.sp,
                fontWeight = FontWeight.Normal,
            )
            Spacer(
                modifier =
                    Modifier
                        .height(28.dp),
            )
            Logo(true)
            Spacer(
                modifier =
                    Modifier
                        .height(16.dp),
            )
            Row(
                horizontalArrangement = Arrangement.End,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(end = 160.dp),
            ) {
                Text(
                    text = "Ver\n${AppConstants.APP_VERSION}",
                    fontSize = 20.sp,
                    color = Color.White,
                )
            }
        }
        Column(
            modifier =
                Modifier
                    .fillMaxSize(),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Image(
                modifier =
                    Modifier
                        .padding(top = 20.dp, bottom = 40.dp)
                        .height(50.dp),
                painter = painterResource(id = R.drawable.pixelro_logo),
                contentDescription = null,
            )
        }
    }
}
