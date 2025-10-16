package com.pixelro.nenoonkiosk.test.bloodPressure

import android.speech.tts.TextToSpeech
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.TTS
import com.pixelro.nenoonkiosk.util.StringProvider
import com.pixelro.nenoonkiosk.feature.components.IconTextButton
import com.pixelro.nenoonkiosk.feature.components.StyledText
import com.pixelro.nenoonkiosk.feature.components.TextStyle

@Composable
fun BloodPressureErrorScreen(
    onReturn: () -> Unit,
    onLogout: () -> Unit,
    navController: NavHostController,
    isSignedIn: Boolean,
) {

    LaunchedEffect(Unit) {
        TTS.stopTTS()
        TTS.speechTTS(
            StringProvider.getString(R.string.bpbio320_error_message),
            TextToSpeech.QUEUE_ADD
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(vertical = 60.dp, horizontal = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(modifier = Modifier.weight(1f))

        Icon(
            painter = painterResource(R.drawable.warning),
            tint = colorResource(R.color.error),
            contentDescription = null,
            modifier = Modifier.size(400.dp)
        )
        Spacer(modifier = Modifier.height(20.dp))
        StyledText(
            text = StringProvider.getString(R.string.bpbio320_error_title),
            style = TextStyle.Error,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.weight(1f))
        Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(20.dp)) {
            IconTextButton(
                onClick = {
                    navController.navigate(BloodPressureTestScreen.Start.name) {
                        popUpTo(BloodPressureTestScreen.Start.name) { inclusive = false }
                    }
                },
                iconId = R.drawable.icon_retry,
                text = StringProvider.getString(R.string.retest),
            )
            IconTextButton(
                onClick = onReturn,
                iconId = R.drawable.icon_back2,
                text = StringProvider.getString(R.string.result_button2_back),
            )
            if (isSignedIn) {
                IconTextButton(
                    onClick = onLogout,
                    iconId = R.drawable.icon_logout,
                    text = StringProvider.getString(R.string.settings_signout),
                )
            }
        }
    }
}