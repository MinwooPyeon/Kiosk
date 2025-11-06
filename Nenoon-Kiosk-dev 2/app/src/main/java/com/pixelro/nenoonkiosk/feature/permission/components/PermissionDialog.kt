package com.pixelro.nenoonkiosk.feature.permission.components

import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.colorResource
import com.pixelro.nenoonkiosk.R

@Composable
fun PermissionDialog(
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("권한 필요") },
        text = {
            Text("앱을 사용하기 위해서는 모든 권한이 필요합니다. 권한을 허용하지 않으면 앱을 사용할 수 없습니다.")
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = colorResource(R.color.main),
                    contentColor = colorResource(R.color.white)
                )
            ) { Text("확인") }
        }
    )
}