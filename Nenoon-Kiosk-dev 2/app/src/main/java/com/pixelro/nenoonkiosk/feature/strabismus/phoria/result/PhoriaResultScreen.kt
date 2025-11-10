package com.pixelro.nenoonkiosk.feature.strabismus.phoria.result

import android.speech.tts.TextToSpeech
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.manager.StrabismusPrintHelper
import com.pixelro.nenoonkiosk.core.util.StringProvider
import com.pixelro.nenoonkiosk.core.util.TTS
import com.pixelro.nenoonkiosk.feature.inspection.components.InspectionDivider
import com.pixelro.nenoonkiosk.feature.inspection.inspectionresult.components.LightBottomArea
import com.pixelro.nenoonkiosk.feature.strabismus.common.component.ResultCard
import com.pixelro.nenoonkiosk.feature.strabismus.common.component.WarningNotice
import com.pixelro.nenoonkiosk.feature.strabismus.common.component.isLandscape
import com.pixelro.nenoonkiosk.ui.theme.Black
import com.pixelro.nenoonkiosk.ui.theme.NenoonKioskTheme
import com.pixelro.nenoonkiosk.ui.theme.bodyTextStyle
import com.pixelro.nenoonkiosk.ui.theme.buttonTextStyle
import kotlin.math.abs

/**
 * 사위 검사 결과 화면
 *
 * @param answer 검사 응답
 * @param isAdjusted 조정 여부
 * @param crossX 십자가 X 좌표
 * @param crossY 십자가 Y 좌표
 * @param circleX 원 X 좌표
 * @param circleY 원 Y 좌표
 * @param onPrintClicked 결과 출력 버튼 클릭 콜백
 * @param onBackToMainClicked 메인으로 돌아가기 버튼 클릭 콜백
 */
@Composable
fun PhoriaResultScreen(
    answer: Int?,
    isAdjusted: Boolean,
    crossX: Float?,
    crossY: Float?,
    circleX: Float?,
    circleY: Float?,
    onPrintClicked: () -> Unit,
    onBackToMainClicked: () -> Unit,
    onLogout: () -> Unit,
) {
    LaunchedEffect(Unit) {
        TTS.speechTTS(
            StringProvider.getString(R.string.tts_result_screen),
            TextToSpeech.QUEUE_FLUSH
        )
    }

    val context = LocalContext.current

    // 텍스트 리소스
    val screenTitle = StringProvider.getStringComposable(R.string.sawi_result_title)
    val normalCaseInfo = StringProvider.getStringComposable(R.string.sawi_result_normal_case_info)
    val normalExampleText = StringProvider.getStringComposable(R.string.sawi_result_normal_example)
    val myResultText = StringProvider.getStringComposable(R.string.sawi_result_my_result)
    val disclaimerText = StringProvider.getStringComposable(R.string.sawi_result_general_disclaimer)
    val printButtonText = StringProvider.getStringComposable(R.string.sawi_result_print_button)
    val backButtonText =
        StringProvider.getStringComposable(R.string.sawi_result_back_to_main_button)

    // 정상 예시 데이터
    val normalLeftTitle: String
    val normalRightTitle: String
    val normalLeftResult: String
    val normalLeftDescription: String
    val normalRightResult: String
    val normalRightDescription: String

    if (isAdjusted || answer == 1) {
        normalLeftTitle = if (isAdjusted) {
            StringProvider.getStringComposable(R.string.sawi_result_horizontal_phoria)
        } else {
            StringProvider.getStringComposable(R.string.sawi_result_left_eye)
        }
        normalRightTitle = if (isAdjusted) {
            StringProvider.getStringComposable(R.string.sawi_result_vertical_phoria)
        } else {
            StringProvider.getStringComposable(R.string.sawi_result_right_eye)
        }
        normalLeftResult =
            "${StringProvider.getStringComposable(R.string.sawi_result_normal_range_exo)}\n${
                StringProvider.getStringComposable(R.string.sawi_result_normal_range_eso)
            }"
        normalLeftDescription =
            StringProvider.getStringComposable(R.string.sawi_result_normal_range_exo_eso_desc)
        normalRightResult = "${StringProvider.getStringComposable(R.string.sawi_result_normal)}\n${
            StringProvider.getStringComposable(R.string.sawi_result_normal_range_vertical)
        }"
        normalRightDescription =
            StringProvider.getStringComposable(R.string.sawi_result_normal_range_vertical_desc)
    } else {
        normalLeftTitle = StringProvider.getStringComposable(R.string.sawi_result_left_eye)
        normalRightTitle = StringProvider.getStringComposable(R.string.sawi_result_right_eye)
        normalLeftResult = StringProvider.getStringComposable(R.string.sawi_result_normal)
        normalLeftDescription =
            StringProvider.getStringComposable(R.string.sawi_result_normal_status)
        normalRightResult = StringProvider.getStringComposable(R.string.sawi_result_normal)
        normalRightDescription =
            StringProvider.getStringComposable(R.string.sawi_result_normal_status)
    }

    // 사용자 결과 데이터
    val userLeftTitle = if (isAdjusted) {
        StringProvider.getStringComposable(R.string.sawi_result_horizontal_phoria)
    } else {
        StringProvider.getStringComposable(R.string.sawi_result_left_eye)
    }
    val userRightTitle = if (isAdjusted) {
        StringProvider.getStringComposable(R.string.sawi_result_vertical_phoria)
    } else {
        StringProvider.getStringComposable(R.string.sawi_result_right_eye)
    }

    var userHorizontalResult = ""
    var userHorizontalDescription = ""
    var userVerticalResult = ""
    var userVerticalDescription = ""
    var isHorizontalNormal = true
    var isVerticalNormal = true

    when (answer) {
        1 -> {
            userHorizontalResult = StringProvider.getStringComposable(R.string.sawi_result_normal)
            userHorizontalDescription =
                StringProvider.getStringComposable(R.string.sawi_result_normal_status)
            userVerticalResult = StringProvider.getStringComposable(R.string.sawi_result_normal)
            userVerticalDescription =
                StringProvider.getStringComposable(R.string.sawi_result_normal_status)
            isHorizontalNormal = true
            isVerticalNormal = true
        }

        2 -> {
            val dpi = 243f
            val pixelDiffX = (crossX ?: 0f) - (circleX ?: 0f)
            val hDev = (pixelDiffX / dpi * 25.4f) / 4f
            val pixelDiffY = (crossY ?: 0f) - (circleY ?: 0f)
            val vDev = (pixelDiffY / dpi * 25.4f) / 4f

            // 수평 편위 판단
            isHorizontalNormal = when {
                hDev > 0 -> abs(hDev) <= 2.5f
                hDev < 0 -> abs(hDev) <= 6.5f
                else -> true
            }

            // 수직 편위 판단
            isVerticalNormal = abs(vDev) <= 0.75f

            val (hResult, hDesc) =
                when {
                    hDev > 0 ->
                        when {
                            abs(hDev) <= 2.5f -> StringProvider.getStringComposable(R.string.sawi_result_normal_range) to StringProvider.getStringComposable(
                                R.string.sawi_result_desc_normal_eso
                            )

                            abs(hDev) <= 5.5f -> stringResource(R.string.sawi_result_mild_esophoria) to StringProvider.getStringComposable(
                                R.string.sawi_result_desc_mild_eso
                            )

                            else -> StringProvider.getStringComposable(R.string.sawi_result_severe_esophoria) to StringProvider.getStringComposable(
                                R.string.sawi_result_desc_severe_eso
                            )
                        }

                    hDev < 0 ->
                        when {
                            abs(hDev) <= 6.5f -> StringProvider.getStringComposable(R.string.sawi_result_normal_range) to StringProvider.getStringComposable(
                                R.string.sawi_result_desc_normal_exo
                            )

                            abs(hDev) <= 10.5f -> StringProvider.getStringComposable(R.string.sawi_result_mild_exophoria) to StringProvider.getStringComposable(
                                R.string.sawi_result_desc_mild_exo
                            )

                            else -> StringProvider.getStringComposable(R.string.sawi_result_severe_exophoria) to StringProvider.getStringComposable(
                                R.string.sawi_result_desc_severe_exo
                            )
                        }

                    else -> StringProvider.getStringComposable(R.string.sawi_result_normal) to StringProvider.getStringComposable(
                        R.string.sawi_result_no_horizontal_phoria
                    )
                }

            val (vResult, vDesc) =
                when {
                    abs(vDev) <= 0f -> stringResource(R.string.sawi_result_normal) to StringProvider.getStringComposable(
                        R.string.sawi_result_no_vertical_phoria
                    )

                    abs(vDev) <= 0.75f -> stringResource(R.string.sawi_result_normal_range) to StringProvider.getStringComposable(
                        R.string.sawi_result_no_vertical_phoria
                    )

                    abs(vDev) <= 1.75f -> stringResource(R.string.sawi_result_mild_vertical_phoria) to StringProvider.getStringComposable(
                        R.string.sawi_result_desc_mild_vertical
                    )

                    else -> stringResource(R.string.sawi_result_severe_vertical_phoria) to StringProvider.getStringComposable(
                        R.string.sawi_result_desc_severe_vertical
                    )
                }

            val hPrism = "(${String.format("%.1f", abs(hDev))}△)"
            val vPrism = "(${String.format("%.1f", abs(vDev))}△)"

            userHorizontalResult = "$hResult $hPrism"
            userHorizontalDescription = hDesc
            userVerticalResult = "$vResult $vPrism"
            userVerticalDescription = vDesc
        }

        3 -> {
            userHorizontalResult = StringProvider.getStringComposable(R.string.sawi_result_normal)
            userHorizontalDescription =
                StringProvider.getStringComposable(R.string.sawi_result_normal_status)
            userVerticalResult =
                StringProvider.getStringComposable(R.string.sawi_result_suppression_suspicion)
            userVerticalDescription =
                StringProvider.getStringComposable(R.string.sawi_result_desc_suppression_right)
            isHorizontalNormal = true
            isVerticalNormal = false
        }

        4 -> {
            userHorizontalResult =
                StringProvider.getStringComposable(R.string.sawi_result_suppression_suspicion)
            userHorizontalDescription =
                StringProvider.getStringComposable(R.string.sawi_result_desc_suppression_left)
            userVerticalResult = StringProvider.getStringComposable(R.string.sawi_result_normal)
            userVerticalDescription =
                StringProvider.getStringComposable(R.string.sawi_result_normal_status)
            isHorizontalNormal = false
            isVerticalNormal = true
        }

        else -> {
            userHorizontalResult = StringProvider.getStringComposable(R.string.sawi_result_error)
            userHorizontalDescription =
                StringProvider.getStringComposable(R.string.sawi_result_error_desc)
            userVerticalResult = "-"
            userVerticalDescription = ""
            isHorizontalNormal = false
            isVerticalNormal = false
        }
    }

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = screenTitle,
            style = bodyTextStyle,
            color = Color.Black,
            modifier = Modifier.padding(vertical = 32.dp),
        )
        InspectionDivider()
        if (!isLandscape()) {
//            Text(
//                text = normalCaseInfo,
//                style = inputTextStyle,
//                color = Color.DarkGray,
//                modifier = Modifier.padding(bottom = 32.dp),
//            )
//
//            Text(
//                text = normalExampleText,
//                style = buttonTextStyle,
//                color = Color.Black,
//                modifier = Modifier.padding(bottom = 16.dp),
//            )
//
//            Row(modifier = Modifier.fillMaxWidth()) {
//                ResultCard(
//                    modifier = Modifier.weight(1f),
//                    title = normalLeftTitle,
//                    result = normalLeftResult,
//                    description = normalLeftDescription,
//                )
//                Spacer(modifier = Modifier.width(16.dp))
//                ResultCard(
//                    modifier = Modifier.weight(1f),
//                    title = normalRightTitle,
//                    result = normalRightResult,
//                    description = normalRightDescription,
//                )
//            }
//
           Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = myResultText,
                style = buttonTextStyle,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 16.dp),
            )
            Row(modifier = Modifier.fillMaxWidth().padding(40.dp)) {
                ResultCard(
                    modifier = Modifier.weight(1f),
                    title = userLeftTitle,
                    result = userHorizontalResult,
                    description = userHorizontalDescription,
                    isNormal = isHorizontalNormal,
                )
                Spacer(modifier = Modifier.width(24.dp))
                ResultCard(
                    modifier = Modifier.weight(1f),
                    title = userRightTitle,
                    result = userVerticalResult,
                    description = userVerticalDescription,
                    isNormal = isVerticalNormal,
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
            Spacer(modifier = Modifier.weight(1F))
            WarningNotice(16.sp)
            LightBottomArea(
                printEnabled = true,
                onPrint = {
                    TTS.speechTTS(
                        StringProvider.getString(R.string.printing_in_progress),
                        TextToSpeech.QUEUE_FLUSH
                    )
                    StrabismusPrintHelper.printPhoriaResult(
                        context = context,
                        hTitle = userLeftTitle,
                        hResult = userHorizontalResult,
                        hDesc = userHorizontalDescription,
                        vTitle = userRightTitle,
                        vResult = userVerticalResult,
                        vDesc = userVerticalDescription,
                    )
                    onPrintClicked()
                },
                onBack = onBackToMainClicked,
                onLogout = onLogout
            )
        } else {
            Row(modifier = Modifier.fillMaxSize().weight(1F), verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.fillMaxSize().weight(2F).padding(40.dp), verticalArrangement = Arrangement.Center, ) {
                    Text(
                        text = myResultText,
                        style = buttonTextStyle,
                        color = Black,
                        modifier = Modifier.padding(bottom = 24.dp),
                    )
                    Row(modifier = Modifier.fillMaxWidth()) {
                        ResultCard(
                            modifier = Modifier.weight(1f),
                            title = userLeftTitle,
                            result = userHorizontalResult,
                            description = userHorizontalDescription,
                            isNormal = isHorizontalNormal,
                        )
                        Spacer(modifier = Modifier.width(24.dp))
                        ResultCard(
                            modifier = Modifier.weight(1f),
                            title = userRightTitle,
                            result = userVerticalResult,
                            description = userVerticalDescription,
                            isNormal = isVerticalNormal,
                        )
                    }
                }
                Spacer(modifier = Modifier.width(28.dp))
                Column(
                    modifier =
                        Modifier
                            .weight(1.2F)
                            .background(Color.White),
                    verticalArrangement = Arrangement.Center
                ) {
                    LightBottomArea(
                        printEnabled = true,
                        onPrint = {
                            TTS.speechTTS(
                                StringProvider.getString(R.string.printing_in_progress),
                                TextToSpeech.QUEUE_FLUSH
                            )
                            StrabismusPrintHelper.printPhoriaResult(
                                context = context,
                                hTitle = userLeftTitle,
                                hResult = userHorizontalResult,
                                hDesc = userHorizontalDescription,
                                vTitle = userRightTitle,
                                vResult = userVerticalResult,
                                vDesc = userVerticalDescription,
                            )
                            onPrintClicked()
                        },
                        onBack = onBackToMainClicked,
                        onLogout = onLogout
                    )
                }
            }
            WarningNotice(14.sp)
        }
    }
}


//가로 모드 내용 잘림-> 수정 필요
@Preview(showBackground = true, device = "spec:width=1280dp,height=800dp,dpi=240")
@Composable
private fun PhoriaResultScreenHorizontalPreview() {
    NenoonKioskTheme {
        PhoriaResultScreen(4, false, null, null, null, null, {}, {}, {})
    }
}

@Preview(showBackground = true, device = "spec:width=800dp,height=1280dp,dpi=240")
@Composable
private fun PhoriaResultScreenVerticalPreview() {
    NenoonKioskTheme {
        PhoriaResultScreen(1, false, null, null, null, null, {}, {}, {})
    }
}

@Preview(showBackground = true, device = "spec:width=1280dp,height=800dp,dpi=240")
@Composable
private fun PhoriaResultScreenAdjustedHorizontalPreview() {
    NenoonKioskTheme {
        PhoriaResultScreen(2, true, 100f, 50f, 120f, 60f, {}, {}, {})
    }
}

@Preview(showBackground = true, device = "spec:width=800dp,height=1280dp,dpi=240")
@Composable
private fun PhoriaResultScreenAdjustedVerticalPreview() {
    NenoonKioskTheme {
        PhoriaResultScreen(2, true, 100f, 50f, 120f, 60f, {}, {}, {})
    }
}

// 조정 모드 비정상 결과 프리뷰 (수평/수직 모두 비정상)
@Preview(showBackground = true, device = "spec:width=1280dp,height=800dp,dpi=240")
@Composable
private fun PhoriaResultScreenAdjustedAbnormalHorizontalPreview() {
    NenoonKioskTheme {
        // crossX=100, circleX=400 -> 수평 비정상, crossY=50, circleY=80 -> 수직 비정상
        PhoriaResultScreen(2, true, 100f, 50f, 400f, 80f, {}, {}, {})
    }
}

@Preview(showBackground = true, device = "spec:width=800dp,height=1280dp,dpi=240")
@Composable
private fun PhoriaResultScreenAdjustedAbnormalVerticalPreview() {
    NenoonKioskTheme {
        // crossX=100, circleX=400 -> 수평 비정상, crossY=50, circleY=80 -> 수직 비정상
        PhoriaResultScreen(2, true, 100f, 50f, 400f, 80f, {}, {}, {})
    }
}
