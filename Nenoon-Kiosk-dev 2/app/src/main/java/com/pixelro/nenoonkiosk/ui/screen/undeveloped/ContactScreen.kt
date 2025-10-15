package com.pixelro.nenoonkiosk.ui.screen.undeveloped

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.data.GlobalValue

//미개발
@Composable
fun ContactScreen(
    toIntroScreen: () -> Unit,
    toVideoTelephonyScreen: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .padding(
                    top = (GlobalValue.statusBarPadding + 20).dp,
                    bottom = 20.dp
                )
                .fillMaxWidth()
                .height(40.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .padding(start = 20.dp)
                    .fillMaxSize(),
                contentAlignment = Alignment.CenterStart
            ) {
                Image(
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .width(28.dp)
                        .clickable(
                            onClick = { toIntroScreen() }
                        ),
                    painter = painterResource(id = R.drawable.icon_back_black),
                    contentDescription = ""
                )
            }
            Text(
                textAlign = TextAlign.Center,
                text = "CONTACT",
                fontSize = 24.sp,
                fontWeight = FontWeight.Medium
            )
        }
        Spacer(
            modifier = Modifier
                .padding(bottom = 20.dp)
                .fillMaxWidth()
                .height(1.dp)
                .background(
                    color = Color(0xffdddddd)
                )
        )
        Box(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth()
                .height(300.dp)
                .background(
                    color = Color(0xff04B431)
                )
                .clickable(
                    onClick = { toVideoTelephonyScreen() }
                ),
            contentAlignment = Alignment.Center
        ) {
            Image(
                modifier = Modifier
                    .height(100.dp)
                    .width(100.dp),
                painter = painterResource(id = R.drawable.profile_icon_2),
                contentDescription = ""
            )
            Text(
                modifier = Modifier
                    .padding(top = 150.dp),
                text = "JUN",
                color = Color(0xffffffff),
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(
            modifier = Modifier
                .padding(bottom = 20.dp)
                .fillMaxWidth()
                .height(1.dp)
        )
        Box(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth()
                .height(300.dp)
                .background(
                    color = Color(0xff04B431)
                ),
            contentAlignment = Alignment.Center
        ) {
            Image(
                modifier = Modifier
                    .height(100.dp)
                    .width(100.dp),
                painter = painterResource(id = R.drawable.profile_icon_1),
                contentDescription = ""
            )
            Text(
                modifier = Modifier
                    .padding(top = 150.dp),
                text = "LISA",
                color = Color(0xffffffff),
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(
            modifier = Modifier
                .padding(bottom = 20.dp)
                .fillMaxWidth()
                .height(1.dp)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Image(
                modifier = Modifier
                    .height(80.dp)
                    .width(80.dp),
                painter = painterResource(id = R.drawable.plus_icon),
                contentDescription = ""
            )
        }
    }
}