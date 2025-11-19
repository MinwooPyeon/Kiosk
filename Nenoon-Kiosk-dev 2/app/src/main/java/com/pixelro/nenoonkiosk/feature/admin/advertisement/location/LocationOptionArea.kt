package com.pixelro.nenoonkiosk.feature.admin.advertisement.location

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.harang.data.db.entity.LocationEntity
import com.harang.data.db.entity.MediaType
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.feature.admin.advertisement.getSubtitleRes
import com.pixelro.nenoonkiosk.feature.admin.advertisement.getTitleRes
import com.pixelro.nenoonkiosk.ui.theme.Green
import com.pixelro.nenoonkiosk.ui.theme.LightGray300
import com.pixelro.nenoonkiosk.ui.theme.LightGreen
import com.pixelro.nenoonkiosk.ui.theme.LightYellow
import com.pixelro.nenoonkiosk.ui.theme.White
import com.pixelro.nenoonkiosk.ui.theme.Yellow200


@Composable
fun LocationOptionArea(
    availableLocations: List<LocationEntity>,
    selectedLocation: LocationEntity?,
    onSelectLocation: (LocationEntity) -> Unit
) {

    Column(
        modifier = Modifier
            .border(1.dp, LightGray300, RoundedCornerShape(12.dp))
            .background(White, RoundedCornerShape(12.dp))
            .padding(24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.admin_select_ad_location),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        availableLocations.forEachIndexed { index, location ->
            if (index > 0) {
                Spacer(modifier = Modifier.height(16.dp))
            }

            // MediaType에 따라 배지 설정
            val (badgeRes, badgeColor, badgeTextColor) = when (location.mediaType) {
                MediaType.IMAGE -> Triple(R.string.admin_badge_image, LightGreen, Green)
                MediaType.VIDEO -> Triple(R.string.admin_badge_video, LightYellow, Yellow200)
            }

            LocationOption(
                location = location,
                title = stringResource(location.getTitleRes()), // name 기반 다국어 제목
                subtitle = stringResource(location.getSubtitleRes()), // name 기반 다국어 부제목
                badge = stringResource(badgeRes),
                badgeColor = badgeColor,
                badgeTextColor = badgeTextColor,
                isSelected = selectedLocation?.id == location.id,
                onSelect = { onSelectLocation(location) }
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF8F8F8, widthDp = 400, heightDp = 300)
@Composable
private fun LocationOptionAreaPreview() {
    LocationOptionArea(
        availableLocations = listOf(
            LocationEntity(id = 1, name = "TEST_LIST_SCREEN", mediaType = MediaType.IMAGE),
            LocationEntity(id = 2, name = "SCREENSAVER", mediaType = MediaType.VIDEO)
        ),
        selectedLocation = null,
        onSelectLocation = {}
    )
}