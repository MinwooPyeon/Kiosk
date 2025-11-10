package com.pixelro.nenoonkiosk.feature.strabismus.aniseikonia.result

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.util.StringProvider
import com.pixelro.nenoonkiosk.core.util.TTS
import com.pixelro.nenoonkiosk.core.manager.StrabismusPrintHelper
import com.pixelro.nenoonkiosk.core.ui.PrimaryButton
import com.pixelro.nenoonkiosk.core.ui.SecondaryButton
import com.pixelro.nenoonkiosk.core.util.StringProvider
import com.pixelro.nenoonkiosk.core.util.TTS
import com.pixelro.nenoonkiosk.feature.inspection.components.InspectionDivider
import com.pixelro.nenoonkiosk.feature.inspection.inspectionresult.components.LightBottomArea
import com.pixelro.nenoonkiosk.feature.strabismus.common.component.DualButtonBottomBar
import com.pixelro.nenoonkiosk.feature.strabismus.common.component.ResultCard
import com.pixelro.nenoonkiosk.feature.strabismus.common.component.WarningNotice
import com.pixelro.nenoonkiosk.feature.strabismus.common.component.isLandscape
import com.pixelro.nenoonkiosk.ui.theme.NenoonKioskTheme
import com.pixelro.nenoonkiosk.ui.theme.bodyTextStyle
import com.pixelro.nenoonkiosk.ui.theme.buttonTextStyle
import kotlin.math.abs

/**
 * 부등상시 검사 결과 화면
 *
 * @param answer 검사 응답
 * @param difference 양안 크기 차이
 * @param onPrintClicked 결과 출력 버튼 클릭 콜백
 * @param onBackToMainClicked 메인으로 돌아가기 버튼 클릭 콜백
 */
@Composable
fun AniseikoniaResultScreen(
    answer: Int?,
    difference: Float?,
    onPrintClicked: () -> Unit,
    onBackToMainClicked: () -> Unit,
    onLogout: () -> Unit,
) {
    LaunchedEffect(Unit) {
        TTS.speechTTS(StringProvider.getString(R.string.tts_result_screen), TextToSpeech.QUEUE_FLUSH)
    }

    val context = LocalContext.current

    // 텍스트 리소스
    val screenTitle = StringProvider.getStringComposable(R.string.fudo_result_title)
    val normalCaseInfo = StringProvider.getStringComposable(R.string.sawi_result_normal_case_info)
    val normalExampleText = StringProvider.getStringComposable(R.string.sawi_result_normal_example)
    val myResultText = StringProvider.getStringComposable(R.string.sawi_result_my_result)
    val disclaimerText = StringProvider.getStringComposable(R.string.sawi_result_general_disclaimer)
    val printButtonText = StringProvider.getStringComposable(R.string.sawi_result_print_button)
    val backButtonText = StringProvider.getStringComposable(R.string.sawi_result_back_to_main_button)

    // 데이터 변수
    var normalCaseVisible = false
    var normalLeftTitle = ""
    var normalLeftResult = ""
    var normalLeftDescription = ""
    var normalRightTitle = ""
    var normalRightResult = ""
    var normalRightDescription = ""

    var userLeftTitle = ""
    var userRightTitle = ""
    var userHorizontalResult = ""
    var userHorizontalDescription = ""
    var userVerticalResult = ""
    var userVerticalDescription = ""
    var isLeftNormal = true
    var isRightNormal = true

    when (answer) {
            1 -> {
                normalCaseVisible = true
                normalLeftTitle = StringProvider.getStringComposable(R.string.fudo_result_retinal_image_size_diff)
                normalLeftResult = "0 ~ 2%"
                normalLeftDescription = StringProvider.getStringComposable(R.string.fudo_result_no_size_diff)
                normalRightTitle = StringProvider.getStringComposable(R.string.fudo_result_clinical_opinion)
                normalRightResult = StringProvider.getStringComposable(R.string.sawi_result_normal)
                normalRightDescription = StringProvider.getStringComposable(R.string.fudo_result_normal_desc)

                val differenceValue = difference ?: 0f
                userLeftTitle = StringProvider.getStringComposable(R.string.fudo_result_retinal_image_size_diff)
                when {
                    differenceValue > 0 -> {
                        userHorizontalResult = StringProvider.getStringComposable(R.string.fudo_result_right_eye_larger, differenceValue)
                        userHorizontalDescription = StringProvider.getStringComposable(R.string.fudo_result_right_eye_larger_desc)
                    }
                    differenceValue < 0 -> {
                        userHorizontalDescription = StringProvider.getStringComposable(R.string.fudo_result_left_eye_larger_desc)
                    }
                    else -> {
                        userHorizontalResult = StringProvider.getStringComposable(R.string.fudo_result_none)
                        userHorizontalDescription = StringProvider.getStringComposable(R.string.fudo_result_no_size_diff)
                    }
                }

                userRightTitle = StringProvider.getStringComposable(R.string.fudo_result_clinical_opinion)

                // 정상 여부 판단
                isLeftNormal = true  // 망막 상 크기 차이는 측정값만 표시하므로 항상 정상색
                isRightNormal = abs(differenceValue) <= 2f  // 임상 의견: 2% 이하면 정상

                val (result, description) =
                    when {
                        abs(differenceValue) < 0.5f -> StringProvider.getStringComposable(R.string.sawi_result_normal) to StringProvider.getStringComposable(R.string.fudo_result_normal_desc)
                        abs(differenceValue) <= 2f -> StringProvider.getStringComposable(R.string.sawi_result_normal_range) to StringProvider.getStringComposable(R.string.fudo_result_normal_desc)
                        abs(differenceValue) <= 4f -> StringProvider.getStringComposable(R.string.fudo_result_mild_aniseikonia) to StringProvider.getStringComposable(R.string.fudo_result_desc_mild)
                        else -> StringProvider.getStringComposable(R.string.fudo_result_severe_aniseikonia) to StringProvider.getStringComposable(R.string.fudo_result_desc_severe)
                    }
                userVerticalResult = result
                userVerticalDescription = description
            }
            2 -> {
                normalCaseVisible = true
                normalLeftTitle = StringProvider.getStringComposable(R.string.sawi_result_left_eye)
                normalLeftResult = StringProvider.getStringComposable(R.string.sawi_result_normal)
                normalLeftDescription = StringProvider.getStringComposable(R.string.sawi_result_normal_status)
                normalRightTitle = StringProvider.getStringComposable(R.string.sawi_result_right_eye)
                normalRightResult = StringProvider.getStringComposable(R.string.sawi_result_normal)
                normalRightDescription = StringProvider.getStringComposable(R.string.sawi_result_normal_status)

                userLeftTitle = StringProvider.getStringComposable(R.string.sawi_result_left_eye)
                userRightTitle = StringProvider.getStringComposable(R.string.sawi_result_right_eye)
                userHorizontalResult = StringProvider.getStringComposable(R.string.sawi_result_normal)
                userHorizontalDescription = StringProvider.getStringComposable(R.string.sawi_result_normal_status)
                userVerticalResult = StringProvider.getStringComposable(R.string.sawi_result_suppression_suspicion)
                userVerticalDescription = StringProvider.getStringComposable(R.string.sawi_result_desc_suppression_right)
                isLeftNormal = true
                isRightNormal = false
            }
            3 -> {
                normalCaseVisible = true
                normalLeftTitle = StringProvider.getStringComposable(R.string.sawi_result_left_eye)
                normalLeftResult = StringProvider.getStringComposable(R.string.sawi_result_normal)
                normalLeftDescription = StringProvider.getStringComposable(R.string.sawi_result_normal_status)
                normalRightTitle = StringProvider.getStringComposable(R.string.sawi_result_right_eye)
                normalRightResult = StringProvider.getStringComposable(R.string.sawi_result_normal)
                normalRightDescription = StringProvider.getStringComposable(R.string.sawi_result_normal_status)

                userLeftTitle = StringProvider.getStringComposable(R.string.sawi_result_left_eye)
                userRightTitle = StringProvider.getStringComposable(R.string.sawi_result_right_eye)
                userHorizontalResult = StringProvider.getStringComposable(R.string.sawi_result_suppression_suspicion)
                userHorizontalDescription = StringProvider.getStringComposable(R.string.sawi_result_desc_suppression_left)
                userVerticalResult = StringProvider.getStringComposable(R.string.sawi_result_normal)
                userVerticalDescription = StringProvider.getStringComposable(R.string.sawi_result_normal_status)
                isLeftNormal = false
                isRightNormal = true
            }
            4 -> {
                normalCaseVisible = true
                normalLeftTitle = StringProvider.getStringComposable(R.string.sawi_result_left_eye)
                normalLeftResult = StringProvider.getStringComposable(R.string.sawi_result_normal)
                normalLeftDescription = StringProvider.getStringComposable(R.string.sawi_result_normal_status)
                normalRightTitle = StringProvider.getStringComposable(R.string.sawi_result_right_eye)
                normalRightResult = StringProvider.getStringComposable(R.string.sawi_result_normal)
                normalRightDescription = StringProvider.getStringComposable(R.string.sawi_result_normal_status)

                userLeftTitle = StringProvider.getStringComposable(R.string.sawi_result_left_eye)
                userRightTitle = StringProvider.getStringComposable(R.string.sawi_result_right_eye)

                userHorizontalResult = StringProvider.getStringComposable(R.string.sawi_result_suppression_suspicion)
                userHorizontalDescription = StringProvider.getStringComposable(R.string.sawi_result_desc_suppression_left)
                userVerticalResult = StringProvider.getStringComposable(R.string.sawi_result_suppression_suspicion)
                userVerticalDescription = StringProvider.getStringComposable(R.string.sawi_result_desc_suppression_right)
                isLeftNormal = false
                isRightNormal = false
            }
            else -> {
                normalCaseVisible = false
                userLeftTitle = StringProvider.getStringComposable(R.string.sawi_result_error)
                userRightTitle = "-"
                userHorizontalResult = StringProvider.getStringComposable(R.string.sawi_result_error_desc)
                userHorizontalDescription = ""
                userVerticalResult = ""
                userVerticalDescription = ""
                isLeftNormal = false
                isRightNormal = false
            }
        }



        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = screenTitle,
                style = bodyTextStyle,
                color = Color.Black,
                modifier = Modifier.padding(top = 32.dp, bottom = 32.dp),
            )
            InspectionDivider()
            if(!isLandscape()) {
//                if (normalCaseVisible) {
//                    Spacer(modifier = Modifier.height(36.dp))
//                    Text(
//                        text = normalExampleText,
//                        style = buttonTextStyle,
//                        color = Color.Black,
//                        modifier = Modifier.padding(bottom = 16.dp),
//                    )
//                    Row(modifier = Modifier.fillMaxWidth()) {
//                        ResultCard(
//                            modifier = Modifier.weight(1f),
//                            title = normalLeftTitle,
//                            result = normalLeftResult,
//                            description = normalLeftDescription,
//                        )
//                        Spacer(modifier = Modifier.width(16.dp))
//                        ResultCard(
//                            modifier = Modifier.weight(1f),
//                            title = normalRightTitle,
//                            result = normalRightResult,
//                            description = normalRightDescription,
//                        )
//                    }
//
//                }
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
                        isNormal = isLeftNormal,
                    )
                    Spacer(modifier = Modifier.width(24.dp))
                    ResultCard(
                        modifier = Modifier.weight(1f),
                        title = userRightTitle,
                        result = userVerticalResult,
                        description = userVerticalDescription,
                        isNormal = isRightNormal,
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
                Spacer(modifier = Modifier.weight(1F))
                WarningNotice(16.sp)
                LightBottomArea(
                    printEnabled = true,
                    onPrint = {
                        TTS.speechTTS(StringProvider.getString(R.string.printing_in_progress), TextToSpeech.QUEUE_FLUSH)
                        StrabismusPrintHelper.printAniseikoniaResult(
                            context = context,
                            retinalTitle = userLeftTitle,
                            opinionTitle = userRightTitle,
                            opinionResult = userVerticalResult,
                            retinalDescription = userHorizontalDescription,
                            opinionDescription = userVerticalDescription,
                        )
                        onPrintClicked()
                    },
                    onBack = onBackToMainClicked,
                    onLogout = onLogout
                )
            }else {
                Row(modifier = Modifier.fillMaxSize().weight(1F), verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.fillMaxSize().weight(2F).padding(40.dp), verticalArrangement = Arrangement.Center) {
                        Text(
                            text = myResultText,
                            style = buttonTextStyle,
                            color = Color.Black,
                            modifier = Modifier.padding(bottom = 24.dp),
                        )
                        Row(modifier = Modifier.fillMaxWidth()) {
                            ResultCard(
                                modifier = Modifier.weight(1f),
                                title = userLeftTitle,
                                result = userHorizontalResult,
                                description = userHorizontalDescription,
                                isNormal = isLeftNormal,
                            )
                            Spacer(modifier = Modifier.width(24.dp))
                            ResultCard(
                                modifier = Modifier.weight(1f),
                                title = userRightTitle,
                                result = userVerticalResult,
                                description = userVerticalDescription,
                                isNormal = isRightNormal,
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
                                StrabismusPrintHelper.printAniseikoniaResult(
                                    context = context,
                                    retinalTitle = userLeftTitle,
//                        retinalResult = formattedResult,
                                    opinionTitle = userRightTitle,
                                    opinionResult = userVerticalResult,
                                    retinalDescription = userHorizontalDescription,
                                    opinionDescription = userVerticalDescription,
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
private fun AniseikoniaResultScreenHorizontalPreview() {
    NenoonKioskTheme {
        AniseikoniaResultScreen(
            answer = 1,
            difference = 1.5f,
            onPrintClicked = {},
            onBackToMainClicked = {},
            onLogout = {},
        )
    }
}

@Preview(showBackground = true, device = "spec:width=800dp,height=1280dp,dpi=240")
@Composable
private fun AniseikoniaResultScreenVerticalPreview() {
    NenoonKioskTheme {
        AniseikoniaResultScreen(
            answer = 1,
            difference = 1.5f,
            onPrintClicked = {},
            onBackToMainClicked = {},
            onLogout = {},
        )
    }
}
