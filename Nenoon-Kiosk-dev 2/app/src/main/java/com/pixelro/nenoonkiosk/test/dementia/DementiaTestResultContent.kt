package com.pixelro.nenoonkiosk.test.dementia

//import androidx.compose.foundation.layout.RowScopeInstance.weight
import android.view.ViewGroup
import android.webkit.WebViewClient
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.data.StringProvider

@Composable
fun DementiaTestResultContent(
    testResult: DementiaTestResult,
    navController: NavHostController
) {
    val isWebViewShowing1 = remember { mutableStateOf(false) }
    val showDementia333 = remember { mutableStateOf(false) }

    when {
        isWebViewShowing1.value -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        color = Color(0xff144AAE)
                    )
            ) {
                Box(
                    modifier = Modifier
                        .padding(
                            start = 40.dp,
                            top = 20.dp,
                            end = 40.dp,
                            bottom = 20.dp
                        )
                        .fillMaxWidth()
                        .height(40.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxHeight()
                            .clickable {
                                isWebViewShowing1.value = false
                            },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            modifier = Modifier
                                .padding(top = 4.dp)
                                .width(28.dp),
                            painter = painterResource(id = R.drawable.icon_back_white),
                            contentDescription = ""
                        )
                        Text(
                            text = StringProvider.getString(R.string.back, ),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xffffffff)
                        )
                    }
                }
                TheContent( "https://m.nid.or.kr/main/main.aspx" )
            }
        }

        showDementia333.value -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        color = Color(0xffffffff)
                    )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable {
                            showDementia333.value = false
                        },
                    contentAlignment = Alignment.TopCenter
                ) {
                    Image(
                        modifier = Modifier
                            .fillMaxSize(),
                        painter = painterResource(id = R.drawable.dementia_2),
                        contentDescription = ""
                    )
                }
            }
        }

        else -> {
            Column {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 65.dp, vertical = 20.dp)
                ) {
                    Text(
                        text = StringProvider.getString(
                            R.string.dementia_result_instruction),
                        color = Color(0xff1d71e1),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Column(
                    modifier = Modifier
                        .padding(top = 10.dp, bottom = 10.dp, start = 20.dp, end = 20.dp)
                        .fillMaxWidth()
                        .background(
                            color = Color(0xfff7f7f7),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(40.dp)

                ) {
                    Text(
                        text = StringProvider.getString(
                            R.string.dementia_result_wording1) + " " + testResult.countActiveScore()
                            .toString() + StringProvider.getString(
                            R.string.dementia_result_wording2),
                        fontSize = 40.sp,
                        fontWeight = FontWeight.Medium,
                    )
                }
            }
        }
    }
}

@Composable
fun TheContent(
    mUrl: String
) {
    AndroidView(factory = {
        android.webkit.WebView(it).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            webViewClient = WebViewClient()
            loadUrl(mUrl)
        }
    }, update = {
        it.loadUrl(mUrl)
    })
}

