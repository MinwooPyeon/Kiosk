package com.pixelro.nenoonkiosk.feature.inspection.visualacuity.result.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pixelro.nenoonkiosk.ui.theme.White
import com.pixelro.nenoonkiosk.ui.theme.bodyTextStyle

/**
 * 시력 검사 결과 한 줄 표시 컴포넌트
 *
 * @param label 라벨 텍스트 (예: "왼쪽", "오른쪽")
 * @param value 시력 값 (예: "0.3", "0.5")
 * @param modifier Modifier
 */
@Composable
fun VisualAcuityResultRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .background(color = White, shape = RoundedCornerShape(8.dp))
            .padding(vertical = 20.dp, horizontal = 30.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "$label : ",
            style = bodyTextStyle,
            fontWeight = FontWeight.Medium,
        )
        Text(
            text = value,
            style = bodyTextStyle,
            fontWeight = FontWeight.Bold,
        )
    }
}