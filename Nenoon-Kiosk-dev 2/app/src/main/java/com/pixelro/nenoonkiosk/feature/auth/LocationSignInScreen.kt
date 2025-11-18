package com.pixelro.nenoonkiosk.feature.auth

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import com.harang.data.db.entity.AdImageEntity
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.constants.NavConstants
import com.pixelro.nenoonkiosk.core.ui.AdCarousel
import com.pixelro.nenoonkiosk.core.ui.Logo
import com.pixelro.nenoonkiosk.core.ui.PrimaryButton
import com.pixelro.nenoonkiosk.core.ui.StyledText
import com.pixelro.nenoonkiosk.core.ui.TextStyle
import com.pixelro.nenoonkiosk.core.util.isLandscape
import com.pixelro.nenoonkiosk.feature.auth.login.LoginViewModel
import com.pixelro.nenoonkiosk.feature.inspection.AdImageRepositoryEntryPoint
import com.pixelro.nenoonkiosk.ui.theme.Gray
import com.pixelro.nenoonkiosk.ui.theme.NEURAL200
import com.pixelro.nenoonkiosk.ui.theme.NenoonKioskTheme
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.ui.text.TextStyle as ComposeTextStyle

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun LocationSignInScreen(
    updateIsSignedIn: (Boolean) -> Unit,
    loginViewModel: LoginViewModel,
    signInNavController: NavController,
    navController: NavController,
) {
    val context = LocalContext.current
    val isLandscape = isLandscape()

    // 언어 가져오기
    val savedLanguage = remember {
        val appLocale = AppCompatDelegate.getApplicationLocales()
        if (appLocale.isEmpty) {
            "ko"
        } else {
            appLocale[0]?.toLanguageTag() ?: "ko"
        }
    }

    // 광고 리스트 가져오기
    val adImageRepository = remember {
        EntryPointAccessors.fromApplication(
            context.applicationContext,
            AdImageRepositoryEntryPoint::class.java
        ).adImageRepository()
    }
    val adImages by adImageRepository.getAdImagesByLocationAndLanguage(1, savedLanguage)
        .collectAsState(initial = emptyList())

    val coroutineScope = rememberCoroutineScope()

    if (isLandscape) {
        LandscapeLocationSignInScreen(
            adImages = adImages,
            onAdPageChange = {
                coroutineScope.launch {
                    delay(100)
                }
            },
            onSignInSkip = {
                loginViewModel.locationSignInSkip(updateIsSignedIn)
                signInNavController.navigate(SignInScreenState.UserSignIn.name)
            },
            onSignIn = { id, password ->
                loginViewModel.locationSignIn(id, password, updateIsSignedIn)
            },
            onValidate = { id, password ->
                loginViewModel.validateLocationSignIn(id, password)
            },
            onNavigateToSettings = {
                navController.navigate(NavConstants.ROUTE_SETTINGS)
            },
            onSignInSuccess = {
                signInNavController.navigate(SignInScreenState.UserSignIn.name)
            }
        )
    } else {
        PortraitLocationSignInScreen(
            adImages = adImages,
            onAdPageChange = {
                coroutineScope.launch {
                    delay(100)
                }
            },
            onSignInSkip = {
                loginViewModel.locationSignInSkip(updateIsSignedIn)
                signInNavController.navigate(SignInScreenState.UserSignIn.name)
            },
            onSignIn = { id, password ->
                loginViewModel.locationSignIn(id, password, updateIsSignedIn)
            },
            onValidate = { id, password ->
                loginViewModel.validateLocationSignIn(id, password)
            },
            onNavigateToSettings = {
                navController.navigate(NavConstants.ROUTE_SETTINGS)
            },
            onSignInSuccess = {
                signInNavController.navigate(SignInScreenState.UserSignIn.name)
            }
        )
    }
}

@Composable
private fun PortraitLocationSignInScreen(
    adImages: List<AdImageEntity> = emptyList(),
    onAdPageChange: () -> Unit = {},
    onSignInSkip: () -> Unit = {},
    onSignIn: suspend (String, String) -> Boolean = { _, _ -> true },
    onValidate: (String, String) -> Boolean = { _, _ -> true },
    onNavigateToSettings: () -> Unit = {},
    onSignInSuccess: () -> Unit = {},
) {
    var id by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var loginError by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // 상단 콘텐츠 (스크롤 가능)
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(40.dp)
                    .verticalScroll(rememberScrollState())
                    .imePadding(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top,
            ) {
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Image(
                        modifier = Modifier
                            .size(40.dp)
                            .clickable(
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() },
                            ) {
                                onNavigateToSettings()
                            },
                        painter = painterResource(id = R.drawable.icon_settings),
                        contentDescription = "",
                    )
                }

                Spacer(modifier = Modifier.height(40.dp))

                Logo()

                Spacer(modifier = Modifier.height(30.dp))

                Text(
                    text = stringResource(id = R.string.location_signin),
                    color = NEURAL200,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 32.sp
                )

                Spacer(modifier = Modifier.height(30.dp))

                InputFields(
                    id = id,
                    onIdChange = { id = it },
                    password = password,
                    onPasswordChange = { password = it }
                )

                Spacer(modifier = Modifier.height(30.dp))

                PrimaryButton(
                    text = stringResource(id = R.string.signin),
                    onClick = {
                        if (!onValidate(id, password)) {
                            return@PrimaryButton
                        }
                        coroutineScope.launch(Dispatchers.Main) {
                            onSignIn(id, password).also { success ->
                                if (success) {
                                    onSignInSuccess()
                                } else {
                                    loginError = true
                                }
                            }
                        }
                    },
                )

                Spacer(modifier = Modifier.height(20.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .width(IntrinsicSize.Max)
                            .clickable(
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() }
                            ) {
                                onSignInSkip()
                            }
                    ) {
                        StyledText(
                            text = stringResource(id = R.string.start_without_signin),
                            style = TextStyle.Message,
                            fontWeight = FontWeight.Bold,
                            color = Gray
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Box(
                            modifier = Modifier
                                .width(200.dp)
                                .height(1.dp)
                                .background(Gray)
                        )
                    }
                }

            }

            // 하단 광고 배너 (고정)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(horizontal = 40.dp, vertical = 5.dp),
                contentAlignment = Alignment.Center
            ) {
                AdCarousel(
                    adImages = adImages,
                    modifier = Modifier.fillMaxSize(),
                    onPageChange = onAdPageChange
                )
            }
        }
    }
}

@Composable
private fun LandscapeLocationSignInScreen(
    adImages: List<AdImageEntity> = emptyList(),
    onAdPageChange: () -> Unit = {},
    onSignInSkip: () -> Unit = {},
    onSignIn: suspend (String, String) -> Boolean = { _, _ -> true },
    onValidate: (String, String) -> Boolean = { _, _ -> true },
    onNavigateToSettings: () -> Unit = {},
    onSignInSuccess: () -> Unit = {},
) {
    var id by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var loginError by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize()) {
        // 설정 버튼
        Image(
            modifier = Modifier
                .padding(30.dp)
                .size(40.dp)
                .align(Alignment.TopEnd)
                .zIndex(10f)
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() },
                ) {
                    onNavigateToSettings()
                },
            painter = painterResource(id = R.drawable.icon_settings),
            contentDescription = "",
        )

        // 전체 스크롤 가능 (광고 포함)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .imePadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(80.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.Bottom
            ) {
                // 왼쪽: 로고 + 기관로그인
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Logo(
                        modifier = Modifier.size(304.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = stringResource(id = R.string.location_signin),
                        color = NEURAL200,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 29.sp
                    )
                }

                // 오른쪽: 입력 필드 + 로그인 버튼
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.width(500.dp)
                ) {
                    InputFields(
                        id = id,
                        onIdChange = { id = it },
                        password = password,
                        onPasswordChange = { password = it },
                        modifier = Modifier.fillMaxWidth(),
                        fontSize = 29.sp
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    PrimaryButton(
                        text = stringResource(id = R.string.signin),
                        onClick = {
                            if (!onValidate(id, password)) {
                                return@PrimaryButton
                            }
                            coroutineScope.launch(Dispatchers.Main) {
                                onSignIn(id, password).also { success ->
                                    if (success) {
                                        onSignInSuccess()
                                    } else {
                                        loginError = true
                                    }
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 20.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .width(IntrinsicSize.Max)
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) {
                            onSignInSkip()
                        }
                ) {
                    StyledText(
                        text = stringResource(id = R.string.start_without_signin),
                        style = TextStyle.Message,
                        fontWeight = FontWeight.Bold,
                        color = Gray
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Box(
                        modifier = Modifier
                            .width(200.dp)
                            .height(1.dp)
                            .background(Gray)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 광고 배너 - 스크롤 영역 안에 포함
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(210.dp)
                    .padding(horizontal = 40.dp, vertical = 0.dp),
                contentAlignment = Alignment.Center
            ) {
                AdCarousel(
                    adImages = adImages,
                    modifier = Modifier.fillMaxSize(),
                    onPageChange = onAdPageChange
                )
            }
        }
    }
}

@Composable
private fun InputFields(
    id: String,
    onIdChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    fontSize: androidx.compose.ui.unit.TextUnit = 36.sp
) {
    Column(modifier = modifier) {
        BasicTextField(
            value = id,
            onValueChange = onIdChange,
            textStyle = ComposeTextStyle(
                fontSize = fontSize,
            ),
            decorationBox = { innerTextField ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                        .border(
                            border = BorderStroke(
                                width = 1.dp,
                                color = Color(0xffc3c3c3),
                            ),
                            shape = RoundedCornerShape(8.dp),
                        )
                        .padding(start = 16.dp),
                    contentAlignment = Alignment.CenterStart,
                ) {
                    if (id.isEmpty()) {
                        Text(
                            text = stringResource(id = R.string.id_input),
                            fontSize = fontSize,
                            color = Color.LightGray,
                        )
                    }
                    innerTextField()
                }
            },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next,
            ),
        )

        Spacer(modifier = Modifier.height(16.dp))

        BasicTextField(
            value = password,
            onValueChange = onPasswordChange,
            visualTransformation = PasswordVisualTransformation(),
            textStyle = ComposeTextStyle(
                fontSize = fontSize,
            ),
            decorationBox = { innerTextField ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                        .border(
                            border = BorderStroke(
                                width = 1.dp,
                                color = Color(0xffc3c3c3),
                            ),
                            shape = RoundedCornerShape(8.dp),
                        )
                        .padding(start = 16.dp),
                    contentAlignment = Alignment.CenterStart,
                ) {
                    if (password.isEmpty()) {
                        Text(
                            text = stringResource(id = R.string.pw_input),
                            fontSize = fontSize,
                            color = Color.LightGray,
                        )
                    }
                    innerTextField()
                }
            },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done,
            ),
        )
    }
}

@Preview(
    showBackground = true,
    widthDp = 1920,
    heightDp = 1080,
    backgroundColor = 0xFFFFFFFF,
    name = "Landscape - 32 inch Full HD"
)
@Composable
private fun LocationSignInScreen_Preview_Landscape_FullHD() {
    val dummyAdImages = listOf(
        AdImageEntity(
            id = 1,
            locationId = 2,
            url = "file:///android_asset/ad_lens.png",
            order = 1,
            language = "ko"
        ),
        AdImageEntity(
            id = 2,
            locationId = 2,
            url = "file:///android_asset/ad_hades.png",
            order = 2,
            language = "ko"
        )
    )

    NenoonKioskTheme {
        LandscapeLocationSignInScreen(adImages = dummyAdImages)
    }
}

@Preview(
    showBackground = true,
    widthDp = 1280,
    heightDp = 800,
    backgroundColor = 0xFFFFFFFF,
    name = "Landscape - Standard Tablet"
)
@Composable
private fun LocationSignInScreen_Preview_Landscape_Tablet() {
    val dummyAdImages = listOf(
        AdImageEntity(
            id = 1,
            locationId = 2,
            url = "file:///android_asset/ad_lens.png",
            order = 1,
            language = "ko"
        )
    )

    NenoonKioskTheme {
        LandscapeLocationSignInScreen(adImages = dummyAdImages)
    }
}

@Preview(
    showBackground = true,
    widthDp = 800,
    heightDp = 1280,
    backgroundColor = 0xFFFFFFFF,
    name = "Portrait - Standard Tablet"
)
@Composable
private fun LocationSignInScreen_Preview_Portrait() {
    val dummyAdImages = listOf(
        AdImageEntity(
            id = 1,
            locationId = 2,
            url = "file:///android_asset/ad_lens.png",
            order = 1,
            language = "ko"
        )
    )

    NenoonKioskTheme {
        PortraitLocationSignInScreen(adImages = dummyAdImages)
    }
}
