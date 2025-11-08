package com.pixelro.nenoonkiosk.feature.strabismus.common.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pixelro.nenoonkiosk.ui.theme.LightGray100
import com.pixelro.nenoonkiosk.ui.theme.NenoonKioskTheme
import com.pixelro.nenoonkiosk.ui.theme.bodyTextStyle
import com.pixelro.nenoonkiosk.ui.theme.inputTextStyle
import com.pixelro.nenoonkiosk.ui.theme.neNoon_blue

/**
 * 검사 결과용 카드 컴포넌트
 *
 * @param modifier Modifier
 * @param title 결과 항목 제목
 * @param result 결과 값
 * @param description 결과 설명
 */
@Composable
fun ResultCard(
    modifier: Modifier = Modifier,
    title: String,
    result: String,
    description: String,
) {
    Card(
        modifier = modifier.height(240.dp),
        colors = CardDefaults.cardColors(containerColor = LightGray100),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = title,
                style = inputTextStyle,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = Color.Black,
            )
            Text(
                text = result,
                style = bodyTextStyle,
                fontSize = 42.sp,
                textAlign = TextAlign.Center,
                color = neNoon_blue,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = description,
                style = inputTextStyle,
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                color = Color.DarkGray,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ResultCardPreview() {
    NenoonKioskTheme {
        ResultCard(
            title = "망막 상 크기 차이",
            result = "오른눈 0.5% 큼",
            description = "오른눈의 망막이 더 큽니다.",
        )
    }
}