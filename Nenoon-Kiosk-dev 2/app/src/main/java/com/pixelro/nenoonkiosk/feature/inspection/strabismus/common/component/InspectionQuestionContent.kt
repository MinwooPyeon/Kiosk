package com.pixelro.nenoonkiosk.feature.inspection.strabismus.common.component

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
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pixelro.nenoonkiosk.core.ui.HowToButton
import com.pixelro.nenoonkiosk.core.ui.PrimaryButton
import com.pixelro.nenoonkiosk.core.ui.TopBarVertical
import com.pixelro.nenoonkiosk.ui.theme.NenoonKioskTheme

/**
 * 검사 질문 라디오 옵션 선택 화면 공통 컴포넌트
 *
 * @param title 화면 제목
 * @param mainText 질문 텍스트
 * @param options 선택 옵션 리스트
 * @param centerContent 중앙 컨텐츠 (Canvas 또는 Image)
 * @param nextButtonText 다음 버튼 텍스트
 * @param onNextClicked 다음 버튼 클릭 콜백 (선택된 옵션 인덱스)
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

    Scaffold(
        containerColor = Color.Black,
        topBar = {
            TopBarVertical(
                title = title,
                showBackButton = true,
                onBackClicked = onBackClicked,
                actions = {
                    HowToButton(onClick = onShowHowToClicked)
                },
                containerColor = Color.Black,
                contentColor = Color.White
            )
        },
        bottomBar = {
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .background(Color.Black)
                        .padding(start = 24.dp, end = 24.dp, top = 24.dp, bottom = 24.dp),
            ) {
                PrimaryButton(
                    onClick = { selectedOption?.let { onNextClicked(it) } },
                    text = nextButtonText,
                    enabled = selectedOption != null,
                )
            }
        },
    ) { paddingValues ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Color.Black)
                    .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            centerContent()

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = mainText,
                color = Color.White,
                fontSize = 48.sp,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(24.dp))

            options.forEachIndexed { index, text ->
                OptionRadioButton(
                    text = text,
                    selected = selectedOption == (index + 1),
                    onClick = { selectedOption = index + 1 },
                )
            }
        }
    }
}

//가로 모드 내용 잘림-> 수정 필요
@Preview(showBackground = true, device = "spec:width=1280dp,height=800dp,dpi=240")
@Composable
private fun InspectionQuestionScreenHorizontalPreview() {
    NenoonKioskTheme {
        InspectionQuestionContent(
            title = "검사 질문",
            mainText = "어떤 항목이 보이나요?",
            options = listOf(
                "정상적으로 보임",
                "조정이 필요함",
                "왼쪽만 보임",
                "오른쪽만 보임"
            ),
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

@Preview(showBackground = true, device = "spec:width=800dp,height=1280dp,dpi=240")
@Composable
private fun InspectionQuestionContentVerticalPreview() {
    NenoonKioskTheme {
        InspectionQuestionContent(
            title = "검사 질문",
            mainText = "어떤 항목이 보이나요?",
            options = listOf(
                "정상적으로 보임",
                "조정이 필요함",
                "왼쪽만 보임",
                "오른쪽만 보임"
            ),
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