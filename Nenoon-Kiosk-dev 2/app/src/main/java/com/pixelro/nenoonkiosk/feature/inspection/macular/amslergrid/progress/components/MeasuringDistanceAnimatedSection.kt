package com.pixelro.nenoonkiosk.feature.inspection.macular.amslergrid.progress.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.runtime.Composable
import com.pixelro.nenoonkiosk.core.util.AnimationProvider

@Composable
fun MeasuringDistanceAnimatedSection(visibleState: MutableTransitionState<Boolean>) {
    AnimatedVisibility(
        visibleState = visibleState,
        enter = AnimationProvider.enterTransition,
        exit = AnimationProvider.exitTransition
    ) {
        //MeasuringDistanceContent()
    }
}