package com.pixelro.nenoonkiosk.feature.strabismustest

import android.speech.tts.TextToSpeech
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.strabismustest.ui.theme.StrabismusTestTheme
import com.example.strabismustest.ui.theme.neNoon_blue
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.util.TTS
import com.pixelro.nenoonkiosk.core.util.StringProvider

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SawiQuestionScreen(
    onNextClicked: (Int) -> Unit,
    onBackClicked: () -> Unit,
    testType: String,
) {
    LaunchedEffect(Unit) {
        TTS.speechTTS(StringProvider.getString(R.string.tts_sawi_question), TextToSpeech.QUEUE_FLUSH)
    }

    var selectedOption by remember { mutableStateOf<Int?>(null) }
    val options = listOf(
        StringProvider.getString(R.string.sawi_question_option1),
        StringProvider.getString(R.string.sawi_question_option2),
        StringProvider.getString(R.string.sawi_question_option3),
        StringProvider.getString(R.string.sawi_question_option4)
    )
    var showHowToDialog by remember { mutableStateOf(false) }

    if (showHowToDialog) {
        SawiHowToDialog(onDismissRequest = { showHowToDialog = false })
    }

    val headerTitle = when (testType) {
        FilterInstructionFragment.TEST_TYPE_SAWI -> StringProvider.getString(R.string.sawi_question_title)
        FilterInstructionFragment.TEST_TYPE_FUDO -> StringProvider.getString(R.string.fudo_question_title)
        else -> StringProvider.getString(R.string.common_test_title)
    }
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(headerTitle, color = Color.White) },
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
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Black
                )
            )
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Black)
                    .padding(start = 24.dp, end = 24.dp, top = 24.dp,bottom = 24.dp)
            ) {
                Button(
                    onClick = { selectedOption?.let { onNextClicked(it) } },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(96.dp)
                        .padding(start = 24.dp, end = 24.dp, bottom = 24.dp),
                    enabled = selectedOption != null,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = neNoon_blue,
                        disabledContainerColor = Color.Gray
                    ),shape = RoundedCornerShape( 12.dp)
                ) {
                    Text(StringProvider.getString(R.string.common_next), fontSize = 36.sp, color = Color.White)
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.Black)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(500.dp)
                    .background(Color(0xFFEBF961)),
                contentAlignment = Alignment.Center
            ) {
                val context = LocalContext.current
                val displayMetrics = context.resources.displayMetrics
                val ppi = displayMetrics.xdpi // Get the exact physical pixels per inch of the screen

                val mmToPx = ppi / 25.4f

                // Compensate for observed rendering inaccuracy (renders as 52mm instead of 48mm on target device).
                // 48mm * (48 / 52) = 44.3mm
                val circleDiameterPx = 44.3f * mmToPx
                val circleThicknessPx = 4 * mmToPx
                val crossLengthPx = 16 * mmToPx
                val crossThicknessPx = 4 * mmToPx

                Canvas(modifier = Modifier.size((circleDiameterPx / LocalDensity.current.density).dp)) {
                    // Draw circle
                    drawCircle(
                        color = Color(0xFF14Fa14),
                        radius = circleDiameterPx / 2,
                        style = androidx.compose.ui.graphics.drawscope.Stroke(width = circleThicknessPx)
                    )

                    // Draw cross
                    // Horizontal line
                    drawLine(
                        color = Color(0xFFEA5821),
                        start = androidx.compose.ui.geometry.Offset(center.x - crossLengthPx / 2, center.y),
                        end = androidx.compose.ui.geometry.Offset(center.x + crossLengthPx / 2, center.y),
                        strokeWidth = crossThicknessPx
                    )
                    // Vertical line
                    drawLine(
                        color = Color(0xFFEA5821),
                        start = androidx.compose.ui.geometry.Offset(center.x, center.y - crossLengthPx / 2),
                        end = androidx.compose.ui.geometry.Offset(center.x, center.y + crossLengthPx / 2),
                        strokeWidth = crossThicknessPx
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = StringProvider.getString(R.string.sawi_question_main_text),
                color = Color.White,
                fontSize = 48.sp,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(24.dp))
            options.forEachIndexed { index, text ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { selectedOption = index + 1 }
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = selectedOption == (index + 1),
                        onClick = { selectedOption = index + 1 },
                        colors = RadioButtonDefaults.colors(
                            selectedColor = Color.White,
                            unselectedColor = Color.White
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = text, color = Color.White, fontSize = 36.sp)
                }
            }
        }
    }
}

@Preview(showBackground = true, device = "spec:width=1280dp,height=800dp,dpi=240")
@Composable
fun SawiQuestionScreenPreview() {
    StrabismusTestTheme {
        SawiQuestionScreen(
            testType = FilterInstructionFragment.TEST_TYPE_SAWI,
            onNextClicked = {},
            onBackClicked = {},
        )
    }
}