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
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.strabismustest.ui.theme.StrabismusTestTheme
import com.example.strabismustest.ui.theme.neNoon_blue

import androidx.compose.ui.platform.LocalConfiguration
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.util.TTS
import com.pixelro.nenoonkiosk.core.util.StringProvider

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FudoAdjustmentScreen(
    onNextClicked: (Int, Float) -> Unit,
    onBackClicked: () -> Unit,
) {
    LaunchedEffect(Unit) {
        TTS.speechTTS(StringProvider.getString(R.string.tts_fudo_adjustment), TextToSpeech.QUEUE_FLUSH)
    }

    var rightMoonScale by remember { mutableStateOf(1f) }
    var scaleDifference by remember { mutableStateOf(0.0f) }
    var showHowToDialog by remember { mutableStateOf(false) }

    if (showHowToDialog) {
        FudoHowToDialog(onDismissRequest = { showHowToDialog = false })
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(StringProvider.getString(R.string.fudo_question_title), color = Color.White) },
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
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Black)
                    .padding(start = 24.dp, end = 24.dp, top = 24.dp,bottom = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                val locale = LocalConfiguration.current.locale
                val fontSize = if (locale.language == "ko") 48.sp else 44.sp
                Text(
                    text = StringProvider.getString(R.string.fudo_adjustment_bottom_text),
                    color = Color.White,
                    fontSize = fontSize,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        onNextClicked(1, scaleDifference)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(96.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = neNoon_blue,
                    ),
                    shape = RoundedCornerShape(12.dp)
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
                .background(Color.Black),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(500.dp)
                    .background(Color(0xFFEBF961)),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_fudo_moon_left),
                    contentDescription = "Left Moon",
                    modifier = Modifier.size(200.dp, 500.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Image(
                    painter = painterResource(id = R.drawable.ic_fudo_moon_right),
                    contentDescription = "Right Moon",
                    modifier = Modifier
                        .size(200.dp, 500.dp)
                        .graphicsLayer(
                            scaleX = rightMoonScale,
                            scaleY = rightMoonScale,
                            transformOrigin = TransformOrigin(0f, 0.5f)
                        )
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            val locale = LocalConfiguration.current.locale
            val fontSize = if (locale.language == "ko") 48.sp else 44.sp
            Text(
                text = StringProvider.getString(R.string.fudo_adjustment_main_text),
                color = Color.White,
                fontSize = fontSize,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.width(48.dp))
                Button(
                    onClick = {
                        rightMoonScale *= 0.995f
                        scaleDifference += 0.5f},
                    modifier = Modifier
                        .weight(1f)
                        .height(96.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = neNoon_blue),
                    shape = RoundedCornerShape( 12.dp)
                ) {
                    Text(StringProvider.getString(R.string.fudo_adjustment_button_left_smaller), color = Color.White,
                        fontSize = 24.sp,
                        textAlign = TextAlign.Center)
                }
                Spacer(modifier = Modifier.width(24.dp))
                Button(
                    onClick = {
                        rightMoonScale *= 1.005f
                        scaleDifference -= 0.5f
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(96.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = neNoon_blue),
                    shape = RoundedCornerShape( 12.dp)
                ) {
                    Text(StringProvider.getString(R.string.fudo_adjustment_button_right_smaller), color = Color.White,
                        fontSize = 24.sp,
                        textAlign = TextAlign.Center)
                }
                Spacer(modifier = Modifier.width(48.dp))

            }

        }
    }
}

@Preview(showBackground = true, device = "spec:width=1280dp,height=800dp,dpi=240")
@Composable
fun FudoAdjustmentScreenPreview() {
    StrabismusTestTheme {
        FudoAdjustmentScreen({ _, _ -> }, {})
    }
}