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
import com.pixelro.nenoonkiosk.feature.inspection.components.InspectionDivider
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
            }
            else -> {
                normalCaseVisible = false
                userLeftTitle = StringProvider.getStringComposable(R.string.sawi_result_error)
                userRightTitle = "-"
                userHorizontalResult = StringProvider.getStringComposable(R.string.sawi_result_error_desc)
                userHorizontalDescription = ""
                userVerticalResult = ""
                userVerticalDescription = ""
            }
        }



        Column(
            modifier =
                Modifier
                    .fillMaxSize()
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
                if (normalCaseVisible) {
                    Spacer(modifier = Modifier.height(36.dp))
                    Text(
                        text = normalExampleText,
                        style = buttonTextStyle,
                        color = Color.Black,
                        modifier = Modifier.padding(bottom = 16.dp),
                    )
                    Row(modifier = Modifier.fillMaxWidth()) {
                        ResultCard(
                            modifier = Modifier.weight(1f),
                            title = normalLeftTitle,
                            result = normalLeftResult,
                            description = normalLeftDescription,
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        ResultCard(
                            modifier = Modifier.weight(1f),
                            title = normalRightTitle,
                            result = normalRightResult,
                            description = normalRightDescription,
                        )
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }

                Text(
                    text = myResultText,
                    style = buttonTextStyle,
                    color = Color.Black,
                    modifier = Modifier.padding(bottom = 16.dp),
                )
                Row(modifier = Modifier.fillMaxWidth()) {
                    ResultCard(
                        modifier = Modifier.weight(1f),
                        title = userLeftTitle,
                        result = userHorizontalResult,
                        description = userHorizontalDescription,
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    ResultCard(
                        modifier = Modifier.weight(1f),
                        title = userRightTitle,
                        result = userVerticalResult,
                        description = userVerticalDescription,
                    )
                }
                Spacer(modifier = Modifier.weight(1F))
                DualButtonBottomBar(
                    primaryButtonText = printButtonText,
                    onPrimaryButtonClick = {
                        TTS.speechTTS(StringProvider.getString(R.string.printing_in_progress), TextToSpeech.QUEUE_FLUSH)
                        val differenceValue = difference ?: 0f
                        val formattedResult =
                            when {
                                differenceValue > 0 -> {
                                    StringProvider.getString(R.string.fudo_result_right_eye_larger_format, differenceValue)
                                }
                                differenceValue < 0 -> {
                                    StringProvider.getString(R.string.fudo_result_left_eye_larger_format, abs(differenceValue))
                                }
                                else -> {
                                    userHorizontalResult
                                }
                            }

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
                    secondaryButtonText = backButtonText,
                    onSecondaryButtonClick = onBackToMainClicked
                )
                WarningNotice(14.sp)
            }else {
                Row(modifier = Modifier.fillMaxSize().weight(1F), verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.fillMaxSize().weight(2F), verticalArrangement = Arrangement.Center) {
                        Text(
                            text = myResultText,
                            style = buttonTextStyle,
                            color = Color.Black,
                            modifier = Modifier.padding(bottom = 16.dp),
                        )
                        Row(modifier = Modifier.fillMaxWidth()) {
                            ResultCard(
                                modifier = Modifier.weight(1f),
                                title = userLeftTitle,
                                result = userHorizontalResult,
                                description = userHorizontalDescription,
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            ResultCard(
                                modifier = Modifier.weight(1f),
                                title = userRightTitle,
                                result = userVerticalResult,
                                description = userVerticalDescription,
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(36.dp))
                    Column(
                        modifier =
                            Modifier
                                .weight(1.2F)
                                .background(Color.White)
                                .padding(16.dp),
                        verticalArrangement = Arrangement.Center
                    ) {
                        PrimaryButton(
                            onClick = {
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
                            text = printButtonText,
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        SecondaryButton(
                            onClick = onBackToMainClicked,
                            text = backButtonText,
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
        )
    }
}
