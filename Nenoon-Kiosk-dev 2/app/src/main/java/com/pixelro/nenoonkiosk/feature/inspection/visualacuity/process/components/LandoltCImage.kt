package com.pixelro.nenoonkiosk.feature.inspection.visualacuity.process.components

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import com.pixelro.nenoonkiosk.R

/**
 * 란돌트 C 이미지 컴포넌트
 *
 * ansNum과 sightLevel에 따라 적절한 란돌트 C 이미지를 표시
 *
 * @param ansNum 정답 방향 (2~7: 6가지 방향)
 * @param sightLevel 시력 난이도 (1~10: 숫자가 클수록 작은 크기)
 * @param modifier Modifier
 */
@Composable
fun LandoltCImage(
    ansNum: Int,
    sightLevel: Int,
    modifier: Modifier = Modifier,
) {
    Image(
        modifier = modifier,
        imageVector = ImageVector.vectorResource(
            id = getLandoltCDrawableId(ansNum, sightLevel)
        ),
        contentDescription = "Landolt C - Direction $ansNum, Level $sightLevel",
    )
}

/**
 * ansNum과 sightLevel에 해당하는 drawable ID를 반환
 *
 * @param ansNum 정답 방향 (2~7)
 * @param sightLevel 시력 난이도 (1~10)
 * @return Drawable resource ID
 */
private fun getLandoltCDrawableId(ansNum: Int, sightLevel: Int): Int {
    return when (ansNum) {
        2 -> when (sightLevel) {
            1 -> R.drawable._50cm_2_1
            2 -> R.drawable._50cm_2_2
            3 -> R.drawable._50cm_2_3
            4 -> R.drawable._50cm_2_4
            5 -> R.drawable._50cm_2_5
            6 -> R.drawable._50cm_2_6
            7 -> R.drawable._50cm_2_7
            8 -> R.drawable._50cm_2_8
            9 -> R.drawable._50cm_2_9
            else -> R.drawable._50cm_2_10
        }
        3 -> when (sightLevel) {
            1 -> R.drawable._50cm_3_1
            2 -> R.drawable._50cm_3_2
            3 -> R.drawable._50cm_3_3
            4 -> R.drawable._50cm_3_4
            5 -> R.drawable._50cm_3_5
            6 -> R.drawable._50cm_3_6
            7 -> R.drawable._50cm_3_7
            8 -> R.drawable._50cm_3_8
            9 -> R.drawable._50cm_3_9
            else -> R.drawable._50cm_3_10
        }
        4 -> when (sightLevel) {
            1 -> R.drawable._50cm_4_1
            2 -> R.drawable._50cm_4_2
            3 -> R.drawable._50cm_4_3
            4 -> R.drawable._50cm_4_4
            5 -> R.drawable._50cm_4_5
            6 -> R.drawable._50cm_4_6
            7 -> R.drawable._50cm_4_7
            8 -> R.drawable._50cm_4_8
            9 -> R.drawable._50cm_4_9
            else -> R.drawable._50cm_4_10
        }
        5 -> when (sightLevel) {
            1 -> R.drawable._50cm_5_1
            2 -> R.drawable._50cm_5_2
            3 -> R.drawable._50cm_5_3
            4 -> R.drawable._50cm_5_4
            5 -> R.drawable._50cm_5_5
            6 -> R.drawable._50cm_5_6
            7 -> R.drawable._50cm_5_7
            8 -> R.drawable._50cm_5_8
            9 -> R.drawable._50cm_5_9
            else -> R.drawable._50cm_5_10
        }
        6 -> when (sightLevel) {
            1 -> R.drawable._50cm_6_1
            2 -> R.drawable._50cm_6_2
            3 -> R.drawable._50cm_6_3
            4 -> R.drawable._50cm_6_4
            5 -> R.drawable._50cm_6_5
            6 -> R.drawable._50cm_6_6
            7 -> R.drawable._50cm_6_7
            8 -> R.drawable._50cm_6_8
            9 -> R.drawable._50cm_6_9
            else -> R.drawable._50cm_6_10
        }
        else -> when (sightLevel) { // 7
            1 -> R.drawable._50cm_7_1
            2 -> R.drawable._50cm_7_2
            3 -> R.drawable._50cm_7_3
            4 -> R.drawable._50cm_7_4
            5 -> R.drawable._50cm_7_5
            6 -> R.drawable._50cm_7_6
            7 -> R.drawable._50cm_7_7
            8 -> R.drawable._50cm_7_8
            9 -> R.drawable._50cm_7_9
            else -> R.drawable._50cm_7_10
        }
    }
}