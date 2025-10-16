package com.pixelro.nenoonkiosk.test.macular.amslergrid

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.util.StringProvider

@Composable
fun AmslerGridTestResultContent(
    testResult: AmslerGridTestResult,
    navController: NavHostController
) {
    val leftSelectedArea = testResult.leftEyeDisorderType
    val rightSelectedArea = testResult.rightEyeDisorderType

    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column() {
//            Text(
//                modifier = Modifier
//                    .padding(start = 40.dp),
//                text = StringProvider.getString(R.string.test_result_normal_right),
//                fontSize = 16.sp,
//                color = Color(0xff878787)
//            )
        }
        /**
         * 정상 결과 그래프
         */
//        Column(
//            modifier = Modifier
//                .padding(start = 80.dp, top = 20.dp)
//                .width(210.dp)
//                .height(210.dp)
//        ) {
//            Row(
//                modifier = Modifier
//                    .width(210.dp)
//                    .weight(1f)
//                    .height(70.dp)
//            ) {
//                Box(
//                    modifier = Modifier
//                        .weight(1f)
//                        .height(70.dp)
//                        .background(
//                            color = Color(0xffffffff)
//                        )
//                        .border(
//                            width = (0.5).dp,
//                            color = Color(0xffc3c3c3),
//                            shape = RectangleShape
//                        )
//                        .drawBehind {
//                            val strokeWidth = 1.5f
//                            val y = strokeWidth / 2
//
//                            drawLine(
//                                Color(0xffc3c3c3),
//                                Offset(0f, 0f),
//                                Offset(size.width, 0f),
//                                strokeWidth
//                            )
//                            drawLine(
//                                Color(0xffc3c3c3),
//                                Offset(0f, 0f),
//                                Offset(0f, size.height),
//                                strokeWidth
//                            )
//                        }
//                ) {
//
//                }
//                Box(
//                    modifier = Modifier
//                        .weight(1f)
//                        .height(70.dp)
//                        .background(
//                            color = Color(0xffffffff)
//                        )
//                        .border(
//                            width = (0.5).dp,
//                            color = Color(0xffc3c3c3),
//                            shape = RectangleShape
//                        )
//                        .drawBehind {
//                            val strokeWidth = 1.5f
//                            val y = strokeWidth / 2
//
//                            drawLine(
//                                Color(0xffc3c3c3),
//                                Offset(0f, 0f),
//                                Offset(size.width, 0f),
//                                strokeWidth
//                            )
//                        }
//                ) {
//
//                }
//                Box(
//                    modifier = Modifier
//                        .weight(1f)
//                        .height(70.dp)
//                        .background(
//                            color = Color(0xffffffff)
//                        )
//                        .border(
//                            width = (0.5).dp,
//                            color = Color(0xffc3c3c3),
//                            shape = RectangleShape
//                        )
//                        .drawBehind {
//                            val strokeWidth = 1.5f
//                            val y = strokeWidth / 2
//
//                            drawLine(
//                                Color(0xffc3c3c3),
//                                Offset(0f, 0f),
//                                Offset(size.width, 0f),
//                                strokeWidth
//                            )
//                            drawLine(
//                                Color(0xffc3c3c3),
//                                Offset(size.height, 0f),
//                                Offset(size.height, size.height),
//                                strokeWidth
//                            )
//                        }
//                ) {
//
//                }
//            }
//            Row(
//                modifier = Modifier
//                    .width(210.dp)
//                    .weight(1f)
//                    .height(70.dp)
//            ) {
//                Box(
//                    modifier = Modifier
//                        .weight(1f)
//                        .height(70.dp)
//                        .background(
//                            color = Color(0xffffffff)
//                        )
//                        .border(
//                            width = (0.5).dp,
//                            color = Color(0xffc3c3c3),
//                            shape = RectangleShape
//                        )
//                        .drawBehind {
//                            val strokeWidth = 1.5f
//                            val y = strokeWidth / 2
//
//                            drawLine(
//                                Color(0xffc3c3c3),
//                                Offset(0f, 0f),
//                                Offset(0f, size.height),
//                                strokeWidth
//                            )
//                        }
//                ) {
//
//                }
//                Box(
//                    modifier = Modifier
//                        .weight(1f)
//                        .height(70.dp)
//                        .background(
//                            color = Color(0xffffffff)
//                        )
//                        .border(
//                            width = (0.5).dp,
//                            color = Color(0xffc3c3c3),
//                            shape = RectangleShape
//                        )
//                ) {
//
//                }
//                Box(
//                    modifier = Modifier
//                        .weight(1f)
//                        .height(70.dp)
//                        .background(
//                            color = Color(0xffffffff)
//                        )
//                        .border(
//                            width = (0.5).dp,
//                            color = Color(0xffc3c3c3),
//                            shape = RectangleShape
//                        )
//                        .drawBehind {
//                            val strokeWidth = 1.5f
//                            val y = strokeWidth / 2
//
//                            drawLine(
//                                Color(0xffc3c3c3),
//                                Offset(size.height, 0f),
//                                Offset(size.height, size.height),
//                                strokeWidth
//                            )
//                        }
//                ) {
//
//                }
//            }
//            Row(
//                modifier = Modifier
//                    .width(210.dp)
//                    .weight(1f)
//                    .height(70.dp)
//            ) {
//                Box(
//                    modifier = Modifier
//                        .weight(1f)
//                        .height(70.dp)
//                        .background(
//                            color = Color(0xffffffff)
//                        )
//                        .border(
//                            width = (0.5).dp,
//                            color = Color(0xffc3c3c3),
//                            shape = RectangleShape
//                        )
//                        .drawBehind {
//                            val strokeWidth = 1.5f
//                            val y = strokeWidth / 2
//
//                            drawLine(
//                                Color(0xffc3c3c3),
//                                Offset(0f, 0f),
//                                Offset(0f, size.height),
//                                strokeWidth
//                            )
//                            drawLine(
//                                Color(0xffc3c3c3),
//                                Offset(0f, size.height),
//                                Offset(size.height, size.height),
//                                strokeWidth
//                            )
//                        }
//                ) {
//
//                }
//                Box(
//                    modifier = Modifier
//                        .weight(1f)
//                        .height(70.dp)
//                        .background(
//                            color = Color(0xffffffff)
//                        )
//                        .border(
//                            width = (0.5).dp,
//                            color = Color(0xffc3c3c3),
//                            shape = RectangleShape
//                        )
//                        .drawBehind {
//                            val strokeWidth = 1.5f
//                            val y = strokeWidth / 2
//
//                            drawLine(
//                                Color(0xffc3c3c3),
//                                Offset(0f, size.height),
//                                Offset(size.height, size.height),
//                                strokeWidth
//                            )
//                        }
//                ) {
//
//                }
//                Box(
//                    modifier = Modifier
//                        .weight(1f)
//                        .height(70.dp)
//                        .background(
//                            color = Color(0xffffffff)
//                        )
//                        .border(
//                            width = (0.5).dp,
//                            color = Color(0xffc3c3c3),
//                            shape = RectangleShape
//                        )
//                        .drawBehind {
//                            val strokeWidth = 1.5f
//                            val y = strokeWidth / 2
//
//                            drawLine(
//                                Color(0xffc3c3c3),
//                                Offset(0f, size.height),
//                                Offset(size.height, size.height),
//                                strokeWidth
//                            )
//                            drawLine(
//                                Color(0xffc3c3c3),
//                                Offset(size.height, 0f),
//                                Offset(size.height, size.height),
//                                strokeWidth
//                            )
//                        }
//                ) {
//
//                }
//            }
//        }
    }
    Text(
        modifier = Modifier
            .padding(start = 40.dp, top = 40.dp)
            .fillMaxWidth(),
        text = StringProvider.getString(R.string.test_result_my_result, ),
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
                modifier = Modifier
                    .padding(bottom = 12.dp),
                text = StringProvider.getString(R.string.test_result_left, ),
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium
            )
            /**
             * 왼쪽 결과 그래프
             */
            Row(
                modifier = Modifier
                    .width(300.dp)
                    .height(100.dp)
            ) {
                Box(
                    modifier = Modifier
                        .width(100.dp)
                        .height(100.dp)
                        .background(
                            color = when (leftSelectedArea[0]) {
                                MacularDisorderType.Normal -> Color(0xffffffff)
                                else -> Color(0xb4ea2525)
                            }
                        )
                        .border(
                            width = (0.5).dp,
                            color =
                            Color(0xffc3c3c3),
                            shape = RectangleShape
                        )
                        .drawBehind {
                            val strokeWidth = 1.5f
                            val y = strokeWidth / 2

                            drawLine(
                                Color(0xffc3c3c3),
                                Offset(0f, 0f),
                                Offset(size.width, 0f),
                                strokeWidth
                            )
                            drawLine(
                                Color(0xffc3c3c3),
                                Offset(0f, 0f),
                                Offset(0f, size.height),
                                strokeWidth
                            )
                        }
                ) {

                }
                Box(
                    modifier = Modifier
                        .width(100.dp)
                        .height(100.dp)
                        .background(
                            color = when (leftSelectedArea[1]) {
                                MacularDisorderType.Normal -> Color(0xffffffff)
                                else -> Color(0xb4ea2525)
                            }
                        )
                        .border(
                            width = (0.5).dp,
                            color =
                                Color(0xffc3c3c3),
                            shape = RectangleShape
                        )
                        .drawBehind {
                            val strokeWidth = 1.5f
                            val y = strokeWidth / 2

                            drawLine(
                                Color(0xffc3c3c3),
                                Offset(0f, 0f),
                                Offset(size.width, 0f),
                                strokeWidth
                            )
                        }
                ) {

                }
                Box(
                    modifier = Modifier
                        .width(100.dp)
                        .height(100.dp)
                        .background(
                            color = when (leftSelectedArea[2]) {
                                MacularDisorderType.Normal -> Color(0xffffffff)
                                else -> Color(0xb4ea2525)
                            }
                        )
                        .border(
                            width = (0.5).dp,
                            color =
                                Color(0xffc3c3c3),
                            shape = RectangleShape
                        )
                        .drawBehind {
                            val strokeWidth = 1.5f
                            val y = strokeWidth / 2

                            drawLine(
                                Color(0xffc3c3c3),
                                Offset(0f, 0f),
                                Offset(size.width, 0f),
                                strokeWidth
                            )
                            drawLine(
                                Color(0xffc3c3c3),
                                Offset(size.height, 0f),
                                Offset(size.height, size.height),
                                strokeWidth
                            )
                        }
                ) {

                }
            }
            Row(
                modifier = Modifier
                    .width(300.dp)
                    .height(100.dp)
            ) {
                Box(
                    modifier = Modifier
                        .width(100.dp)
                        .height(100.dp)
                        .background(
                            color = when (leftSelectedArea[3]) {
                                MacularDisorderType.Normal -> Color(0xffffffff)
                                else -> Color(0xb4ea2525)
                            }
                        )
                        .border(
                            width = (0.5).dp,
                            color =
                                Color(0xffc3c3c3),
                            shape = RectangleShape
                        )
                        .drawBehind {
                            val strokeWidth = 1.5f
                            val y = strokeWidth / 2

                            drawLine(
                                Color(0xffc3c3c3),
                                Offset(0f, 0f),
                                Offset(0f, size.height),
                                strokeWidth
                            )
                        }
                ) {

                }
                Box(
                    modifier = Modifier
                        .width(100.dp)
                        .height(100.dp)
                        .background(
                            color = when (leftSelectedArea[4]) {
                                MacularDisorderType.Normal -> Color(0xffffffff)
                                else -> Color(0xb4ea2525)
                            }
                        )
                        .border(
                            width = (0.5).dp,
                            color =
                                Color(0xffc3c3c3),
                            shape = RectangleShape
                        )
                ) {

                }
                Box(
                    modifier = Modifier
                        .width(100.dp)
                        .height(100.dp)
                        .background(
                            color = when (leftSelectedArea[5]) {
                                MacularDisorderType.Normal -> Color(0xffffffff)
                                else -> Color(0xb4ea2525)
                            }
                        )
                        .border(
                            width = (0.5).dp,
                            color =
                                Color(0xffc3c3c3),
                            shape = RectangleShape
                        )
                        .drawBehind {
                            val strokeWidth = 1.5f
                            val y = strokeWidth / 2

                            drawLine(
                                Color(0xffc3c3c3),
                                Offset(size.height, 0f),
                                Offset(size.height, size.height),
                                strokeWidth
                            )
                        }
                ) {

                }
            }
            Row(
                modifier = Modifier
                    .width(300.dp)
                    .height(100.dp)
            ) {
                Box(
                    modifier = Modifier
                        .width(100.dp)
                        .height(100.dp)
                        .background(
                            color = when (leftSelectedArea[6]) {
                                MacularDisorderType.Normal -> Color(0xffffffff)
                                else -> Color(0xb4ea2525)
                            }
                        )
                        .border(
                            width = (0.5).dp,
                            color =
                                Color(0xffc3c3c3),
                            shape = RectangleShape
                        )
                        .drawBehind {
                            val strokeWidth = 1.5f
                            val y = strokeWidth / 2

                            drawLine(
                                Color(0xffc3c3c3),
                                Offset(0f, 0f),
                                Offset(0f, size.height),
                                strokeWidth
                            )
                            drawLine(
                                Color(0xffc3c3c3),
                                Offset(0f, size.height),
                                Offset(size.height, size.height),
                                strokeWidth
                            )
                        }
                ) {

                }
                Box(
                    modifier = Modifier
                        .width(100.dp)
                        .height(100.dp)
                        .background(
                            color = when (leftSelectedArea[7]) {
                                MacularDisorderType.Normal -> Color(0xffffffff)
                                else -> Color(0xb4ea2525)
                            }
                        )
                        .border(
                            width = (0.5).dp,
                            color =
                                Color(0xffc3c3c3),
                            shape = RectangleShape
                        )
                        .drawBehind {
                            val strokeWidth = 1.5f
                            val y = strokeWidth / 2

                            drawLine(
                                Color(0xffc3c3c3),
                                Offset(0f, size.height),
                                Offset(size.height, size.height),
                                strokeWidth
                            )
                        }
                ) {

                }
                Box(
                    modifier = Modifier
                        .width(100.dp)
                        .height(100.dp)
                        .background(
                            color = when (leftSelectedArea[8]) {
                                MacularDisorderType.Normal -> Color(0xffffffff)
                                else -> Color(0xb4ea2525)
                            }
                        )
                        .border(
                            width = (0.5).dp,
                            color =
                                Color(0xffc3c3c3),
                            shape = RectangleShape
                        )
                        .drawBehind {
                            val strokeWidth = 1.5f
                            val y = strokeWidth / 2

                            drawLine(
                                Color(0xffc3c3c3),
                                Offset(0f, size.height),
                                Offset(size.height, size.height),
                                strokeWidth
                            )
                            drawLine(
                                Color(0xffc3c3c3),
                                Offset(size.height, 0f),
                                Offset(size.height, size.height),
                                strokeWidth
                            )
                        }
                ) {

                }
            }
        }
        Spacer(
            modifier = Modifier
                .width(50.dp)
        )
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
                modifier = Modifier
                    .padding(bottom = 12.dp),
                text = StringProvider.getString(R.string.test_result_right, ),
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium
            )
            /**
             * 오른쪽 결과 그래프
             */
            Row(
                modifier = Modifier
                    .width(300.dp)
                    .height(100.dp)
            ) {
                Box(
                    modifier = Modifier
                        .width(100.dp)
                        .height(100.dp)
                        .background(
                            color = when (rightSelectedArea[0]) {
                                MacularDisorderType.Normal -> Color(0xffffffff)
                                else -> Color(0xb4ea2525)
                            }
                        )
                        .border(
                            width = (0.5).dp,
                            color =
                                Color(0xffc3c3c3),
                            shape = RectangleShape
                        )
                        .drawBehind {
                            val strokeWidth = 1.5f
                            val y = strokeWidth / 2

                            drawLine(
                                Color(0xffc3c3c3),
                                Offset(0f, 0f),
                                Offset(size.width, 0f),
                                strokeWidth
                            )
                            drawLine(
                                Color(0xffc3c3c3),
                                Offset(0f, 0f),
                                Offset(0f, size.height),
                                strokeWidth
                            )
                        }
                ) {

                }
                Box(
                    modifier = Modifier
                        .width(100.dp)
                        .height(100.dp)
                        .background(
                            color = when (rightSelectedArea[1]) {
                                MacularDisorderType.Normal -> Color(0xffffffff)
                                else -> Color(0xb4ea2525)
                            }
                        )
                        .border(
                            width = (0.5).dp,
                            color =
                                Color(0xffc3c3c3),
                            shape = RectangleShape
                        )
                        .drawBehind {
                            val strokeWidth = 1.5f
                            val y = strokeWidth / 2

                            drawLine(
                                Color(0xffc3c3c3),
                                Offset(0f, 0f),
                                Offset(size.width, 0f),
                                strokeWidth
                            )
                        }
                ) {

                }
                Box(
                    modifier = Modifier
                        .width(100.dp)
                        .height(100.dp)
                        .background(
                            color = when (rightSelectedArea[2]) {
                                MacularDisorderType.Normal -> Color(0xffffffff)
                                else -> Color(0xb4ea2525)
                            }
                        )
                        .border(
                            width = (0.5).dp,
                            color =
                                Color(0xffc3c3c3),
                            shape = RectangleShape
                        )
                        .drawBehind {
                            val strokeWidth = 1.5f
                            val y = strokeWidth / 2

                            drawLine(
                                Color(0xffc3c3c3),
                                Offset(0f, 0f),
                                Offset(size.width, 0f),
                                strokeWidth
                            )
                            drawLine(
                                Color(0xffc3c3c3),
                                Offset(size.height, 0f),
                                Offset(size.height, size.height),
                                strokeWidth
                            )
                        }
                ) {

                }
            }
            Row(
                modifier = Modifier
                    .width(300.dp)
                    .height(100.dp)
            ) {
                Box(
                    modifier = Modifier
                        .width(100.dp)
                        .height(100.dp)
                        .background(
                            color = when (rightSelectedArea[3]) {
                                MacularDisorderType.Normal -> Color(0xffffffff)
                                else -> Color(0xb4ea2525)
                            }
                        )
                        .border(
                            width = (0.5).dp,
                            color =
                                Color(0xffc3c3c3),
                            shape = RectangleShape
                        )
                        .drawBehind {
                            val strokeWidth = 1.5f
                            val y = strokeWidth / 2

                            drawLine(
                                Color(0xffc3c3c3),
                                Offset(0f, 0f),
                                Offset(0f, size.height),
                                strokeWidth
                            )
                        }
                ) {

                }
                Box(
                    modifier = Modifier
                        .width(100.dp)
                        .height(100.dp)
                        .background(
                            color = when (rightSelectedArea[4]) {
                                MacularDisorderType.Normal -> Color(0xffffffff)
                                else -> Color(0xb4ea2525)
                            }
                        )
                        .border(
                            width = (0.5).dp,
                            color =
                                Color(0xffc3c3c3),
                            shape = RectangleShape
                        )
                ) {

                }
                Box(
                    modifier = Modifier
                        .width(100.dp)
                        .height(100.dp)
                        .background(
                            color = when (rightSelectedArea[5]) {
                                MacularDisorderType.Normal -> Color(0xffffffff)
                                else -> Color(0xb4ea2525)
                            }
                        )
                        .border(
                            width = (0.5).dp,
                            color =
                                Color(0xffc3c3c3),
                            shape = RectangleShape
                        )
                        .drawBehind {
                            val strokeWidth = 1.5f
                            val y = strokeWidth / 2

                            drawLine(
                                Color(0xffc3c3c3),
                                Offset(size.height, 0f),
                                Offset(size.height, size.height),
                                strokeWidth
                            )
                        }
                ) {

                }
            }
            Row(
                modifier = Modifier
                    .width(300.dp)
                    .height(100.dp)
            ) {
                Box(
                    modifier = Modifier
                        .width(100.dp)
                        .height(100.dp)
                        .background(
                            color = when (rightSelectedArea[6]) {
                                MacularDisorderType.Normal -> Color(0xffffffff)
                                else -> Color(0xb4ea2525)
                            }
                        )
                        .border(
                            width = (0.5).dp,
                            color =
                                Color(0xffc3c3c3),
                            shape = RectangleShape
                        )
                        .drawBehind {
                            val strokeWidth = 1.5f
                            val y = strokeWidth / 2

                            drawLine(
                                Color(0xffc3c3c3),
                                Offset(0f, 0f),
                                Offset(0f, size.height),
                                strokeWidth
                            )
                            drawLine(
                                Color(0xffc3c3c3),
                                Offset(0f, size.height),
                                Offset(size.height, size.height),
                                strokeWidth
                            )
                        }
                ) {

                }
                Box(
                    modifier = Modifier
                        .width(100.dp)
                        .height(100.dp)
                        .background(
                            color = when (rightSelectedArea[7]) {
                                MacularDisorderType.Normal -> Color(0xffffffff)
                                else -> Color(0xb4ea2525)
                            }
                        )
                        .border(
                            width = (0.5).dp,
                            color =
                                Color(0xffc3c3c3),
                            shape = RectangleShape
                        )
                        .drawBehind {
                            val strokeWidth = 1.5f
                            val y = strokeWidth / 2

                            drawLine(
                                Color(0xffc3c3c3),
                                Offset(0f, size.height),
                                Offset(size.height, size.height),
                                strokeWidth
                            )
                        }
                ) {

                }
                Box(
                    modifier = Modifier
                        .width(100.dp)
                        .height(100.dp)
                        .background(
                            color = when (rightSelectedArea[8]) {
                                MacularDisorderType.Normal -> Color(0xffffffff)
                                else -> Color(0xb4ea2525)
                            }
                        )
                        .border(
                            width = (0.5).dp,
                            color =
                                Color(0xffc3c3c3),
                            shape = RectangleShape
                        )
                        .drawBehind {
                            val strokeWidth = 1.5f
                            val y = strokeWidth / 2

                            drawLine(
                                Color(0xffc3c3c3),
                                Offset(0f, size.height),
                                Offset(size.height, size.height),
                                strokeWidth
                            )
                            drawLine(
                                Color(0xffc3c3c3),
                                Offset(size.height, 0f),
                                Offset(size.height, size.height),
                                strokeWidth
                            )
                        }
                ) {

                }
            }
        }
    }
}