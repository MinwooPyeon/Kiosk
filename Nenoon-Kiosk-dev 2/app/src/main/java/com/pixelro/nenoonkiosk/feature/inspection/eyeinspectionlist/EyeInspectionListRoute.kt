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
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.pixelro.nenoonkiosk.core.constants.NavConstants
import com.pixelro.nenoonkiosk.core.util.TTS
import com.pixelro.nenoonkiosk.feature.inspection.eyeinspectionlist.EyeInspectionListViewModel
import kotlinx.coroutines.delay
import org.orbitmvi.orbit.compose.collectAsState

@RequiresApi(Build.VERSION_CODES.S)
@OptIn(ExperimentalFoundationApi::class, ExperimentalAnimationApi::class)
@Composable
fun EyeInspectionListRoute(
    viewModel: EyeInspectionListViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val sharedPreferences = remember {
        context.getSharedPreferences(NavConstants.PREFERENCE_NAME, Context.MODE_PRIVATE)
    }
    val savedLanguage = remember {
        sharedPreferences.getString("language", "defaultLanguage")
    }

    val state = viewModel.collectAsState().value

    // 광고 페이저
    val pagerState = rememberPagerState(
        initialPage = Int.MAX_VALUE / 2,
        initialPageOffsetFraction = 0f,
        pageCount = { Int.MAX_VALUE }
    )

    // TTS 중단 + 광고 오토슬라이드
    LaunchedEffect(Unit) {
        TTS.tts.stop()
        viewModel.startDescriptionBlinking()
        while (true) {
            delay(5000)
            pagerState.animateScrollToPage(
                page = pagerState.currentPage + 1,
                animationSpec = tween(1000)
            )
        }
    }

    EyeInspectionListScreen(
        savedLanguage = savedLanguage,
        isSenior = state.isSenior,
        isDialogShowing = state.isDialogShowing,
        isPresbyopiaDone = state.isPresbyopiaDone,
        isShortVisualAcuityDone = state.isShortVisualAcuityDone,
        isAmslerGridDone = state.isAmslerGridDone,
        isMChartDone = state.isMChartDone,
        isDescriptionShowing = state.isDescriptionShowing,
        pagerState = pagerState,
        onBackToIntro = { viewModel.navigateToIntro() },
        onOpenSettings = { viewModel.navigateToSettings() },
        onOpenTest = { type -> viewModel.onTestSelected(type) },
        onDismissDialog = { viewModel.dismissDialog() },
        onConfirmTest = { viewModel.navigateToTestFromDialog() }
    )
}
