package com.pixelro.nenoonkiosk.feature.auth.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.ui.PrimaryButton
import com.pixelro.nenoonkiosk.core.ui.SecondaryButton
import com.pixelro.nenoonkiosk.core.util.StringProvider

@Composable
fun ActionButtons(
    isUserSignInSkipped: Boolean,
    isQrPrintButtonEnabled: Boolean,
    onPrintClick: () -> Unit,
    onFaceEnrollClick: () -> Unit,
    onSignOutClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        PrimaryButton(
            onClick = onPrintClick,
            text = StringProvider.getString(R.string.qr_code_print_button),
            enabled = !isUserSignInSkipped && isQrPrintButtonEnabled,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(20.dp))

        PrimaryButton(
            onClick = onFaceEnrollClick,
            text = StringProvider.getString(R.string.face_enroll_button_text),
            enabled = !isUserSignInSkipped,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(20.dp))

        SecondaryButton(
            onClick = onSignOutClick,
            text = if (!isUserSignInSkipped) {
                StringProvider.getString(R.string.settings_signout)
            } else {
                StringProvider.getString(R.string.signin)
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}