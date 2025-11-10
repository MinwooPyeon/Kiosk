package com.pixelro.nenoonkiosk.feature.inspection.inspectionresult.components

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.ui.PrimaryButton
import com.pixelro.nenoonkiosk.core.ui.SecondaryButton
import com.pixelro.nenoonkiosk.core.util.StringProvider
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun LightBottomArea(
    printEnabled: Boolean,
    onPrint: () -> Unit,
    onBack: () -> Unit,
    onLogout: () -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    var isPrinting by remember { mutableStateOf(false) } // 버튼 비활성화 상태 관리

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
                    onClick = {
                        if (!isPrinting) {
                            isPrinting = true
                            onPrint()
                            coroutineScope.launch {
                                delay(3000) // 3초 동안 다시 못 누르게
                                isPrinting = false
                            }
                        }
                    },
                    text = StringProvider.getStringComposable(R.string.result_button1_print),
                    modifier = Modifier
                        .padding(start = 40.dp, end = 40.dp, bottom = 20.dp),
                    enabled = !isPrinting // 버튼 비활성화 처리
                )
            }

            PrimaryButton(
                onClick = onBack,
                text = StringProvider.getStringComposable(R.string.result_dementia_back),
                modifier = Modifier
                    .padding(start = 40.dp, end = 40.dp, bottom = 20.dp),
            )

            Spacer(modifier = Modifier.height(40.dp))

            SecondaryButton(
                onClick = onLogout,
                text = StringProvider.getStringComposable(R.string.settings_signout),
                iconDrawable = R.drawable.icon_logout,
                modifier = Modifier
                    .padding(start = 40.dp, end = 40.dp),
            )
        }
    }
}
