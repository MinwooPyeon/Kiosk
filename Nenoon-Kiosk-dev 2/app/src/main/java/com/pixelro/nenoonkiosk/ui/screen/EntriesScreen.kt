package com.pixelro.nenoonkiosk.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.pixelro.nenoonkiosk.NenoonViewModel
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.data.GlobalValue
import com.pixelro.nenoonkiosk.data.StringProvider
import com.pixelro.nenoonkiosk.ui.theme.cafe24Family
import com.pixelro.nenoonkiosk.ui.theme.notoSansKrFamily

//처음에 스플래시 화면
@Composable
fun EntriesScreen() {
    val systemUiController = rememberSystemUiController()

    DisposableEffect(true) {
        systemUiController.systemBarsDarkContentEnabled = false
        onDispose {
            systemUiController.systemBarsDarkContentEnabled = true
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 20.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Spacer(
            modifier = Modifier
                .height(20.dp)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ){
            Text(
//                text = StringProvider.getString(R.string.splash_description),
                text = "기재사항",
                fontSize = 42.sp,
                fontWeight = FontWeight.ExtraBold,
            )
        }
        Text(
            text = "(1) 품목명: 암슬러 격자검사 소프트웨어",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
//                text = StringProvider.getString(R.string.splash_app_name),
            text = "(2) 모델명: Screening Charts for Macular Degeneration",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
//                text = StringProvider.getString(R.string.splash_app_name),
            text = "(3) 제조업허가번호: 제8253호",
            fontSize = 26.sp,
            fontWeight = FontWeight.Light
        )
        Text(
//                text = StringProvider.getString(R.string.splash_app_name),
            text = "(4) 제조업자의 상호 및 주소",
            fontSize = 26.sp,
            fontWeight = FontWeight.Light,
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 20.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top,
        ) {
            Text(
                text = "① 상호: 주식회사 픽셀로",
                fontSize = 26.sp,
                fontWeight = FontWeight.Light
            )
            Spacer(
                modifier = Modifier
                    .height(10.dp)
            )
            Text(
//                text = StringProvider.getString(R.string.splash_app_name),
                text = "② 주소: 경기도 성남시 분당구 야탑로205번길 26, 324호(야탑동, 성남 시니어산업혁신센터)",
                fontSize = 26.sp,
                fontWeight = FontWeight.Light
            )
        }
        Text(
//                text = StringProvider.getString(R.string.splash_app_name),
            text = "(5) 품목허가번호 : 제허 23-1122호(조건부)",
            fontSize = 26.sp,
            fontWeight = FontWeight.Light,
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 80.dp, end = 10.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top,
        ) {
            Text(
                text = "(6) 사용목적 : 격자 형태의 표를 제시하여 시야의 중앙 및 중앙부 불규칙성을 검사하기 위해 사용하는 소프트웨어",
                fontSize = 26.sp,
                fontWeight = FontWeight.Light
            )
        }
        Text(
//                text = StringProvider.getString(R.string.splash_app_name),
            text = "(7) 제조번호 : 제조 후 별도 기재",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
        )
        Text(
//                text = StringProvider.getString(R.string.splash_app_name),
            text = "\t제조연원일 : 제조 후 별도 기재",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
        )
        Text(
//                text = StringProvider.getString(R.string.splash_app_name),
            text = "(8) 성능 및 사용방법: 사용설명서 참조",
            fontSize = 26.sp,
            fontWeight = FontWeight.Light,
        )
        Text(
//                text = StringProvider.getString(R.string.splash_app_name),
            text = "(9) 사용 시 주의사항 : 사용설명서 참조",
            fontSize = 26.sp,
            fontWeight = FontWeight.Light,
        )
        Text(
//                text = StringProvider.getString(R.string.splash_app_name),
            text = "(10) 보관 또는 저장방법 : 사용설명서 참조",
            fontSize = 26.sp,
            fontWeight = FontWeight.Light,
        )
        Text(
//                text = StringProvider.getString(R.string.splash_app_name),
            text = "(11) 소프트웨어 명칭 및 버전",
            fontSize = 26.sp,
            fontWeight = FontWeight.Light,
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top,
        ) {
            Text(
                text = "1) 명칭 : 내눈 암슬러차트 검사",
                fontSize = 26.sp,
                fontWeight = FontWeight.Light
            )
            Spacer(
                modifier = Modifier
                    .height(10.dp)
            )
            Text(
//                text = StringProvider.getString(R.string.splash_app_name),
                text = "2) 버전 : 1.0",
                fontSize = 26.sp,
                fontWeight = FontWeight.Light
            )
        }
        Text(
//                text = StringProvider.getString(R.string.splash_app_name),
            text = "(12) 본 제품은 의료기기임.",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
        )
        Text(
//                text = StringProvider.getString(R.string.splash_app_name),
            text = "(13) 의료기기 표준코드(UDI) : (01)8809685071280",
            fontSize = 26.sp,
            fontWeight = FontWeight.Light,
        )
        Spacer(
            modifier = Modifier
                .height(20.dp)
        )

    }
}