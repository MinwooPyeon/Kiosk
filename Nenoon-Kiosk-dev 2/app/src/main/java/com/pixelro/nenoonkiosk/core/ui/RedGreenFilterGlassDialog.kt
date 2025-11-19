package com.pixelro.nenoonkiosk.core.ui

import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.pixelro.nenoonkiosk.core.util.isLandscape
import com.pixelro.nenoonkiosk.feature.survey.model.SurveyGlass
import com.pixelro.nenoonkiosk.ui.theme.White

@Composable
fun RedGreenFilterGlassDialog(
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit,
    wearsGlasses: SurveyGlass,
    tts: TextToSpeech,
) {
    var isTtsFinished by remember { mutableStateOf(false) }

    DisposableEffect(Unit) {
        val listener =
            object : UtteranceProgressListener() {
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
            "red_green_filter_glass_prompt",
        )
    }

    Dialog(onDismissRequest = onDismissRequest) {
        Column(
            modifier = if (!isLandscape())
                Modifier
                    .width(800.dp)
                    .height(1000.dp)
                    .background(
                        color = White,
                        shape = RoundedCornerShape(16.dp),
                    )
                    .padding(40.dp)
            else Modifier
                .width(1000.dp)
                .height(600.dp)
                .background(
                    color = White,
                    shape = RoundedCornerShape(16.dp),
                )
                .padding(40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Image(
                painter =
                    painterResource(
                        id =
                            if (wearsGlasses == SurveyGlass.Yes) {
                                R.drawable.eyefilterimageglasses
                            } else {
                                R.drawable.eyefilterimage
                            },
                    ),
                contentDescription = null,
                modifier = if(isLandscape()) Modifier.size(300.dp) else Modifier.size(400.dp) ,
            )
            Spacer(modifier = Modifier.height(40.dp))
            Text(
                text = StringProvider.getString(R.string.red_green_filter_glass_prompt),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(40.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
            ) {
                Box(
                    modifier =
                        Modifier
                            .width(300.dp)
                            .height(100.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isTtsFinished) Color(0xFF1D71E1) else Color.Gray)
                            .clickable(enabled = isTtsFinished) { onConfirm() },
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = StringProvider.getString(R.string.confirm),
                        color = Color.White,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
    }
}
