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
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.feature.admin.advertisement.AdLocation
import com.pixelro.nenoonkiosk.ui.theme.Green
import com.pixelro.nenoonkiosk.ui.theme.LightGray300
import com.pixelro.nenoonkiosk.ui.theme.LightGreen
import com.pixelro.nenoonkiosk.ui.theme.LightYellow
import com.pixelro.nenoonkiosk.ui.theme.White
import com.pixelro.nenoonkiosk.ui.theme.Yellow200

data class LocationOptionConfig(
    val location: AdLocation,
    val titleRes: Int,
    val subtitleRes: Int,
    val badgeRes: Int,
    val badgeColor: Color,
    val badgeTextColor: Color
)


@Composable
fun LocationOptionArea(
    selectedLocation: AdLocation?,
    onSelectLocation: (AdLocation) -> Unit
) {
    val locationOptions = listOf(
        LocationOptionConfig(
            location = AdLocation.TEST_LIST_SCREEN,
            titleRes = R.string.admin_test_list_screen,
            subtitleRes = R.string.admin_test_list_subtitle,
            badgeRes = R.string.admin_badge_image,
            badgeColor = LightGreen,
            badgeTextColor = Green
        ),
        LocationOptionConfig(
            location = AdLocation.SCREENSAVER,
            titleRes = R.string.admin_screensaver,
            subtitleRes = R.string.admin_screensaver_subtitle,
            badgeRes = R.string.admin_badge_video,
            badgeColor = LightYellow,
            badgeTextColor = Yellow200
        )
    )

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

        locationOptions.forEachIndexed { index, config ->
            if (index > 0) {
                Spacer(modifier = Modifier.height(16.dp))
            }

            LocationOption(
                location = config.location,
                title = stringResource(config.titleRes),
                subtitle = stringResource(config.subtitleRes),
                badge = stringResource(config.badgeRes),
                badgeColor = config.badgeColor,
                badgeTextColor = config.badgeTextColor,
                isSelected = selectedLocation == config.location,
                onSelect = { onSelectLocation(config.location) }
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF8F8F8, widthDp = 400, heightDp = 300)
@Composable
private fun LocationOptionAreaPreview() {
    LocationOptionArea(
        selectedLocation = AdLocation.TEST_LIST_SCREEN,
        onSelectLocation = {}
    )
}