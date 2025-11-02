package com.pixelro.nenoonkiosk.feature.inspection.gripStrength

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import com.pixelro.nenoonkiosk.feature.inspection.gripStrength.error.GripErrorEvent
import com.pixelro.nenoonkiosk.feature.inspection.gripStrength.error.GripErrorUiState
import com.pixelro.nenoonkiosk.feature.inspection.gripStrength.error.GripStrengthErrorScreen
import com.pixelro.nenoonkiosk.feature.inspection.gripStrength.inprogress.GripInProgressEvent
import com.pixelro.nenoonkiosk.feature.inspection.gripStrength.inprogress.GripInProgressUiState
import com.pixelro.nenoonkiosk.feature.inspection.gripStrength.inprogress.GripStrengthInProgressScreen
import com.pixelro.nenoonkiosk.feature.inspection.gripStrength.instructions.GripInstructionsEvent
import com.pixelro.nenoonkiosk.feature.inspection.gripStrength.instructions.GripInstructionsUiState
import com.pixelro.nenoonkiosk.feature.inspection.gripStrength.instructions.GripStrengthInstructionsScreen
import com.pixelro.nenoonkiosk.feature.inspection.gripStrength.start.GripStrengthStartScreen
import kotlinx.coroutines.flow.MutableStateFlow
import org.orbitmvi.orbit.compose.collectAsState

@Composable
fun GripStrengthRoute(
    isSignedIn: Boolean,
    viewModel: GripStrengthViewModel = hiltViewModel()
) {
    val state = viewModel.collectAsState().value

    LaunchedEffect(Unit) {
        viewModel.init(isSignedIn)
    }

    when (state.currentScreen) {
        GripScreen.START -> {
            GripStrengthStartScreen(
                state = state,
                onEvent = { event -> viewModel.handleEvent(event) }
            )
        }
        GripScreen.INSTRUCTIONS -> {
            GripStrengthInstructionsScreen(
                state = GripInstructionsUiState(ttsSpeaking = state.ttsSpeaking),
                ttsWarningActive = MutableStateFlow(false),
                onEvent = { event ->
                    when (event) {
                        GripInstructionsEvent.StartPressed -> {
                            viewModel.handleEvent(GripStrengthEvent.ProceedToTest)
                        }
                    }
                }
            )
        }
        GripScreen.IN_PROGRESS -> {
            GripStrengthInProgressScreen(
                state = GripInProgressUiState(
                    testState = state.testState,
                    rightGripValue = state.rightGripValue,
                    leftGripValue = state.leftGripValue,
                    countdown = state.countdown
                ),
                onEvent = { event ->
                    when (event) {
                        GripInProgressEvent.StartPressed -> {
                            viewModel.handleEvent(GripStrengthEvent.StartPressed)
                        }
                    }
                }
            )
        }
        GripScreen.ERROR -> {
            GripStrengthErrorScreen(
                state = GripErrorUiState(isSignedIn = state.isSignedIn),
                onEvent = { event ->
                    when (event) {
                        GripErrorEvent.Retry -> viewModel.handleEvent(GripStrengthEvent.Retry)
                        GripErrorEvent.Return -> viewModel.handleEvent(GripStrengthEvent.Return)
                        GripErrorEvent.Logout -> viewModel.handleEvent(GripStrengthEvent.Logout)
                    }
                }
            )
        }
    }
}
