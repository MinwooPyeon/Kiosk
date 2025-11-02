package com.pixelro.nenoonkiosk.feature.inspection.macular.amslergrid.result

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.feature.inspection.macular.amslergrid.MacularDisorderType

@Composable
fun AmslerGridInspectionResultScreen(
    leftSelectedArea: List<MacularDisorderType>,
    rightSelectedArea: List<MacularDisorderType>
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            modifier =
                Modifier
                    .padding(start = 40.dp, top = 40.dp)
                    .fillMaxWidth(),
            text = stringResource(R.string.test_result_my_result),
            fontSize = 28.sp,
            fontWeight = FontWeight.Medium,
        )
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            Column(
                modifier =
                    Modifier
                        .background(
                            color = Color(0xfff7f7f7),
                            shape = RoundedCornerShape(8.dp),
                        )
                        .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    modifier =
                        Modifier
                            .padding(bottom = 12.dp),
                    text = stringResource(R.string.test_result_left),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                )
                /**
                 * 왼쪽 결과 그래프
                 */
                Row(
                    modifier =
                        Modifier
                            .width(300.dp)
                            .height(100.dp),
                ) {
                    Box(
                        modifier =
                            Modifier
                                .width(100.dp)
                                .height(100.dp)
                                .background(
                                    color =
                                        when (leftSelectedArea[0]) {
                                            MacularDisorderType.Normal -> Color(0xffffffff)
                                            else -> Color(0xb4ea2525)
                                        },
                                )
                                .border(
                                    width = (0.5).dp,
                                    color =
                                        Color(0xffc3c3c3),
                                    shape = RectangleShape,
                                )
                                .drawBehind {
                                    val strokeWidth = 1.5f
                                    val y = strokeWidth / 2

                                    drawLine(
                                        Color(0xffc3c3c3),
                                        Offset(0f, 0f),
                                        Offset(size.width, 0f),
                                        strokeWidth,
                                    )
                                    drawLine(
                                        Color(0xffc3c3c3),
                                        Offset(0f, 0f),
                                        Offset(0f, size.height),
                                        strokeWidth,
                                    )
                                },
                    ) {
                    }
                    Box(
                        modifier =
                            Modifier
                                .width(100.dp)
                                .height(100.dp)
                                .background(
                                    color =
                                        when (leftSelectedArea[1]) {
                                            MacularDisorderType.Normal -> Color(0xffffffff)
                                            else -> Color(0xb4ea2525)
                                        },
                                )
                                .border(
                                    width = (0.5).dp,
                                    color =
                                        Color(0xffc3c3c3),
                                    shape = RectangleShape,
                                )
                                .drawBehind {
                                    val strokeWidth = 1.5f
                                    val y = strokeWidth / 2

                                    drawLine(
                                        Color(0xffc3c3c3),
                                        Offset(0f, 0f),
                                        Offset(size.width, 0f),
                                        strokeWidth,
                                    )
                                },
                    ) {
                    }
                    Box(
                        modifier =
                            Modifier
                                .width(100.dp)
                                .height(100.dp)
                                .background(
                                    color =
                                        when (leftSelectedArea[2]) {
                                            MacularDisorderType.Normal -> Color(0xffffffff)
                                            else -> Color(0xb4ea2525)
                                        },
                                )
                                .border(
                                    width = (0.5).dp,
                                    color =
                                        Color(0xffc3c3c3),
                                    shape = RectangleShape,
                                )
                                .drawBehind {
                                    val strokeWidth = 1.5f
                                    val y = strokeWidth / 2

                                    drawLine(
                                        Color(0xffc3c3c3),
                                        Offset(0f, 0f),
                                        Offset(size.width, 0f),
                                        strokeWidth,
                                    )
                                    drawLine(
                                        Color(0xffc3c3c3),
                                        Offset(size.height, 0f),
                                        Offset(size.height, size.height),
                                        strokeWidth,
                                    )
                                },
                    ) {
                    }
                }
                Row(
                    modifier =
                        Modifier
                            .width(300.dp)
                            .height(100.dp),
                ) {
                    Box(
                        modifier =
                            Modifier
                                .width(100.dp)
                                .height(100.dp)
                                .background(
                                    color =
                                        when (leftSelectedArea[3]) {
                                            MacularDisorderType.Normal -> Color(0xffffffff)
                                            else -> Color(0xb4ea2525)
                                        },
                                )
                                .border(
                                    width = (0.5).dp,
                                    color =
                                        Color(0xffc3c3c3),
                                    shape = RectangleShape,
                                )
                                .drawBehind {
                                    val strokeWidth = 1.5f
                                    val y = strokeWidth / 2

                                    drawLine(
                                        Color(0xffc3c3c3),
                                        Offset(0f, 0f),
                                        Offset(0f, size.height),
                                        strokeWidth,
                                    )
                                },
                    ) {
                    }
                    Box(
                        modifier =
                            Modifier
                                .width(100.dp)
                                .height(100.dp)
                                .background(
                                    color =
                                        when (leftSelectedArea[4]) {
                                            MacularDisorderType.Normal -> Color(0xffffffff)
                                            else -> Color(0xb4ea2525)
                                        },
                                )
                                .border(
                                    width = (0.5).dp,
                                    color =
                                        Color(0xffc3c3c3),
                                    shape = RectangleShape,
                                ),
                    ) {
                    }
                    Box(
                        modifier =
                            Modifier
                                .width(100.dp)
                                .height(100.dp)
                                .background(
                                    color =
                                        when (leftSelectedArea[5]) {
                                            MacularDisorderType.Normal -> Color(0xffffffff)
                                            else -> Color(0xb4ea2525)
                                        },
                                )
                                .border(
                                    width = (0.5).dp,
                                    color =
                                        Color(0xffc3c3c3),
                                    shape = RectangleShape,
                                )
                                .drawBehind {
                                    val strokeWidth = 1.5f
                                    val y = strokeWidth / 2

                                    drawLine(
                                        Color(0xffc3c3c3),
                                        Offset(size.height, 0f),
                                        Offset(size.height, size.height),
                                        strokeWidth,
                                    )
                                },
                    ) {
                    }
                }
                Row(
                    modifier =
                        Modifier
                            .width(300.dp)
                            .height(100.dp),
                ) {
                    Box(
                        modifier =
                            Modifier
                                .width(100.dp)
                                .height(100.dp)
                                .background(
                                    color =
                                        when (leftSelectedArea[6]) {
                                            MacularDisorderType.Normal -> Color(0xffffffff)
                                            else -> Color(0xb4ea2525)
                                        },
                                )
                                .border(
                                    width = (0.5).dp,
                                    color =
                                        Color(0xffc3c3c3),
                                    shape = RectangleShape,
                                )
                                .drawBehind {
                                    val strokeWidth = 1.5f
                                    val y = strokeWidth / 2

                                    drawLine(
                                        Color(0xffc3c3c3),
                                        Offset(0f, 0f),
                                        Offset(0f, size.height),
                                        strokeWidth,
                                    )
                                    drawLine(
                                        Color(0xffc3c3c3),
                                        Offset(0f, size.height),
                                        Offset(size.height, size.height),
                                        strokeWidth,
                                    )
                                },
                    ) {
                    }
                    Box(
                        modifier =
                            Modifier
                                .width(100.dp)
                                .height(100.dp)
                                .background(
                                    color =
                                        when (leftSelectedArea[7]) {
                                            MacularDisorderType.Normal -> Color(0xffffffff)
                                            else -> Color(0xb4ea2525)
                                        },
                                )
                                .border(
                                    width = (0.5).dp,
                                    color =
                                        Color(0xffc3c3c3),
                                    shape = RectangleShape,
                                )
                                .drawBehind {
                                    val strokeWidth = 1.5f
                                    val y = strokeWidth / 2

                                    drawLine(
                                        Color(0xffc3c3c3),
                                        Offset(0f, size.height),
                                        Offset(size.height, size.height),
                                        strokeWidth,
                                    )
                                },
                    ) {
                    }
                    Box(
                        modifier =
                            Modifier
                                .width(100.dp)
                                .height(100.dp)
                                .background(
                                    color =
                                        when (leftSelectedArea[8]) {
                                            MacularDisorderType.Normal -> Color(0xffffffff)
                                            else -> Color(0xb4ea2525)
                                        },
                                )
                                .border(
                                    width = (0.5).dp,
                                    color =
                                        Color(0xffc3c3c3),
                                    shape = RectangleShape,
                                )
                                .drawBehind {
                                    val strokeWidth = 1.5f
                                    val y = strokeWidth / 2

                                    drawLine(
                                        Color(0xffc3c3c3),
                                        Offset(0f, size.height),
                                        Offset(size.height, size.height),
                                        strokeWidth,
                                    )
                                    drawLine(
                                        Color(0xffc3c3c3),
                                        Offset(size.height, 0f),
                                        Offset(size.height, size.height),
                                        strokeWidth,
                                    )
                                },
                    ) {
                    }
                }
            }
            Spacer(
                modifier =
                    Modifier
                        .width(50.dp),
            )
            Column(
                modifier =
                    Modifier
                        .background(
                            color = Color(0xfff7f7f7),
                            shape = RoundedCornerShape(8.dp),
                        )
                        .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    modifier =
                        Modifier
                            .padding(bottom = 12.dp),
                    text = stringResource(R.string.test_result_right),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                )
                /**
                 * 오른쪽 결과 그래프
                 */
                Row(
                    modifier =
                        Modifier
                            .width(300.dp)
                            .height(100.dp),
                ) {
                    Box(
                        modifier =
                            Modifier
                                .width(100.dp)
                                .height(100.dp)
                                .background(
                                    color =
                                        when (rightSelectedArea[0]) {
                                            MacularDisorderType.Normal -> Color(0xffffffff)
                                            else -> Color(0xb4ea2525)
                                        },
                                )
                                .border(
                                    width = (0.5).dp,
                                    color =
                                        Color(0xffc3c3c3),
                                    shape = RectangleShape,
                                )
                                .drawBehind {
                                    val strokeWidth = 1.5f
                                    val y = strokeWidth / 2

                                    drawLine(
                                        Color(0xffc3c3c3),
                                        Offset(0f, 0f),
                                        Offset(size.width, 0f),
                                        strokeWidth,
                                    )
                                    drawLine(
                                        Color(0xffc3c3c3),
                                        Offset(0f, 0f),
                                        Offset(0f, size.height),
                                        strokeWidth,
                                    )
                                },
                    ) {
                    }
                    Box(
                        modifier =
                            Modifier
                                .width(100.dp)
                                .height(100.dp)
                                .background(
                                    color =
                                        when (rightSelectedArea[1]) {
                                            MacularDisorderType.Normal -> Color(0xffffffff)
                                            else -> Color(0xb4ea2525)
                                        },
                                )
                                .border(
                                    width = (0.5).dp,
                                    color =
                                        Color(0xffc3c3c3),
                                    shape = RectangleShape,
                                )
                                .drawBehind {
                                    val strokeWidth = 1.5f
                                    val y = strokeWidth / 2

                                    drawLine(
                                        Color(0xffc3c3c3),
                                        Offset(0f, 0f),
                                        Offset(size.width, 0f),
                                        strokeWidth,
                                    )
                                },
                    ) {
                    }
                    Box(
                        modifier =
                            Modifier
                                .width(100.dp)
                                .height(100.dp)
                                .background(
                                    color =
                                        when (rightSelectedArea[2]) {
                                            MacularDisorderType.Normal -> Color(0xffffffff)
                                            else -> Color(0xb4ea2525)
                                        },
                                )
                                .border(
                                    width = (0.5).dp,
                                    color =
                                        Color(0xffc3c3c3),
                                    shape = RectangleShape,
                                )
                                .drawBehind {
                                    val strokeWidth = 1.5f
                                    val y = strokeWidth / 2

                                    drawLine(
                                        Color(0xffc3c3c3),
                                        Offset(0f, 0f),
                                        Offset(size.width, 0f),
                                        strokeWidth,
                                    )
                                    drawLine(
                                        Color(0xffc3c3c3),
                                        Offset(size.height, 0f),
                                        Offset(size.height, size.height),
                                        strokeWidth,
                                    )
                                },
                    ) {
                    }
                }
                Row(
                    modifier =
                        Modifier
                            .width(300.dp)
                            .height(100.dp),
                ) {
                    Box(
                        modifier =
                            Modifier
                                .width(100.dp)
                                .height(100.dp)
                                .background(
                                    color =
                                        when (rightSelectedArea[3]) {
                                            MacularDisorderType.Normal -> Color(0xffffffff)
                                            else -> Color(0xb4ea2525)
                                        },
                                )
                                .border(
                                    width = (0.5).dp,
                                    color =
                                        Color(0xffc3c3c3),
                                    shape = RectangleShape,
                                )
                                .drawBehind {
                                    val strokeWidth = 1.5f
                                    val y = strokeWidth / 2

                                    drawLine(
                                        Color(0xffc3c3c3),
                                        Offset(0f, 0f),
                                        Offset(0f, size.height),
                                        strokeWidth,
                                    )
                                },
                    ) {
                    }
                    Box(
                        modifier =
                            Modifier
                                .width(100.dp)
                                .height(100.dp)
                                .background(
                                    color =
                                        when (rightSelectedArea[4]) {
                                            MacularDisorderType.Normal -> Color(0xffffffff)
                                            else -> Color(0xb4ea2525)
                                        },
                                )
                                .border(
                                    width = (0.5).dp,
                                    color =
                                        Color(0xffc3c3c3),
                                    shape = RectangleShape,
                                ),
                    ) {
                    }
                    Box(
                        modifier =
                            Modifier
                                .width(100.dp)
                                .height(100.dp)
                                .background(
                                    color =
                                        when (rightSelectedArea[5]) {
                                            MacularDisorderType.Normal -> Color(0xffffffff)
                                            else -> Color(0xb4ea2525)
                                        },
                                )
                                .border(
                                    width = (0.5).dp,
                                    color =
                                        Color(0xffc3c3c3),
                                    shape = RectangleShape,
                                )
                                .drawBehind {
                                    val strokeWidth = 1.5f
                                    val y = strokeWidth / 2

                                    drawLine(
                                        Color(0xffc3c3c3),
                                        Offset(size.height, 0f),
                                        Offset(size.height, size.height),
                                        strokeWidth,
                                    )
                                },
                    ) {
                    }
                }
                Row(
                    modifier =
                        Modifier
                            .width(300.dp)
                            .height(100.dp),
                ) {
                    Box(
                        modifier =
                            Modifier
                                .width(100.dp)
                                .height(100.dp)
                                .background(
                                    color =
                                        when (rightSelectedArea[6]) {
                                            MacularDisorderType.Normal -> Color(0xffffffff)
                                            else -> Color(0xb4ea2525)
                                        },
                                )
                                .border(
                                    width = (0.5).dp,
                                    color =
                                        Color(0xffc3c3c3),
                                    shape = RectangleShape,
                                )
                                .drawBehind {
                                    val strokeWidth = 1.5f
                                    val y = strokeWidth / 2

                                    drawLine(
                                        Color(0xffc3c3c3),
                                        Offset(0f, 0f),
                                        Offset(0f, size.height),
                                        strokeWidth,
                                    )
                                    drawLine(
                                        Color(0xffc3c3c3),
                                        Offset(0f, size.height),
                                        Offset(size.height, size.height),
                                        strokeWidth,
                                    )
                                },
                    ) {
                    }
                    Box(
                        modifier =
                            Modifier
                                .width(100.dp)
                                .height(100.dp)
                                .background(
                                    color =
                                        when (rightSelectedArea[7]) {
                                            MacularDisorderType.Normal -> Color(0xffffffff)
                                            else -> Color(0xb4ea2525)
                                        },
                                )
                                .border(
                                    width = (0.5).dp,
                                    color =
                                        Color(0xffc3c3c3),
                                    shape = RectangleShape,
                                )
                                .drawBehind {
                                    val strokeWidth = 1.5f
                                    val y = strokeWidth / 2

                                    drawLine(
                                        Color(0xffc3c3c3),
                                        Offset(0f, size.height),
                                        Offset(size.height, size.height),
                                        strokeWidth,
                                    )
                                },
                    ) {
                    }
                    Box(
                        modifier =
                            Modifier
                                .width(100.dp)
                                .height(100.dp)
                                .background(
                                    color =
                                        when (rightSelectedArea[8]) {
                                            MacularDisorderType.Normal -> Color(0xffffffff)
                                            else -> Color(0xb4ea2525)
                                        },
                                )
                                .border(
                                    width = (0.5).dp,
                                    color =
                                        Color(0xffc3c3c3),
                                    shape = RectangleShape,
                                )
                                .drawBehind {
                                    val strokeWidth = 1.5f
                                    val y = strokeWidth / 2

                                    drawLine(
                                        Color(0xffc3c3c3),
                                        Offset(0f, size.height),
                                        Offset(size.height, size.height),
                                        strokeWidth,
                                    )
                                    drawLine(
                                        Color(0xffc3c3c3),
                                        Offset(size.height, 0f),
                                        Offset(size.height, size.height),
                                        strokeWidth,
                                    )
                                },
                    ) {
                    }
                }
            }
        }
    }
}

private fun mockAreas(vararg abnormalIdx: Int): List<MacularDisorderType> =
    List(9) { i -> if (abnormalIdx.contains(i)) MacularDisorderType.Distorted else MacularDisorderType.Normal }

@Preview(
    showBackground = true,
    widthDp = 888,
    heightDp = 600,
    name = "Amsler Result – All Normal",
    apiLevel = 34
)
@Composable
private fun Preview_AmslerGridInspectionResult_AllNormal() {
    AmslerGridInspectionResultScreen(
        leftSelectedArea = mockAreas(),
        rightSelectedArea = mockAreas()
    )
}

@Preview(
    showBackground = true,
    widthDp = 888,
    heightDp = 600,
    name = "Amsler Result – Mixed Abnormal",
    apiLevel = 34
)
@Composable
private fun Preview_AmslerGridInspectionResult_Mixed() {
    AmslerGridInspectionResultScreen(
        leftSelectedArea = mockAreas(0, 4, 8),
        rightSelectedArea = mockAreas(1, 3, 5, 7)
    )
}