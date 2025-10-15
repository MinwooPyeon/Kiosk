package com.pixelro.nenoonkiosk.test.gripStrength

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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.TTS
import com.pixelro.nenoonkiosk.bTManager.InGrip.InGripViewModel
import com.pixelro.nenoonkiosk.data.StringProvider
import com.pixelro.nenoonkiosk.ui.components.IconTextButton
import com.pixelro.nenoonkiosk.ui.components.StyledText
import com.pixelro.nenoonkiosk.ui.components.TextStyle

@Composable
fun GripStrengthErrorScreen(
    onReturn: () -> Unit,
    onLogout: () -> Unit,
    navController: NavHostController,
    isSignedIn: Boolean,
    viewModel: InGripViewModel,
) {

    LaunchedEffect(Unit) {
        TTS.stopTTS()
        TTS.speechTTS(
            StringProvider.getString(R.string.ingrip_measurement_failed_tts),
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
            text = StringProvider.getString(R.string.ingrip_error_title),
            style = TextStyle.Error,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.weight(1f))
        Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(20.dp)) {
            IconTextButton(
                onClick = {
                    navController.navigate(GripStrengthTestScreen.Instructions.name) {
                        popUpTo(GripStrengthTestScreen.Error.name) { inclusive = true }
                    }
                },
                iconId = R.drawable.icon_retry,
                text = StringProvider.getString(R.string.ingrip_retest),
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

@Preview
@Composable
fun GripStrengthErrorScreenPreview() {
    GripStrengthErrorScreen(
        onReturn = {},
        onLogout = {},
        navController = NavHostController(LocalContext.current),
        isSignedIn = true,
        viewModel = InGripViewModel()
    )
}