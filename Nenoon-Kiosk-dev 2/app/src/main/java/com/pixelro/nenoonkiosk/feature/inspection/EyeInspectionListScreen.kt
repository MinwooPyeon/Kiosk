package com.pixelro.nenoonkiosk.feature.inspection

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.ui.Advertisement
import com.pixelro.nenoonkiosk.core.ui.InspectionSelectionButton
import com.pixelro.nenoonkiosk.core.ui.NenoonTopBar
import com.pixelro.nenoonkiosk.core.ui.SettingsButton
import com.pixelro.nenoonkiosk.core.ui.SurveyRecommendationDialog
import com.pixelro.nenoonkiosk.core.ui.TopBarOrientation
import com.pixelro.nenoonkiosk.feature.inspection.dementia.components.WarningBar
import com.pixelro.nenoonkiosk.ui.theme.LightGray
import com.pixelro.nenoonkiosk.ui.theme.White
import com.pixelro.nenoonkiosk.ui.theme.bodyTextStyle

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun EyeTestInspectionScreen(
    savedLanguage: String?,
    isSenior: Boolean,
    isDialogShowing: Boolean,
    selectedTest: InspectionType,
    isPresbyopiaDone: Boolean,
    isShortVisualAcuityDone: Boolean,
    isAmslerGridDone: Boolean,
    isMChartDone: Boolean,
    isDescriptionShowing: Boolean,
    pagerState: PagerState,
    onBackToIntro: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenTest: (InspectionType) -> Unit,
    onDismissDialog: () -> Unit,
    toTestScreen: (InspectionType) -> Unit,
) {
    val warningTextSize = if (savedLanguage == "ru") 10.sp else 16.sp
    val isLandscapeMode = run {
        val config = androidx.compose.ui.platform.LocalConfiguration.current
        config.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE
    }

    val transition = rememberInfiniteTransition(label = "descShift")
    val shiftVal by transition.animateFloat(
        initialValue = 0f,
        targetValue = 20f,
        animationSpec = infiniteRepeatable(
            animation = keyframes { durationMillis = 2000 },
            repeatMode = RepeatMode.Reverse
        ),
        label = "descShiftAnim"
    )

    if (isDialogShowing) {
        SurveyRecommendationDialog(
            onDismissRequest = onDismissDialog,
            toTestScreen = toTestScreen,
            toIntroScreen = onBackToIntro,
            selectedTest = selectedTest
        )
    }

    if (isLandscapeMode) {
        LandscapeLayout(
            savedLanguage = savedLanguage,
            isSenior = isSenior,
            isShortVisualAcuityDone = isShortVisualAcuityDone,
            isPresbyopiaDone = isPresbyopiaDone,
            isAmslerGridDone = isAmslerGridDone,
            isMChartDone = isMChartDone,
            onBackToIntro = onBackToIntro,
            onOpenSettings = onOpenSettings,
            onOpenTest = onOpenTest,
            warningTextSize = warningTextSize,
            pagerState = pagerState
        )
    } else {
        PortraitLayout(
            savedLanguage = savedLanguage,
            isSenior = isSenior,
            isDescriptionShowing = isDescriptionShowing,
            shiftVal = shiftVal,
            isShortVisualAcuityDone = isShortVisualAcuityDone,
            isPresbyopiaDone = isPresbyopiaDone,
            isAmslerGridDone = isAmslerGridDone,
            isMChartDone = isMChartDone,
            onBackToIntro = onBackToIntro,
            onOpenSettings = onOpenSettings,
            onOpenTest = onOpenTest,
            warningTextSize = warningTextSize,
            pagerState = pagerState
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun PortraitLayout(
    savedLanguage: String?,
    isSenior: Boolean,
    isDescriptionShowing: Boolean,
    shiftVal: Float,
    isShortVisualAcuityDone: Boolean,
    isPresbyopiaDone: Boolean,
    isAmslerGridDone: Boolean,
    isMChartDone: Boolean,
    onBackToIntro: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenTest: (InspectionType) -> Unit,
    warningTextSize: androidx.compose.ui.unit.TextUnit,
    pagerState: PagerState,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = White)
    ) {
        NenoonTopBar(
            title = stringResource(R.string.test_list_tittle),
            orientation = TopBarOrientation.Vertical,
            showBackButton = true,
            onBackClicked = onBackToIntro,
            actions = { SettingsButton(toSettingsScreen = onOpenSettings) },
            containerColor = White,
            contentColor = Color.Black
        )

        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(color = LightGray)
        )

        if (!isSenior) {
            HorizontalPager(
                contentPadding = PaddingValues(
                    start = 40.dp,
                    top = 20.dp,
                    end = 40.dp,
                    bottom = 20.dp
                ),
                pageSpacing = 40.dp,
                state = pagerState
            ) { page ->
                Advertisement(page)
            }
        }

        Box(
            modifier = Modifier
                .padding(start = 40.dp, end = 40.dp, bottom = 20.dp)
                .fillMaxWidth()
                .height(80.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            if (isDescriptionShowing) {
                Text(
                    modifier = Modifier
                        .offset(y = shiftVal.dp)
                        .align(Alignment.Center),
                    text = stringResource(R.string.test_list_description),
                    style = bodyTextStyle,
                    textAlign = TextAlign.Center
                )
            }
        }

        TestListSection(
            isSenior = isSenior,
            isShortVisualAcuityDone = isShortVisualAcuityDone,
            isPresbyopiaDone = isPresbyopiaDone,
            isAmslerGridDone = isAmslerGridDone,
            isMChartDone = isMChartDone,
            onOpenTest = onOpenTest
        )

        WarningBar(warningTextSize = warningTextSize)
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun LandscapeLayout(
    savedLanguage: String?,
    isSenior: Boolean,
    isShortVisualAcuityDone: Boolean,
    isPresbyopiaDone: Boolean,
    isAmslerGridDone: Boolean,
    isMChartDone: Boolean,
    onBackToIntro: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenTest: (InspectionType) -> Unit,
    warningTextSize: androidx.compose.ui.unit.TextUnit,
    pagerState: PagerState,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = White)
    ) {
        NenoonTopBar(
            title = stringResource(R.string.test_list_tittle),
            orientation = TopBarOrientation.Horizontal,
            showBackButton = true,
            onBackClicked = onBackToIntro,
            actions = { SettingsButton(toSettingsScreen = onOpenSettings) },
            containerColor = White,
            contentColor = Color.Black
        )

        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(color = LightGray)
        )

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 30.dp, end = 30.dp, bottom = 16.dp)
        ) {
            // 왼쪽: 광고 + 경고
            Column(
                modifier = Modifier
                    .weight(0.45f)
                    .fillMaxHeight()
                    .padding(end = 20.dp),
                verticalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween
            ) {
                // 광고 (위에 꽉)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.TopCenter
                ) {
                    if (!isSenior) {
                        HorizontalPager(
                            contentPadding = PaddingValues(
                                start = 0.dp,
                                top = 0.dp,
                                end = 0.dp,
                                bottom = 0.dp
                            ),
                            pageSpacing = 20.dp,
                            state = pagerState,
                            modifier = Modifier.fillMaxSize()
                        ) { page ->
                            Advertisement(page)
                        }
                    }
                }

                // 경고 (아래)
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.BottomStart
                ) {
                    WarningBar(warningTextSize = warningTextSize)
                }
            }

            // 구분선
            Spacer(
                modifier = Modifier
                    .width(1.dp)
                    .fillMaxHeight()
                    .background(color = LightGray)
            )

            // 오른쪽: 검사 목록 (버튼 간격)
            Column(
                modifier = Modifier
                    .weight(0.55f)
                    .fillMaxHeight()
                    .padding(top = 12.dp, start = 20.dp, end = 8.dp, bottom = 8.dp),
                verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(12.dp)
            ) {
                // 단거리 시력
                InspectionSelectionButton(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    title1 = stringResource(R.string.test_predescription_short_visual_acuity_title1),
                    title2 = stringResource(R.string.test_predescription_short_visual_acuity_title2),
                    alignment = Alignment.CenterStart,
                    isDone = isShortVisualAcuityDone,
                    isSenior = isSenior,
                    time = 2,
                    onClickMethod = { onOpenTest(InspectionType.ShortDistanceVisualAcuity) }
                )

                // 노안(안구 나이)
                InspectionSelectionButton(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    title1 = stringResource(R.string.test_predescription_presbyopia_title1),
                    title2 = stringResource(R.string.test_predescription_presbyopia_title2),
                    alignment = Alignment.CenterStart,
                    isDone = isPresbyopiaDone,
                    isSenior = isSenior,
                    time = 3,
                    onClickMethod = { onOpenTest(InspectionType.Presbyopia) }
                )

                // 암슬러
                InspectionSelectionButton(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    title1 = stringResource(R.string.test_predescription_amsler_title1),
                    title2 = stringResource(R.string.test_predescription_amsler_title2),
                    alignment = Alignment.CenterStart,
                    isDone = isAmslerGridDone,
                    isSenior = isSenior,
                    time = 2,
                    onClickMethod = { onOpenTest(InspectionType.AmslerGrid) }
                )

                // M-Chart
                InspectionSelectionButton(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    title1 = stringResource(R.string.test_predescription_mchart_title1),
                    title2 = stringResource(R.string.test_predescription_mchart_title2),
                    alignment = Alignment.CenterStart,
                    isDone = isMChartDone,
                    isSenior = isSenior,
                    time = 2,
                    onClickMethod = { onOpenTest(InspectionType.MChart) }
                )
            }
        }
    }
}

@Composable
private fun TestListSection(
    isSenior: Boolean,
    isShortVisualAcuityDone: Boolean,
    isPresbyopiaDone: Boolean,
    isAmslerGridDone: Boolean,
    isMChartDone: Boolean,
    onOpenTest: (InspectionType) -> Unit
) {
    Column {
        val itemModifier = Modifier.weight(1f)

        InspectionSelectionButton(
            modifier = itemModifier,
            title1 = stringResource(R.string.test_predescription_short_visual_acuity_title1),
            title2 = stringResource(R.string.test_predescription_short_visual_acuity_title2),
            alignment = Alignment.CenterStart,
            isDone = isShortVisualAcuityDone,
            isSenior = isSenior,
            time = 2,
            onClickMethod = { onOpenTest(InspectionType.ShortDistanceVisualAcuity) }
        )

        Spacer(Modifier.height(20.dp))

        InspectionSelectionButton(
            modifier = itemModifier,
            title1 = stringResource(R.string.test_predescription_presbyopia_title1),
            title2 = stringResource(R.string.test_predescription_presbyopia_title2),
            alignment = Alignment.CenterStart,
            isDone = isPresbyopiaDone,
            isSenior = isSenior,
            time = 3,
            onClickMethod = { onOpenTest(InspectionType.Presbyopia) }
        )

        Spacer(Modifier.height(20.dp))

        InspectionSelectionButton(
            modifier = itemModifier,
            title1 = stringResource(R.string.test_predescription_amsler_title1),
            title2 = stringResource(R.string.test_predescription_amsler_title2),
            alignment = Alignment.CenterStart,
            isDone = isAmslerGridDone,
            isSenior = isSenior,
            time = 2,
            onClickMethod = { onOpenTest(InspectionType.AmslerGrid) }
        )

        Spacer(Modifier.height(20.dp))

        InspectionSelectionButton(
            modifier = itemModifier,
            title1 = stringResource(R.string.test_predescription_mchart_title1),
            title2 = stringResource(R.string.test_predescription_mchart_title2),
            alignment = Alignment.CenterStart,
            isDone = isMChartDone,
            isSenior = isSenior,
            time = 2,
            onClickMethod = { onOpenTest(InspectionType.MChart) }
        )

        Spacer(Modifier.height(20.dp))
    }
}

// Preview
@OptIn(ExperimentalFoundationApi::class)
@Preview(
    showBackground = true,
    widthDp = 888,
    heightDp = 1422,
    name = "EyeTestList - Senior False - Portrait",
    apiLevel = 34
)
@Composable
private fun Preview_EyeTestList_SeniorFalse_Portrait() {
    val fakePager = rememberPagerState(
        initialPage = Int.MAX_VALUE / 2,
        pageCount = { Int.MAX_VALUE }
    )
    EyeTestInspectionScreen(
        savedLanguage = "ko",
        isSenior = false,
        isDialogShowing = false,
        selectedTest = InspectionType.None,
        isPresbyopiaDone = false,
        isShortVisualAcuityDone = true,
        isAmslerGridDone = false,
        isMChartDone = true,
        isDescriptionShowing = true,
        pagerState = fakePager,
        onBackToIntro = {},
        onOpenSettings = {},
        onOpenTest = {},
        onDismissDialog = {},
        toTestScreen = {}
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Preview(
    showBackground = true,
    widthDp = 888,
    heightDp = 1422,
    name = "EyeTestList - Senior True - Portrait (No Ads)",
    apiLevel = 34
)
@Composable
private fun Preview_EyeTestList_SeniorTrue_Portrait() {
    val fakePager = rememberPagerState(
        initialPage = Int.MAX_VALUE / 2,
        pageCount = { Int.MAX_VALUE }
    )
    EyeTestInspectionScreen(
        savedLanguage = "es",
        isSenior = true,
        isDialogShowing = true,
        selectedTest = InspectionType.MChart,
        isPresbyopiaDone = true,
        isShortVisualAcuityDone = true,
        isAmslerGridDone = false,
        isMChartDone = false,
        isDescriptionShowing = true,
        pagerState = fakePager,
        onBackToIntro = {},
        onOpenSettings = {},
        onOpenTest = {},
        onDismissDialog = {},
        toTestScreen = {}
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Preview(
    showBackground = true,
    widthDp = 1422,
    heightDp = 888,
    name = "EyeTestList - Senior False - Landscape",
    apiLevel = 34
)
@Composable
private fun Preview_EyeTestList_SeniorFalse_Landscape() {
    val fakePager = rememberPagerState(
        initialPage = Int.MAX_VALUE / 2,
        pageCount = { Int.MAX_VALUE }
    )
    EyeTestInspectionScreen(
        savedLanguage = "ko",
        isSenior = false,
        isDialogShowing = false,
        selectedTest = InspectionType.None,
        isPresbyopiaDone = false,
        isShortVisualAcuityDone = true,
        isAmslerGridDone = false,
        isMChartDone = true,
        isDescriptionShowing = true,
        pagerState = fakePager,
        onBackToIntro = {},
        onOpenSettings = {},
        onOpenTest = {},
        onDismissDialog = {},
        toTestScreen = {}
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Preview(
    showBackground = true,
    widthDp = 1422,
    heightDp = 888,
    name = "EyeTestList - Senior True - Landscape (No Ads)",
    apiLevel = 34
)
@Composable
private fun Preview_EyeTestList_SeniorTrue_Landscape() {
    val fakePager = rememberPagerState(
        initialPage = Int.MAX_VALUE / 2,
        pageCount = { Int.MAX_VALUE }
    )
    EyeTestInspectionScreen(
        savedLanguage = "ko",
        isSenior = true,
        isDialogShowing = false,
        selectedTest = InspectionType.MChart,
        isPresbyopiaDone = true,
        isShortVisualAcuityDone = true,
        isAmslerGridDone = false,
        isMChartDone = false,
        isDescriptionShowing = true,
        pagerState = fakePager,
        onBackToIntro = {},
        onOpenSettings = {},
        onOpenTest = {},
        onDismissDialog = {},
        toTestScreen = {}
    )
}
