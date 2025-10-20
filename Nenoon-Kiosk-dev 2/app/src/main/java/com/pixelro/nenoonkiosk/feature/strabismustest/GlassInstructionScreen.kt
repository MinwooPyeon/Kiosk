package com.pixelro.nenoonkiosk.feature.strabismustest

import android.speech.tts.TextToSpeech
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
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
fun GlassInstructionScreen(
    testType: String,
    onYesClicked: () -> Unit,
    onNoClicked: () -> Unit,
    onBackClicked: () -> Unit,
) {
    LaunchedEffect(Unit) {
        TTS.speechTTS(StringProvider.getString(R.string.tts_glass_instruction), TextToSpeech.QUEUE_FLUSH)
    }

    val headerTitle = when (testType) {
        "sawi" -> StringProvider.getString(R.string.sawi_question_title)
        "fudo" -> StringProvider.getString(R.string.fudo_question_title)
        else -> StringProvider.getString(R.string.glass_instruction_title)
    }
    var showHowToDialog by remember { mutableStateOf(false) }

    if (showHowToDialog) {
        if (testType == "sawi") {
            SawiHowToDialog(onDismissRequest = { showHowToDialog = false })
        } else {
            FudoHowToDialog(onDismissRequest = { showHowToDialog = false })
        }
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
                    onClick = onYesClicked,
                    modifier = Modifier
                        .weight(1f)
                        .height(96.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = neNoon_blue),
                    shape = RoundedCornerShape( 12.dp)
                ) {
                    Text(StringProvider.getString(R.string.common_yes), fontSize = 36.sp, color = Color.White)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Button(
                    onClick = onNoClicked,
                    modifier = Modifier
                        .weight(1f)
                        .height(96.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    shape = RoundedCornerShape( 12.dp)
                ) {
                    Text(StringProvider.getString(R.string.common_no), fontSize = 36.sp, color = neNoon_blue)
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
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(50.dp))
            Text(
                text = StringProvider.getString(R.string.glass_instruction_main_text),
                color = Color.White,
                fontSize = 48.sp,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(50.dp))
            Image(
                painter = painterResource(id = R.drawable.glassimage),
                contentDescription = "Glass Image",
                modifier = Modifier
                    .width(1000.dp)
                    .height(1100.dp)
            )
        }
    }
}

@Preview(showBackground = true, device = "spec:width=1280dp,height=800dp,dpi=240")
@Composable
fun GlassInstructionScreenPreview() {
    StrabismusTestTheme {
        GlassInstructionScreen(
            testType = "sawi",
            onYesClicked = {},
            onNoClicked = {},
            onBackClicked = {},
        )
    }
}