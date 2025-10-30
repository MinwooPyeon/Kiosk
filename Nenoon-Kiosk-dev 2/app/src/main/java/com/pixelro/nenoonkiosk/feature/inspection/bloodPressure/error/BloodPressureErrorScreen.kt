package com.pixelro.nenoonkiosk.feature.inspection.bloodPressure.error

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.ui.IconTextButton
import com.pixelro.nenoonkiosk.core.ui.StyledText
import com.pixelro.nenoonkiosk.core.ui.TextStyle
import com.pixelro.nenoonkiosk.core.util.StringProvider

@Composable
fun BloodPressureErrorScreen(
    onReturn: () -> Unit,
    onLogout: () -> Unit,
    isSignedIn: Boolean,
    toStart: () -> Unit,
) {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(vertical = 60.dp, horizontal = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Spacer(modifier = Modifier.weight(1f))

        Icon(
            painter = painterResource(R.drawable.warning),
            tint = colorResource(R.color.error),
            contentDescription = null,
            modifier = Modifier.size(400.dp),
        )
        Spacer(modifier = Modifier.height(20.dp))
        StyledText(
            text = stringResource(R.string.bpbio320_error_title),
            style = TextStyle.Error,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.weight(1f))
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            IconTextButton(
                onClick = {
                    toStart
                },
                iconId = R.drawable.icon_retry,
                text = stringResource(R.string.retest),
            )
            IconTextButton(
                onClick = onReturn,
                iconId = R.drawable.icon_back2,
                text = stringResource(R.string.result_button2_back),
            )
            if (isSignedIn) {
                IconTextButton(
                    onClick = onLogout,
                    iconId = R.drawable.icon_logout,
                    text = stringResource(R.string.settings_signout),
                )
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 888, heightDp = 1422, name = "BP Error – Idle")
@Composable
private fun Preview_BP_Error_Idle() {
    BloodPressureErrorScreen(
        onReturn = {},
        onLogout = {},
        isSignedIn = true,
        toStart = {},
    )
}

@Preview(showBackground = true, widthDp = 888, heightDp = 1422, name = "BP Error – false")
@Composable
private fun Preview_BP_Error_false() {
    BloodPressureErrorScreen(
        onReturn = {},
        onLogout = {},
        isSignedIn = false,
        toStart = {},
    )
}