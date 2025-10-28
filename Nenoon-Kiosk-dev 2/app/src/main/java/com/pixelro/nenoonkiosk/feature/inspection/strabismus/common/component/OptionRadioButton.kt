package com.pixelro.nenoonkiosk.feature.inspection.strabismus.common.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pixelro.nenoonkiosk.ui.theme.NenoonKioskTheme
import com.pixelro.nenoonkiosk.ui.theme.inputTextStyle

/**
 * 검사 공통 옵션 라디오 버튼 컴포넌트
 *
 * @param text 옵션 텍스트
 * @param selected 선택 여부
 * @param onClick 클릭 이벤트
 */
@Composable
fun OptionRadioButton(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(start = 48.dp, end = 24.dp, top = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick,
            colors =
                RadioButtonDefaults.colors(
                    selectedColor = Color.White,
                    unselectedColor = Color.White,
                ),
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = text, color = Color.White, style = inputTextStyle)
    }
}

@Preview(showBackground = true, device = "spec:width=800dp,height=1280dp,dpi=240")
@Composable
private fun OptionRadioButtonVerticalPreview() {
    NenoonKioskTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Black)
                .padding(16.dp)
        ) {
            OptionRadioButton(
                text = "원과 십자가가 모두 보입니다",
                selected = true,
                onClick = {}
            )
            OptionRadioButton(
                text = "원만 보입니다",
                selected = false,
                onClick = {}
            )
            OptionRadioButton(
                text = "십자가만 보입니다",
                selected = false,
                onClick = {}
            )
        }
    }
}