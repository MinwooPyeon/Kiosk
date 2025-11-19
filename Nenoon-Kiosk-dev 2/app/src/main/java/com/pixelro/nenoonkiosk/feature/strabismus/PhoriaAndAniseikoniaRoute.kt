package com.pixelro.nenoonkiosk.feature.strabismus

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.pixelro.nenoonkiosk.feature.inspection.AdImageRepositoryEntryPoint
import com.pixelro.nenoonkiosk.feature.inspection.InspectionType
import com.pixelro.nenoonkiosk.feature.main.NenoonViewModel
import dagger.hilt.android.EntryPointAccessors

@RequiresApi(Build.VERSION_CODES.S)
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PhoriaAndAniseikoniaRoute(
    checkIsTestDone: (InspectionType) -> Boolean,
    toTestScreen: (InspectionType) -> Unit,
    toIntroScreen: () -> Unit,
    toSettingsScreen: () -> Unit,
    isPhoriaDone: Boolean,
    isAniseikoniaDone: Boolean,
    viewModel: NenoonViewModel
) {
    val context = LocalContext.current

    // SettingsScreen에서 설정한 언어를 AppCompatDelegate로부터 가져오기
    val savedLanguage = remember {
        val appLocale = AppCompatDelegate.getApplicationLocales()
        if (appLocale.isEmpty) {
            "ko"
        } else {
            appLocale[0]?.toLanguageTag() ?: "ko"
        }
    }

    val isSenior by viewModel.isSenior.collectAsState()
    val surveyGlass by viewModel.surveyGlass.collectAsState()

    // 광고 리스트 가져오기 (Hilt EntryPoint)
    val adImageRepository = remember {
        EntryPointAccessors.fromApplication(
            context.applicationContext,
            AdImageRepositoryEntryPoint::class.java
        ).adImageRepository()
    }
    val adImages by adImageRepository.getAdImagesByLocationAndLanguage(1, savedLanguage)
        .collectAsState(initial = emptyList())

    // 다이얼로그 상태는 Route에서 소유
    var showSurveyDialog by remember { mutableStateOf(false) }
    var showFilterDialog by remember { mutableStateOf(false) }

    val uiState = PhoriaAniseikoniaUiState(
        savedLanguage = savedLanguage,
        isSenior = isSenior,
        isPhoriaDone = isPhoriaDone,
        isAniseikoniaDone = isAniseikoniaDone,
        surveyGlass = surveyGlass,
        showSurveyDialog = showSurveyDialog,
        showFilterDialog = showFilterDialog,
        adImages = adImages
    )

    PhoriaAndAniseikoniaScreen(
        ui = uiState,
        checkIsTestDone = checkIsTestDone,
        onEvent = { ev ->
            when (ev) {
                is PhoriaAniseikoniaEvent.BackToIntro -> toIntroScreen()
                is PhoriaAniseikoniaEvent.OpenSettings -> toSettingsScreen()
                is PhoriaAniseikoniaEvent.StartTest -> {
                    when (ev.type) {
                        InspectionType.Phoria -> {
                            // 적녹 필터 확인 다이얼로그로 유도
                            showFilterDialog = true
                        }
                        InspectionType.Aniseikonia -> {
                            if (checkIsTestDone(InspectionType.Aniseikonia)) {
                                showSurveyDialog = true
                            } else {
                                toTestScreen(InspectionType.Aniseikonia)
                            }
                        }
                        else -> Unit
                    }
                }
                PhoriaAniseikoniaEvent.DismissSurveyDialog -> showSurveyDialog = false
                PhoriaAniseikoniaEvent.ShowSurveyDialog -> showSurveyDialog = true
                PhoriaAniseikoniaEvent.DismissFilterDialog -> showFilterDialog = false
                PhoriaAniseikoniaEvent.ConfirmFilterDialog -> {
                    showFilterDialog = false
                    toTestScreen(InspectionType.Phoria)
                }
            }
        }
    )
}