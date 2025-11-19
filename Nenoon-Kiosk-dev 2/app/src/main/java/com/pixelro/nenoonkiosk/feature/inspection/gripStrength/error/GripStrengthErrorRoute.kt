package com.pixelro.nenoonkiosk.feature.inspection.gripStrength.error

import android.speech.tts.TextToSpeech
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavHostController
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.util.StringProvider
import com.pixelro.nenoonkiosk.core.util.TTS
import com.pixelro.nenoonkiosk.feature.inspection.gripStrength.GripStrengthInspectionNavRoute

@Composable
fun GripStrengthErrorRoute(
    navController: NavHostController,
    isSignedIn: Boolean,
    onReturn: () -> Unit,
    onLogout: () -> Unit,
) {
    LaunchedEffect(Unit) {
        TTS.stopTTS()
        TTS.speechTTS(
            StringProvider.getString(R.string.ingrip_measurement_failed_tts),
            TextToSpeech.QUEUE_ADD
        )
    }

    val uiState = GripErrorUiState(isSignedIn = isSignedIn)

    GripStrengthErrorScreen(
        state = uiState,
        onEvent = { ev ->
            when (ev) {
                GripErrorEvent.Retry -> {
                    navController.navigate(GripStrengthInspectionNavRoute.Instructions.name) {
                        popUpTo(GripStrengthInspectionNavRoute.Error.name) { inclusive = true }
                    }
                }
                GripErrorEvent.Return -> onReturn()
                GripErrorEvent.Logout -> onLogout()
            }
        },
    )
}