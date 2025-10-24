package com.pixelro.nenoonkiosk.feature.strabismustest

import android.speech.tts.TextToSpeech
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
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
fun FudoQuestionScreen(
    onNextClicked: (Int) -> Unit,
    onBackClicked: () -> Unit,
    onShowHowToClicked: () -> Unit,
) {
    LaunchedEffect(Unit) {
        TTS.speechTTS(StringProvider.getString(R.string.tts_fudo_question), TextToSpeech.QUEUE_FLUSH)
    }

    var selectedOption by remember { mutableStateOf<Int?>(null) }
    val options =
        listOf(
            StringProvider.getString(R.string.fudo_question_option1),
            StringProvider.getString(R.string.fudo_question_option2),
            StringProvider.getString(R.string.fudo_question_option3),
        )

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
                    onClick = { selectedOption?.let { onNextClicked(it) } },
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(96.dp),
                    enabled = selectedOption != null,
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor = neNoon_blue,
                            disabledContainerColor = Color.Gray,
                        ),
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
                    .background(Color.Black),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_fudo_test),
                contentDescription = "Fudo Test Image",
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(500.dp)
                        .background(Color(0xFFEBF961)),
            )
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = StringProvider.getString(R.string.sawi_question_main_text),
                color = Color.White,
                fontSize = 48.sp,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(16.dp))

            options.forEachIndexed { index, text ->
                OptionRadioButton(
                    text = text,
                    selected = selectedOption == (index + 1),
                    onClick = { selectedOption = index + 1 },
                )
            }
        }
    }
}

@Composable
fun OptionRadioButton(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(start = 48.dp, end = 24.dp, top = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick,
            colors =
                RadioButtonDefaults.colors(
                    selectedColor = Color.White,
                    unselectedColor = Color.White,
                ),
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = text, color = Color.White, fontSize = 36.sp)
    }
}
