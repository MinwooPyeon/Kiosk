package com.pixelro.nenoonkiosk.feature.categorylist

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalInspectionMode
import com.pixelro.nenoonkiosk.core.util.TTS
import com.pixelro.nenoonkiosk.feature.inspection.InspectionType
import kotlinx.coroutines.delay

@OptIn(ExperimentalFoundationApi::class, ExperimentalAnimationApi::class)
@Composable
fun CategoryListRoute(
    pid: Int,
    isSignInSkipped: () -> Boolean,
    toEyeTestScreen: () -> Unit,
    toContact: () -> Unit,
    toDementiaTestScreen: (InspectionType) -> Unit,
    toExternalDeviceTestListScreen: () -> Unit,
    toStrabismusTestListScreen: () -> Unit,
    toIntroScreen: () -> Unit,
    toPrintScreen: () -> Unit,
    toAccountManagementScreen: () -> Unit,
    toSettingsScreen: () -> Unit,
) {
    val isPreview = LocalInspectionMode.current

    // Preview-safe infinite animation
    val isDescriptionShowing = remember { mutableStateOf(true) }
    LaunchedEffect(!isPreview) {
        if (!isPreview) {
            TTS.tts.stop()
            while (true) {
                delay(5000)
                for (i in 1..3) {
                    isDescriptionShowing.value = false
                    delay(250)
                    isDescriptionShowing.value = true
                    delay(250)
                }
            }
        }
    }

    CategoryListScreenContent(
        isSignInSkipped = isSignInSkipped,
        toEyeTestScreen = toEyeTestScreen,
        toDementiaTestScreen = toDementiaTestScreen,
        toExternalDeviceTestListScreen = toExternalDeviceTestListScreen,
        toStrabismusTestListScreen = toStrabismusTestListScreen,
        toPrintScreen = toPrintScreen,
        toAccountManagementScreen = toAccountManagementScreen,
    )
}


