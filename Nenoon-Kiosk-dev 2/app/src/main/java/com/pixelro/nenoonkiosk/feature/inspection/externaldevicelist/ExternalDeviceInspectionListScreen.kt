package com.pixelro.nenoonkiosk.feature.inspection.externaldevicelist

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
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
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.constants.GlobalValue
import com.pixelro.nenoonkiosk.core.constants.NavConstants
import com.pixelro.nenoonkiosk.core.ui.Advertisement
import com.pixelro.nenoonkiosk.core.ui.InspectionSelectionButton
import com.pixelro.nenoonkiosk.core.ui.SettingsButton
import com.pixelro.nenoonkiosk.core.ui.SurveyRecommendationDialog
import com.pixelro.nenoonkiosk.core.util.StringProvider
import com.pixelro.nenoonkiosk.feature.inspection.InspectionType
import com.pixelro.nenoonkiosk.feature.inspection.externaldevice.ExternalDeviceInspectionListSideEffect
import com.pixelro.nenoonkiosk.feature.inspection.externaldevice.ExternalDeviceInspectionListUiState
import kotlinx.coroutines.delay

/**
 * 외부 장비 검사 목록 화면
 *
 * State Hoisting 패턴을 적용하여 상태는 상위에서 관리하고,
 * 이벤트는 콜백으로 전달받습니다.
 *
 * @param state UI 상태
 * @param onSideEffect 사용자 이벤트 처리 콜백
 * @param onBackClick 뒤로가기 콜백
 * @param onSettingsClick 설정 버튼 콜백
 * @param onNavigateToInspection 검사 화면 이동 콜백
 * @param onNavigateToSurvey 설문 화면 이동 콜백
 */
@RequiresApi(Build.VERSION_CODES.S)
@OptIn(ExperimentalFoundationApi::class, ExperimentalAnimationApi::class)
@Composable
fun ExternalDeviceInspectionListScreen(
    state: ExternalDeviceInspectionListUiState,
    onSideEffect: (ExternalDeviceInspectionListSideEffect) -> Unit,
    onBackClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onNavigateToInspection: (InspectionType) -> Unit,
    onNavigateToSurvey: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val sharedPreferences = remember {
        context.getSharedPreferences(NavConstants.PREFERENCE_NAME, Context.MODE_PRIVATE)
    }
    val savedLanguage = sharedPreferences.getString("language", "defaultLanguage")
    val warningTextSize = if (savedLanguage == "ru") 10.sp else 16.sp

    // 광고 페이저 상태
    val pagerState = rememberPagerState(
        initialPage = Int.MAX_VALUE / 2,
        initialPageOffsetFraction = 0f,
        pageCount = { Int.MAX_VALUE },
    )

    // 설명 텍스트 애니메이션 상태
    var isDescriptionShowing by remember { mutableStateOf(true) }

    // 광고 자동 스크롤 및 설명 깜빡임 효과
    LaunchedEffect(Unit) {
        while (true) {
            delay(5000)
            pagerState.animateScrollToPage(
                page = (pagerState.currentPage + 1),
                animationSpec = tween(1000),
            )
            for (i in 1..3) {
                isDescriptionShowing = false
                delay(250)
                isDescriptionShowing = true
                delay(250)
            }
        }
    }

    // 설명 텍스트 이동 애니메이션
    val transition = rememberInfiniteTransition(label = "")
    val shiftVal by transition.animateFloat(
        initialValue = 0f,
        targetValue = 20f,
        animationSpec = infiniteRepeatable(
            animation = keyframes { durationMillis = 2000 },
            repeatMode = RepeatMode.Reverse,
        ),
        label = "",
    )

    // 검사 재진행 확인 다이얼로그
    if (state.isDialogShowing) {
        SurveyRecommendationDialog(
            onDismissRequest = {
                onSideEffect(ExternalDeviceInspectionListSideEffect.OnDialogDismissed)
            },
            onConfirmTest = {
                onNavigateToInspection(state.selectedInspection)
            },
            onBackToIntro = {
                onNavigateToSurvey()
            }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(color = Color(0xffffffff)),
    ) {
        // 상단 네비게이션 바
        TopNavigationBar(
            savedLanguage = savedLanguage,
            onBackClick = onBackClick,
            onSettingsClick = onSettingsClick
        )

        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(color = Color(0xffebebeb)),
        )

        // 광고 영역 (시니어 모드가 아닐 때만 표시)
        if (!state.isSenior) {
            HorizontalPager(
                contentPadding = PaddingValues(
                    start = 40.dp,
                    top = 20.dp,
                    end = 40.dp,
                    bottom = 20.dp
                ),
                pageSpacing = 40.dp,
                state = pagerState,
            ) {
                Advertisement(it)
            }
        }

        // 설명 텍스트
        Box(
            modifier = Modifier
                .padding(start = 40.dp, end = 40.dp, bottom = 20.dp)
                .fillMaxWidth()
                .height(80.dp),
            contentAlignment = Alignment.TopCenter,
        ) {
            if (isDescriptionShowing) {
                Text(
                    modifier = Modifier.offset(x = 0.dp, y = shiftVal.dp),
                    text = StringProvider.getString(R.string.test_list_description),
                    fontSize = if (savedLanguage == "es") 20.sp else 38.sp,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center,
                )
            }
        }

        // 검사 목록
        Box {
            Column(
                modifier = Modifier
            ) {
                val buttonModifier = Modifier.weight(1f)

                // 혈압 검사
                InspectionSelectionButton(
                    modifier = buttonModifier,
                    title1 = StringProvider.getString(R.string.test_predescription_blood_pressure_title1),
                    title2 = StringProvider.getString(R.string.test_predescription_blood_pressure_title2),
                    onClickMethod = {
                        onSideEffect(
                            ExternalDeviceInspectionListSideEffect.OnInspectionSelected(
                                InspectionType.BloodPressure
                            )
                        )
                    },
                    alignment = Alignment.CenterStart,
                    isDone = state.isBloodPressureDone,
                    isSenior = state.isSenior,
                    time = 2,
                    large = true,
                )

                Spacer(modifier = Modifier.height(20.dp))

                // 악력 검사
                InspectionSelectionButton(
                    modifier = buttonModifier,
                    title1 = StringProvider.getString(R.string.test_predescription_grip_strength_title1),
                    title2 = StringProvider.getString(R.string.test_predescription_grip_strength_title2),
                    onClickMethod = {
                        onSideEffect(
                            ExternalDeviceInspectionListSideEffect.OnInspectionSelected(
                                InspectionType.GripStrength
                            )
                        )
                    },
                    alignment = Alignment.CenterStart,
                    isDone = state.isGripStrengthDone,
                    isSenior = state.isSenior,
                    time = 2,
                    large = true,
                )

                Spacer(modifier = Modifier.height(20.dp))

                // 하단 경고 문구
                WarningSection(
                    warningTextSize = warningTextSize,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

/**
 * 상단 네비게이션 바 컴포넌트
 */
@Composable
private fun TopNavigationBar(
    savedLanguage: String?,
    onBackClick: () -> Unit,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .padding(
                start = 40.dp,
                top = (GlobalValue.statusBarPadding + 20).dp,
                end = 40.dp,
                bottom = 20.dp,
            )
            .fillMaxWidth()
            .height(40.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxHeight()
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() },
                    onClick = onBackClick
                ),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                modifier = Modifier
                    .padding(top = 4.dp)
                    .width(28.dp),
                painter = painterResource(id = R.drawable.icon_back_black),
                contentDescription = "",
            )
            Text(
                text = StringProvider.getString(R.string.navigation_tosurvey_button),
                fontSize = if (savedLanguage == "es") 12.sp else 24.sp,
                fontWeight = FontWeight.Medium,
            )
        }

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = StringProvider.getString(R.string.test_list_tittle),
                fontSize = 24.sp,
                fontWeight = FontWeight.Medium,
            )
        }

        SettingsButton(onSettingsClick)
    }
}

/**
 * 하단 경고 문구 컴포넌트
 */
@Composable
private fun WarningSection(
    warningTextSize: TextUnit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.BottomCenter,
    ) {
        Row(
            modifier = Modifier
                .padding(
                    start = 40.dp,
                    bottom = (GlobalValue.navigationBarPadding + 40).dp,
                )
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                modifier = Modifier
                    .padding(end = 20.dp)
                    .width(44.dp),
                painter = painterResource(id = R.drawable.icon_warning),
                contentDescription = "",
            )
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 40.dp),
                text = buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(
                            color = Color(0xff999999),
                            fontSize = warningTextSize,
                        )
                    ) {
                        append(StringProvider.getString(R.string.test_list_screen_warning1))
                    }
                    withStyle(
                        style = SpanStyle(
                            color = Color(0xffff0000),
                            fontSize = warningTextSize,
                            fontWeight = FontWeight.Bold,
                        )
                    ) {
                        append(StringProvider.getString(R.string.test_list_screen_warning2))
                    }
                    withStyle(
                        style = SpanStyle(
                            color = Color(0xff999999),
                            fontSize = warningTextSize,
                        )
                    ) {
                        append(StringProvider.getString(R.string.test_list_screen_warning3))
                    }
                },
            )
        }
    }
}

/**
 * Preview - 기본 상태
 */
@RequiresApi(Build.VERSION_CODES.S)
@Preview(showBackground = true, widthDp = 800, heightDp = 1280)
@Composable
private fun PreviewExternalDeviceInspectionListScreen() {
    ExternalDeviceInspectionListScreen(
        state = ExternalDeviceInspectionListUiState(
            isBloodPressureDone = false,
            isGripStrengthDone = false,
            isSenior = false
        ),
        onSideEffect = {},
        onBackClick = {},
        onSettingsClick = {},
        onNavigateToInspection = {},
        onNavigateToSurvey = {}
    )
}

/**
 * Preview - 검사 완료 상태
 */
@RequiresApi(Build.VERSION_CODES.S)
@Preview(showBackground = true, widthDp = 800, heightDp = 1280)
@Composable
private fun PreviewExternalDeviceInspectionListScreenWithCompletedTests() {
    ExternalDeviceInspectionListScreen(
        state = ExternalDeviceInspectionListUiState(
            isBloodPressureDone = true,
            isGripStrengthDone = true,
            isSenior = false
        ),
        onSideEffect = {},
        onBackClick = {},
        onSettingsClick = {},
        onNavigateToInspection = {},
        onNavigateToSurvey = {}
    )
}

/**
 * Preview - 시니어 모드
 */
@RequiresApi(Build.VERSION_CODES.S)
@Preview(showBackground = true, widthDp = 800, heightDp = 1280)
@Composable
private fun PreviewExternalDeviceInspectionListScreenSeniorMode() {
    ExternalDeviceInspectionListScreen(
        state = ExternalDeviceInspectionListUiState(
            isBloodPressureDone = false,
            isGripStrengthDone = false,
            isSenior = true
        ),
        onSideEffect = {},
        onBackClick = {},
        onSettingsClick = {},
        onNavigateToInspection = {},
        onNavigateToSurvey = {}
    )
}
