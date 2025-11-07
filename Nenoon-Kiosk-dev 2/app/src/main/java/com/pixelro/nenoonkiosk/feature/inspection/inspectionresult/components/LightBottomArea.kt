package com.pixelro.nenoonkiosk.feature.inspection.inspectionresult.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.ui.PrimaryButton
import com.pixelro.nenoonkiosk.core.ui.SecondaryButton
import com.pixelro.nenoonkiosk.core.util.StringProvider

@Composable
fun LightBottomArea(
    printEnabled: Boolean,
    onPrint: () -> Unit,
    onBack: () -> Unit,
    onLogout: () -> Unit,
) {
    // 하단 버튼 영역
    Box(
        modifier = Modifier
            .padding(bottom = 40.dp)
            .fillMaxWidth(),
        contentAlignment = Alignment.BottomCenter,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (printEnabled) {
                PrimaryButton(
                    onClick = onPrint,
                    text = StringProvider.getStringComposable(R.string.result_button1_print),
                    modifier =
                        Modifier
                            .padding(start = 40.dp, end = 40.dp, bottom = 20.dp),
                )
            }

            PrimaryButton(
                onClick = onBack,
                text = StringProvider.getStringComposable(R.string.result_dementia_back),
                modifier =
                    Modifier
                        .padding(start = 40.dp, end = 40.dp, bottom = 20.dp),
            )

            Spacer(modifier = Modifier.height(40.dp))

            SecondaryButton(
                onClick = onLogout,
                text = StringProvider.getStringComposable(R.string.settings_signout),
                iconDrawable = R.drawable.icon_logout,
                modifier =
                    Modifier
                        .padding(start = 40.dp, end = 40.dp),
            )
        }
    }
}