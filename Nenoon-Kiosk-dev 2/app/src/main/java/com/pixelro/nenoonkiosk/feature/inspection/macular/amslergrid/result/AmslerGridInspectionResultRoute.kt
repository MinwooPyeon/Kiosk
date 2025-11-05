package com.pixelro.nenoonkiosk.feature.inspection.macular.amslergrid.result

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.util.StringProvider
import com.pixelro.nenoonkiosk.core.util.isLandscape
import com.pixelro.nenoonkiosk.feature.inspection.macular.amslergrid.AmslerGridTestResult
import com.pixelro.nenoonkiosk.feature.inspection.macular.amslergrid.MacularDisorderType

private val GridLineColor = Color(0xffc3c3c3)
private const val GridStrokePx = 1.5f

@Composable
fun AmslerGridTestResultContent(
    testResult: AmslerGridTestResult,
) {
    val leftSelectedArea = testResult.leftEyeDisorderType
    val rightSelectedArea = testResult.rightEyeDisorderType

    Text(
        modifier = Modifier
            .padding(start = 40.dp, top = 40.dp)
            .fillMaxWidth(),
        text = StringProvider.getString(R.string.test_result_my_result),
        fontSize = 28.sp,
        fontWeight = FontWeight.Medium
    )


        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            // LEFT
            Column(
                modifier = Modifier
                    .background(
                        color = Color(0xfff7f7f7),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    modifier = Modifier.padding(bottom = 12.dp),
                    text = StringProvider.getString(R.string.test_result_left),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium
                )
                AmslerGrid3x3(types = leftSelectedArea)
            }

            Spacer(modifier = Modifier.width(50.dp))

            // RIGHT
            Column(
                modifier = Modifier
                    .background(
                        color = Color(0xfff7f7f7),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    modifier = Modifier.padding(bottom = 12.dp),
                    text = StringProvider.getString(R.string.test_result_right),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium
                )
                AmslerGrid3x3(types = rightSelectedArea)
            }
        }

}

/**
 * 3×3 Amsler 그리드.
 * [types]는 인덱스 0..8 순서로 좌상단→우하단(행 우선).
 * 외곽선만 그리도록 각 셀에 top/left/right/bottom 플래그를 부여.
 */
@Composable
private fun AmslerGrid3x3(types: List<MacularDisorderType>) {
    val CellSize = if(isLandscape()) 50.dp else 100.dp
    Column {
        Row(modifier = Modifier.width(CellSize * 3).height(CellSize)) {
            AmslerGridCell(types[0], drawTop = true,  drawLeft = true)
            AmslerGridCell(types[1], drawTop = true)
            AmslerGridCell(types[2], drawTop = true,  drawRight = true)
        }
        Row(modifier = Modifier.width(CellSize * 3).height(CellSize)) {
            AmslerGridCell(types[3], drawLeft = true)
            AmslerGridCell(types[4])
            AmslerGridCell(types[5], drawRight = true)
        }
        Row(modifier = Modifier.width(CellSize * 3).height(CellSize)) {
            AmslerGridCell(types[6], drawLeft = true,  drawBottom = true)
            AmslerGridCell(types[7], drawBottom = true)
            AmslerGridCell(types[8], drawRight = true, drawBottom = true)
        }
    }
}

/**
 * 셀 하나: 상태 색상과 외곽선(상/하/좌/우)만 그리기.
 */
@Composable
private fun AmslerGridCell(
    type: MacularDisorderType,
    drawTop: Boolean = false,
    drawLeft: Boolean = false,
    drawRight: Boolean = false,
    drawBottom: Boolean = false,
) {
    val fillColor = when (type) {
        MacularDisorderType.Normal -> Color(0xffffffff)
        else -> Color(0xb4ea2525)
    }
    val CellSize = if(isLandscape()) 50.dp else 100.dp
    Box(
        modifier = Modifier
            .width(CellSize)
            .height(CellSize)
            .background(color = fillColor)
            .border(width = 0.5.dp, color = GridLineColor, shape = RectangleShape)
            .drawBehind {
                if (drawTop) {
                    drawLine(GridLineColor, Offset(0f, 0f), Offset(size.width, 0f), GridStrokePx)
                }
                if (drawLeft) {
                    drawLine(GridLineColor, Offset(0f, 0f), Offset(0f, size.height), GridStrokePx)
                }
                if (drawRight) {
                    drawLine(GridLineColor, Offset(size.width, 0f), Offset(size.width, size.height), GridStrokePx)
                }
                if (drawBottom) {
                    drawLine(GridLineColor, Offset(0f, size.height), Offset(size.width, size.height), GridStrokePx)
                }
            }
    )
}
