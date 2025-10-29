package com.pixelro.nenoonkiosk.feature.setting.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.ui.NenoonTopBar
import com.pixelro.nenoonkiosk.ui.theme.NenoonKioskTheme

@Composable
fun SettingSelectionDialog(
    titleResId: Int,
    items: List<Pair<String, String>>,
    onItemSelected: (String) -> Unit,
    onDismissRequest: () -> Unit
) {
    Dialog(onDismissRequest = onDismissRequest) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 50.dp), // 다이얼로그 외부 여백
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .width(600.dp)
                    .height(1000.dp)
                    .background(Color.White, RoundedCornerShape(8.dp))
            ) {
                NenoonTopBar(
                    title = stringResource(id = titleResId),
                    showBackButton = false,
                    onBackClicked = {}
                )

                // 스크롤 가능하도록 LazyColumn 사용
                LazyColumn(
                    modifier = Modifier.weight(1f)
                ) {
                    items(items) { (value, text) ->
                        SettingItem(
                            text = text,
                            onClick = {
                                onItemSelected(value)
                                onDismissRequest()
                            }
                        )
                    }
                }
            }
        }
    }
}


/* ---------------- 프리뷰 UI ---------------- */

@Preview(
    name = "언어 선택 다이얼로그 (세로형)",
    showBackground = true,
    widthDp = 800,
    heightDp = 1280
)
@Composable
private fun LanguageDialogPortraitPreview() {
    DialogPreviewContent(
        title = stringResource(id = R.string.settings_language),
        items = listOf(
            "ko" to "한국어",
            "en" to "English",
            "zh" to "汉语",
            "ja" to "日本語",
            "fr" to "Français",
            "ru" to "Русский",
            "es" to "Español"
        )
    )
}

@Preview(
    name = "언어 선택 다이얼로그 (가로형)",
    showBackground = true,
    widthDp = 1280,
    heightDp = 800
)
@Composable
private fun LanguageDialogLandscapePreview() {
    DialogPreviewContent(
        title = stringResource(id = R.string.settings_language),
        items = listOf(
            "ko" to "한국어",
            "en" to "English",
            "zh" to "汉语",
            "ja" to "日本語",
            "fr" to "Français",
            "ru" to "Русский",
            "es" to "Español"
        )
    )
}

@Preview(
    name = "혈압계 선택 다이얼로그 (세로형)",
    showBackground = true,
    widthDp = 800,
    heightDp = 1280
)
@Composable
private fun BloodPressureDialogPortraitPreview() {
    DialogPreviewContent(
        title = stringResource(id = R.string.blood_pressure_monitor_image_content_description),
        items = listOf(
            "BPBIO320" to "BPBIO320",
            "BP170B" to "BP170B"
        )
    )
}

@Preview(
    name = "혈압계 선택 다이얼로그 (가로형)",
    showBackground = true,
    widthDp = 1280,
    heightDp = 800
)
@Composable
private fun BloodPressureDialogLandscapePreview() {
    DialogPreviewContent(
        title = stringResource(id = R.string.blood_pressure_monitor_image_content_description),
        items = listOf(
            "BPBIO320" to "BPBIO320",
            "BP170B" to "BP170B"
        )
    )
}

/* ---------------- 공통 프리뷰 UI ---------------- */
@Composable
private fun DialogPreviewContent(
    title: String,
    items: List<Pair<String, String>>
) {
    NenoonKioskTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0x99000000))
                .padding(vertical = 50.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .width(600.dp)
                    .height(1000.dp)
                    .background(Color.White, RoundedCornerShape(8.dp))
            ) {
                NenoonTopBar(
                    title = title,
                    showBackButton = false,
                    onBackClicked = {}
                )

                LazyColumn(
                    modifier = Modifier.weight(1f)
                ) {
                    items(items) { (_, text) ->
                        SettingItem(
                            text = text,
                            onClick = {}
                        )
                    }
                }
            }
        }
    }
}
