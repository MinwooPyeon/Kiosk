package com.pixelro.nenoonkiosk.feature.inspection.inspectionresult.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.ui.ProgressIndicator
import com.pixelro.nenoonkiosk.core.util.isLandscape
import com.pixelro.nenoonkiosk.ui.theme.White
import com.pixelro.nenoonkiosk.ui.theme.bodyTextStyle

@Composable
fun LoadingView() {
    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(color = White),
        contentAlignment = Alignment.Center,
    ) {
        if (isLandscape()) {
            // 가로 모드: 이미지 왼쪽, 텍스트+인디케이터 오른쪽
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                Image(
                    painter = painterResource(R.drawable.loading_icon),
                    contentDescription = stringResource(R.string.tts_wait_for_result),
                    modifier = Modifier.size(400.dp),
                )
                Spacer(modifier = Modifier.width(60.dp))
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    ProgressIndicator()
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(
                        text = stringResource(R.string.tts_wait_for_result),
                        style = bodyTextStyle,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        } else {
            // 세로 모드: 이미지 위, 텍스트+인디케이터 아래
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Image(
                    painter = painterResource(R.drawable.loading_icon),
                    contentDescription = stringResource(R.string.tts_wait_for_result),
                    modifier = Modifier.size(360.dp),
                )
                Spacer(modifier = Modifier.height(40.dp))
                ProgressIndicator()
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = stringResource(R.string.tts_wait_for_result),
                    style = bodyTextStyle,
                    fontWeight = FontWeight.Bold,
                )

            }
        }
    }
}