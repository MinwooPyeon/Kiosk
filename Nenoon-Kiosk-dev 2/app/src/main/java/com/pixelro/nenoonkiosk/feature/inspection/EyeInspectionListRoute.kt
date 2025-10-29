package com.pixelro.nenoonkiosk.feature.inspection

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.pixelro.nenoonkiosk.core.constants.NavConstants
import com.pixelro.nenoonkiosk.core.util.TTS
import com.pixelro.nenoonkiosk.feature.main.NenoonViewModel
import kotlinx.coroutines.delay

@RequiresApi(Build.VERSION_CODES.S)
@OptIn(ExperimentalFoundationApi::class, ExperimentalAnimationApi::class)
@Composable
fun EyeTestInspectionRoute(
    checkIsTestDone: (InspectionType) -> Boolean,
    toTestScreen: (InspectionType) -> Unit,
    toIntroScreen: () -> Unit,
    toSettingsScreen: () -> Unit,
    isPresbyopiaDone: Boolean,
    isShortVisualAcuityDone: Boolean,
    isAmslerGridDone: Boolean,
    isMChartDone: Boolean,
    viewModel: NenoonViewModel,
) {
    val context = LocalContext.current
    val sharedPreferences = remember {
        context.getSharedPreferences(NavConstants.PREFERENCE_NAME, Context.MODE_PRIVATE)
    }
    val savedLanguage = remember {
        sharedPreferences.getString("language", "defaultLanguage")
    }

    // 상태 (saveable)
    var isDialogShowing by rememberSaveable { mutableStateOf(false) }
    var selectedTest by rememberSaveable { mutableStateOf(InspectionType.None) }
    var isDescriptionShowing by rememberSaveable { mutableStateOf(true) }

    // 외부 StateFlow 수집 (한 번)
    val isSenior by viewModel.isSenior.collectAsState()

    // 광고 페이저
    val pagerState = rememberPagerState(
        initialPage = Int.MAX_VALUE / 2,
        initialPageOffsetFraction = 0f,
        pageCount = { Int.MAX_VALUE }
    )

    // TTS 중단 + 광고 오토슬라이드 + 안내문 깜빡임
    LaunchedEffect(Unit) {
        TTS.tts.stop()
        while (true) {
            delay(5000)
            pagerState.animateScrollToPage(
                page = pagerState.currentPage + 1,
                animationSpec = tween(1000)
            )
            repeat(3) {
                isDescriptionShowing = false
                delay(250)
                isDescriptionShowing = true
                delay(250)
            }
        }
    }

    EyeTestInspectionScreen(
        savedLanguage = savedLanguage,
        isSenior = isSenior,
        isDialogShowing = isDialogShowing,
        selectedTest = selectedTest,
        isPresbyopiaDone = isPresbyopiaDone,
        isShortVisualAcuityDone = isShortVisualAcuityDone,
        isAmslerGridDone = isAmslerGridDone,
        isMChartDone = isMChartDone,
        isDescriptionShowing = isDescriptionShowing,
        pagerState = pagerState,
        // 콜백들
        onBackToIntro = toIntroScreen,
        onOpenSettings = toSettingsScreen,
        onOpenTest = { type ->
            selectedTest = type
            if (checkIsTestDone(type)) {
                isDialogShowing = true
            } else {
                toTestScreen(type)
            }
        },
        onDismissDialog = { isDialogShowing = false },
        toTestScreen = toTestScreen
    )
}