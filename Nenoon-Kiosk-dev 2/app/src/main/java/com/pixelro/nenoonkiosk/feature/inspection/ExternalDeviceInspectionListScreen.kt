package com.pixelro.nenoonkiosk.feature.inspection

import android.content.res.Configuration
import android.os.Build
import android.speech.tts.TextToSpeech
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.harang.data.db.entity.AdImageEntity
import com.harang.data.repository.AdImageRepository
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.constants.GlobalValue
import com.pixelro.nenoonkiosk.core.ui.AdCarousel
import com.pixelro.nenoonkiosk.core.ui.NenoonTopBar
import com.pixelro.nenoonkiosk.core.ui.SettingsButton
import com.pixelro.nenoonkiosk.core.ui.SimpleInspectionSelectionButton
import com.pixelro.nenoonkiosk.core.ui.SurveyRecommendationDialog
import com.pixelro.nenoonkiosk.core.ui.TopBarOrientation
import com.pixelro.nenoonkiosk.core.util.StringProvider
import com.pixelro.nenoonkiosk.core.util.TTS
import com.pixelro.nenoonkiosk.feature.main.NenoonViewModel
import com.pixelro.nenoonkiosk.ui.theme.DarkRed
import com.pixelro.nenoonkiosk.ui.theme.DividerGray
import com.pixelro.nenoonkiosk.ui.theme.Gray999
import com.pixelro.nenoonkiosk.ui.theme.White
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.delay

// 원하는 검사 항목 선택하는 뷰
@RequiresApi(Build.VERSION_CODES.S)
@OptIn(ExperimentalFoundationApi::class, ExperimentalAnimationApi::class)
@Composable
fun ExternalDeviceInspectionListScreen(
    checkIsTestDone: (InspectionType) -> Boolean,
    toTestScreen: (InspectionType) -> Unit,
    toIntroScreen: () -> Unit,
    toSettingsScreen: () -> Unit,
    isBloodPressureDone: Boolean,
    isGripStrengthDone: Boolean,
    viewModel: NenoonViewModel,
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
    val warningTextSize = if (savedLanguage == "ru") 10.sp else 16.sp
    val isLandscapeMode =
        LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE

    // 광고 리스트 가져오기 (Hilt EntryPoint)
    val adImageRepository = remember {
        EntryPointAccessors.fromApplication(
            context.applicationContext,
            AdImageRepositoryEntryPoint::class.java
        ).adImageRepository()
    }
    val adImages by adImageRepository.getAdImagesByLocationAndLanguage(1, savedLanguage)
        .collectAsState(initial = emptyList())

    val pagerState =
        rememberPagerState(
            initialPage = Int.MAX_VALUE / 2,
            initialPageOffsetFraction = 0f,
            pageCount = { Int.MAX_VALUE },
        )
    val isDescriptionShowing = remember { mutableStateOf(true) }
    LaunchedEffect(true) {
        TTS.stopTTS()
        TTS.speechTTS(StringProvider.getString(R.string.select_test_tts), TextToSpeech.QUEUE_ADD)
        while (true) {
            delay(5000)
            pagerState.animateScrollToPage(
                page = (pagerState.currentPage + 1),
                animationSpec = tween(1000),
            )
            for (i in 1..3) {
                isDescriptionShowing.value = false
                delay(250)
                isDescriptionShowing.value = true
                delay(250)
            }
        }
    }
    var isDialogShowing by remember { mutableStateOf(false) }
    var selectedTest by remember { mutableStateOf(InspectionType.None) }
    val transition = rememberInfiniteTransition(label = "")
    val shiftVal by transition.animateFloat(
        initialValue = 0f,
        targetValue = 20f,
        animationSpec =
            infiniteRepeatable(
                animation =
                    keyframes {
                        durationMillis = 2000
                    },
                repeatMode = RepeatMode.Reverse,
            ),
        label = "",
    )
    val isSeniorValue by viewModel.isSenior.collectAsState()

    if (isDialogShowing) {
        SurveyRecommendationDialog(
            onDismissRequest = {
                isDialogShowing = false
            },
            toTestScreen = toTestScreen,
            toIntroScreen = toIntroScreen,
            selectedTest = selectedTest,
        )
    }

    if (isLandscapeMode) {
        LandscapeExternalDeviceLayout(
            adImages = adImages,
            savedLanguage = savedLanguage,
            isSeniorValue = isSeniorValue,
            toIntroScreen = toIntroScreen,
            toSettingsScreen = toSettingsScreen,
            pagerState = pagerState,
            checkIsTestDone = checkIsTestDone,
            toTestScreen = toTestScreen,
            isBloodPressureDone = isBloodPressureDone,
            isGripStrengthDone = isGripStrengthDone,
            selectedTest = selectedTest,
            onSelectedTestChange = { selectedTest = it },
            onDialogShow = { isDialogShowing = true },
            warningTextSize = warningTextSize
        )
    } else {
        PortraitExternalDeviceLayout(
            adImages = adImages,
            savedLanguage = savedLanguage,
            isSeniorValue = isSeniorValue,
            toIntroScreen = toIntroScreen,
            toSettingsScreen = toSettingsScreen,
            pagerState = pagerState,
            isDescriptionShowing = isDescriptionShowing,
            shiftVal = shiftVal,
            checkIsTestDone = checkIsTestDone,
            toTestScreen = toTestScreen,
            isBloodPressureDone = isBloodPressureDone,
            isGripStrengthDone = isGripStrengthDone,
            selectedTest = selectedTest,
            onSelectedTestChange = { selectedTest = it },
            onDialogShow = { isDialogShowing = true },
            warningTextSize = warningTextSize
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun PortraitExternalDeviceLayout(
    adImages: List<AdImageEntity>,
    savedLanguage: String?,
    isSeniorValue: Boolean,
    toIntroScreen: () -> Unit,
    toSettingsScreen: () -> Unit,
    pagerState: androidx.compose.foundation.pager.PagerState,
    isDescriptionShowing: androidx.compose.runtime.MutableState<Boolean>,
    shiftVal: Float,
    checkIsTestDone: (InspectionType) -> Boolean,
    toTestScreen: (InspectionType) -> Unit,
    isBloodPressureDone: Boolean,
    isGripStrengthDone: Boolean,
    selectedTest: InspectionType,
    onSelectedTestChange: (InspectionType) -> Unit,
    onDialogShow: () -> Unit,
    warningTextSize: androidx.compose.ui.unit.TextUnit,
) {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(color = White),
    ) {
        NenoonTopBar(
            title = stringResource(R.string.test_list_tittle),
            orientation = TopBarOrientation.Vertical,
            showBackButton = true,
            onBackClicked = toIntroScreen,
            actions = { SettingsButton(toSettingsScreen = toSettingsScreen) },
            containerColor = White,
            contentColor = Color.Black
        )

        Spacer(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(color = DividerGray),
        )

        if (!isSeniorValue) {
            AdCarousel(
                adImages = adImages,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 40.dp, top = 20.dp, end = 40.dp, bottom = 20.dp)
            )
        }

        Box(
            modifier =
                Modifier
                    .padding(start = 40.dp, end = 40.dp, bottom = 20.dp)
                    .fillMaxWidth()
                    .height(80.dp),
            contentAlignment = Alignment.TopCenter,
        ) {
            if (isDescriptionShowing.value) {
                Text(
                    modifier =
                        Modifier
                            .offset(x = 0.dp, y = shiftVal.dp),
                    text =
                        stringResource(
                            R.string.test_list_description,
                        ),
                    fontSize =
                        if (savedLanguage == "es") {
                            20.sp
                        } else {
                            38.sp
                        },
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center,
                )
            }
        }

        ExternalDeviceTestListSection(
            checkIsTestDone = checkIsTestDone,
            toTestScreen = toTestScreen,
            isBloodPressureDone = isBloodPressureDone,
            isGripStrengthDone = isGripStrengthDone,
            isSeniorValue = isSeniorValue,
            selectedTest = selectedTest,
            onSelectedTestChange = onSelectedTestChange,
            onDialogShow = onDialogShow,
        )

        ExternalDeviceWarningBar(warningTextSize = warningTextSize)
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun LandscapeExternalDeviceLayout(
    adImages: List<AdImageEntity>,
    savedLanguage: String?,
    isSeniorValue: Boolean,
    toIntroScreen: () -> Unit,
    toSettingsScreen: () -> Unit,
    pagerState: androidx.compose.foundation.pager.PagerState,
    checkIsTestDone: (InspectionType) -> Boolean,
    toTestScreen: (InspectionType) -> Unit,
    isBloodPressureDone: Boolean,
    isGripStrengthDone: Boolean,
    selectedTest: InspectionType,
    onSelectedTestChange: (InspectionType) -> Unit,
    onDialogShow: () -> Unit,
    warningTextSize: androidx.compose.ui.unit.TextUnit,
) {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(color = White),
    ) {
        NenoonTopBar(
            title = stringResource(R.string.test_list_tittle),
            orientation = TopBarOrientation.Horizontal,
            showBackButton = true,
            onBackClicked = toIntroScreen,
            actions = { SettingsButton(toSettingsScreen = toSettingsScreen) },
            containerColor = White,
            contentColor = Color.Black
        )

        Spacer(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(color = DividerGray),
        )

        Row(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(start = 30.dp, end = 30.dp, bottom = 16.dp),
        ) {
            // 왼쪽: 광고 + 경고
            Column(
                modifier =
                    Modifier
                        .weight(0.45f)
                        .fillMaxHeight()
                        .padding(end = 20.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // 광고 (위에 꽉)
                Box(
                    modifier =
                        Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                    contentAlignment = Alignment.TopCenter,
                ) {
                    if (!isSeniorValue) {
                        AdCarousel(
                            adImages = adImages,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }

                // 경고 (아래에 배치)
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.BottomStart
                ) {
                    ExternalDeviceWarningBar(warningTextSize = warningTextSize)
                }
            }

            // 구분선
            Spacer(
                modifier =
                    Modifier
                        .width(1.dp)
                        .fillMaxHeight()
                        .background(color = DividerGray),
            )

            // 오른쪽: 검사 목록 (버튼 2개 아래까지 꽉)
            Column(
                modifier =
                    Modifier
                        .weight(0.55f)
                        .fillMaxHeight()
                        .padding(top = 12.dp, start = 20.dp, end = 8.dp, bottom = 12.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // 혈압 검사
                SimpleInspectionSelectionButton(
                    modifier =
                        Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                    title =
                        stringResource(
                            R.string.test_predescription_blood_pressure_title1,
                        ),
                    onClick = {
                        onSelectedTestChange(InspectionType.BloodPressure)
                        if (checkIsTestDone(InspectionType.BloodPressure)) {
                            onDialogShow()
                        } else {
                            toTestScreen(InspectionType.BloodPressure)
                        }
                    },
                    isDone = isBloodPressureDone,
                    time = 2,
                )

                // 악력 검사
                SimpleInspectionSelectionButton(
                    modifier =
                        Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                    title =
                        stringResource(
                            R.string.test_predescription_grip_strength_title1,
                        ),
                    onClick = {
                        onSelectedTestChange(InspectionType.GripStrength)
                        if (checkIsTestDone(InspectionType.GripStrength)) {
                            onDialogShow()
                        } else {
                            toTestScreen(InspectionType.GripStrength)
                        }
                    },
                    isDone = isGripStrengthDone,
                    time = 2,
                )
            }
        }
    }
}

@Composable
private fun ExternalDeviceTestListSection(
    checkIsTestDone: (InspectionType) -> Boolean,
    toTestScreen: (InspectionType) -> Unit,
    isBloodPressureDone: Boolean,
    isGripStrengthDone: Boolean,
    isSeniorValue: Boolean,
    selectedTest: InspectionType,
    onSelectedTestChange: (InspectionType) -> Unit,
    onDialogShow: () -> Unit,
) {
    Column {
        val modifier = Modifier
            .weight(1f)
            .padding(start = 40.dp, end = 40.dp, bottom = 5.dp)

        SimpleInspectionSelectionButton(
            modifier = modifier,
            title =
                stringResource(
                    R.string.test_predescription_blood_pressure_title1,
                ),
            onClick = {
                onSelectedTestChange(InspectionType.BloodPressure)
                if (checkIsTestDone(InspectionType.BloodPressure)) {
                    onDialogShow()
                } else {
                    toTestScreen(InspectionType.BloodPressure)
                }
            },
            isDone = isBloodPressureDone,
            time = 2,
        )
        Spacer(modifier = Modifier.height(20.dp))

        SimpleInspectionSelectionButton(
            modifier = modifier,
            title =
                stringResource(
                    R.string.test_predescription_grip_strength_title1,
                ),
            onClick = {
                onSelectedTestChange(InspectionType.GripStrength)
                if (checkIsTestDone(InspectionType.GripStrength)) {
                    onDialogShow()
                } else {
                    toTestScreen(InspectionType.GripStrength)
                }
            },
            isDone = isGripStrengthDone,
            time = 2,
        )

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
private fun ExternalDeviceWarningBar(warningTextSize: androidx.compose.ui.unit.TextUnit) {
    Row(
        modifier =
            Modifier
                .padding(
                    start = 40.dp,
                    bottom = (GlobalValue.navigationBarPadding + 40).dp,
                )
                .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            modifier =
                Modifier
                    .padding(end = 20.dp)
                    .width(44.dp),
            painter = painterResource(id = R.drawable.icon_warning),
            contentDescription = "",
        )
        Text(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(end = 40.dp),
            text =
                buildAnnotatedString {
                    withStyle(
                        style =
                            SpanStyle(
                                color = Gray999,
                                fontSize = warningTextSize,
                            ),
                    ) {
                        append(
                            stringResource(
                                R.string.test_list_screen_warning1,
                            ),
                        )
                    }
                    withStyle(
                        style =
                            SpanStyle(
                                color = DarkRed,
                                fontSize = warningTextSize,
                                fontWeight = FontWeight.Bold,
                            ),
                    ) {
                        append(
                            stringResource(
                                R.string.test_list_screen_warning2,
                            ),
                        )
                    }
                    withStyle(
                        style =
                            SpanStyle(
                                color = Gray999,
                                fontSize = warningTextSize,
                            ),
                    ) {
                        append(
                            stringResource(
                                R.string.test_list_screen_warning3,
                            ),
                        )
                    }
                },
        )
    }
}

// Preview
@OptIn(ExperimentalFoundationApi::class)
@Preview(
    showBackground = true,
    widthDp = 800,
    heightDp = 1280,
    name = "ExternalDevice - Senior False - Portrait",
    apiLevel = 34
)
@Composable
private fun Preview_ExternalDeviceTestList_SeniorFalse_Portrait() {
    val fakePager = rememberPagerState(
        initialPage = Int.MAX_VALUE / 2,
        pageCount = { Int.MAX_VALUE }
    )
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
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        PortraitExternalDeviceLayout(
            adImages = dummyAdImages,
            savedLanguage = "ko",
            isSeniorValue = false,
            toIntroScreen = {},
            toSettingsScreen = {},
            pagerState = fakePager,
            isDescriptionShowing = remember { mutableStateOf(true) },
            shiftVal = 0f,
            checkIsTestDone = { false },
            toTestScreen = {},
            isBloodPressureDone = false,
            isGripStrengthDone = true,
            selectedTest = InspectionType.None,
            onSelectedTestChange = {},
            onDialogShow = {},
            warningTextSize = 16.sp
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Preview(
    showBackground = true,
    widthDp = 800,
    heightDp = 1280,
    name = "ExternalDevice - Senior True - Portrait (No Ads)",
    apiLevel = 34
)
@Composable
private fun Preview_ExternalDeviceTestList_SeniorTrue_Portrait() {
    val fakePager = rememberPagerState(
        initialPage = Int.MAX_VALUE / 2,
        pageCount = { Int.MAX_VALUE }
    )
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        PortraitExternalDeviceLayout(
            adImages = emptyList(),
            savedLanguage = "ko",
            isSeniorValue = true,
            toIntroScreen = {},
            toSettingsScreen = {},
            pagerState = fakePager,
            isDescriptionShowing = remember { mutableStateOf(true) },
            shiftVal = 0f,
            checkIsTestDone = { false },
            toTestScreen = {},
            isBloodPressureDone = true,
            isGripStrengthDone = false,
            selectedTest = InspectionType.None,
            onSelectedTestChange = {},
            onDialogShow = {},
            warningTextSize = 16.sp
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Preview(
    showBackground = true,
    widthDp = 1280,
    heightDp = 800,
    name = "ExternalDevice - Senior False - Landscape",
    apiLevel = 34
)
@Composable
private fun Preview_ExternalDeviceTestList_SeniorFalse_Landscape() {
    val fakePager = rememberPagerState(
        initialPage = Int.MAX_VALUE / 2,
        pageCount = { Int.MAX_VALUE }
    )
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
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        LandscapeExternalDeviceLayout(
            adImages = dummyAdImages,
            savedLanguage = "ko",
            isSeniorValue = false,
            toIntroScreen = {},
            toSettingsScreen = {},
            pagerState = fakePager,
            checkIsTestDone = { false },
            toTestScreen = {},
            isBloodPressureDone = false,
            isGripStrengthDone = true,
            selectedTest = InspectionType.None,
            onSelectedTestChange = {},
            onDialogShow = {},
            warningTextSize = 16.sp
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Preview(
    showBackground = true,
    widthDp = 1280,
    heightDp = 800,
    name = "ExternalDevice - Senior True - Landscape (No Ads)",
    apiLevel = 34
)
@Composable
private fun Preview_ExternalDeviceTestList_SeniorTrue_Landscape() {
    val fakePager = rememberPagerState(
        initialPage = Int.MAX_VALUE / 2,
        pageCount = { Int.MAX_VALUE }
    )
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        LandscapeExternalDeviceLayout(
            adImages = emptyList(),
            savedLanguage = "ko",
            isSeniorValue = true,
            toIntroScreen = {},
            toSettingsScreen = {},
            pagerState = fakePager,
            checkIsTestDone = { false },
            toTestScreen = {},
            isBloodPressureDone = true,
            isGripStrengthDone = false,
            selectedTest = InspectionType.None,
            onSelectedTestChange = {},
            onDialogShow = {},
            warningTextSize = 16.sp
        )
    }
}

@dagger.hilt.EntryPoint
@dagger.hilt.InstallIn(dagger.hilt.components.SingletonComponent::class)
interface AdImageRepositoryEntryPoint {
    fun adImageRepository(): AdImageRepository
}
