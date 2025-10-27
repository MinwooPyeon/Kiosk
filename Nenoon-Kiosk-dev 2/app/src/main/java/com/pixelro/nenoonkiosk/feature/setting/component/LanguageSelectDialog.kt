package com.pixelro.nenoonkiosk.feature.setting.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.ui.TopBarVertical

@Composable
fun LanguageSelectDialog(
    updateLanguage: (String) -> Unit,
    onDismissRequest: () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(),
    ) {
        Column(
            modifier = Modifier
                .width(600.dp)
                .height(1000.dp)
                .background(color = Color.White, shape = RoundedCornerShape(8.dp)),
        ) {
            // ✅ Locale 변경에 따라 자동 갱신됨
            TopBarVertical(
                title = stringResource(R.string.settings_language),
                showBackButton = false,
                onBackClicked = {},
            )

            // 언어 목록 (한국어, 영어, 중국어 등)
            val languages = listOf(
                "ko" to "한국어",
                "en" to "English",
                "zh" to "汉语",
                "ja" to "日本語",
                "fr" to "Français",
                "ru" to "Русский",
                "es" to "Español"
            )

            languages.forEach { (code, name) ->
                SettingItem(
                    text = name,
                    onClick = { updateLanguage(code) }
                )
            }
        }
    }
}

@Preview(
    name = "언어 선택 다이얼로그 프리뷰",
    showBackground = true,
    widthDp = 800,
    heightDp = 1280
)
@Composable
private fun LanguageSelectDialogPreview() {
    com.pixelro.nenoonkiosk.ui.theme.NenoonKioskTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            Column(
                modifier = Modifier
                    .width(600.dp)
                    .height(1000.dp)
                    .background(Color.White, RoundedCornerShape(8.dp))
            ) {
                // ✅ Preview에서도 stringResource 자동 작동
                TopBarVertical(
                    title = stringResource(R.string.settings_language),
                    showBackButton = false,
                    onBackClicked = {},
                )

                val languages = listOf(
                    "ko" to "한국어",
                    "en" to "English",
                    "zh" to "汉语",
                    "ja" to "日本語",
                    "fr" to "Français",
                    "ru" to "Русский",
                    "es" to "Español"
                )

                languages.forEach { (code, name) ->
                    SettingItem(
                        text = name,
                        onClick = {}
                    )
                }
            }
        }
    }
}
