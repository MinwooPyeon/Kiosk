package com.pixelro.nenoonkiosk.feature.inspection.dementia

import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.util.StringProvider

@Composable
fun DementiaTestResultContent(
    testResult: DementiaTestResult,
    navController: NavHostController,
) {
    var isWebViewShowing1 by rememberSaveable { mutableStateOf(false) }
    var showDementia333 by rememberSaveable { mutableStateOf(false) }

    when {
        isWebViewShowing1 -> {
            WebContainer(
                onBack = { isWebViewShowing1 = false }
            ) {
                TheContent("https://m.nid.or.kr/main/main.aspx")
            }
        }

        showDementia333 -> {
            GuideImageContainer(
                imageRes = R.drawable.dementia_2,
                onClose = { showDementia333 = false }
            )
        }

        else -> {
            Column {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 65.dp, vertical = 20.dp),
                ) {
                    Text(
                        text = StringProvider.getString(R.string.dementia_result_instruction),
                        color = Color(0xff1d71e1),
                        fontSize = 24.sp,
                    )
                }

                Column(
                    modifier = Modifier
                        .padding(top = 10.dp, bottom = 10.dp, start = 20.dp, end = 20.dp)
                        .fillMaxWidth()
                        .background(color = Color(0xfff7f7f7), shape = RoundedCornerShape(8.dp))
                        .padding(40.dp),
                ) {
                    Text(
                        text = StringProvider.getString(R.string.dementia_result_wording1) + " " +
                                testResult.countActiveScore().toString() +
                                StringProvider.getString(R.string.dementia_result_wording2),
                        fontSize = 40.sp,
                    )
                }

                // (원 코드상 여기엔 추가 버튼/이미지가 없었음.
                //  만약 웹/가이드 노출 트리거가 필요하면 동일한 스타일의 요소를 여기에 추가하세요.
                //  디자인 고정 요구로 기본 출력만 유지합니다.)
            }
        }
    }
}

@Composable
private fun WebContainer(
    onBack: () -> Unit,
    content: @Composable () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color(0xff144AAE)),
    ) {
        Box(
            modifier = Modifier
                .padding(start = 40.dp, top = 20.dp, end = 40.dp, bottom = 20.dp)
                .fillMaxWidth()
                .height(40.dp),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxHeight()
                    .clickable { onBack() },
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Image(
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .width(28.dp),
                    painter = painterResource(id = R.drawable.icon_back_white),
                    contentDescription = "",
                )
                Text(
                    text = StringProvider.getString(R.string.back),
                    fontSize = 24.sp,
                    color = Color(0xffffffff),
                )
            }
        }
        content()
    }
}

@Composable
private fun GuideImageContainer(
    imageRes: Int,
    onClose: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color(0xffffffff)),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable { onClose() },
            contentAlignment = Alignment.TopCenter,
        ) {
            Image(
                modifier = Modifier.fillMaxSize(),
                painter = painterResource(id = imageRes),
                contentDescription = "",
            )
        }
    }
}

@Composable
fun TheContent(mUrl: String) {
    AndroidView(
        factory = { ctx ->
            WebView(ctx).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                )
                webViewClient = WebViewClient()
                loadUrl(mUrl)
            }
        },
        update = { it.loadUrl(mUrl) }
    )
}
