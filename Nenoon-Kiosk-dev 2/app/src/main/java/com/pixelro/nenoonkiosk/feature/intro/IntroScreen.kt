package com.pixelro.nenoonkiosk.feature.intro

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.pixelro.nenoonkiosk.feature.intro.components.IntroText
import com.pixelro.nenoonkiosk.feature.intro.components.IntroTopBar
import com.pixelro.nenoonkiosk.feature.intro.components.LogoWithVersion
import com.pixelro.nenoonkiosk.feature.intro.components.StartButton

@Composable
fun IntroRoute(
    viewModel: IntroViewModel = hiltViewModel()
) {
    val transition = rememberInfiniteTransition(label = "intro alpha")
    val alphaVal by transition.animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 700
            },
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha animation"
    )

    IntroScreen(
        alphaVal = alphaVal,
        toSurveyScreen = { viewModel.navigateToSurvey() },
        toSettingsScreen = { viewModel.navigateToSettings() }
    )
}

// 시작 버튼 있는 화면
@Composable
fun IntroScreen(
    alphaVal: Float,
    toSurveyScreen: () -> Unit,
    toSettingsScreen: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        /**
         * 상단 바
         */
        IntroTopBar(toSettingsScreen)

        Spacer(modifier = Modifier.weight(0.2F))

        LogoWithVersion()

        Spacer(modifier = Modifier.weight(0.5F))

        /**
         * 시작 버튼
         */
        StartButton(alphaVal, toSurveyScreen)

        Spacer(modifier = Modifier.weight(1F))

        IntroText()
    }
}

@Preview(showBackground = true, widthDp = 888, heightDp = 1422, apiLevel = 34)
@Composable
fun IntroScreenPreview() {
    IntroScreen(
        alphaVal = 1f,
        toSurveyScreen = {},
        toSettingsScreen = {}
    )
}
