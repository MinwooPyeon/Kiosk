package com.pixelro.nenoonkiosk.feature.inspection

import android.content.res.Configuration.ORIENTATION_LANDSCAPE
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.harang.data.db.entity.AdImageEntity
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.ui.AdCarousel
import com.pixelro.nenoonkiosk.core.ui.NenoonTopBar
import com.pixelro.nenoonkiosk.core.ui.SettingsButton
import com.pixelro.nenoonkiosk.core.ui.SimpleInspectionSelectionButton
import com.pixelro.nenoonkiosk.core.ui.SurveyRecommendationDialog
import com.pixelro.nenoonkiosk.core.ui.TopBarOrientation
import com.pixelro.nenoonkiosk.core.ui.TwoLineInspectionSelectionButton
import com.pixelro.nenoonkiosk.feature.inspection.dementia.components.WarningBar
import com.pixelro.nenoonkiosk.ui.theme.LightGray100
import com.pixelro.nenoonkiosk.ui.theme.White
import com.pixelro.nenoonkiosk.ui.theme.bodyTextStyle
import com.pixelro.nenoonkiosk.ui.theme.neNoon_blue

@Composable
fun EyeTestInspectionScreen(
    adImages: List<AdImageEntity> = emptyList(),
    savedLanguage: String?,
    isSenior: Boolean,
    isDialogShowing: Boolean,
    selectedTest: InspectionType,
    isPresbyopiaDone: Boolean,
    isShortVisualAcuityDone: Boolean,
    isAmslerGridDone: Boolean,
    isMChartDone: Boolean,
    isDescriptionShowing: Boolean,
    onAdPageChange: () -> Unit,
    onBackToIntro: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenTest: (InspectionType) -> Unit,
    onDismissDialog: () -> Unit,
    toTestScreen: (InspectionType) -> Unit,
) {
    val warningTextSize = if (savedLanguage == "ru") 10.sp else 16.sp
    val isLandscapeMode = run {
        val config = LocalConfiguration.current
        config.orientation == ORIENTATION_LANDSCAPE
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
            adImages = adImages,
            isSenior = isSenior,
            isShortVisualAcuityDone = isShortVisualAcuityDone,
            isPresbyopiaDone = isPresbyopiaDone,
            isAmslerGridDone = isAmslerGridDone,
            isMChartDone = isMChartDone,
            onBackToIntro = onBackToIntro,
            onOpenSettings = onOpenSettings,
            onOpenTest = onOpenTest,
            onAdPageChange = onAdPageChange,
            warningTextSize = warningTextSize
        )
    } else {
        PortraitLayout(
            adImages = adImages,
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
            onAdPageChange = onAdPageChange,
            warningTextSize = warningTextSize
        )
    }
}

@Composable
private fun PortraitLayout(
    adImages: List<AdImageEntity>,
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
    onAdPageChange: () -> Unit,
    warningTextSize: androidx.compose.ui.unit.TextUnit,
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
                .background(color = LightGray100)
        )

        if (!isSenior) {
            AdCarousel(
                adImages = adImages,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 40.dp, top = 20.dp, end = 40.dp, bottom = 20.dp),
                onPageChange = onAdPageChange
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 40.dp)
        ) {
            // 시력 검사 섹션
            Text(
                text = stringResource(R.string.test_predescription_short_visual_acuity_title1),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = neNoon_blue,
                modifier = Modifier.padding(top = 12.dp, bottom = 8.dp)
            )

            // 근거리
            SimpleInspectionSelectionButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(bottom = 8.dp),
                title = stringResource(R.string.short_visual_acuity_name2),
                isDone = isShortVisualAcuityDone,
                time = 2,
                onClick = { onOpenTest(InspectionType.ShortDistanceVisualAcuity) }
            )

            // 원거리
            SimpleInspectionSelectionButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(bottom = 16.dp),
                title = stringResource(R.string.long_visual_acuity_name),
                isDone = false, // TODO: Add state parameter
                time = 2,
                onClick = { onOpenTest(InspectionType.LongDistanceVisualAcuity) }
            )

            // 황반 변성 검사 섹션
            Text(
                text = stringResource(R.string.macular_degeneration_name),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = neNoon_blue,
                modifier = Modifier.padding(top = 4.dp, bottom = 8.dp)
            )

            // 암슬러 차트
            SimpleInspectionSelectionButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(bottom = 8.dp),
                title = stringResource(R.string.test_predescription_amsler_title1),
                isDone = isAmslerGridDone,
                time = 2,
                onClick = { onOpenTest(InspectionType.AmslerGrid) }
            )

            // 엠식 변형시
            SimpleInspectionSelectionButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(bottom = 16.dp),
                title = stringResource(R.string.test_predescription_mchart_title1),
                isDone = isMChartDone,
                time = 2,
                onClick = { onOpenTest(InspectionType.MChart) }
            )

            // 안구 나이 검사 섹션
            Text(
                text = stringResource(R.string.test_predescription_presbyopia_title2),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = neNoon_blue,
                modifier = Modifier.padding(top = 4.dp, bottom = 8.dp)
            )

            // 노안조절력 검사
            SimpleInspectionSelectionButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(bottom = 8.dp),
                title = stringResource(R.string.test_predescription_presbyopia_title1),
                isDone = isPresbyopiaDone,
                time = 3,
                onClick = { onOpenTest(InspectionType.Presbyopia) }
            )

            Spacer(modifier = Modifier.height(10.dp))

            WarningBar(warningTextSize = warningTextSize)
        }
    }
}

@Composable
private fun LandscapeLayout(
    adImages: List<AdImageEntity>,
    isSenior: Boolean,
    isShortVisualAcuityDone: Boolean,
    isPresbyopiaDone: Boolean,
    isAmslerGridDone: Boolean,
    isMChartDone: Boolean,
    onBackToIntro: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenTest: (InspectionType) -> Unit,
    onAdPageChange: () -> Unit,
    warningTextSize: androidx.compose.ui.unit.TextUnit,
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
                .background(color = LightGray100)
        )

        Column(
            modifier = Modifier
                .fillMaxSize().padding(30.dp)
        ) {
            // 2x2 그리드
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // 첫 번째 행: 시력검사 (왼쪽 위) | 황반변성 검사 (오른쪽 위)
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(26.dp)
                ) {
                    // 왼쪽 위: 시력검사
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.test_predescription_short_visual_acuity_title1),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = neNoon_blue,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )

                        // 근거리
                        SimpleInspectionSelectionButton(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            title = stringResource(R.string.short_visual_acuity_name2),
                            isDone = isShortVisualAcuityDone,
                            time = 2,
                            onClick = { onOpenTest(InspectionType.ShortDistanceVisualAcuity) }
                        )

                        // 원거리
                        SimpleInspectionSelectionButton(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            title = stringResource(R.string.long_visual_acuity_name),
                            isDone = false,
                            time = 2,
                            onClick = { onOpenTest(InspectionType.LongDistanceVisualAcuity) }
                        )
                    }

                    // 오른쪽 위: 황반변성 검사
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.macular_degeneration_name),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = neNoon_blue,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )

                        // 암슬러 차트
                        SimpleInspectionSelectionButton(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            title = stringResource(R.string.test_predescription_amsler_title1),
                            isDone = isAmslerGridDone,
                            time = 2,
                            onClick = { onOpenTest(InspectionType.AmslerGrid) }
                        )

                        // 엠식 변형시
                        SimpleInspectionSelectionButton(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            title = stringResource(R.string.test_predescription_mchart_title1),
                            isDone = isMChartDone,
                            time = 2,
                            onClick = { onOpenTest(InspectionType.MChart) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(1.dp))

                // 두 번째 행: 광고 배너 (왼쪽 아래) | 안구 나이 검사 + 경고 (오른쪽 아래)
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(26.dp)
                ) {
                    // 왼쪽 아래: 광고 배너
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        contentAlignment = Alignment.Center
                    ) {
                        if (!isSenior) {
                            AdCarousel(
                                adImages = adImages,
                                modifier = Modifier.fillMaxSize(),
                                onPageChange = onAdPageChange
                            )
                        }
                    }

                    // 오른쪽 아래: 안구 나이 검사 + 경고
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.test_predescription_presbyopia_title2),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = neNoon_blue,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )

                        // 노안조절력 검사
                        SimpleInspectionSelectionButton(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            title = stringResource(R.string.test_predescription_presbyopia_title1),
                            isDone = isPresbyopiaDone,
                            time = 3,
                            onClick = { onOpenTest(InspectionType.Presbyopia) }
                        )

                        // 경고 배너
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                            contentAlignment = Alignment.BottomStart
                        ) {
                            WarningBar(warningTextSize = warningTextSize)
                        }
                    }
                }
            }
        }
    }
}

// Preview
@Preview(
    showBackground = true,
    widthDp = 888,
    heightDp = 1422,
    name = "EyeTestList - Senior False - Portrait",
    apiLevel = 34
)
@Composable
private fun Preview_EyeTestList_SeniorFalse_Portrait() {
    val dummyAdImages = listOf(
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

    EyeTestInspectionScreen(
        adImages = dummyAdImages,
        savedLanguage = "ko",
        isSenior = false,
        isDialogShowing = false,
        selectedTest = InspectionType.None,
        isPresbyopiaDone = false,
        isShortVisualAcuityDone = true,
        isAmslerGridDone = false,
        isMChartDone = true,
        isDescriptionShowing = true,
        onAdPageChange = {},
        onBackToIntro = {},
        onOpenSettings = {},
        onOpenTest = {},
        onDismissDialog = {},
        toTestScreen = {}
    )
}

@Preview(
    showBackground = true,
    widthDp = 1280,
    heightDp = 800,
    name = "EyeTestList - Senior False - Landscape",
    apiLevel = 34
)
@Composable
private fun Preview_EyeTestList_SeniorFalse_Landscape() {
    val dummyAdImages = listOf(
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

    EyeTestInspectionScreen(
        adImages = dummyAdImages,
        savedLanguage = "ko",
        isSenior = false,
        isDialogShowing = false,
        selectedTest = InspectionType.None,
        isPresbyopiaDone = false,
        isShortVisualAcuityDone = true,
        isAmslerGridDone = false,
        isMChartDone = true,
        isDescriptionShowing = true,
        onAdPageChange = {},
        onBackToIntro = {},
        onOpenSettings = {},
        onOpenTest = {},
        onDismissDialog = {},
        toTestScreen = {}
    )
}
