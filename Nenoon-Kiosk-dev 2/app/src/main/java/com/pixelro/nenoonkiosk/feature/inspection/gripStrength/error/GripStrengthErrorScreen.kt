package com.pixelro.nenoonkiosk.feature.inspection.gripStrength.error

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.ui.PrimaryButton

@Composable
fun GripStrengthErrorScreen(
    isSignedIn: Boolean,
    onRetry: () -> Unit,
    onReturn: () -> Unit,
    onLogout: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = stringResource(R.string.test_failed),
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Red
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                text = stringResource(R.string.grip_strength_error_message),
                fontSize = 24.sp,
                textAlign = TextAlign.Center
            )
            Text(
                text = stringResource(R.string.grip_strength_error_reason),
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                color = Color.Gray
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            PrimaryButton(
                text = stringResource(R.string.retry),
                onClick = onRetry
            )
            PrimaryButton(
                text = stringResource(R.string.return_to_menu),
                onClick = onReturn
            )
            if (isSignedIn) {
                PrimaryButton(
                    text = stringResource(R.string.logout),
                    onClick = onLogout
                )
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 800, heightDp = 1280)
@Composable
private fun Preview_GripStrengthError() {
    GripStrengthErrorScreen(
        isSignedIn = true,
        onRetry = {},
        onReturn = {},
        onLogout = {}
    )
}
