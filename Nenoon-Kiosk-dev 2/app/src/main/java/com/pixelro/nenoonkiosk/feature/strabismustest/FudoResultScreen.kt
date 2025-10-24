package com.pixelro.nenoonkiosk.feature.strabismustest

import android.speech.tts.TextToSpeech
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.util.StringProvider
import com.pixelro.nenoonkiosk.core.util.StringProviderForSawi
import com.pixelro.nenoonkiosk.core.util.TTS
import com.pixelro.nenoonkiosk.feature.inspection.strabismus.StrabismusPrintHelper
import com.pixelro.nenoonkiosk.ui.theme.neNoon_blue
import kotlin.math.abs

@Composable
fun FudoResultScreen(
    answer: Int?,
    difference: Float?,
    onPrintClicked: () -> Unit,
    onBackToMainClicked: () -> Unit,
) {
    LaunchedEffect(Unit) {
        TTS.speechTTS(StringProvider.getString(R.string.tts_result_screen), TextToSpeech.QUEUE_FLUSH)
    }

    val context = LocalContext.current

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
            text = StringProvider.getString(R.string.fudo_result_title),
            fontSize = 40.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(top = 32.dp, bottom = 16.dp),
        )

        Text(
            text = StringProvider.getString(R.string.sawi_result_normal_case_info),
            fontSize = 24.sp,
            color = Color.DarkGray,
            modifier = Modifier.padding(bottom = 32.dp),
        )

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
                normalLeftTitle = StringProvider.getString(R.string.fudo_result_retinal_image_size_diff)
                normalLeftResult = "0 ~ 2%"
                normalLeftDescription = StringProvider.getString(R.string.fudo_result_no_size_diff)
                normalRightTitle = StringProvider.getString(R.string.fudo_result_clinical_opinion)
                normalRightResult = StringProvider.getString(R.string.sawi_result_normal)
                normalRightDescription = StringProvider.getString(R.string.fudo_result_normal_desc)

                val differenceValue = difference ?: 0f
                userLeftTitle = StringProvider.getString(R.string.fudo_result_retinal_image_size_diff)
                when {
                    differenceValue > 0 -> {
                        userHorizontalResult = StringProviderForSawi.getString(R.string.fudo_result_right_eye_larger, differenceValue)
                        userHorizontalDescription = StringProvider.getString(R.string.fudo_result_right_eye_larger_desc)
                    }
                    differenceValue < 0 -> {
                        userHorizontalResult = StringProviderForSawi.getString(R.string.fudo_result_left_eye_larger, abs(differenceValue))
                        userHorizontalDescription = StringProvider.getString(R.string.fudo_result_left_eye_larger_desc)
                    }
                    else -> {
                        userHorizontalResult = StringProvider.getString(R.string.fudo_result_none)
                        userHorizontalDescription = StringProvider.getString(R.string.fudo_result_no_size_diff)
                    }
                }

                userRightTitle = StringProvider.getString(R.string.fudo_result_clinical_opinion)
                val (result, description) =
                    when {
                        abs(differenceValue) < 0.5f -> StringProvider.getString(R.string.sawi_result_normal) to StringProvider.getString(R.string.fudo_result_normal_desc)
                        abs(differenceValue) <= 2f -> StringProvider.getString(R.string.sawi_result_normal_range) to StringProvider.getString(R.string.fudo_result_normal_desc)
                        abs(differenceValue) <= 4f -> StringProvider.getString(R.string.fudo_result_mild_aniseikonia) to StringProvider.getString(R.string.fudo_result_desc_mild)
                        else -> StringProvider.getString(R.string.fudo_result_severe_aniseikonia) to StringProvider.getString(R.string.fudo_result_desc_severe)
                    }
                userVerticalResult = result
                userVerticalDescription = description
            }
            2 -> {
                normalCaseVisible = true
                normalLeftTitle = StringProvider.getString(R.string.sawi_result_left_eye)
                normalLeftResult = StringProvider.getString(R.string.sawi_result_normal)
                normalLeftDescription = StringProvider.getString(R.string.sawi_result_normal_status)
                normalRightTitle = StringProvider.getString(R.string.sawi_result_right_eye)
                normalRightResult = StringProvider.getString(R.string.sawi_result_normal)
                normalRightDescription = StringProvider.getString(R.string.sawi_result_normal_status)

                userLeftTitle = StringProvider.getString(R.string.sawi_result_left_eye)
                userRightTitle = StringProvider.getString(R.string.sawi_result_right_eye)
                userHorizontalResult = StringProvider.getString(R.string.sawi_result_normal)
                userHorizontalDescription = StringProvider.getString(R.string.sawi_result_normal_status)
                userVerticalResult = StringProvider.getString(R.string.sawi_result_suppression_suspicion)
                userVerticalDescription = StringProvider.getString(R.string.sawi_result_desc_suppression_right)
            }
            3 -> {
                normalCaseVisible = true
                normalLeftTitle = StringProvider.getString(R.string.sawi_result_left_eye)
                normalLeftResult = StringProvider.getString(R.string.sawi_result_normal)
                normalLeftDescription = StringProvider.getString(R.string.sawi_result_normal_status)
                normalRightTitle = StringProvider.getString(R.string.sawi_result_right_eye)
                normalRightResult = StringProvider.getString(R.string.sawi_result_normal)
                normalRightDescription = StringProvider.getString(R.string.sawi_result_normal_status)

                userLeftTitle = StringProvider.getString(R.string.sawi_result_left_eye)
                userRightTitle = StringProvider.getString(R.string.sawi_result_right_eye)
                userHorizontalResult = StringProvider.getString(R.string.sawi_result_suppression_suspicion)
                userHorizontalDescription = StringProvider.getString(R.string.sawi_result_desc_suppression_left)
                userVerticalResult = StringProvider.getString(R.string.sawi_result_normal)
                userVerticalDescription = StringProvider.getString(R.string.sawi_result_normal_status)
            }
            4 -> {
                normalCaseVisible = true
                normalLeftTitle = StringProvider.getString(R.string.sawi_result_left_eye)
                normalLeftResult = StringProvider.getString(R.string.sawi_result_normal)
                normalLeftDescription = StringProvider.getString(R.string.sawi_result_normal_status)
                normalRightTitle = StringProvider.getString(R.string.sawi_result_right_eye)
                normalRightResult = StringProvider.getString(R.string.sawi_result_normal)
                normalRightDescription = StringProvider.getString(R.string.sawi_result_normal_status)

                userLeftTitle = StringProvider.getString(R.string.sawi_result_left_eye)
                userRightTitle = StringProvider.getString(R.string.sawi_result_right_eye)

                userHorizontalResult = StringProvider.getString(R.string.sawi_result_suppression_suspicion)
                userHorizontalDescription = StringProvider.getString(R.string.sawi_result_desc_suppression_left)
                userVerticalResult = StringProvider.getString(R.string.sawi_result_suppression_suspicion)
                userVerticalDescription = StringProvider.getString(R.string.sawi_result_desc_suppression_right)
            }
            else -> {
                normalCaseVisible = false
                userLeftTitle = StringProvider.getString(R.string.sawi_result_error)
                userRightTitle = "-"
                userHorizontalResult = StringProvider.getString(R.string.sawi_result_error_desc)
                userHorizontalDescription = ""
                userVerticalResult = ""
                userVerticalDescription = ""
            }
        }

        if (normalCaseVisible) {
            Text(
                text = StringProvider.getString(R.string.sawi_result_normal_example),
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
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
            text = StringProvider.getString(R.string.sawi_result_my_result),
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
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

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = StringProvider.getString(R.string.sawi_result_general_disclaimer),
            fontSize = 12.sp,
            color = Color.DarkGray,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp),
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                TTS.speechTTS(StringProvider.getString(R.string.printing_in_progress), TextToSpeech.QUEUE_FLUSH)
                val differenceValue = difference ?: 0f
                val formattedResult =
                    when {
                        differenceValue > 0 -> {
                            StringProviderForSawi.getString(R.string.fudo_result_right_eye_larger_format, differenceValue)
                        }
                        differenceValue < 0 -> {
                            StringProviderForSawi.getString(R.string.fudo_result_left_eye_larger_format, abs(differenceValue))
                        }
                        else -> {
                            userHorizontalResult
                        }
                    }

                StrabismusPrintHelper.printFudoResult(
                    context = context,
                    retinalTitle = userLeftTitle,
//                    retinalResult = formattedResult,
                    opinionTitle = userRightTitle,
                    opinionResult = userVerticalResult,
                    retinalDescription = userHorizontalDescription,
                    opinionDescription = userVerticalDescription,
                )
            },
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(96.dp),
            colors = ButtonDefaults.buttonColors(containerColor = neNoon_blue),
            shape = RoundedCornerShape(12.dp),
        ) {
            Text(StringProvider.getString(R.string.sawi_result_print_button), fontSize = 36.sp, color = Color.White)
        }

        Button(
            onClick = onBackToMainClicked,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(96.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
            shape = RoundedCornerShape(12.dp),
        ) {
            Text(StringProvider.getString(R.string.sawi_result_back_to_main_button), fontSize = 36.sp, color = neNoon_blue)
        }
    }
}

@Composable
fun AutoSizedText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    style: TextStyle = LocalTextStyle.current,
) {
    var scaledTextStyle by remember { mutableStateOf(style) }
    var readyToDraw by remember { mutableStateOf(false) }

    Text(
        text,
        modifier =
            modifier.drawWithContent {
                if (readyToDraw) {
                    drawContent()
                }
            },
        color = color,
        style = scaledTextStyle,
        softWrap = false,
        maxLines = 1,
        onTextLayout = { textLayoutResult ->
            if (textLayoutResult.didOverflowWidth && !readyToDraw) {
                val newFontSize = scaledTextStyle.fontSize * 0.95
                scaledTextStyle = scaledTextStyle.copy(fontSize = newFontSize)
            } else {
                readyToDraw = true
            }
        },
    )
}

@Composable
fun ResultCard(
    modifier: Modifier = Modifier,
    title: String,
    result: String,
    description: String,
) {
    Card(
        modifier = modifier.height(240.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .height(IntrinsicSize.Min),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
        ) {
            AutoSizedText(
                text = title,
                style =
                    TextStyle(
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                    ),
                color = Color.Black,
            )
            AutoSizedText(
                text = result,
                style =
                    TextStyle(
                        fontSize = 42.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                    ),
                color = neNoon_blue,
            )
            Text(
                text = description,
                fontSize = 16.sp,
                color = Color.DarkGray,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
fun SawiResultCard(
    modifier: Modifier = Modifier,
    title: String,
    result1: String,
    result2: String,
    description: String,
) {
    Card(
        modifier = modifier.height(220.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        // 👇 이 Column의 속성이 변경되었습니다.
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            AutoSizedText(
                text = title,
                style =
                    TextStyle(
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                    ),
                color = Color.Black,
            )
            AutoSizedText(
                text = result1,
                style =
                    TextStyle(
                        fontSize = 42.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                    ),
                color = neNoon_blue,
            )

            if (result2.isNotEmpty()) {
                AutoSizedText(
                    text = result2,
                    style =
                        TextStyle(
                            fontSize = 42.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                        ),
                    color = neNoon_blue,
                )
            }
            Spacer(modifier = Modifier.height(1.dp))

            Text(
                text = description,
                fontSize = 16.sp,
                color = Color.DarkGray,
                textAlign = TextAlign.Center,
            )
        }
    }
}
