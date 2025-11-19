package com.pixelro.nenoonkiosk.feature.inspection.macular.mchart.progress

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.datasource.RawResourceDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.ui.PrimaryButton
import com.pixelro.nenoonkiosk.core.util.isLandscape
import com.pixelro.nenoonkiosk.feature.inspection.macular.mchart.MChartInspectionUiState
import com.pixelro.nenoonkiosk.ui.theme.NenoonKioskTheme
import com.pixelro.nenoonkiosk.ui.theme.White
import com.pixelro.nenoonkiosk.ui.theme.neNoon_blue
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
@Composable
fun MChartInspectionContent(
    uiState: MChartInspectionUiState,
    exoPlayer: ExoPlayer?,
    onStraightClick: () -> Unit,
    onBentClick: () -> Unit,
    onTTSDone: () -> Unit,
) {
    val isLandscape = isLandscape()

    if (isLandscape) {
        LandscapeMChartContent(
            uiState = uiState,
            exoPlayer = exoPlayer,
            onStraightClick = onStraightClick,
            onBentClick = onBentClick
        )
    } else {
        PortraitMChartContent(
            uiState = uiState,
            exoPlayer = exoPlayer,
            onStraightClick = onStraightClick,
            onBentClick = onBentClick
        )
    }
}

@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
@Composable
private fun PortraitMChartContent(
    uiState: MChartInspectionUiState,
    exoPlayer: ExoPlayer?,
    onStraightClick: () -> Unit,
    onBentClick: () -> Unit,
) {
    val inPreview = LocalInspectionMode.current
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // 상단 안내문
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 40.dp, vertical = 20.dp),
            text = buildAnnotatedString {
                append(stringResource(R.string.mchart_test_description_1) + " ")
                withStyle(SpanStyle(neNoon_blue, fontWeight = FontWeight.Bold)) {
                    append(stringResource(R.string.mchart_test_description_2) + " ")
                }
                append(stringResource(R.string.mchart_test_description_3))
                withStyle(SpanStyle(neNoon_blue, fontWeight = FontWeight.Bold)) {
                    append(" " + stringResource(R.string.mchart_test_description_4))
                }
                append(stringResource(R.string.mchart_test_description_5))
            },
            fontSize = 32.sp,
            color = White,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center
        )

        // 검사 시표 (영상 or 이미지)
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(700.dp)
                .background(White, RoundedCornerShape(12.dp))
        ) {
            ChartContent(
                uiState = uiState,
                exoPlayer = exoPlayer,
                inPreview = inPreview,
                coroutineScope = coroutineScope
            )
        }

        // 하단 영역
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 40.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (!uiState.isTTSSpeaking) {
                PrimaryButton(
                    onClick = onStraightClick,
                    text = stringResource(R.string.mchart_test_content_straight),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                PrimaryButton(
                    onClick = onBentClick,
                    text = stringResource(R.string.mchart_test_content_bent),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                )
            } else if (uiState.isLeftEye) {
                Text(
                    text = buildAnnotatedString {
                        append(stringResource(R.string.presbyopia_video_guide_1))
                        withStyle(SpanStyle(color = neNoon_blue, fontWeight = FontWeight.Bold)) {
                            append(" " + stringResource(R.string.presbyopia_video_guide_2))
                        }
                        append(stringResource(R.string.presbyopia_video_guide_3))
                    },
                    fontWeight = FontWeight.Bold,
                    fontSize = 45.sp,
                    textAlign = TextAlign.Center,
                    color = White
                )
            }
        }
    }
}

@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
@Composable
private fun LandscapeMChartContent(
    uiState: MChartInspectionUiState,
    exoPlayer: ExoPlayer?,
    onStraightClick: () -> Unit,
    onBentClick: () -> Unit,
) {
    val inPreview = LocalInspectionMode.current
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // 상단 안내문
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp),
            text = buildAnnotatedString {
                append(stringResource(R.string.mchart_test_description_1) + " ")
                withStyle(SpanStyle(neNoon_blue, fontWeight = FontWeight.Bold)) {
                    append(stringResource(R.string.mchart_test_description_2) + " ")
                }
                append(stringResource(R.string.mchart_test_description_3))
                withStyle(SpanStyle(neNoon_blue, fontWeight = FontWeight.Bold)) {
                    append(" " + stringResource(R.string.mchart_test_description_4))
                }
                append(stringResource(R.string.mchart_test_description_5))
            },
            fontSize = 28.sp,
            color = White,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center
        )

        // 검사 시표 (영상 or 이미지)
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(500.dp)
                .background(White, RoundedCornerShape(12.dp))
        ) {
            ChartContent(
                uiState = uiState,
                exoPlayer = exoPlayer,
                inPreview = inPreview,
                coroutineScope = coroutineScope
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 하단 영역
        if (!uiState.isTTSSpeaking) {
            Column(
                modifier = Modifier.fillMaxWidth(0.6f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                PrimaryButton(
                    onClick = onStraightClick,
                    text = stringResource(R.string.mchart_test_content_straight),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                )
                PrimaryButton(
                    onClick = onBentClick,
                    text = stringResource(R.string.mchart_test_content_bent),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                )
            }
        } else if (uiState.isLeftEye) {
            Text(
                modifier = Modifier.padding(top = 20.dp),
                text = buildAnnotatedString {
                    append(stringResource(R.string.presbyopia_video_guide_1))
                    withStyle(SpanStyle(color = neNoon_blue, fontWeight = FontWeight.Bold)) {
                        append(" " + stringResource(R.string.presbyopia_video_guide_2))
                    }
                    append(stringResource(R.string.presbyopia_video_guide_3))
                },
                fontWeight = FontWeight.Bold,
                fontSize = 36.sp,
                textAlign = TextAlign.Center,
                color = White
            )
        }
    }
}

@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
@Composable
private fun ChartContent(
    uiState: MChartInspectionUiState,
    exoPlayer: ExoPlayer?,
    inPreview: Boolean,
    coroutineScope: kotlinx.coroutines.CoroutineScope,
) {
    val showVideo =
        !inPreview &&
                uiState.isTTSSpeaking &&
                !uiState.isTesting &&
                uiState.isLeftEye &&
                exoPlayer != null

    if (showVideo) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { context ->
                PlayerView(context).apply {
                    useController = false
                    resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                    player = exoPlayer
                    exoPlayer.apply {
                        setMediaItem(
                            MediaItem.fromUri(
                                RawResourceDataSource.buildRawResourceUri(R.raw.mchart_video_2)
                            )
                        )
                        prepare()
                        pause()
                        coroutineScope.launch {
                            delay(4000)
                            play()
                        }
                    }
                }
            }
        )
    } else {
        Image(
            modifier = Modifier
                .padding(start = 40.dp, end = 40.dp, top = 10.dp)
                .fillMaxSize()
                .rotate(if (uiState.isVertical) 0f else 90f),
            painter = painterResource(id = uiState.imageId),
            contentDescription = null,
        )
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF000000,
    apiLevel = 34,
    widthDp = 800,
    heightDp = 1280,
    name = "MChart - Portrait"
)
@Composable
private fun MChartContentPreview_Portrait() {
    val dummyState = MChartInspectionUiState(
        isLeftEye = true,
        isVertical = true,
        currentLevel = 5,
        imageId = R.drawable.mchart_0_0,
        isTTSSpeaking = false,
        isTesting = true
    )

    NenoonKioskTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            PortraitMChartContent(
                uiState = dummyState,
                exoPlayer = null,
                onStraightClick = {},
                onBentClick = {}
            )
        }
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF000000,
    apiLevel = 34,
    widthDp = 1422,
    heightDp = 888,
    name = "MChart - Landscape"
)
@Composable
private fun MChartContentPreview_Landscape() {
    val dummyState = MChartInspectionUiState(
        isLeftEye = true,
        isVertical = true,
        currentLevel = 5,
        imageId = R.drawable.mchart_0_0,
        isTTSSpeaking = false,
        isTesting = true
    )

    NenoonKioskTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            LandscapeMChartContent(
                uiState = dummyState,
                exoPlayer = null,
                onStraightClick = {},
                onBentClick = {}
            )
        }
    }
}
