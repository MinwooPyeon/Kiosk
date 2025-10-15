package com.pixelro.nenoonkiosk.data

import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideOut
import androidx.compose.ui.unit.IntOffset

object AnimationProvider {
    val enterTransition = slideIn(
    animationSpec = TweenSpec(durationMillis = 500),
    initialOffset = { IntOffset(150, 0) }
    ) + fadeIn(
        animationSpec = TweenSpec(durationMillis = 500)
    )

    val popEnterTransition = slideIn(
        animationSpec = TweenSpec(durationMillis = 500),
        initialOffset = { IntOffset(-150, 0) }
    ) + fadeIn(
        animationSpec = TweenSpec(durationMillis = 500)
    )

    val exitTransition = slideOut(
        animationSpec = TweenSpec(durationMillis = 500),
        targetOffset = { IntOffset(-150, 0) }
    ) + fadeOut(
        animationSpec = TweenSpec(durationMillis = 500)
    )

    val popExitTransition = slideOut(
        animationSpec = TweenSpec(durationMillis = 500),
        targetOffset = { IntOffset(150, 0) }
    ) + fadeOut(
        animationSpec = TweenSpec(durationMillis = 500)
    )


}