package com.pixelro.nenoonkiosk.feature.permission.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.feature.permission.PermissionItem

@Composable
fun PermissionItemRow(
    item: PermissionItem,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .height(120.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(color = Color(0xFFEEEEEE), shape = RoundedCornerShape(8.dp))
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            modifier = Modifier
                .padding(start = 40.dp)
                .width(40.dp)
                .height(40.dp),
            painter = painterResource(id = item.iconRes),
            contentDescription = null
        )
        Column(
            modifier = Modifier.padding(start = 40.dp)
        ) {
            Text(
                text = item.title,
                fontWeight = FontWeight.Medium,
                fontSize = 20.sp
            )
            Text(
                text = item.description,
                color = Color(0xff878787)
            )
        }
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.CenterEnd
        ) {
            Image(
                modifier = Modifier
                    .padding(end = 40.dp)
                    .width(32.dp)
                    .height(32.dp),
                painter = painterResource(
                    id = if (item.checked) R.drawable.baseline_check_48_on
                    else R.drawable.baseline_check_48_off
                ),
                contentDescription = null
            )
        }
    }
}
