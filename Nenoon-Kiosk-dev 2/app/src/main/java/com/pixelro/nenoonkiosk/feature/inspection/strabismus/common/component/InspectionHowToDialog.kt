package com.pixelro.nenoonkiosk.feature.inspection.strabismus.common.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.pixelro.nenoonkiosk.core.ui.PrimaryButton
import com.pixelro.nenoonkiosk.feature.inspection.strabismus.model.HowToStepData
import com.pixelro.nenoonkiosk.ui.theme.NenoonKioskTheme
import com.pixelro.nenoonkiosk.ui.theme.bodyTextStyle
import com.pixelro.nenoonkiosk.ui.theme.inputTextStyle
import com.pixelro.nenoonkiosk.ui.theme.neNoon_blue

/**
 * 검사 가이드 다이얼로그 컴포넌트
 *
 * @param title 다이얼로그 제목
 * @param steps 검사 단계 목록
 * @param confirmText 확인 버튼 텍스트
 * @param onDismissRequest 닫기 이벤트
 */
@Composable
fun InspectionHowToDialog(
    title: String,
    steps: List<HowToStepData>,
    confirmText: String,
    onDismissRequest: () -> Unit,
) {
    Dialog(onDismissRequest = onDismissRequest) {
        Column(
            modifier =
                Modifier
                    .background(Color.White)
                    .padding(24.dp),
        ) {
            Text(
                text = title,
                style = bodyTextStyle,
                color = Color.Black,
                modifier =
                    Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(bottom = 16.dp),
            )
            steps.forEach { stepData ->
                HowToStep(
                    step = stepData.step,
                    instruction = stepData.instruction,
                )
            }
            Spacer(modifier = Modifier.height(32.dp))
            PrimaryButton(
                onClick = onDismissRequest,
                text = confirmText,
            )
        }
    }
}

/**
 * 검사 가이드 단계 항목
 *
 * @param step 단계 번호 (예: "1단계")
 * @param instruction 단계 설명
 */
@Composable
fun HowToStep(
    step: String,
    instruction: String,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = step,
            style = inputTextStyle,
            fontSize = 22.sp,
            color = neNoon_blue,
            modifier = Modifier.padding(end = 16.dp),
        )
        Text(
            text = instruction,
            style = inputTextStyle,
            fontSize = 24.sp,
            color = Color.Black,
            modifier = Modifier.weight(1f),
        )
    }
}

@Preview(showBackground = true, device = "spec:width=1280dp,height=800dp,dpi=240")
@Composable
private fun InspectionHowToDialogHorizontalPreview() {
    NenoonKioskTheme {
        InspectionHowToDialog(
            title = "검사 가이드",
            steps =
                listOf(
                    HowToStepData("1단계", "첫 번째 단계 설명입니다."),
                    HowToStepData("2단계", "두 번째 단계 설명입니다."),
                    HowToStepData("3단계", "세 번째 단계 설명입니다."),
                ),
            confirmText = "확인",
            onDismissRequest = {},
        )
    }
}