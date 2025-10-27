
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.ui.TopBarVertical
import com.pixelro.nenoonkiosk.feature.auth.login.LoginViewModel
import com.pixelro.nenoonkiosk.feature.main.NenoonViewModel
import com.pixelro.nenoonkiosk.feature.setting.component.BloodPressureMonitorSelector
import com.pixelro.nenoonkiosk.feature.setting.component.LanguageSelectDialog
import com.pixelro.nenoonkiosk.feature.setting.component.SettingItem

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
    val settingsDialogState by viewModel.settingsDialogState.collectAsState()
    val isUserSignedIn by loginViewModel.isUserSignedIn.collectAsState(initial = false)
    val isLocationSignedIn by loginViewModel.isLocationSignedIn.collectAsState(initial = false)

    when (settingsDialogState) {
        NenoonViewModel.SettingsDialogState.Language -> {
            LanguageSelectDialog(
                updateLanguage = { viewModel.updateLanguage(it) },
                onDismissRequest = { viewModel.setSettingsDialogState(NenoonViewModel.SettingsDialogState.None) }
            )
        }

        NenoonViewModel.SettingsDialogState.BloodPressureMonitorType -> {
            BloodPressureMonitorSelector {
                viewModel.setSettingsDialogState(NenoonViewModel.SettingsDialogState.None)
            }
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
        TopBarVertical(
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
    name = "설정 화면 프리뷰",
    showBackground = true,
    widthDp = 800,
    heightDp = 1280,
    apiLevel = 34
)
@Composable
private fun SettingsScreenPreview() {
    com.pixelro.nenoonkiosk.ui.theme.NenoonKioskTheme {
        androidx.compose.material3.Surface(
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
