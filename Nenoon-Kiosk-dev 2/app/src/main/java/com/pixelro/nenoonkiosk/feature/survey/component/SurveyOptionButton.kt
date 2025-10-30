package com.pixelro.nenoonkiosk.feature.survey.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pixelro.nenoonkiosk.ui.theme.neNoon_blue
import com.pixelro.nenoonkiosk.ui.theme.selectLargeTextStyle

/**
 * 설문조사 선택 버튼 컴포넌트
 *
 * @param text 버튼에 표시될 텍스트
 * @param isSelected 선택 여부
 * @param onClick 클릭 이벤트
 * @param modifier Modifier
 *
 */
@Composable
fun SurveyOptionButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val buttonColor by animateColorAsState(
        targetValue = if (isSelected) neNoon_blue else Color.White,
        animationSpec = tween(durationMillis = 500),
        label = "buttonColor",
    )

    val textColor by animateColorAsState(
        targetValue = if (isSelected) Color.White else neNoon_blue,
        animationSpec = tween(durationMillis = 500),
        label = "textColor",
    )

    Box(
        modifier =
            modifier
                .clip(RoundedCornerShape(8.dp))
                .border(
                    BorderStroke(4.dp, neNoon_blue),
                    RoundedCornerShape(8.dp),
                )
                .background(buttonColor, RoundedCornerShape(8.dp))
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() },
                    onClick = onClick,
                )
                .padding(horizontal = 20.dp, vertical = 16.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style = selectLargeTextStyle,
            color = textColor,
        )
    }
}

// ==================== Previews ====================
@Preview(
    name = "버튼 비교",
    showBackground = true,
    widthDp = 400,
    heightDp = 300,
)
@Composable
private fun SurveyOptionButtonComparisonPreview() {
    Column(modifier = Modifier.padding(20.dp)) {
        SurveyOptionButton(
            text = "선택됨",
            isSelected = true,
            onClick = {},
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(100.dp),
        )

        Spacer(modifier = Modifier.height(20.dp))

        SurveyOptionButton(
            text = "선택 안됨",
            isSelected = false,
            onClick = {},
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(100.dp),
        )
    }
}