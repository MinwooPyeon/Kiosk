package com.pixelro.nenoonkiosk.feature.permission.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pixelro.nenoonkiosk.core.util.isLandscape
import com.pixelro.nenoonkiosk.feature.permission.PermissionItemUi

@Composable
fun PermissionItem(items: List<PermissionItemUi>) {
    if (isLandscape()) {
        // 가로 모드: 2x2 그리드
        items.chunked(2).forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                rowItems.forEach { item ->
                    Box(modifier = Modifier.weight(1f)) {
                        PermissionItemRow(item)
                    }
                }
            }
            Spacer(modifier = Modifier.fillMaxHeight(0.04f))
        }
    } else {
        // 세로 모드: 세로 나열
        items.forEach { item ->
            PermissionItemRow(item)
            Spacer(modifier = Modifier.fillMaxHeight(0.04f))
        }
    }
}