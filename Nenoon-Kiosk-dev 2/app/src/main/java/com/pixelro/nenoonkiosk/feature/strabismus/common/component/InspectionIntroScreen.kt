package com.pixelro.nenoonkiosk.feature.strabismus.common.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.ui.TopBarVertical
import com.pixelro.nenoonkiosk.core.util.StringProvider
import com.pixelro.nenoonkiosk.ui.theme.NenoonKioskTheme
import com.pixelro.nenoonkiosk.ui.theme.inputTextStyle
import com.pixelro.nenoonkiosk.ui.theme.selectLargeTextStyle

/**
 * 검사 인트로 화면 공통 컴포넌트
 *
 * @param title 검사 제목
 * @param description 검사 설명
 * @param onStartClicked 검사 시작 버튼 클릭 이벤트
 * @param onBackClicked 뒤로가기 버튼 클릭 이벤트
 * @param howToDialog 가이드 다이얼로그 컴포넌트
 */
@Composable
fun InspectionIntroScreen(
    title: String,
    description: String,
    onStartClicked: () -> Unit,
    onBackClicked: () -> Unit,
    howToDialog: @Composable (onDismiss: () -> Unit) -> Unit,
) {
    var showHowToDialog by remember { mutableStateOf(false) }

    if (showHowToDialog) {
        howToDialog { showHowToDialog = false }
    }

    Scaffold(
        containerColor = Color.White,
        topBar = {
            TopBarVertical(
                title = "",
                showBackButton = true,
                onBackClicked = onBackClicked,
            )
        },
        bottomBar = {
            DualButtonBottomBar(
                primaryButtonText = StringProvider.getStringComposable(R.string.common_start_test),
                onPrimaryButtonClick = onStartClicked,
                secondaryButtonText = StringProvider.getStringComposable(R.string.common_view_test_guide),
                onSecondaryButtonClick = { showHowToDialog = true }
            )
        }
    ) { paddingValues ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
        ) {
            Text(
                text = title,
                style = selectLargeTextStyle,
                modifier = Modifier.padding(top = 16.dp, start = 16.dp),
            )
            Text(
                text = description,
                style = inputTextStyle,
                color = Color.Gray,
                modifier = Modifier.padding(top = 40.dp, start = 16.dp, end = 16.dp),
            )
        }
    }
}

@Preview(showBackground = true, device = "spec:width=1280dp,height=800dp,dpi=240")
@Composable
private fun InspectionIntroScreenHorizontalPreview() {
    NenoonKioskTheme {
        InspectionIntroScreen(
            title = "사위 검사",
            description = "두 눈의 균형 상태를 평가하는 검사입니다.",
            onStartClicked = {},
            onBackClicked = {},
            howToDialog = {}
        )
    }
}

@Preview(showBackground = true, device = "spec:width=800dp,height=1280dp,dpi=240")
@Composable
private fun InspectionIntroScreenVerticalPreview() {
    NenoonKioskTheme {
        InspectionIntroScreen(
            title = "사위 검사",
            description = "두 눈의 균형 상태를 평가하는 검사입니다.",
            onStartClicked = {},
            onBackClicked = {},
            howToDialog = {}
        )
    }
}