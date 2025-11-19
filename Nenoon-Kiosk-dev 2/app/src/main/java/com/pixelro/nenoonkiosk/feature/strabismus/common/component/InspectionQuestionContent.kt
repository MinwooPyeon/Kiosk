package com.pixelro.nenoonkiosk.feature.strabismus.common.component

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pixelro.nenoonkiosk.core.ui.HowToButton
import com.pixelro.nenoonkiosk.core.ui.NenoonTopBar
import com.pixelro.nenoonkiosk.core.ui.PrimaryButton
import com.pixelro.nenoonkiosk.core.ui.TopBarOrientation
import com.pixelro.nenoonkiosk.core.ui.TopBarVertical
import com.pixelro.nenoonkiosk.ui.theme.Black
import com.pixelro.nenoonkiosk.ui.theme.NenoonKioskTheme
import com.pixelro.nenoonkiosk.ui.theme.White

/**
 * 검사 질문 라디오 옵션 선택 화면 공통 컴포넌트
 *
 * @param title 화면 제목
 * @param mainText 질문 텍스트
 * @param options 선택 옵션 리스트
 * @param centerContent 중앙 컨텐츠 (Canvas 또는 Image)
 * @param nextButtonText 다음 버튼 텍스트
 * @param onNextClicked 다음 버튼 클릭 콜백 (선택된 옵션 인덱스, 1-base)
 * @param onBackClicked 뒤로가기 버튼 클릭 콜백
 * @param onShowHowToClicked 도움말 버튼 클릭 콜백
 */
@Composable
fun InspectionQuestionContent(
    title: String,
    mainText: String,
    options: List<String>,
    centerContent: @Composable () -> Unit,
    nextButtonText: String,
    onNextClicked: (Int) -> Unit,
    onBackClicked: () -> Unit,
    onShowHowToClicked: () -> Unit,
) {
    var selectedOption by remember { mutableStateOf<Int?>(null) }
    val landscape = isLandscape()

    Scaffold(
        containerColor = Color.Black,
        topBar = {
            TopBarVertical(
                title = title,
                showBackButton = true,
                onBackClicked = onBackClicked,
                actions = { HowToButton(onClick = onShowHowToClicked) },
                containerColor = Color.Black,
                contentColor = Color.White,
            )
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Black)
                    .padding(horizontal = 24.dp, vertical = 24.dp),
            ) {
                PrimaryButton(
                    onClick = { selectedOption?.let { onNextClicked(it) } },
                    text = nextButtonText,
                    enabled = selectedOption != null,
                )
            }
        },
    ) { paddingValues ->
        if (landscape) {
            // ===== 가로 레이아웃 =====
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Color.Black)
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 왼쪽: 센터 컨텐츠 (여유있게 확장)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(Color.Black),
                    contentAlignment = Alignment.Center
                ) {
                    // 필요 시 컨텐츠가 너무 크지 않도록 maxWidth/Height 제한
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .fillMaxHeight(0.9f),
                        contentAlignment = Alignment.Center
                    ) {
                        centerContent()
                    }
                }

                // 오른쪽: 질문 + 옵션 (스크롤)
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = mainText,
                        color = Color.White,
                        fontSize = 40.sp, // 가로에서는 살짝 줄임
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 8.dp)
                    )

                    Spacer(Modifier.height(16.dp))

                    options.forEachIndexed { index, text ->
                        OptionRadioButton(
                            text = text,
                            selected = selectedOption == (index + 1),
                            onClick = { selectedOption = index + 1 },
                        )
                        Spacer(Modifier.height(8.dp))
                    }

                    Spacer(Modifier.height(24.dp))
                }
            }
        } else {
            // ===== 세로 레이아웃 =====
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Color.Black)
                    .padding(horizontal = 24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(16.dp))

                // centerContent
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    centerContent()
                }

                Spacer(Modifier.height(24.dp))

                Text(
                    text = mainText,
                    color = Color.White,
                    fontSize = 48.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(24.dp))

                options.forEachIndexed { index, text ->
                    OptionRadioButton(
                        text = text,
                        selected = selectedOption == (index + 1),
                        onClick = { selectedOption = index + 1 },
                    )
                    Spacer(Modifier.height(8.dp))
                }

                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

/** 현재 기기가 가로 모드인지 여부 */
@Composable
fun isLandscape(): Boolean {
    val c = LocalConfiguration.current
    return c.orientation == Configuration.ORIENTATION_LANDSCAPE ||
            c.screenWidthDp > c.screenHeightDp
}

// ------------------ Previews ------------------

@Preview(showBackground = true, device = "spec:width=1280dp,height=800dp,dpi=240", name = "Landscape")
@Composable
private fun InspectionQuestionScreenHorizontalPreview() {
    NenoonKioskTheme {
        InspectionQuestionContent(
            title = "검사 질문",
            mainText = "어떤 항목이 보이나요?",
            options = listOf("정상적으로 보임", "조정이 필요함", "왼쪽만 보임", "오른쪽만 보임"),
            centerContent = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.8f)
                        .background(Color.Gray),
                    contentAlignment = Alignment.Center
                ) {
                    Text("검사 이미지", color = Color.White, fontSize = 24.sp)
                }
            },
            nextButtonText = "다음",
            onNextClicked = {},
            onBackClicked = {},
            onShowHowToClicked = {}
        )
    }
}

@Preview(showBackground = true, device = "spec:width=800dp,height=1280dp,dpi=240", name = "Portrait")
@Composable
private fun InspectionQuestionContentVerticalPreview() {
    NenoonKioskTheme {
        InspectionQuestionContent(
            title = "검사 질문",
            mainText = "어떤 항목이 보이나요?",
            options = listOf("정상적으로 보임", "조정이 필요함", "왼쪽만 보임", "오른쪽만 보임"),
            centerContent = {
                Box(
                    modifier = Modifier
                        .size(300.dp)
                        .background(Color.Gray),
                    contentAlignment = Alignment.Center
                ) {
                    Text("검사 이미지", color = Color.White, fontSize = 24.sp)
                }
            },
            nextButtonText = "다음",
            onNextClicked = {},
            onBackClicked = {},
            onShowHowToClicked = {}
        )
    }
}
