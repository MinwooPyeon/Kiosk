package com.pixelro.nenoonkiosk.feature.permission

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.constants.GlobalValue
import com.pixelro.nenoonkiosk.core.util.StringProvider
import com.pixelro.nenoonkiosk.feature.permission.components.PermissionDialog
import com.pixelro.nenoonkiosk.feature.permission.components.PermissionItemRow

@Composable
fun PermissionScreen(
    state: PermissionState,
    onPermissionItemClick: (PermissionItem) -> Unit,
    onConfirmClick: () -> Unit,
    onDismissDialog: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 40.dp, end = 40.dp)
        ) {
            Spacer(modifier = Modifier.height((GlobalValue.statusBarPadding + 152).dp))

            Text(
                text = "앱 사용을 위해\n접근 권한 허용이 필요해요",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                modifier = Modifier.padding(top = 80.dp),
                text = "권한이 모두 설정되어있어야 다음 화면으로 넘어갑니다\n체크되지 않은 항목을 선택하면 설정 페이지로 이동합니다",
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xff878787)
            )

            Spacer(modifier = Modifier.height(80.dp))

            state.permissionItems.forEach { item ->
                PermissionItemRow(
                    item = item,
                    onClick = { onPermissionItemClick(item) }
                )
                Spacer(modifier = Modifier.height(20.dp))
            }
        }

        Button(
            onClick = onConfirmClick,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(40.dp)
                .height(60.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = colorResource(R.color.main),
                contentColor = colorResource(R.color.white)
            )
        ) {
            Text(
                text = StringProvider.getStringComposable(R.string.confirm),
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }

    if (state.showRequireAllDialog) {
        PermissionDialog(
            onDismiss = onDismissDialog
        )
    }
}
