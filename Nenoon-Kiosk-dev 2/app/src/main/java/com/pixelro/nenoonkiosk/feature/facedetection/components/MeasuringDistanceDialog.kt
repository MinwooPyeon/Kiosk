package com.pixelro.nenoonkiosk.feature.facedetection.components

import android.speech.tts.TextToSpeech
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.util.StringProvider
import com.pixelro.nenoonkiosk.core.util.TTS
import com.pixelro.nenoonkiosk.feature.facedetection.FaceDetectionViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * 눈가리개 사용 dialog
 */
@Composable
fun MeasuringDistanceDialog(
    onDismissRequest: () -> Unit,
    faceDetectionViewModel: FaceDetectionViewModel = hiltViewModel(),
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(),
    ) {
        val coroutineScope = rememberCoroutineScope()
        val isWarningShowing = remember { mutableStateOf(false) }
        LaunchedEffect(true) {
            TTS.speechTTS(StringProvider.getString(R.string.tts_dialog), TextToSpeech.QUEUE_ADD)
        }
        Column(
            modifier =
                Modifier
                    .width(800.dp)
                    .height(1000.dp)
                    .background(
                        color = Color(0xffffffff),
                        shape = RoundedCornerShape(8.dp),
                    ),
        ) {
            Column(
                modifier =
                    Modifier
                        .height(800.dp),
            ) {
                Text(
                    modifier =
                        Modifier
                            .padding(start = 20.dp, top = 20.dp, end = 20.dp, bottom = 8.dp)
                            .fillMaxWidth(),
                    text =
                        buildAnnotatedString {
                            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                                append(
                                    StringProvider.getString(
                                        R.string.dialog_description1_pickup1,
                                    ),
                                )
                            }
                            withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = colorResource(R.color.main))) {
                                append(
                                    StringProvider.getString(
                                        R.string.dialog_description1_pickup2,
                                    ),
                                )
                            }
                            append(
                                StringProvider.getString(
                                    R.string.dialog_description1_pickup3,
                                ),
                            )
                        },
                    fontSize = 40.sp,
                    fontWeight = FontWeight.ExtraBold,
                )
                Box(
                    modifier =
                        Modifier
                            .fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    Image(
                        modifier =
                            Modifier
                                .fillMaxSize()
                                .rotate(-25f),
                        painter = painterResource(id = R.drawable.occluder2),
                        contentDescription = null,
                    )
                    if (isWarningShowing.value) {
                        Text(
                            modifier =
                                Modifier
                                    .padding(start = 40.dp, end = 40.dp)
                                    .border(
                                        border = BorderStroke(2.dp, Color(0xFF000000)),
                                        shape = RoundedCornerShape(8.dp),
                                    )
                                    .background(
                                        color = Color(0xFFFFFFFF),
                                        shape = RoundedCornerShape(8.dp),
                                    )
                                    .padding(20.dp)
                                    .fillMaxWidth(),
                            text =
                                buildAnnotatedString {
                                    append(
                                        StringProvider.getString(
                                            R.string.dialog_description2_announcement1,
                                        ),
                                    )
                                    withStyle(
                                        style =
                                            SpanStyle(
                                                color = Color(0xff1d71e1),
                                                fontWeight = FontWeight.Bold,
                                            ),
                                    ) {
                                        append(
                                            StringProvider.getString(
                                                R.string.dialog_description2_announcement2,
                                            ),
                                        )
                                    }
                                    append(
                                        StringProvider.getString(
                                            R.string.dialog_description2_announcement3,
                                        ),
                                    )
                                },
                            fontSize = 50.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }
            Box(
                modifier =
                    Modifier
                        .fillMaxSize(),
                contentAlignment = Alignment.BottomCenter,
            ) {
                Text(
                    modifier =
                        Modifier
                            .padding(start = 20.dp, top = 8.dp, end = 20.dp, bottom = 20.dp)
                            .fillMaxWidth()
                            .clip(
                                shape = RoundedCornerShape(8.dp),
                            )
                            .border(
                                border = BorderStroke(1.dp, Color(0xffc3c3c3)),
                                shape = RoundedCornerShape(8.dp),
                            )
                            .clickable {
                                if (TTS.tts.isSpeaking) {
                                    coroutineScope.launch {
                                        for (i in 1..3) {
                                            isWarningShowing.value = true
                                            delay(400)
                                            isWarningShowing.value = false
                                            delay(400)
                                        }
                                        isWarningShowing.value = true
                                        delay(2000)
                                        isWarningShowing.value = false
                                    }
                                } else {
                                    faceDetectionViewModel.updateIsOccluderPickedTTSDone(true)
                                    onDismissRequest()
                                }
                            }
                            .padding(20.dp),
                    text = StringProvider.getString(R.string.confirm),
                    fontSize = 50.sp,
                    color = Color(0xff1d71e1),
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}