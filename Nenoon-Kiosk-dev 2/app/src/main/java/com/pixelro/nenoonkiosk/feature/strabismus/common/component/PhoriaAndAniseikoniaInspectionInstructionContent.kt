package com.pixelro.nenoonkiosk.feature.strabismus.common.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.ui.HowToButton
import com.pixelro.nenoonkiosk.core.ui.NenoonTopBar
import com.pixelro.nenoonkiosk.core.ui.PrimaryButton
import com.pixelro.nenoonkiosk.core.ui.SettingsButton
import com.pixelro.nenoonkiosk.core.ui.TopBarOrientation
import com.pixelro.nenoonkiosk.core.ui.TopBarVertical
import com.pixelro.nenoonkiosk.core.util.StringProvider
import com.pixelro.nenoonkiosk.feature.strabismus.aniseikonia.howtodialog.AniseikoniaHowToDialog
import com.pixelro.nenoonkiosk.feature.strabismus.phoria.howtodialog.PhoriaHowToDialog
import com.pixelro.nenoonkiosk.ui.theme.Black
import com.pixelro.nenoonkiosk.ui.theme.NenoonKioskTheme
import com.pixelro.nenoonkiosk.ui.theme.White

/**
 * 검사 안내 화면 공통 컴포넌트
 *
 * @param testType 검사 타입 (phoria/aniseikonia)
 * @param texts 안내 텍스트 목록 (여러 줄 가능)
 * @param imageRes 표시할 이미지 리소스 ID
 * @param onBackClicked 뒤로가기 버튼 클릭 콜백
 * @param bottomBar 하단 버튼 영역
 */
@Composable
fun PhoriaAndAniseikoniaInspectionInstructionContent(
    testType: String,
    texts: List<String>,
    imageRes: Int,
    onBackClicked: () -> Unit,
    bottomBar: @Composable () -> Unit,
) {
    val headerTitle =
        when (testType) {
            "phoria" -> StringProvider.getStringComposable(R.string.sawi_question_title)
            "aniseikonia" -> StringProvider.getStringComposable(R.string.fudo_question_title)
            else -> StringProvider.getStringComposable(R.string.common_test_title)
        }

    var showHowToDialog by remember { mutableStateOf(false) }

    if (showHowToDialog) {
        if (testType == "phoria") {
            PhoriaHowToDialog(onDismissRequest = { showHowToDialog = false })
        } else {
            AniseikoniaHowToDialog(onDismissRequest = { showHowToDialog = false })
        }
    }

    Scaffold(
        containerColor = Color.Black,
        topBar = {
            TopBarVertical(
                title = headerTitle,
                showBackButton = true,
                onBackClicked = onBackClicked,
                actions = {
                    HowToButton(onClick = { showHowToDialog = true })
                },
                containerColor = Color.Black,
                contentColor = Color.White,
            )
        },
        bottomBar = bottomBar,
    ) { paddingValues ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Color.Black)
                    .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(40.dp))
            texts.forEach { text ->
                Text(
                    text = text,
                    color = Color.White,
                    fontSize = 42.sp,
                    textAlign = TextAlign.Center,
                )
            }
            Spacer(modifier = Modifier.height(50.dp))
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = null,
                modifier =
                    Modifier
                        .width(1000.dp)
                        .height(1100.dp),
            )
        }
    }
}

@Preview(showBackground = true, device = "spec:width=1280dp,height=800dp,dpi=240")
@Composable
private fun InspectionInstructionScreenHorizontalPreview() {
    NenoonKioskTheme {
        PhoriaAndAniseikoniaInspectionInstructionContent(
            testType = "phoria",
            texts = listOf("검사 안내 화면 예시", "두 번째 줄 텍스트"),
            imageRes = R.drawable.glassimage,
            onBackClicked = {},
            bottomBar = {
                Column(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .background(Color.Black)
                            .padding(start = 24.dp, end = 24.dp, top = 24.dp, bottom = 24.dp),
                ) {
                    PrimaryButton(
                        onClick = {},
                        text = "다음"
                    )
                }
            }
        )
    }
}

@Preview(showBackground = true, device = "spec:width=800dp,height=1280dp,dpi=240")
@Composable
private fun InspectionInstructionContentVerticalPreview() {
    NenoonKioskTheme {
        PhoriaAndAniseikoniaInspectionInstructionContent(
            testType = "aniseikonia",
            texts = listOf("검사 안내 화면 예시"),
            imageRes = R.drawable.eyefilterimage,
            onBackClicked = {},
            bottomBar = {
                Column(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .background(Color.Black)
                            .padding(start = 24.dp, end = 24.dp, top = 24.dp, bottom = 24.dp),
                ) {
                    PrimaryButton(
                        onClick = {},
                        text = "다음"
                    )
                }
            }
        )
    }
}