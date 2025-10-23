package com.pixelro.nenoonkiosk.feature.strabismustest

import android.speech.tts.TextToSpeech
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.util.StringProvider
import com.pixelro.nenoonkiosk.core.util.TTS
import com.pixelro.nenoonkiosk.ui.theme.NEURAL200
import com.pixelro.nenoonkiosk.ui.theme.NenoonKioskTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterInstructionScreen(
    testType: String,
    isWearingGlasses: Boolean,
    onNextClicked: () -> Unit,
    onBackClicked: () -> Unit,
) {
    LaunchedEffect(Unit) {
        TTS.speechTTS(StringProvider.getString(R.string.tts_filter_instruction), TextToSpeech.QUEUE_FLUSH)
    }

    val headerTitle =
        when (testType) {
            "sawi" -> StringProvider.getString(R.string.sawi_question_title)
            "fudo" -> StringProvider.getString(R.string.fudo_question_title)
            else -> StringProvider.getString(R.string.common_test_title)
        }
    var showHowToDialog by remember { mutableStateOf(false) }

    if (showHowToDialog) {
        if (testType == "sawi") {
            SawiHowToDialog(onDismissRequest = { showHowToDialog = false })
        } else {
            FudoHowToDialog(onDismissRequest = { showHowToDialog = false })
        }
    }

    val imageRes =
        if (isWearingGlasses) {
            R.drawable.eyefilterimageglasses
        } else {
            R.drawable.eyefilterimage
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
                    onClick = onNextClicked,
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(96.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NEURAL200),
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
        ) {
            Spacer(modifier = Modifier.height(40.dp))
            Text(
                text = StringProvider.getString(R.string.filter_instruction_main_text_1),
                color = Color.White,
                fontSize = 42.sp,
                textAlign = TextAlign.Center,
            )
            Text(
                text = StringProvider.getString(R.string.filter_instruction_main_text_2),
                color = Color.White,
                fontSize = 42.sp,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(50.dp))
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = "Filter Image",
                modifier =
                    Modifier
                        .width(1000.dp)
                        .height(1100.dp),
            )
        }
    }
}

@Preview(showBackground = true, device = "spec:width=1280dp,height=800dp,dpi=240")
@Composable
fun FilterInstructionScreenPreview() {
    NenoonKioskTheme {
        FilterInstructionScreen(
            testType = "sawi",
            isWearingGlasses = false,
            onNextClicked = {},
            onBackClicked = {},
        )
    }
}
