package com.pixelro.nenoonkiosk.feature.auth.component

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.ui.ProgressIndicator
import com.pixelro.nenoonkiosk.core.ui.StyledText
import com.pixelro.nenoonkiosk.core.ui.TextStyle

@Composable
fun QrCodeContent(
    showProgressIndicator: Boolean,
    isUserSignedIn: Boolean,
    isUserSignInSkipped: Boolean,
    userName: String?,
    qrCodeBitmap: Bitmap?
) {
    if (showProgressIndicator) {
        ProgressIndicator()
    } else if (!isUserSignInSkipped && isUserSignedIn) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            StyledText(
                text = stringResource(
                    id = R.string.qr_code_user_name,
                    userName ?: stringResource(R.string.default_user_name)
                ),
            )

            if (qrCodeBitmap != null) {
                Image(
                    bitmap = qrCodeBitmap.asImageBitmap(),
                    contentDescription = stringResource(R.string.qr_code_image_description),
                    modifier = Modifier
                        .size(400.dp)
                        .padding(top = 40.dp)
                        .clip(MaterialTheme.shapes.medium),
                )
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.size(400.dp),
                ) {
                    ProgressIndicator()
                }
            }
        }
    } else {
        StyledText(
            text = stringResource(R.string.not_signed_in_message),
            style = TextStyle.Error,
        )
    }
}
