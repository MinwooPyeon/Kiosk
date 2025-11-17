package com.pixelro.nenoonkiosk.feature.inspection.visualacuity.process.components

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.feature.inspection.visualacuity.process.components.VisualAcuityChartSizeMode

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
    sizeMode: VisualAcuityChartSizeMode,
    modifier: Modifier = Modifier,
) {
    Image(
        modifier = modifier,
        imageVector = ImageVector.vectorResource(
            id = getLandoltCDrawableId(ansNum, sightLevel, sizeMode)
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
private fun getLandoltCDrawableId(
    ansNum: Int,
    sightLevel: Int,
    sizeMode: VisualAcuityChartSizeMode,
): Int {
    val baseName =
        when (sizeMode) {
            VisualAcuityChartSizeMode.ShortDistance -> "_50cm"
            VisualAcuityChartSizeMode.LongDistance -> "_3m"
        }
    return resourceIdFor(baseName, ansNum, sightLevel)
}

private fun resourceIdFor(
    base: String,
    ansNum: Int,
    sightLevel: Int,
): Int {
    val candidateNames =
        listOf(
            "${base}_${ansNum}_${sightLevel}",
            "_50cm_${ansNum}_${sightLevel}",
        )

    candidateNames.forEach { name ->
        getDrawableId(name)?.let { return it }
    }

    error("Drawable resource not found for ${base}_${ansNum}_${sightLevel}")
}

private fun getDrawableId(name: String): Int? =
    runCatching {
        val field = R.drawable::class.java.getDeclaredField(name)
        field.getInt(null)
    }.getOrNull()