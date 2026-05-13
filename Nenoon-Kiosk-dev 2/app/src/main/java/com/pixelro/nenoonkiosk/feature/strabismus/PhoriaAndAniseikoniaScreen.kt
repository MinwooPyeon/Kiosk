package com.pixelro.nenoonkiosk.feature.strabismus

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
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
import com.harang.data.db.entity.AdImageEntity
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.ui.AdCarousel
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PhoriaAndAniseikoniaScreen(
    ui: PhoriaAniseikoniaUiState,
    checkIsTestDone: (InspectionType) -> Boolean,
    onEvent: (PhoriaAniseikoniaEvent) -> Unit,
) {
    var showDescription by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        TTS.stopTTS()
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
            title = stringResource(R.string.test_list_tittle),
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
                    if (!ui.isSenior) {
                        AdCarousel(
                            adImages = ui.adImages,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 40.dp, top = 20.dp, end = 40.dp, bottom = 20.dp)
                        )
                    }
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
            if (!ui.isSenior) {
                AdCarousel(
                    adImages = ui.adImages,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 40.dp, top = 20.dp, end = 40.dp, bottom = 20.dp)
                )
            }
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
    showFilterDialog = false, // Preview에서는 TTS 문제로 다이얼로그 비활성화
    adImages = listOf(
        AdImageEntity(
            id = 1,
            locationId = 1,
            url = "file:///android_asset/ad_lens.png",
            order = 1,
            language = "ko"
        ),
        AdImageEntity(
            id = 2,
            locationId = 1,
            url = "file:///android_asset/ad_hades.png",
            order = 2,
            language = "ko"
        )
    )
)

@Preview(
    showBackground = true, widthDp = 800, heightDp = 1280, name = "Portrait – Adult"
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
    showBackground = true, widthDp = 1280, heightDp = 800, name = "Landscape – Adult"
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
    showBackground = true, widthDp = 800, heightDp = 1280, name = "Portrait – Senior"
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
    showBackground = true, widthDp = 1280, heightDp = 800, name = "Landscape – Senior + Survey"
)
@Composable
private fun Preview_Landscape_Senior_Survey() {
    PhoriaAndAniseikoniaScreen(
        ui = fakeUi(senior = true, showSurvey = true),
        checkIsTestDone = { true },
        onEvent = {}
    )
}
