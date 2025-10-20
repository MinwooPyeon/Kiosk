package com.pixelro.nenoonkiosk.feature.strabismustest

import android.speech.tts.TextToSpeech
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.strabismustest.ui.theme.neNoon_blue
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.util.TTS
import com.pixelro.nenoonkiosk.core.util.StringProvider
import com.pixelro.nenoonkiosk.feature.inspection.strabismus.StrabismusPrintHelper
import kotlin.math.abs

@Composable
fun SawiResultScreen(
    answer: Int?,
    isAdjusted: Boolean,
    crossX: Float?,
    crossY: Float?,
    circleX: Float?,
    circleY: Float?,
    onPrintClicked: () -> Unit,
    onBackToMainClicked: () -> Unit
) {
    LaunchedEffect(Unit) {
        TTS.speechTTS(StringProvider.getString(R.string.tts_result_screen), TextToSpeech.QUEUE_FLUSH)
    }

    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = StringProvider.getString(R.string.sawi_result_title),
            fontSize = 40.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(top = 32.dp, bottom = 16.dp)
        )

        Text(
            text = StringProvider.getString(R.string.sawi_result_normal_case_info),
            fontSize = 24.sp,
            color = Color.DarkGray,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        val normalLeftTitle: String
        val normalLeftDescription: String
        val normalRightTitle: String
        val normalRightDescription: String

        if (isAdjusted) {
            normalLeftTitle = StringProvider.getString(R.string.sawi_result_horizontal_phoria)
            normalLeftDescription = StringProvider.getString(R.string.sawi_result_no_horizontal_phoria)
            normalRightTitle = StringProvider.getString(R.string.sawi_result_vertical_phoria)
            normalRightDescription = StringProvider.getString(R.string.sawi_result_no_vertical_phoria)
        } else {
            normalLeftTitle = StringProvider.getString(R.string.sawi_result_left_eye)
            normalLeftDescription = StringProvider.getString(R.string.sawi_result_normal_status)
            normalRightTitle = StringProvider.getString(R.string.sawi_result_right_eye)
            normalRightDescription = StringProvider.getString(R.string.sawi_result_normal_status)
        }

        Text(
            text = StringProvider.getString(R.string.sawi_result_normal_example),
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        if(isAdjusted || answer == 1) {
            Row(modifier = Modifier.fillMaxWidth()) {
                SawiResultCard(
                    modifier = Modifier.weight(1f),
                    title = normalLeftTitle,
                    result1 = StringProvider.getString(R.string.sawi_result_normal_range_exo),
                    result2 = StringProvider.getString(R.string.sawi_result_normal_range_eso),
                    description = StringProvider.getString(R.string.sawi_result_normal_range_exo_eso_desc)
                )
                Spacer(modifier = Modifier.width(16.dp))
                SawiResultCard(
                    modifier = Modifier.weight(1f),
                    title = normalRightTitle,
                    result1 = StringProvider.getString(R.string.sawi_result_normal),
                    result2 = StringProvider.getString(R.string.sawi_result_normal_range_vertical),
                    description = StringProvider.getString(R.string.sawi_result_normal_range_vertical_desc)
                )
            }
        }
        else {
            Row(modifier = Modifier.fillMaxWidth()) {
                SawiResultCard(
                    modifier = Modifier.weight(1f),
                    title = normalLeftTitle,
                    result1 = StringProvider.getString(R.string.sawi_result_normal),
                    result2 =  "",
                    description = StringProvider.getString(R.string.sawi_result_normal_status)
                )
                Spacer(modifier = Modifier.width(16.dp))
                SawiResultCard(
                    modifier = Modifier.weight(1f),
                    title = normalRightTitle,
                    result1 = StringProvider.getString(R.string.sawi_result_normal),
                    result2 = "",
                    description = StringProvider.getString(R.string.sawi_result_normal_status)
                )
            }
        }
        Spacer(modifier = Modifier.height(24.dp))

        val userLeftTitle: String
        val userRightTitle: String
        var userHorizontalResult = ""
        var userHorizontalDescription = ""
        var userVerticalResult = ""
        var userVerticalDescription = ""

        if (isAdjusted) {
            userLeftTitle = StringProvider.getString(R.string.sawi_result_horizontal_phoria)
            userRightTitle = StringProvider.getString(R.string.sawi_result_vertical_phoria)
        } else {
            userLeftTitle = StringProvider.getString(R.string.sawi_result_left_eye)
            userRightTitle = StringProvider.getString(R.string.sawi_result_right_eye)
        }

        when (answer) {
            1 -> {
                userHorizontalResult = StringProvider.getString(R.string.sawi_result_normal)
                userHorizontalDescription = StringProvider.getString(R.string.sawi_result_normal_status)
                userVerticalResult = StringProvider.getString(R.string.sawi_result_normal)
                userVerticalDescription = StringProvider.getString(R.string.sawi_result_normal_status)
            }
            2 -> { // Adjustment was made
                val dpi = 243f

                val pixelDiffX = (crossX ?: 0f) - (circleX ?: 0f)
                val hDev = (pixelDiffX / dpi * 25.4f) / 4f

                val pixelDiffY = (crossY ?: 0f) - (circleY ?: 0f)
                val vDev = (pixelDiffY / dpi * 25.4f) / 4f

                val (hResult, hDesc) = when {
                    hDev > 0 -> when { // Esophoria
                        abs(hDev) <= 2.5f -> StringProvider.getString(R.string.sawi_result_normal_range) to StringProvider.getString(R.string.sawi_result_desc_normal_eso)
                        abs(hDev) <= 5.5f -> StringProvider.getString(R.string.sawi_result_mild_esophoria) to StringProvider.getString(R.string.sawi_result_desc_mild_eso)
                        else -> StringProvider.getString(R.string.sawi_result_severe_esophoria) to StringProvider.getString(R.string.sawi_result_desc_severe_eso)
                    }
                    hDev < 0 -> when { // Exophoria
                        abs(hDev) <= 6.5f -> StringProvider.getString(R.string.sawi_result_normal_range) to StringProvider.getString(R.string.sawi_result_desc_normal_exo)
                        abs(hDev) <= 10.5f -> StringProvider.getString(R.string.sawi_result_mild_exophoria) to StringProvider.getString(R.string.sawi_result_desc_mild_exo)
                        else -> StringProvider.getString(R.string.sawi_result_severe_exophoria) to StringProvider.getString(R.string.sawi_result_desc_severe_exo)
                    }
                    else -> StringProvider.getString(R.string.sawi_result_normal) to StringProvider.getString(R.string.sawi_result_no_horizontal_phoria)
                }

                val (vResult, vDesc) = when {
                    abs(vDev) <= 0f -> StringProvider.getString(R.string.sawi_result_normal) to StringProvider.getString(R.string.sawi_result_no_vertical_phoria)
                    abs(vDev) <= 0.75f -> StringProvider.getString(R.string.sawi_result_normal_range) to StringProvider.getString(R.string.sawi_result_no_vertical_phoria)
                    abs(vDev) <= 1.75f -> StringProvider.getString(R.string.sawi_result_mild_vertical_phoria) to StringProvider.getString(R.string.sawi_result_desc_mild_vertical)
                    else -> StringProvider.getString(R.string.sawi_result_severe_vertical_phoria) to StringProvider.getString(R.string.sawi_result_desc_severe_vertical)
                }

                val hPrism = "(${String.format("%.1f", abs(hDev))}△)"
                val vPrism = "(${String.format("%.1f", abs(vDev))}△)"

                userHorizontalResult = "$hResult $hPrism"
                userHorizontalDescription = hDesc
                userVerticalResult = "$vResult $vPrism"
                userVerticalDescription = vDesc
            }
            3 -> { // Only circle seen
                userHorizontalResult = StringProvider.getString(R.string.sawi_result_normal)
                userHorizontalDescription = StringProvider.getString(R.string.sawi_result_normal_status)
                userVerticalResult = StringProvider.getString(R.string.sawi_result_suppression_suspicion)
                userVerticalDescription = StringProvider.getString(R.string.sawi_result_desc_suppression_right)
            }
            4 -> { // Only cross seen
                userHorizontalResult = StringProvider.getString(R.string.sawi_result_suppression_suspicion)
                userHorizontalDescription = StringProvider.getString(R.string.sawi_result_desc_suppression_left)
                userVerticalResult = StringProvider.getString(R.string.sawi_result_normal)
                userVerticalDescription = StringProvider.getString(R.string.sawi_result_normal_status)
            }
            else -> {
                userHorizontalResult = StringProvider.getString(R.string.sawi_result_error)
                userHorizontalDescription = StringProvider.getString(R.string.sawi_result_error_desc)
                userVerticalResult = "-"
                userVerticalDescription = ""
            }
        }

        Text(
            text = StringProvider.getString(R.string.sawi_result_my_result),
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Row(modifier = Modifier.fillMaxWidth()) {
            val hResultParts = userHorizontalResult.split("(")
            val vResultParts = userVerticalResult.split("(")

            SawiResultCard(
                modifier = Modifier.weight(1f),
                title = userLeftTitle,
                result1 = hResultParts.getOrElse(0) { "" }.trim(),
                result2 = if (hResultParts.size > 1) "(${hResultParts[1]}" else "",
                description = userHorizontalDescription
            )
            Spacer(modifier = Modifier.width(16.dp))
            SawiResultCard(
                modifier = Modifier.weight(1f),
                title = userRightTitle,
                result1 = vResultParts.getOrElse(0) { "" }.trim(),
                result2 = if (vResultParts.size > 1) "(${vResultParts[1]}" else "",
                description = userVerticalDescription
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = StringProvider.getString(R.string.sawi_result_general_disclaimer),
            fontSize = 14.sp,
            color = Color.DarkGray,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                TTS.speechTTS(StringProvider.getString(R.string.printing_in_progress),TextToSpeech.QUEUE_FLUSH)
                StrabismusPrintHelper.printSawiResult(
                    context = context,
                    hTitle = userLeftTitle,
                    hResult = userHorizontalResult,
                    hDesc = userHorizontalDescription,
                    vTitle = userRightTitle,
                    vResult = userVerticalResult,
                    vDesc = userVerticalDescription
                )
                onPrintClicked()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(96.dp),
            colors = ButtonDefaults.buttonColors(containerColor = neNoon_blue)
            ,shape = RoundedCornerShape( 12.dp)
        ) {
            Text(StringProvider.getString(R.string.sawi_result_print_button), fontSize = 36.sp, color = Color.White)
        }

        Button(
            onClick = onBackToMainClicked,
            modifier = Modifier
                .fillMaxWidth()
                .height(96.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.White)
            ,shape = RoundedCornerShape( 12.dp)
        ) {
            Text(StringProvider.getString(R.string.sawi_result_back_to_main_button), fontSize = 36.sp, color = neNoon_blue)
        }
    }

}