package com.pixelro.nenoonkiosk.feature.permission

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.ui.PrimaryButton
import com.pixelro.nenoonkiosk.core.util.isLandscape
import com.pixelro.nenoonkiosk.feature.permission.components.PermissionDialog
import com.pixelro.nenoonkiosk.feature.permission.components.PermissionItem
import com.pixelro.nenoonkiosk.ui.theme.Gray

@Composable
fun PermissionRequestScreen(
    state: PermissionRequestState,
    items: List<PermissionItemUi>,
    onConfirmClick: () -> Unit,
    onDismissDialog: () -> Unit
) {
    val titleText = "앱 사용을 위해\n접근 권한 허용이 필요해요"
    val descriptionText = "권한이 모두 설정되어있어야 다음 화면으로 넘어갑니다.\n체크되지 않은 항목을 선택하면 설정 페이지로 이동합니다."

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight(0.9f)
                .fillMaxWidth(0.9f)
        ) {
            Spacer(modifier = Modifier.fillMaxHeight(0.15f))

            Text(
                text = titleText,
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.fillMaxHeight(0.08f))

            Text(
                text = descriptionText.let { if (isLandscape()) it.replace("\n", " ") else it },
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                color = Gray
            )

            Spacer(modifier = Modifier.fillMaxHeight(0.08f))

            PermissionItem(items = items)
        }

        PrimaryButton(
            onClick = onConfirmClick,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 40.dp)
                .fillMaxWidth(0.9f),
            enabled = true,
            text = stringResource(R.string.confirm)
        )
    }

    if (state.showRequireAllDialog) {
        PermissionDialog(
            onDismiss = onDismissDialog
        )
    }
}

@Preview(showBackground = true, widthDp = 1280, heightDp = 800, apiLevel = 34)
@Composable
private fun PermissionRequestScreen_Preview_AllGranted() {
    val dummyState = PermissionRequestState(
        isWriteSettingsGranted = true,
        isCameraGranted = true,
        isBluetoothPermsGranted = true,
        isBluetoothOn = true,
        showRequireAllDialog = false
    )
    val items = listOf(
        PermissionItemUi("시스템 설정 변경", "화면 보호기 영상 재생에 이용", R.drawable.icon_settings, true) {},
        PermissionItemUi("카메라 권한", "거리 측정을 위해 이용", R.drawable.icon_camera, true) {},
        PermissionItemUi("블루투스 권한", "프린터 연결을 위한 용도", R.drawable.icon_bluetooth, true) {},
        PermissionItemUi(
            "블루투스 서비스",
            "프린터를 연결하기 위한 블루투스 확인 용도",
            R.drawable.icon_bluetoothon,
            true
        ) {}
    )
    PermissionRequestScreen(
        state = dummyState,
        items = items,
        onConfirmClick = {},
        onDismissDialog = {}
    )
}

@Preview(showBackground = true, widthDp = 800, heightDp = 1280, apiLevel = 34)
@Composable
private fun PermissionRequestScreen_Preview_PartialGranted_WithDialog() {
    val dummyState = PermissionRequestState(
        isWriteSettingsGranted = true,
        isCameraGranted = false,
        isBluetoothPermsGranted = true,
        isBluetoothOn = false,
        showRequireAllDialog = true
    )
    val items = listOf(
        PermissionItemUi("시스템 설정 변경", "화면 보호기 영상 재생에 이용", R.drawable.icon_settings, true) {},
        PermissionItemUi("카메라 권한", "거리 측정을 위해 이용", R.drawable.icon_camera, false) {},
        PermissionItemUi("블루투스 권한", "프린터 연결을 위한 용도", R.drawable.icon_bluetooth, true) {},
        PermissionItemUi(
            "블루투스 서비스",
            "프린터를 연결하기 위한 블루투스 확인 용도",
            R.drawable.icon_bluetoothon,
            false
        ) {}
    )
    PermissionRequestScreen(
        state = dummyState,
        items = items,
        onConfirmClick = {},
        onDismissDialog = {}
    )
}
