package com.pixelro.nenoonkiosk.feature.screensaver

import android.content.Context
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withAnnotation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.constants.GlobalValue
import com.pixelro.nenoonkiosk.core.util.StringProvider
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@androidx.annotation.OptIn(UnstableApi::class)
@Composable
fun ScreenSaverRoute(
    viewModel: ScreenSaverViewModel = hiltViewModel()
) {
    val state = viewModel.collectAsState().value

    viewModel.collectSideEffect { /* Navigator가 처리 */ }

    LaunchedEffect(Unit) {
        viewModel.initializeVideo()
    }

    ScreenSaverScreen(
        exoPlayer = viewModel.exoPlayer,
        state = state,
        onScreenTouched = { viewModel.onScreenTouched() }
    )
}

@androidx.annotation.OptIn(UnstableApi::class)
@OptIn(ExperimentalTextApi::class)
@Composable
fun ScreenSaverScreen(
    exoPlayer: ExoPlayer,
    state: ScreenSaverUiState,
    onScreenTouched: () -> Unit
) {
    val localContext = LocalContext.current
    val sharedPreferences = remember {
        localContext.getSharedPreferences(
            SharedPreferencesConstants.PREFERENCE_NAME,
            Context.MODE_PRIVATE
        )
    }
    val savedLanguage = sharedPreferences.getString("language", "defaultLanguage")

    val transition = rememberInfiniteTransition(label = "shimmer")
    val shiftVal by transition.animateFloat(
        initialValue = 0f,
        targetValue = 0.5f,
        animationSpec = infiniteRepeatable(
            animation = keyframes { durationMillis = 1000 },
            repeatMode = RepeatMode.Reverse
        ),
        label = "shift"
    )

    val text = buildAnnotatedString {
        append(StringProvider.getString(R.string.screensaver_description1))
        withAnnotation("squiggles", annotation = "ignored") {
            withStyle(
                SpanStyle(
                    color = Color(0xff1d71e1),
                    baselineShift = BaselineShift(shiftVal)
                )
            ) {
                append(StringProvider.getString(R.string.screensaver_description2))
            }
        }
        withAnnotation("squiggles2", annotation = "ignored") {
            withStyle(
                if (savedLanguage == "ko") {
                    SpanStyle(fontSize = 42.sp)
                } else {
                    SpanStyle()
                }
            ) {
                append(StringProvider.getString(R.string.screensaver_description3))
            }
        }
    }

    val systemUiController = rememberSystemUiController()

    DisposableEffect(true) {
        systemUiController.systemBarsDarkContentEnabled = false
        if (state is ScreenSaverUiState.Ready && state.isVideoPlaying) {
            exoPlayer.play()
        }
        onDispose {
            systemUiController.systemBarsDarkContentEnabled = true
            exoPlayer.stop()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color(0xff000000))
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { onScreenTouched() },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.padding(top = GlobalValue.statusBarPadding.dp))

        Box {
            Column(
                modifier = Modifier.height(300.dp),
                verticalArrangement = Arrangement.Bottom
            ) {
                Text(
                    modifier = Modifier.padding(bottom = 20.dp),
                    text = text,
                    color = Color(0xffffffff),
                    fontWeight = FontWeight.Bold,
                    fontSize = 60.sp,
                    textAlign = TextAlign.Center
                )
            }
        }

        AndroidView(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Max)
                .background(color = Color(0xff000000)),
            factory = { context ->
                PlayerView(context).apply {
                    resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                    player = exoPlayer
                    useController = false
                }
            }
        )

        Spacer(modifier = Modifier.height(300.dp))
    }
}