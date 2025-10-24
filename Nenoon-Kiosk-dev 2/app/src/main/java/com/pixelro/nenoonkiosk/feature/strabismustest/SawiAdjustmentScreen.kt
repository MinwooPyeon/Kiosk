package com.pixelro.nenoonkiosk.feature.strabismustest

import android.speech.tts.TextToSpeech
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.util.StringProvider
import com.pixelro.nenoonkiosk.core.util.TTS
import com.pixelro.nenoonkiosk.ui.theme.neNoon_blue
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SawiAdjustmentScreen(
    onConfirmClicked: (Offset, Offset) -> Unit,
    onBackClicked: () -> Unit,
    onShowHowToClicked: () -> Unit,
) {
    LaunchedEffect(Unit) {
        TTS.speechTTS(StringProvider.getString(R.string.tts_sawi_adjustment), TextToSpeech.QUEUE_FLUSH)
    }

    var crosshairPosition by remember { mutableStateOf<Offset?>(null) }
    var circlePosition by remember { mutableStateOf<Offset?>(null) }
    var showHowToDialog by remember { mutableStateOf(false) }

    if (showHowToDialog) {
        SawiHowToDialog(onDismissRequest = { showHowToDialog = false })
    }
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(StringProvider.getString(R.string.sawi_question_title), color = Color.White) },
                navigationIcon = {
                    TextButton(onClick = onBackClicked) {
                        Text(StringProvider.getString(R.string.common_exit), color = Color.White, fontSize = 24.sp)
                    }
                },
                actions = {
                    TextButton(onClick = { showHowToDialog = true }) {
                        Text(StringProvider.getString(R.string.common_test_guide), color = Color.White, fontSize = 24.sp)
                    }
                },
                colors =
                    TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.Black,
                    ),
            )
        },
        bottomBar = {
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .background(Color.Black)
                        .padding(start = 24.dp, end = 24.dp, top = 24.dp, bottom = 24.dp),
            ) {
                Button(
                    onClick = {
                        if (crosshairPosition != null && circlePosition != null) {
                            onConfirmClicked(crosshairPosition!!, circlePosition!!)
                        }
                    },
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(96.dp)
                            .padding(start = 24.dp, end = 24.dp, bottom = 24.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = neNoon_blue),
                    shape = RoundedCornerShape(12.dp),
                ) {
                    Text(StringProvider.getString(R.string.common_next), fontSize = 36.sp, color = Color.White)
                }
            }
        },
    ) { paddingValues ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Color.Black)
                    .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            var boxSize by remember { mutableStateOf<IntOffset?>(null) }
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(500.dp)
                        .background(Color(0xFFEBF961))
                        .onGloballyPositioned { coordinates ->
                            if (boxSize == null) {
                                boxSize = IntOffset(coordinates.size.width, coordinates.size.height)
                                val center = Offset(coordinates.size.width / 2f, coordinates.size.height / 2f)
                                circlePosition = center
                                crosshairPosition = center
                            }
                        },
                contentAlignment = Alignment.TopStart,
            ) {
                if (boxSize != null && circlePosition != null && crosshairPosition != null) {
                    val context = LocalContext.current
                    val displayMetrics = context.resources.displayMetrics
                    val ppi = displayMetrics.xdpi
                    val mmToPx = ppi / 25.4f
                    val circleDiameterPx = 44.3f * mmToPx
                    val circleThicknessPx = 4 * mmToPx
                    val crossLengthPx = 16 * mmToPx
                    val crossThicknessPx = 4 * mmToPx

                    val circleDiameterDp = (circleDiameterPx / LocalDensity.current.density).dp
                    val circleDiameterPxValue = with(LocalDensity.current) { circleDiameterDp.toPx() }

                    Canvas(
                        modifier =
                            Modifier
                                .offset {
                                    IntOffset(
                                        (circlePosition!!.x - circleDiameterPxValue / 2f).roundToInt(),
                                        (circlePosition!!.y - circleDiameterPxValue / 2f).roundToInt(),
                                    )
                                }
                                .size(circleDiameterDp),
                    ) {
                        drawCircle(
                            color = Color(0xFF14Fa14),
                            radius = circleDiameterPx / 2,
                            style = Stroke(width = circleThicknessPx),
                        )
                    }
                    Canvas(
                        modifier =
                            Modifier
                                .offset {
                                    IntOffset(
                                        (crosshairPosition!!.x - circleDiameterPxValue / 2f).roundToInt(),
                                        (crosshairPosition!!.y - circleDiameterPxValue / 2f).roundToInt(),
                                    )
                                }
                                .size(circleDiameterDp)
                                .pointerInput(Unit) {
                                    detectDragGestures { change, dragAmount ->
                                        change.consume()
                                        val newPosition = crosshairPosition!! + dragAmount
                                        val constrainedX = newPosition.x.coerceIn(0f, boxSize!!.x.toFloat())
                                        val constrainedY = newPosition.y.coerceIn(0f, boxSize!!.y.toFloat())
                                        crosshairPosition = Offset(constrainedX, constrainedY)
                                    }
                                },
                    ) {
                        drawLine(
                            color = Color(0xFFEA5821),
                            start = Offset(center.x - crossLengthPx / 2, center.y),
                            end = Offset(center.x + crossLengthPx / 2, center.y),
                            strokeWidth = crossThicknessPx,
                        )
                        drawLine(
                            color = Color(0xFFEA5821),
                            start = Offset(center.x, center.y - crossLengthPx / 2),
                            end = Offset(center.x, center.y + crossLengthPx / 2),
                            strokeWidth = crossThicknessPx,
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = StringProvider.getString(R.string.sawi_adjustment_main_text),
                color = Color.White,
                fontSize = 48.sp,
                textAlign = TextAlign.Center,
            )
        }
    }
}
