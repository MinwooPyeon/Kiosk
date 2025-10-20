package com.pixelro.nenoonkiosk.core.ui

import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.util.StringProvider
import com.pixelro.nenoonkiosk.feature.survey.surveytype.SurveyGlass

@Composable
fun RedGreenFilterGlassDialog(
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit,
    wearsGlasses: SurveyGlass,
    tts: TextToSpeech
) {
    var isTtsFinished by remember { mutableStateOf(false) }

    DisposableEffect(Unit) {
        val listener = object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {}
            override fun onDone(utteranceId: String?) {
                if (utteranceId == "red_green_filter_glass_prompt") {
                    isTtsFinished = true
                }
            }
            override fun onError(utteranceId: String?) {}
        }
        tts.setOnUtteranceProgressListener(listener)
        onDispose {
            tts.setOnUtteranceProgressListener(null)
        }
    }

    LaunchedEffect(Unit) {
        tts.speak(
            StringProvider.getString(R.string.tts_red_green_filter_glass_prompt),
            TextToSpeech.QUEUE_FLUSH,
            null,
            "red_green_filter_glass_prompt"
        )
    }

    Dialog(onDismissRequest = onDismissRequest) {
        Column(
            modifier = Modifier
                .width(800.dp)
                .height(1000.dp)
                .background(
                    color = Color(0xffffffff),
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(
                    id = if (wearsGlasses == SurveyGlass.Yes) {
                        R.drawable.eyefilterimageglasses
                    } else {
                        R.drawable.eyefilterimage
                    }
                ),
                contentDescription = null,
                modifier = Modifier.size(400.dp)
            )
            Spacer(modifier = Modifier.height(40.dp))
            Text(
                text = StringProvider.getString(R.string.red_green_filter_glass_prompt),
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(40.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .width(300.dp)
                        .height(100.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isTtsFinished) Color(0xFF1D71E1) else Color.Gray)
                        .clickable(enabled = isTtsFinished) { onConfirm() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = StringProvider.getString(R.string.confirm),
                        color = Color.White,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}