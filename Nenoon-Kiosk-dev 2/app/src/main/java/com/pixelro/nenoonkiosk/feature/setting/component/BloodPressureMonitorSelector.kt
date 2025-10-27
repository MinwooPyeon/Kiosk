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
import com.pixelro.nenoonkiosk.core.manager.SharedPreferencesManager
import com.pixelro.nenoonkiosk.core.ui.TopBarVertical
import com.pixelro.nenoonkiosk.ui.theme.NenoonKioskTheme

@Composable
fun BloodPressureMonitorSelector(onDismissRequest: () -> Unit) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(),
    ) {
        Column(
            modifier = Modifier
                .width(600.dp)
                .height(1000.dp)
                .background(Color.White, RoundedCornerShape(8.dp))
        ) {
            // 상단 바 (TopBarVertical 사용)
            TopBarVertical(
                title = stringResource(R.string.blood_pressure_monitor_image_content_description),
                showBackButton = false,
                onBackClicked = {}
            )

            val monitorTypes = listOf(
                SharedPreferencesManager.BloodPressureMonitorType.BPBIO320,
                SharedPreferencesManager.BloodPressureMonitorType.BP170B
            )

            // 각 항목을 SettingItem으로 표시
            monitorTypes.forEach { type ->
                SettingItem(
                    text = type.name,
                    onClick = {
                        SharedPreferencesManager.putBloodPressureMonitorType(type)
                        onDismissRequest()
                    }
                )
            }
        }
    }
}

@Preview(
    name = "혈압계 선택 다이얼로그 프리뷰",
    showBackground = true,
    widthDp = 800,
    heightDp = 1280
)
@Composable
private fun BloodPressureMonitorSelectorPreview() {
    NenoonKioskTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            Column(
                modifier = Modifier
                    .width(600.dp)
                    .height(1000.dp)
                    .background(Color.White, RoundedCornerShape(8.dp))
            ) {
                TopBarVertical(
                    title = stringResource(R.string.blood_pressure_monitor_image_content_description),
                    showBackButton = false,
                    onBackClicked = {}
                )

                val monitorTypes = listOf(
                    SharedPreferencesManager.BloodPressureMonitorType.BPBIO320,
                    SharedPreferencesManager.BloodPressureMonitorType.BP170B
                )

                monitorTypes.forEach { type ->
                    SettingItem(
                        text = type.name,
                        onClick = {}
                    )
                }
            }
        }
    }
}
