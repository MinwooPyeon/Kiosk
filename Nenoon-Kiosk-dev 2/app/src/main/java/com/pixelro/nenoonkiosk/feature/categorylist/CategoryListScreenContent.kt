package com.pixelro.nenoonkiosk.feature.categorylist

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.ui.InspectionCategoryButton
import com.pixelro.nenoonkiosk.core.ui.Logo
import com.pixelro.nenoonkiosk.feature.inspection.InspectionType
import com.pixelro.nenoonkiosk.ui.theme.neNoon_blue

@Composable
fun CategoryListScreenContent(
    isSignInSkipped: () -> Boolean,
    toEyeTestScreen: () -> Unit,
    toContact: () -> Unit,
    toDementiaTestScreen: (InspectionType) -> Unit,
    toExternalDeviceTestListScreen: () -> Unit,
    toStrabismusTestListScreen: () -> Unit,
    toIntroScreen: () -> Unit,
    toPrintScreen: () -> Unit,
    toAccountManagementScreen: () -> Unit,
    toSettingsScreen: () -> Unit,
) {
    val isPreview = LocalInspectionMode.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(neNoon_blue),
    ) {
        Spacer(modifier = Modifier.weight(1f))
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Logo(true)
        }
        Spacer(modifier = Modifier.weight(1f))

        val testButtons = listOf(
            Triple(R.string.blood_pressure_and_grip_strength, R.drawable.blood_pressure_and_grip_strength_icon) { toExternalDeviceTestListScreen() },
            Triple(R.string.eye_test, R.drawable.eye_test_icon) { toEyeTestScreen() },
            Triple(R.string.cross_eye_test, R.drawable.cross_eye_icon) { toStrabismusTestListScreen() },
            Triple(R.string.dementia_test, R.drawable.dementia_icon) { toDementiaTestScreen(
                InspectionType.Dementia) }
        )

        testButtons.forEachIndexed { index, (titleId, icon, onClick) ->
            InspectionCategoryButton(
                iconRes = icon,
                title = stringResource(id = titleId),
                onClick = onClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(115.dp)
                    .padding(horizontal = 40.dp)
            )
            Spacer(modifier = Modifier.height(if (index == testButtons.lastIndex) 40.dp else 20.dp))
        }
    }

    // 상단 버튼들
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(40.dp),
        horizontalArrangement = Arrangement.End
    ) {
        if (!isSignInSkipped()) {
            Card(
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { toPrintScreen() },
                backgroundColor = Color.White
            ) {
                Icon(
                    modifier = Modifier
                        .width(60.dp)
                        .padding(20.dp),
                    painter = painterResource(id = R.drawable.icon_print),
                    contentDescription = ""
                )
            }
            Spacer(modifier = Modifier.width(20.dp))
        }

        Card(
            modifier = Modifier
                .size(100.dp)
                .clip(RoundedCornerShape(8.dp))
                .clickable { toAccountManagementScreen() },
            backgroundColor = Color.White
        ) {
            Icon(
                modifier = Modifier
                    .width(60.dp)
                    .padding(20.dp),
                painter = painterResource(id = R.drawable.account_icon),
                contentDescription = ""
            )
        }
    }
}
