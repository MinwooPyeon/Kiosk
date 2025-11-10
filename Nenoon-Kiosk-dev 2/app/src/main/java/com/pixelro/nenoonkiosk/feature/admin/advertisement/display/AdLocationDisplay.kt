package com.pixelro.nenoonkiosk.feature.admin.advertisement.display

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.ui.theme.LightGray300
import com.pixelro.nenoonkiosk.ui.theme.White


@Composable
fun AdLocationDisplay(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .border(1.dp, LightGray300, RoundedCornerShape(12.dp))
            .background(White, RoundedCornerShape(12.dp))
            .padding(24.dp)
    ) {
        Text(
            text = stringResource(R.string.admin_ad_location_preview),
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // 미리보기 이미지 영역
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFFF5F5F5)),
            contentAlignment = Alignment.Center
        ) {
            // 실제로는 이미지를 표시
            Text(
                text = stringResource(R.string.admin_ad_location_preview),
                color = Color.Gray
            )
        }
    }
}