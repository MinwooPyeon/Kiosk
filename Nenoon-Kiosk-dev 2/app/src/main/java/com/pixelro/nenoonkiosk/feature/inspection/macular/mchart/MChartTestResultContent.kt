package com.pixelro.nenoonkiosk.feature.inspection.macular.mchart

import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.util.StringProvider

@Composable
fun MChartTestResultContent(
    testResult: MChartTestResult,
    navController: NavHostController,
) {
    val result = testResult
    Column(
        modifier =
            Modifier
                .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            modifier =
                Modifier
                    .padding(start = 40.dp, top = 40.dp)
                    .fillMaxWidth(),
            text = StringProvider.getString(R.string.test_result_my_result),
            fontSize = 28.sp,
            fontWeight = FontWeight.Medium,
        )
        Row(
            modifier =
                Modifier
                    .padding(start = 40.dp, top = 20.dp, end = 40.dp)
                    .fillMaxWidth(),
        ) {
            Column(
                modifier =
                    Modifier
                        .weight(1f)
                        .height(200.dp)
                        .background(
                            color = Color(0xfff7f9f9),
                            shape = RoundedCornerShape(8.dp),
                        )
                        .padding(20.dp),
            ) {
                Text(
                    text =
                        StringProvider.getString(
                            R.string.test_result_left,
                        ),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                )
                /**
                 * 왼쪽 눈 검사 결과
                 */
                Box(
                    modifier =
                        Modifier
                            .padding(top = 20.dp)
                            .fillMaxWidth(),
                    contentAlignment = Alignment.CenterStart,
                ) {
                    Text(
                        modifier =
                            Modifier
                                .background(
                                    color = Color(0xffdcebff),
                                    shape = RoundedCornerShape(4.dp),
                                )
                                .padding(start = 12.dp, top = 4.dp, end = 12.dp, bottom = 4.dp),
                        text =
                            StringProvider.getString(
                                R.string.mchart_result_vertical,
                            ),
                        color = Color(0xff1d71e1),
                        fontSize = 20.sp,
                    )
                    Box(
                        modifier =
                            Modifier
                                .padding(end = 20.dp)
                                .fillMaxWidth(),
                        contentAlignment = Alignment.CenterEnd,
                    ) {
                        Text(
                            modifier =
                                Modifier
                                    .padding(start = 100.dp),
                            text = "${String.format("%.1f", testResult.leftEyeVertical.toFloat() / 10)}°",
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
                Box(
                    modifier =
                        Modifier
                            .padding(top = 20.dp)
                            .fillMaxWidth(),
                    contentAlignment = Alignment.CenterStart,
                ) {
                    Text(
                        modifier =
                            Modifier
                                .background(
                                    color = Color(0xfffff8de),
                                    shape = RoundedCornerShape(4.dp),
                                )
                                .padding(start = 12.dp, top = 4.dp, end = 12.dp, bottom = 4.dp),
                        text =
                            StringProvider.getString(
                                R.string.mchart_result_horizontal,
                            ),
                        color = Color(0xffffb800),
                        fontSize = 20.sp,
                    )
                    Box(
                        modifier =
                            Modifier
                                .padding(end = 20.dp)
                                .fillMaxWidth(),
                        contentAlignment = Alignment.CenterEnd,
                    ) {
                        Text(
                            modifier =
                                Modifier
                                    .padding(start = 100.dp),
                            text = "${String.format("%.1f", testResult.leftEyeHorizontal.toFloat() / 10)}°",
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }
            Spacer(
                modifier =
                    Modifier
                        .width(20.dp),
            )
            Column(
                modifier =
                    Modifier
                        .weight(1f)
                        .height(200.dp)
                        .background(
                            color = Color(0xfff7f9f9),
                            shape = RoundedCornerShape(8.dp),
                        )
                        .padding(20.dp),
            ) {
                Text(
                    text =
                        StringProvider.getString(
                            R.string.test_result_right,
                        ),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                )
                /**
                 * 오른쪽 눈 검사 결과
                 */
                Box(
                    modifier =
                        Modifier
                            .padding(top = 20.dp)
                            .fillMaxWidth(),
                    contentAlignment = Alignment.CenterStart,
                ) {
                    Text(
                        modifier =
                            Modifier
                                .background(
                                    color = Color(0xffdcebff),
                                    shape = RoundedCornerShape(4.dp),
                                )
                                .padding(start = 12.dp, top = 4.dp, end = 12.dp, bottom = 4.dp),
                        text =
                            StringProvider.getString(
                                R.string.mchart_result_vertical,
                            ),
                        color = Color(0xff1d71e1),
                        fontSize = 20.sp,
                    )
                    Box(
                        modifier =
                            Modifier
                                .padding(end = 20.dp)
                                .fillMaxWidth(),
                        contentAlignment = Alignment.CenterEnd,
                    ) {
                        Text(
                            modifier =
                                Modifier
                                    .padding(start = 100.dp),
                            text = "${String.format("%.1f", testResult.rightEyeVertical.toFloat() / 10)}°",
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
                Box(
                    modifier =
                        Modifier
                            .padding(top = 20.dp)
                            .fillMaxWidth(),
                    contentAlignment = Alignment.CenterStart,
                ) {
                    Text(
                        modifier =
                            Modifier
                                .background(
                                    color = Color(0xfffff8de),
                                    shape = RoundedCornerShape(4.dp),
                                )
                                .padding(start = 12.dp, top = 4.dp, end = 12.dp, bottom = 4.dp),
                        text =
                            StringProvider.getString(
                                R.string.mchart_result_horizontal,
                            ),
                        color = Color(0xffffb800),
                        fontSize = 20.sp,
                    )
                    Box(
                        modifier =
                            Modifier
                                .padding(end = 20.dp)
                                .fillMaxWidth(),
                        contentAlignment = Alignment.CenterEnd,
                    ) {
                        Text(
                            modifier =
                                Modifier
                                    .padding(start = 100.dp),
                            text = "${String.format("%.1f", testResult.rightEyeHorizontal.toFloat() / 10)}°",
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun MChartTestResultContentScreenPreview() {
    MChartTestResultContent(
        testResult =
            MChartTestResult(
                leftEyeVertical = 0,
                leftEyeHorizontal = 0,
                rightEyeVertical = 0,
                rightEyeHorizontal = 0,
            ),
        navController = NavHostController(LocalContext.current),
    )
}
