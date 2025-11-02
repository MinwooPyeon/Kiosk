package com.pixelro.nenoonkiosk.feature.inspection.visualacuity.process

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Text
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.util.AnimationProvider
import com.pixelro.nenoonkiosk.feature.inspection.visualacuity.process.components.CantSeeButton
import com.pixelro.nenoonkiosk.feature.inspection.visualacuity.process.components.DirectionSelectionButton
import com.pixelro.nenoonkiosk.feature.inspection.visualacuity.process.components.VisualAcuityChartBox
import com.pixelro.nenoonkiosk.ui.theme.White
import com.pixelro.nenoonkiosk.ui.theme.neNoon_blue

@Composable
fun VisualAcuityInspectionCommonContent(
    visualAcuityInspectionCommonContentVisibleState: MutableTransitionState<Boolean>,
    randomList: List<Int>,
    ansNum: Int,
    sightLevel: Int,
    isFaceDetected: Boolean,
    isFacingForward: Boolean,
    onAnswerSelected: (Int, (Float) -> Unit) -> Unit
) {
    AnimatedVisibility(
        visibleState = visualAcuityInspectionCommonContentVisibleState,
        enter = AnimationProvider.enterTransition,
        exit = AnimationProvider.exitTransition
    ) {
        VisualAcuityInspectionScreen(
            randomList = randomList,
            ansNum = ansNum,
            sightLevel = sightLevel,
            isFaceDetected = isFaceDetected,
            isFacingForward = isFacingForward,
            onAnswerSelected = onAnswerSelected
        )
    }
}

@Composable
private fun VisualAcuityInspectionScreen(
    randomList: List<Int>,
    ansNum: Int,
    sightLevel: Int,
    isFaceDetected: Boolean,
    isFacingForward: Boolean,
    onAnswerSelected: (Int, (Float) -> Unit) -> Unit
) {
    var progress by remember { mutableStateOf(0.1f) }
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec
    )

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        VisualAcuityChartBox(
            ansNum = ansNum,
            sightLevel = sightLevel,
            isFaceDetected = isFaceDetected,
            isFacingForward = isFacingForward
        )

        Text(
            modifier = Modifier.padding(top = 40.dp),
            text = stringResource(R.string.visual_acuity_description),
            fontSize = 40.sp,
            color = White,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(20.dp))

        LinearProgressIndicator(
            modifier = Modifier
                .padding(bottom = 20.dp)
                .width(490.dp)
                .height(20.dp),
            progress = animatedProgress,
            color = neNoon_blue
        )

        Row {
            DirectionSelectionButton(
                direction = randomList[0],
                onClick = { onAnswerSelected(0) { progress = it } }
            )
            DirectionSelectionButton(
                direction = randomList[1],
                onClick = { onAnswerSelected(1) { progress = it } }
            )
            DirectionSelectionButton(
                direction = randomList[2],
                onClick = { onAnswerSelected(2) { progress = it } }
            )
        }

        CantSeeButton(
            onClick = { onAnswerSelected(3) { progress = it } }
        )
    }
}
