package com.pixelro.nenoonkiosk.feature.intro

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.pixelro.nenoonkiosk.core.ui.NenoonTopBar
import com.pixelro.nenoonkiosk.core.ui.SettingsButton
import com.pixelro.nenoonkiosk.core.util.isLandscape
import com.pixelro.nenoonkiosk.feature.intro.components.IntroText
import com.pixelro.nenoonkiosk.feature.intro.components.LogoWithVersion
import com.pixelro.nenoonkiosk.feature.intro.components.StartButton

// 시작 버튼 있는 화면
@Composable
fun IntroScreen(
    toSurveyScreen: () -> Unit,
    toSettingsScreen: () -> Unit,
) {
    val transition = rememberInfiniteTransition()
    val alphaVal by transition.animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec =
            infiniteRepeatable(
                animation =
                    keyframes {
                        durationMillis = 700
                    },
                repeatMode = RepeatMode.Reverse,
            ),
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            /**
             * 상단 바
             */
            NenoonTopBar(
                title = "",
                showBackButton = false,
                actions = {
                    SettingsButton(toSettingsScreen = toSettingsScreen)
                }
            )
        }

        /**
         * 중앙 콘텐츠 (로고와 버튼)
         */
        if (isLandscape()) {
            // 가로 모드: Row로 배치
            Row(
                modifier = Modifier
                    .align(Alignment.Center),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                LogoWithVersion()
                Spacer(modifier = Modifier.width(200.dp))
                StartButton(alphaVal, toSurveyScreen)
            }
        } else {
            // 세로 모드: Column으로 배치
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                LogoWithVersion()
                Spacer(modifier = Modifier.height(80.dp))
                StartButton(alphaVal, toSurveyScreen)
            }
        }

        /**
         * 하단 텍스트
         */
        IntroText(
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Preview(showBackground = true, widthDp = 800, heightDp = 1280, apiLevel = 34)
@Composable
fun IntroScreenPreviewVertical() {
    IntroScreen(
        toSurveyScreen = {},
        toSettingsScreen = {}
    )
}

@Preview(showBackground = true, widthDp = 1280, heightDp = 800, apiLevel = 34)
@Composable
fun IntroScreenPreviewHorizental() {
    IntroScreen(
        toSurveyScreen = {},
        toSettingsScreen = {}
    )
}