import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.manager.SharedPreferencesManager
import com.pixelro.nenoonkiosk.core.ui.NenoonTopBar
import com.pixelro.nenoonkiosk.feature.setting.SettingsSideEffect
import com.pixelro.nenoonkiosk.feature.setting.SettingsUiState
import com.pixelro.nenoonkiosk.feature.setting.SettingsViewModel
import com.pixelro.nenoonkiosk.feature.setting.component.SettingItem
import com.pixelro.nenoonkiosk.feature.setting.component.SettingSelectionDialog
import com.pixelro.nenoonkiosk.ui.theme.NenoonKioskTheme
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect


@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun SettingsRoute(
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val state by viewModel.collectAsState()
    val context = LocalContext.current

    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is SettingsSideEffect.ShowToast -> {
                Toast.makeText(context, sideEffect.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    when (state.showDialog) {
        is SettingsUiState.DialogType.Language -> {
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
                onItemSelected = viewModel::onLanguageSelected,
                onDismissRequest = viewModel::onDialogDismiss
            )
        }

        is SettingsUiState.DialogType.BloodPressureMonitor -> {
            SettingSelectionDialog(
                titleResId = R.string.blood_pressure_monitor_image_content_description,
                items = listOf(
                    "BPBIO320" to "BPBIO320",
                    "BP170B" to "BP170B"
                ),
                onItemSelected = viewModel::onBloodPressureMonitorSelected,
                onDismissRequest = viewModel::onDialogDismiss
            )
        }

        is SettingsUiState.DialogType.None -> {}
    }

    SettingsScreen(
        state = state,
        onBackClick = viewModel::onBackClick,
        onLanguageClick = viewModel::onLanguageClick,
        onBloodPressureMonitorClick = viewModel::onBloodPressureMonitorClick,
        onLoginClick = viewModel::onLoginClick
    )
}

@Composable
fun SettingsScreen(
    state: SettingsUiState,
    onBackClick: () -> Unit,
    onLanguageClick: () -> Unit,
    onBloodPressureMonitorClick: () -> Unit,
    onLoginClick: () -> Unit
) {
    val languageDisplayName = when (state.currentLanguage) {
        "ko" -> "한국어"
        "en" -> "English"
        "zh" -> "汉语"
        "ja" -> "日本語"
        "fr" -> "Français"
        "ru" -> "Русский"
        "es" -> "Español"
        else -> "한국어"
    }

    val monitorDisplayName = when (state.currentBloodPressureMonitorType) {
        SharedPreferencesManager.BloodPressureMonitorType.BPBIO320 -> "BPBIO320"
        SharedPreferencesManager.BloodPressureMonitorType.BP170B -> "BP170B"
    }

    Column(Modifier.fillMaxSize()) {
        NenoonTopBar(
            title = stringResource(R.string.settings_title),
            showBackButton = true,
            onBackClicked = onBackClick
        )

        SettingItem(
            text = "${stringResource(R.string.settings_language)}: $languageDisplayName",
            onClick = onLanguageClick
        )

        if (state.isLocationSignedIn) {
            SettingItem(
                text = if (state.isUserSignedIn)
                    stringResource(R.string.settings_signout)
                else
                    stringResource(R.string.signin),
                textColor = if (state.isUserSignedIn) Color.Red else Color.Black,
                onClick = onLoginClick
            )
        }

        SettingItem(
            text = "${stringResource(R.string.blood_pressure_monitor_image_content_description)}: $monitorDisplayName",
            onClick = onBloodPressureMonitorClick
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
            color = MaterialTheme.colorScheme.background
        ) {
            SettingsScreen(
                state = SettingsUiState(
                    isLocationSignedIn = true,
                    isUserSignedIn = true,
                    currentLanguage = "ko",
                    currentBloodPressureMonitorType = SharedPreferencesManager.BloodPressureMonitorType.BPBIO320,
                    showDialog = SettingsUiState.DialogType.None
                ),
                onBackClick = {},
                onLanguageClick = {},
                onBloodPressureMonitorClick = {},
                onLoginClick = {}
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
            color = MaterialTheme.colorScheme.background
        ) {
            SettingsScreen(
                state = SettingsUiState(
                    isLocationSignedIn = true,
                    isUserSignedIn = false,
                    currentLanguage = "en",
                    currentBloodPressureMonitorType = SharedPreferencesManager.BloodPressureMonitorType.BP170B,
                    showDialog = SettingsUiState.DialogType.None
                ),
                onBackClick = {},
                onLanguageClick = {},
                onBloodPressureMonitorClick = {},
                onLoginClick = {}
            )
        }
    }
}