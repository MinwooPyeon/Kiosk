package com.pixelro.nenoonkiosk.feature.strabismus

import android.os.Build
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.ui.NenoonTopBar
import com.pixelro.nenoonkiosk.core.ui.RedGreenFilterGlassDialog
import com.pixelro.nenoonkiosk.core.ui.SettingsButton
import com.pixelro.nenoonkiosk.core.ui.SurveyRecommendationDialog
import com.pixelro.nenoonkiosk.core.ui.TopBarOrientation
import com.pixelro.nenoonkiosk.core.util.TTS
import com.pixelro.nenoonkiosk.core.util.isLandscape
import com.pixelro.nenoonkiosk.feature.inspection.InspectionType
import com.pixelro.nenoonkiosk.feature.strabismus.common.component.*
import com.pixelro.nenoonkiosk.feature.survey.model.SurveyGlass
import com.pixelro.nenoonkiosk.ui.theme.White
import kotlinx.coroutines.delay

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PhoriaAndAniseikoniaScreen(
    ui: PhoriaAniseikoniaUiState,
    checkIsTestDone: (InspectionType) -> Boolean,
    onEvent: (PhoriaAniseikoniaEvent) -> Unit,
) {
    val pagerState = rememberPagerState(
        initialPage = Int.MAX_VALUE / 2,
        initialPageOffsetFraction = 0f,
        pageCount = { Int.MAX_VALUE }
    )

    var showDescription by remember { mutableStateOf(true) }
    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.DONUT) {
            TTS.tts.stop()
        }
        while (true) {
            delay(5000)
            pagerState.animateScrollToPage(
                page = pagerState.currentPage + 1,
                animationSpec = tween(1000)
            )
            repeat(3) {
                showDescription = false
                delay(250)
                showDescription = true
                delay(250)
            }
        }
    }

    val transition = rememberInfiniteTransition(label = "desc-bounce")
    val shiftVal by transition.animateFloat(
        initialValue = 0f,
        targetValue = 20f,
        animationSpec = infiniteRepeatable(
            animation = keyframes { durationMillis = 2000 },
            repeatMode = RepeatMode.Reverse
        ),
        label = "desc-bounce-anim"
    )

    val configuration = LocalConfiguration.current
    val warningTextSize = if (ui.savedLanguage == "ru") 10.sp else 16.sp

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(White)
    ) {

        NenoonTopBar(
            title =  stringResource(R.string.cross_eye_test),
            orientation = if(isLandscape()) TopBarOrientation.Horizontal else TopBarOrientation.Vertical,
            showBackButton = true,
            onBackClicked = { onEvent(PhoriaAniseikoniaEvent.BackToIntro) },
            actions = { SettingsButton(toSettingsScreen = { onEvent(PhoriaAniseikoniaEvent.OpenSettings) }) },
            containerColor = White,
            contentColor = Color.Black
        )

        DividerLine()

        if (isLandscape()) {
            // 가로: 좌측(목록/경고), 우측(광고/설명)
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                ) {
                    AdsCarousel(
                        isSenior = ui.isSenior,
                        pagerState = pagerState
                    )
                    Spacer(Modifier.weight(1F))
                    WarningNotice(warningTextSize = warningTextSize)
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                ) {
                    InspectionList(
                        isPhoriaDone = ui.isPhoriaDone,
                        isAniseikoniaDone = ui.isAniseikoniaDone,
                        onStartPhoria = { onEvent(PhoriaAniseikoniaEvent.StartTest(InspectionType.Phoria)) },
                        onStartAniseikonia = { onEvent(PhoriaAniseikoniaEvent.StartTest(InspectionType.Aniseikonia)) }
                    )
                }


            }
        } else {
            AdsCarousel(
                isSenior = ui.isSenior,
                pagerState = pagerState
            )
            DescriptionTicker(
                visible = showDescription,
                shiftVal = shiftVal,
                savedLanguage = ui.savedLanguage
            )
            InspectionList(
                isPhoriaDone = ui.isPhoriaDone,
                isAniseikoniaDone = ui.isAniseikoniaDone,
                onStartPhoria = { onEvent(PhoriaAniseikoniaEvent.StartTest(InspectionType.Phoria)) },
                onStartAniseikonia = { onEvent(PhoriaAniseikoniaEvent.StartTest(InspectionType.Aniseikonia)) }
            )
            WarningNotice(warningTextSize = warningTextSize)
        }
    }

    if (ui.showFilterDialog) {
        RedGreenFilterGlassDialog(
            onDismissRequest = { onEvent(PhoriaAniseikoniaEvent.DismissFilterDialog) },
            onConfirm = {
                onEvent(PhoriaAniseikoniaEvent.ConfirmFilterDialog)
            },
            wearsGlasses = ui.surveyGlass,
            tts = TTS.tts
        )
    }

    if (ui.showSurveyDialog) {
        SurveyRecommendationDialog(
            onDismissRequest = { onEvent(PhoriaAniseikoniaEvent.DismissSurveyDialog) },
            toTestScreen = { type -> onEvent(PhoriaAniseikoniaEvent.StartTest(type)) },
            toIntroScreen = { onEvent(PhoriaAniseikoniaEvent.BackToIntro) },
            selectedTest = InspectionType.Aniseikonia
        )
    }
}

@Composable
private fun DividerLine() {
    Spacer(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(Color(0xFFEBEBEB))
    )
}

@Composable
private fun fakeUi(
    lang: String = "ko",
    senior: Boolean = false,
    phoriaDone: Boolean = false,
    aniseikoniaDone: Boolean = true,
    surveyGlass: SurveyGlass = SurveyGlass.Yes,
    showSurvey: Boolean = false,
    showFilter: Boolean = false
) = PhoriaAniseikoniaUiState(
    savedLanguage = lang,
    isSenior = senior,
    isPhoriaDone = phoriaDone,
    isAniseikoniaDone = aniseikoniaDone,
    surveyGlass = surveyGlass,
    showSurveyDialog = showSurvey,
    showFilterDialog = showFilter
)

@Preview(
    showBackground = true, widthDp = 888, heightDp = 1422, name = "Portrait – Adult"
)
@Composable
private fun Preview_Portrait_Adult() {
    PhoriaAndAniseikoniaScreen(
        ui = fakeUi(lang = "ko", senior = false),
        checkIsTestDone = { false },
        onEvent = {}
    )
}

@Preview(
    showBackground = true, widthDp = 1422, heightDp = 888, name = "Landscape – Adult"
)
@Composable
private fun Preview_Landscape_Adult() {
    PhoriaAndAniseikoniaScreen(
        ui = fakeUi(lang = "es", senior = false),
        checkIsTestDone = { false },
        onEvent = {}
    )
}

@Preview(
    showBackground = true, widthDp = 888, heightDp = 1422, name = "Portrait – Senior"
)
@Composable
private fun Preview_Portrait_Senior() {
    PhoriaAndAniseikoniaScreen(
        ui = fakeUi(lang = "ru", senior = true, showFilter = true),
        checkIsTestDone = { it == InspectionType.Aniseikonia },
        onEvent = {}
    )
}

@Preview(
    showBackground = true, widthDp = 1422, heightDp = 888, name = "Landscape – Senior + Survey"
)
@Composable
private fun Preview_Landscape_Senior_Survey() {
    PhoriaAndAniseikoniaScreen(
        ui = fakeUi(senior = true, showSurvey = true),
        checkIsTestDone = { true },
        onEvent = {}
    )
}
