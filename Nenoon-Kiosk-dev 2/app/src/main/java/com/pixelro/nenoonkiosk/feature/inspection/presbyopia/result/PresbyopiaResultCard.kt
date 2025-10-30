package com.pixelro.nenoonkiosk.feature.inspection.presbyopia.result

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pixelro.nenoonkiosk.ui.theme.LightGray
import com.pixelro.nenoonkiosk.ui.theme.inputTextStyle
import com.pixelro.nenoonkiosk.ui.theme.neNoon_blue

/**
 * 노안 검사 결과 카드
 *
 * @param isNormal 정상 여부 (true: 정상, false: 노안)
 * @param normalMessage 정상일 때 표시할 메시지
 * @param abnormalMessagePrefix 노안일 때 메시지 앞부분
 * @param ageRange 눈 나이 범위 (예: "43 ~ 47세")
 * @param abnormalMessageSuffix 노안일 때 메시지 뒷부분
 * @param distancePrefix 거리 정보 앞부분 (예: "약 ")
 * @param avgDistance 평균 거리
 * @param distanceSuffix 거리 정보 뒷부분 (예: "cm에서 흐려집니다")
 */
@Composable
fun PresbyopiaResultCard(
    isNormal: Boolean,
    normalMessage: String,
    abnormalMessagePrefix: String,
    ageRange: String,
    abnormalMessageSuffix: String,
    distancePrefix: String,
    avgDistance: String,
    distanceSuffix: String,
) {
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .background(
                    color = LightGray,
                    shape = RoundedCornerShape(8.dp),
                )
                .padding(40.dp),
    ) {
        if (isNormal) {
            Text(
                text = normalMessage,
                style = inputTextStyle,
            )
        } else {
            Row {
                Text(
                    text = abnormalMessagePrefix,
                    style = inputTextStyle,
                )
                Text(
                    text = ageRange,
                    color = neNoon_blue,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = abnormalMessageSuffix,
                    style = inputTextStyle,
                )
            }
        }

        Row {
            Text(
                text = distancePrefix,
                fontSize = 24.sp,
                fontWeight = FontWeight.Medium,
            )
            Text(
                text = avgDistance,
                color = neNoon_blue,
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
            )
            Text(
                text = distanceSuffix,
                fontSize = 24.sp,
                fontWeight = FontWeight.Medium,
            )
        }
    }
}