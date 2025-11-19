package com.pixelro.nenoonkiosk.feature.inspection.bloodPressure.error

import android.speech.tts.TextToSpeech
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavHostController
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.util.StringProvider
import com.pixelro.nenoonkiosk.core.util.TTS
import com.pixelro.nenoonkiosk.feature.inspection.bloodPressure.BloodPressureInspectionNavRoute

@Composable
fun BloodPressureErrorRoute(
    onReturn: () -> Unit,
    onLogout: () -> Unit,
    navController: NavHostController,
    isSignedIn: Boolean,
) {
    LaunchedEffect(Unit) {
        TTS.stopTTS()
        TTS.speechTTS(
            StringProvider.getString(R.string.bpbio320_error_message),
            TextToSpeech.QUEUE_ADD,
        )
    }

    BloodPressureErrorScreen(
        onReturn = onReturn,
        onLogout = onLogout,
        isSignedIn = isSignedIn,
        toStart = {
            navController.navigate(BloodPressureInspectionNavRoute.Start.name) {
                popUpTo(BloodPressureInspectionNavRoute.Start.name) { inclusive = false }
            }
        },
    )
}