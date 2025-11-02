package com.pixelro.nenoonkiosk.feature.inspection

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.constants.GlobalValue
import com.pixelro.nenoonkiosk.core.ui.Advertisement
import com.pixelro.nenoonkiosk.core.ui.InspectionSelectionButton
import com.pixelro.nenoonkiosk.core.ui.SettingsButton
import com.pixelro.nenoonkiosk.core.ui.SurveyRecommendationDialog
import com.pixelro.nenoonkiosk.core.util.StringProvider
import com.pixelro.nenoonkiosk.feature.inspection.dementia.components.WarningBar
import com.pixelro.nenoonkiosk.ui.theme.bodyTextStyle

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun EyeInspectionListScreen(
    savedLanguage: String?,
    isSenior: Boolean,
    isDialogShowing: Boolean,
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
    onConfirmTest: () -> Unit
) {
    val warningTextSize = if (savedLanguage == "ru") 10.sp else 16.sp
    val titleBackFontSize = if (savedLanguage == "es") 12.sp else 24.sp

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
            onConfirmTest = onConfirmTest,
            onBackToIntro = onBackToIntro
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color(0xFFFFFFFF))
    ) {
        TopBar(
            savedLanguage = savedLanguage,
            titleText = StringProvider.getStringComposable(R.string.test_list_tittle),
            onBackToIntro = onBackToIntro,
            onOpenSettings = onOpenSettings,
            titleBackFontSize = titleBackFontSize
        )

        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(color = Color(0xFFEBEBEB))
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
                    text = StringProvider.getStringComposable(R.string.test_list_description),
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

@Composable
private fun TopBar(
    savedLanguage: String?,
    titleText: String,
    onBackToIntro: () -> Unit,
    onOpenSettings: () -> Unit,
    titleBackFontSize: TextUnit
) {
    Box(
        modifier = Modifier
            .padding(
                start = 40.dp,
                top = (GlobalValue.statusBarPadding + 20).dp,
                end = 40.dp,
                bottom = 20.dp
            )
            .fillMaxWidth()
            .height(40.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxHeight()
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) { onBackToIntro() },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                modifier = Modifier
                    .padding(top = 4.dp)
                    .width(28.dp),
                painter = painterResource(id = R.drawable.icon_back_black),
                contentDescription = ""
            )
            Text(
                text = StringProvider.getStringComposable(R.string.navigation_tosurvey_button),
                fontSize = titleBackFontSize,
                fontWeight = FontWeight.Medium
            )
        }

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = titleText,
                fontSize = 24.sp,
                fontWeight = FontWeight.Medium
            )
        }

        SettingsButton(onOpenSettings)
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
            title1 = StringProvider.getStringComposable(R.string.test_predescription_short_visual_acuity_title1),
            title2 = StringProvider.getStringComposable(R.string.test_predescription_short_visual_acuity_title2),
            alignment = Alignment.CenterStart,
            isDone = isShortVisualAcuityDone,
            isSenior = isSenior,
            time = 2,
            onClickMethod = { onOpenTest(InspectionType.ShortDistanceVisualAcuity) }
        )

        Spacer(Modifier.height(20.dp))

        InspectionSelectionButton(
            modifier = itemModifier,
            title1 = StringProvider.getStringComposable(R.string.test_predescription_presbyopia_title1),
            title2 = StringProvider.getStringComposable(R.string.test_predescription_presbyopia_title2),
            alignment = Alignment.CenterStart,
            isDone = isPresbyopiaDone,
            isSenior = isSenior,
            time = 3,
            onClickMethod = { onOpenTest(InspectionType.Presbyopia) }
        )

        Spacer(Modifier.height(20.dp))

        InspectionSelectionButton(
            modifier = itemModifier,
            title1 = StringProvider.getStringComposable(R.string.test_predescription_amsler_title1),
            title2 = StringProvider.getStringComposable(R.string.test_predescription_amsler_title2),
            alignment = Alignment.CenterStart,
            isDone = isAmslerGridDone,
            isSenior = isSenior,
            time = 2,
            onClickMethod = { onOpenTest(InspectionType.AmslerGrid) }
        )

        Spacer(Modifier.height(20.dp))

        InspectionSelectionButton(
            modifier = itemModifier,
            title1 = StringProvider.getStringComposable(R.string.test_predescription_mchart_title1),
            title2 = StringProvider.getStringComposable(R.string.test_predescription_mchart_title2),
            alignment = Alignment.CenterStart,
            isDone = isMChartDone,
            isSenior = isSenior,
            time = 2,
            onClickMethod = { onOpenTest(InspectionType.MChart) }
        )

        Spacer(Modifier.height(20.dp))
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Preview(
    showBackground = true,
    widthDp = 888,
    heightDp = 1422,
    name = "EyeTestList - Senior False",
    apiLevel = 34
)
@Composable
private fun Preview_EyeTestList_SeniorFalse() {
    val fakePager = rememberPagerState(
        initialPage = Int.MAX_VALUE / 2,
        pageCount = { Int.MAX_VALUE }
    )
    EyeInspectionListScreen(
        savedLanguage = "ko",
        isSenior = false,
        isDialogShowing = false,
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
        onConfirmTest = {}
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Preview(
    showBackground = true,
    widthDp = 888,
    heightDp = 1422,
    name = "EyeTestList - Senior True (No Ads)",
    apiLevel = 34
)
@Composable
private fun Preview_EyeTestList_SeniorTrue() {
    val fakePager = rememberPagerState(
        initialPage = Int.MAX_VALUE / 2,
        pageCount = { Int.MAX_VALUE }
    )
    EyeInspectionListScreen(
        savedLanguage = "es",
        isSenior = true,
        isDialogShowing = true,
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
        onConfirmTest = {}
    )
}
