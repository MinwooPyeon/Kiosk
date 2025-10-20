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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.util.StringProvider
import com.pixelro.nenoonkiosk.core.util.TTS
import com.pixelro.nenoonkiosk.ui.theme.neNoon_blue

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
