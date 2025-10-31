package com.pixelro.nenoonkiosk.feature.inspection.macular.amslergrid.progress

import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import com.pixelro.nenoonkiosk.feature.inspection.macular.amslergrid.AmslerGridEvent
import com.pixelro.nenoonkiosk.feature.inspection.macular.amslergrid.AmslerGridUiState
import com.pixelro.nenoonkiosk.feature.inspection.macular.amslergrid.MacularDisorderType
import com.pixelro.nenoonkiosk.feature.inspection.macular.amslergrid.progress.components.AmslerGridAnimatedSection
import com.pixelro.nenoonkiosk.feature.inspection.macular.amslergrid.progress.components.MeasuringDistanceAnimatedSection

@Composable
fun AmslerGridInspectionContent(
    measuringDistanceVisible: MutableTransitionState<Boolean>,
    amslerGridVisible: MutableTransitionState<Boolean>,
    state: AmslerGridUiState,
    onEvent: (AmslerGridEvent) -> Unit,
    modifier: Modifier = Modifier,
    guideVideo: (@Composable () -> Unit)? = null,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xff000000)),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            contentAlignment = Alignment.TopCenter,
            modifier = Modifier.fillMaxSize(),
        ) {
            MeasuringDistanceAnimatedSection(measuringDistanceVisible)

            AmslerGridAnimatedSection(amslerGridVisible,state,onEvent,guideVideo)
        }
    }
}

private fun previewState(
    isBlinkingDone: Boolean = false,
    isDotShowing: Boolean = true,
    isFaceCenter: Boolean = false,
    isTestStarted: Boolean = false,
    isLeftEye: Boolean = true,
    rotX: Float = 0f,
    rotY: Float = 0f,
    shouldPlayGuideVideo: Boolean = false,
    selectedAreas: List<MacularDisorderType> = List(9) { MacularDisorderType.Normal },
    header: String = "미리보기 안내문이 여기에 표시됩니다."
) = AmslerGridUiState(
    isBlinkingDone = isBlinkingDone,
    isDotShowing = isDotShowing,
    isFaceCenter = isFaceCenter,
    isTestStarted = isTestStarted,
    isLeftEye = isLeftEye,
    rotX = rotX,
    rotY = rotY,
    shouldPlayGuideVideo = shouldPlayGuideVideo,
    selectedAreas = selectedAreas,
    headerAnnotated = AnnotatedString(header),
    showMeasuringDistance = true,
    showAmslerGrid = true,
    isSelectTtsDone = true,
)

@Composable
private fun PreviewGuideVideoStub() {
    // 실제 앱에선 ExoPlayer/AndroidView 사용.
    // 프리뷰에서는 단순 색 박스로 대체.
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFEFF3FF))
    )
}

/** 1) 거리측정 화면만 보이는 상태 */
@Preview(showBackground = true, widthDp = 888, heightDp = 1422, name = "Amsler – MeasuringDistance Visible",apiLevel = 34)
@Composable
private fun Preview_Amsler_MeasuringDistance_Only() {
    val measuring = MutableTransitionState(true).apply { targetState = true }
    val grid = MutableTransitionState(false).apply { targetState = false }

    AmslerGridInspectionContent(
        measuringDistanceVisible = measuring,
        amslerGridVisible = grid,
        state = previewState(
            isBlinkingDone = false,
            isFaceCenter = false,
            header = "거리 측정 안내 화면 (프리뷰)"
        ),
        onEvent = {},
        guideVideo = { PreviewGuideVideoStub() },
    )
}

/** 2) 격자 표시 – 깜빡임 단계(아직 얼굴 센터 아님) */
@Preview(showBackground = true, widthDp = 888, heightDp = 1422, name = "Amsler – Grid (Blinking / Not Centered)",apiLevel = 34)
@Composable
private fun Preview_Amsler_Grid_Blinking_NotCentered() {
    val measuring = MutableTransitionState(false).apply { targetState = false }
    val grid = MutableTransitionState(true).apply { targetState = true }

    AmslerGridInspectionContent(
        measuringDistanceVisible = measuring,
        amslerGridVisible = grid,
        state = previewState(
            isBlinkingDone = false,
            isFaceCenter = false,
            rotX = 5f,
            rotY = -7f,
            header = "깜빡임 단계입니다. 화면 중앙의 점을 보세요."
        ),
        onEvent = {},
        guideVideo = { PreviewGuideVideoStub() },
    )
}

/** 3) 얼굴 센터 + 좌안 + 가이드 비디오 재생 */
@Preview(showBackground = true, widthDp = 888, heightDp = 1422, name = "Amsler – Grid (Centered / Left Eye / Guide Video)",apiLevel = 34)
@Composable
private fun Preview_Amsler_Grid_Centered_LeftEye_Guide() {
    val measuring = MutableTransitionState(false).apply { targetState = false }
    val grid = MutableTransitionState(true).apply { targetState = true }

    AmslerGridInspectionContent(
        measuringDistanceVisible = measuring,
        amslerGridVisible = grid,
        state = previewState(
            isBlinkingDone = true,
            isFaceCenter = true,
            isLeftEye = true,
            shouldPlayGuideVideo = true,
            header = "화면 중앙의 점을 바라본 상태입니다. 가이드가 재생됩니다."
        ),
        onEvent = {},
        guideVideo = { PreviewGuideVideoStub() },
    )
}

/** 4) 검사 진행중(오버레이 9분할 표시) + 일부 영역 선택됨 */
@Preview(showBackground = true, widthDp = 888, heightDp = 1422, name = "Amsler – Testing (Overlay / Selected Areas)", apiLevel = 34)
@Composable
private fun Preview_Amsler_Testing_Overlay_Selected() {
    val measuring = MutableTransitionState(false).apply { targetState = false }
    val grid = MutableTransitionState(true).apply { targetState = true }

    val areas = List(9) { index ->
        when (index) {
            1, 4, 7 -> MacularDisorderType.Distorted
            else -> MacularDisorderType.Normal
        }
    }

    AmslerGridInspectionContent(
        measuringDistanceVisible = measuring,
        amslerGridVisible = grid,
        state = previewState(
            isBlinkingDone = true,
            isFaceCenter = true,
            isTestStarted = true,
            isLeftEye = false,
            rotX = 0f,
            rotY = 0f,
            selectedAreas = areas,
            header = "보이는 왜곡 구역을 터치해 선택하세요."
        ),
        onEvent = {},
        guideVideo = null,
    )
}