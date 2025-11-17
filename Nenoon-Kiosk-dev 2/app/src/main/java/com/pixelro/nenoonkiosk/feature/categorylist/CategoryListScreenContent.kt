package com.pixelro.nenoonkiosk.feature.categorylist

import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.harang.data.db.entity.AdImageEntity
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.ui.AdCarousel
import com.pixelro.nenoonkiosk.core.ui.Logo
import com.pixelro.nenoonkiosk.core.util.isLandscape
import com.pixelro.nenoonkiosk.feature.categorylist.components.InspectionCategoryButton
import com.pixelro.nenoonkiosk.feature.inspection.AdImageRepositoryEntryPoint
import com.pixelro.nenoonkiosk.feature.inspection.InspectionType
import com.pixelro.nenoonkiosk.ui.theme.neNoon_blue
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

@Composable
fun CategoryListScreenContent(
    isSignInSkipped: () -> Boolean,
    toEyeTestScreen: () -> Unit,
    toDementiaTestScreen: (InspectionType) -> Unit,
    toExternalDeviceTestListScreen: () -> Unit,
    toStrabismusTestListScreen: () -> Unit,
    toPrintScreen: () -> Unit,
    toAccountManagementScreen: () -> Unit,
) {
    val config = LocalConfiguration.current
    val screenHeight = config.screenHeightDp.dp
    val screenWidth = config.screenWidthDp.dp
    val isLandscape = isLandscape()

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val isPreview = LocalInspectionMode.current

    // 언어 가져오기
    val savedLanguage = remember {
        val appLocale = AppCompatDelegate.getApplicationLocales()
        if (appLocale.isEmpty) {
            "ko"
        } else {
            appLocale[0]?.toLanguageTag() ?: "ko"
        }
    }

    // 광고 리스트 가져오기 (Preview 모드 체크)
    val adImages by if (isPreview) {
        // Preview 모드에서는 더미 데이터 사용
        remember {
            flowOf(
                listOf(
                    AdImageEntity(
                        id = 1,
                        locationId = 2,
                        url = "file:///android_asset/ad_lens.png",
                        order = 1,
                        language = "ko"
                    )
                )
            )
        }.collectAsState(initial = emptyList())
    } else {
        // 실제 앱에서는 Repository 사용
        val adImageRepository = remember {
            EntryPointAccessors.fromApplication(
                context.applicationContext,
                AdImageRepositoryEntryPoint::class.java
            ).adImageRepository()
        }
        adImageRepository.getAdImagesByLocationAndLanguage(2, savedLanguage)
            .collectAsState(initial = emptyList())
    }

    // 동적 크기 계산
    val topPadding = (screenHeight * 0.05f).coerceIn(40.dp, 80.dp)
    val horizontalPadding = (screenWidth * 0.05f).coerceIn(40.dp, 60.dp)
    val iconSize = (screenWidth * 0.078f).coerceIn(80.dp, 120.dp)
    val buttonHeight = if (isLandscape) {
        (screenHeight * 0.135f).coerceIn(110.dp, 140.dp)
    } else {
        (screenHeight * 0.09f).coerceIn(110.dp, 130.dp)
    }
    val buttonSpacing = (screenHeight * 0.02f).coerceIn(15.dp, 25.dp)
    val logoSize = if (isLandscape) 380.dp else 304.dp

    val testButtons = listOf(
        Triple(R.string.eye_test, R.drawable.eye_test_icon) { toEyeTestScreen() },
        Triple(
            R.string.blood_pressure_and_grip_strength,
            R.drawable.blood_pressure_and_grip_strength_icon
        ) { toExternalDeviceTestListScreen() },
        // Triple(R.string.urine_test, R.drawable.urine_test_icon) { /* toUrineTestScreen() */ },
        Triple(R.string.cross_eye_test, R.drawable.cross_eye_icon) { toStrabismusTestListScreen() },
        Triple(R.string.dementia_test, R.drawable.dementia_icon) {
            toDementiaTestScreen(
                InspectionType.Dementia
            )
        }
    )

    Box(modifier = Modifier.fillMaxSize()) {
        if (isLandscape) {
            // 가로모드: 좌우 분할
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .background(neNoon_blue)
            ) {
                // 왼쪽: 로고 + 계정관리/프린트 버튼 + 광고
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .padding(vertical = topPadding),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    // 로고 (크기 증가)
                    Logo(
                        white = true,
                        modifier = Modifier.size(logoSize)
                    )

                    Spacer(modifier = Modifier.height(buttonSpacing))

                    // 계정 관리 + 프린트 버튼 (가로 배치)
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(buttonSpacing)
                    ) {
                        // 계정 관리 버튼
                        Card(
                            modifier = Modifier
                                .size(iconSize)
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { toAccountManagementScreen() },
                            backgroundColor = Color.White
                        ) {
                            Icon(
                                modifier = Modifier.padding(iconSize * 0.2f),
                                painter = painterResource(id = R.drawable.account_icon),
                                contentDescription = ""
                            )
                        }

                        // 프린트 버튼
                        if (!isSignInSkipped()) {
                            Card(
                                modifier = Modifier
                                    .size(iconSize)
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { toPrintScreen() },
                                backgroundColor = Color.White
                            ) {
                                Icon(
                                    modifier = Modifier.padding(iconSize * 0.2f),
                                    painter = painterResource(id = R.drawable.icon_print),
                                    contentDescription = ""
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(buttonSpacing))

                    // 광고 배너
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .height(150.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        AdCarousel(
                            adImages = adImages,
                            modifier = Modifier.fillMaxSize(),
                            onPageChange = {
                                coroutineScope.launch {
                                    delay(100)
                                }
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(topPadding * 0.5f))
                }

                // 오른쪽: 카테고리 버튼들만
                Column(
                    modifier = Modifier
                        .weight(1.6f)
                        .fillMaxHeight()
                        .padding(horizontal = horizontalPadding, vertical = topPadding),
                    verticalArrangement = Arrangement.Center
                ) {
                    // 카테고리 버튼들
                    testButtons.forEachIndexed { index, (titleId, icon, onClick) ->
                        InspectionCategoryButton(
                            iconRes = icon,
                            title = stringResource(id = titleId),
                            onClick = onClick,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(buttonHeight)
                        )
                        if (index != testButtons.lastIndex) {
                            Spacer(modifier = Modifier.height(buttonSpacing))
                        }
                    }
                }
            }
        } else {
            // 세로모드: 기존 레이아웃
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(neNoon_blue),
            ) {
                Spacer(modifier = Modifier.weight(1f))

                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Logo(true)
                }

                Spacer(modifier = Modifier.weight(1f))

                testButtons.forEachIndexed { index, (titleId, icon, onClick) ->
                    InspectionCategoryButton(
                        iconRes = icon,
                        title = stringResource(id = titleId),
                        onClick = onClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(buttonHeight)
                            .padding(horizontal = horizontalPadding)
                    )
                    Spacer(
                        modifier = Modifier.height(
                            if (index == testButtons.lastIndex) topPadding else buttonSpacing
                        )
                    )
                }
            }
        }

        // 상단 버튼들 (세로모드에서만 overlay)
        if (!isLandscape) {
            Row(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(horizontalPadding, topPadding),
                horizontalArrangement = Arrangement.End
            ) {
                if (!isSignInSkipped()) {
                    Card(
                        modifier = Modifier
                            .size(iconSize)
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { toPrintScreen() },
                        backgroundColor = Color.White
                    ) {
                        Icon(
                            modifier = Modifier.padding(iconSize * 0.2f),
                            painter = painterResource(id = R.drawable.icon_print),
                            contentDescription = ""
                        )
                    }
                    Spacer(modifier = Modifier.width(buttonSpacing))
                }

                Card(
                    modifier = Modifier
                        .size(iconSize)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { toAccountManagementScreen() },
                    backgroundColor = Color.White
                ) {
                    Icon(
                        modifier = Modifier.padding(iconSize * 0.2f),
                        painter = painterResource(id = R.drawable.account_icon),
                        contentDescription = ""
                    )
                }
            }
        }
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF1D71E1,
    widthDp = 800,
    heightDp = 1280,
    name = "Portrait - Standard Tablet"
)
@Composable
fun CategoryListScreenContentVerticalPreview() {
    CategoryListScreenContent(
        isSignInSkipped = { false },
        toEyeTestScreen = {},
        toDementiaTestScreen = {},
        toExternalDeviceTestListScreen = {},
        toStrabismusTestListScreen = {},
        toPrintScreen = {},
        toAccountManagementScreen = {}
    )
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF1D71E1,
    widthDp = 1280,
    heightDp = 800,
    name = "Landscape - Standard Tablet"
)
@Composable
fun CategoryListScreenContentHorizontalPreview() {
    CategoryListScreenContent(
        isSignInSkipped = { false },
        toEyeTestScreen = {},
        toDementiaTestScreen = {},
        toExternalDeviceTestListScreen = {},
        toStrabismusTestListScreen = {},
        toPrintScreen = {},
        toAccountManagementScreen = {}
    )
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF1D71E1,
    widthDp = 1920,
    heightDp = 1080,
    name = "Landscape - 32 inch Full HD"
)
@Composable
fun CategoryListScreenContent32InchPreview() {
    CategoryListScreenContent(
        isSignInSkipped = { false },
        toEyeTestScreen = {},
        toDementiaTestScreen = {},
        toExternalDeviceTestListScreen = {},
        toStrabismusTestListScreen = {},
        toPrintScreen = {},
        toAccountManagementScreen = {}
    )
}
