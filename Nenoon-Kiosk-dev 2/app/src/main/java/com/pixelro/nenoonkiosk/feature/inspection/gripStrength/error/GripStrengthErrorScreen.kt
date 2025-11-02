package com.pixelro.nenoonkiosk.feature.inspection.gripStrength.error

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.ui.IconTextButton
import com.pixelro.nenoonkiosk.core.ui.StyledText
import com.pixelro.nenoonkiosk.core.ui.TextStyle

data class GripErrorUiState(
    val isSignedIn: Boolean = true
)

sealed class GripErrorEvent {
    object Retry : GripErrorEvent()
    object Return : GripErrorEvent()
    object Logout : GripErrorEvent()
}

@Composable
fun GripStrengthErrorScreen(
    state: GripErrorUiState,
    onEvent: (GripErrorEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(vertical = 60.dp, horizontal = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(modifier = Modifier.weight(1f))

        Icon(
            painter = painterResource(R.drawable.warning),
            tint = colorResource(R.color.error),
            contentDescription = null,
            modifier = Modifier.size(400.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        StyledText(
            text = stringResource(R.string.ingrip_error_title),
            style = TextStyle.Error,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.weight(1f))

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            IconTextButton(
                onClick = { onEvent(GripErrorEvent.Retry) },
                iconId = R.drawable.icon_retry,
                text = stringResource(R.string.ingrip_retest)
            )

            IconTextButton(
                onClick = { onEvent(GripErrorEvent.Return) },
                iconId = R.drawable.icon_back_2,
                text = stringResource(R.string.result_button_2_back)
            )

            if (state.isSignedIn) {
                IconTextButton(
                    onClick = { onEvent(GripErrorEvent.Logout) },
                    iconId = R.drawable.icon_logout,
                    text = stringResource(R.string.settings_signout)
                )
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 888, heightDp = 1422, name = "Error SignedIn")
@Composable
private fun Preview_GripErrorSignedIn() {
    GripStrengthErrorScreen(
        state = GripErrorUiState(isSignedIn = true),
        onEvent = {}
    )
}
