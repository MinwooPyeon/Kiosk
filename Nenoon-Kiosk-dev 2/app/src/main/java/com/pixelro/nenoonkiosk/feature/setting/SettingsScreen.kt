
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.os.LocaleListCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.manager.SharedPreferencesManager
import com.pixelro.nenoonkiosk.core.ui.NenoonTopBar
import com.pixelro.nenoonkiosk.core.ui.TopBarVertical
import com.pixelro.nenoonkiosk.feature.auth.login.LoginViewModel
import com.pixelro.nenoonkiosk.feature.main.NenoonViewModel
import com.pixelro.nenoonkiosk.feature.setting.component.SettingItem
import com.pixelro.nenoonkiosk.feature.setting.component.SettingSelectionDialog
import com.pixelro.nenoonkiosk.ui.theme.NenoonKioskTheme

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun SettingsScreen(
    isSignedIn: Boolean,
    toSignInScreen: () -> Unit,
    toSoftwareInfoScreen: () -> Unit,
    onBack: () -> Unit,
    viewModel: NenoonViewModel = hiltViewModel(),
    loginViewModel: LoginViewModel = hiltViewModel(),
) {
    // 2. Activity Context 가져오기 (AppCompatActivity로 캐스팅) [1, 2]
    val context = LocalContext.current
    val activity = context as? AppCompatActivity

    // 3. 언어 변경 API 호출 함수 정의 (UI 레이어의 핵심 로직)
    val applyNewLanguage: (String) -> Unit = { langCode ->

        // 4. 언어 코드를 ViewModel에게 전달하여 데이터/서비스 로직을 처리하게 함
        // 예: SharedPreferences에 저장, TTS 설정 등
        viewModel.updateLanguage(langCode)

        // 5. 프레임워크 API 호출 (Activity가 AppCompatActivity를 상속했는지 확인)
        if (activity != null) {
            // 언어 코드를 LocaleListCompat 객체로 변환 [3, 4]
            // 예: "en" -> LocaleListCompat.forLanguageTags("en")
            val appLocale: LocaleListCompat = LocaleListCompat.forLanguageTags(langCode)

            // API 호출: 시스템 설정을 업데이트하고 Activity를 재시작함 [3-6]
            // 이 호출은 메인 스레드에서 해야 합니다. [3, 4]
            AppCompatDelegate.setApplicationLocales(appLocale)

            // 참고: setApplicationLocales() 호출이 Activity를 재시작하므로,
            // 별도로 dismissRequest를 호출할 필요는 없지만,
            // setSettingsDialogState를 여기서 호출하여 상태를 정리하는 것도 가능합니다.
            // viewModel.setSettingsDialogState(NenoonViewModel.SettingsDialogState.None)
        } else {
            // 경고 또는 오류 처리 (MainActivity가 AppCompatActivity를 확장하지 않은 경우)
            // throw IllegalStateException("Activity must extend AppCompatActivity for setApplicationLocales() to work with Compose.")
        }
    }


    val settingsDialogState by viewModel.settingsDialogState.collectAsState()
    val isUserSignedIn by loginViewModel.isUserSignedIn.collectAsState(initial = false)
    val isLocationSignedIn by loginViewModel.isLocationSignedIn.collectAsState(initial = false)

    when (settingsDialogState) {
        NenoonViewModel.SettingsDialogState.Language -> {
            SettingSelectionDialog(
                titleResId = R.string.settings_language,
                items = listOf(
                    "ko" to "한국어",
                    "en" to "English",
                    "zh" to "汉语",
                    "ja" to "日本語",
                    "fr" to "Français",
                    "ru" to "Русский",
                    "es" to "Español"
                ),
                onItemSelected = { langCode ->
                    applyNewLanguage
                },
                onDismissRequest = {
                    viewModel.setSettingsDialogState(NenoonViewModel.SettingsDialogState.None)
                }
            )
        }

        NenoonViewModel.SettingsDialogState.BloodPressureMonitorType -> {
            SettingSelectionDialog(
                titleResId = R.string.blood_pressure_monitor_image_content_description,
                items = listOf(
                    "BPBIO320" to "BPBIO320",
                    "BP170B" to "BP170B"
                ),
                onItemSelected = { type ->
                    SharedPreferencesManager.putBloodPressureMonitorType(
                        SharedPreferencesManager.BloodPressureMonitorType.valueOf(type)
                    )
                },
                onDismissRequest = {
                    viewModel.setSettingsDialogState(NenoonViewModel.SettingsDialogState.None)
                }
            )
        }

        else -> {}
    }


    SettingsScreenContent(
        isSignedIn = isSignedIn,
        isLocationSignedIn = isLocationSignedIn,
        isUserSignedIn = isUserSignedIn,
        onLanguageClick = {
            viewModel.setSettingsDialogState(NenoonViewModel.SettingsDialogState.Language)
        },
        onBloodPressureClick = {
            viewModel.setSettingsDialogState(NenoonViewModel.SettingsDialogState.BloodPressureMonitorType)
        },
        onLoginClick = {
            if (isSignedIn) {
                if (isUserSignedIn) loginViewModel.userSignOut()
                else loginViewModel.locationSignOut()
            }
            toSignInScreen()
        },
        onBack = onBack,
    )
}

@Composable
private fun SettingsScreenContent(
    isSignedIn: Boolean,
    isLocationSignedIn: Boolean,
    isUserSignedIn: Boolean,
    onLanguageClick: () -> Unit,
    onBloodPressureClick: () -> Unit,
    onLoginClick: () -> Unit,
    onBack: () -> Unit,
) {
    Column(Modifier.fillMaxSize()) {

        // 상단바
        NenoonTopBar(
            title = stringResource(R.string.settings_title),
            showBackButton = true,
            onBackClicked = onBack,
        )

        // 언어 설정
        SettingItem(
            text = stringResource(R.string.settings_language),
            onClick = onLanguageClick
        )

        // 로그인 / 로그아웃
        if (isLocationSignedIn) {
            SettingItem(
                text = if (isSignedIn)
                    stringResource(R.string.settings_signout)
                else
                    stringResource(R.string.signin),
                textColor = if (isSignedIn) Color.Red else Color.Black,
                onClick = onLoginClick
            )
        }

        // 혈압계 선택
        SettingItem(
            text = stringResource(R.string.blood_pressure_monitor_image_content_description),
            onClick = onBloodPressureClick
        )
    }
}
@Preview(
    name = "설정 화면 세로형 프리뷰",
    showBackground = true,
    widthDp = 800,
    heightDp = 1280,
    apiLevel = 34
)
@Composable
private fun SettingsScreenPortraitPreview() {
    NenoonKioskTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = androidx.compose.material3.MaterialTheme.colorScheme.background
        ) {
            SettingsScreenContent(
                isSignedIn = true,
                isLocationSignedIn = true,
                isUserSignedIn = true,
                onLanguageClick = {},
                onBloodPressureClick = {},
                onLoginClick = {},
                onBack = {}
            )
        }
    }
}

@Preview(
    name = "설정 화면 가로형 프리뷰",
    showBackground = true,
    widthDp = 1280,
    heightDp = 800,
    apiLevel = 34
)
@Composable
private fun SettingsScreenLandscapePreview() {
    NenoonKioskTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = androidx.compose.material3.MaterialTheme.colorScheme.background
        ) {
            SettingsScreenContent(
                isSignedIn = true,
                isLocationSignedIn = true,
                isUserSignedIn = true,
                onLanguageClick = {},
                onBloodPressureClick = {},
                onLoginClick = {},
                onBack = {}
            )
        }
    }
}
