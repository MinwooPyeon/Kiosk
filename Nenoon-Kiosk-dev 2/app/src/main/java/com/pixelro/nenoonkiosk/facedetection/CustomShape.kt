package com.pixelro.nenoonkiosk.facedetection

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntSize
import androidx.hilt.navigation.compose.hiltViewModel
import com.pixelro.nenoonkiosk.R

@Composable
fun CustomShape(
    viewModel: FaceDetectionViewModel = hiltViewModel()
) {
    var size by remember { mutableStateOf(IntSize.Zero) }
    val leftEyePosition = viewModel.leftEyePosition.collectAsState().value
    val rightEyePosition = viewModel.rightEyePosition.collectAsState().value
    val textBox = viewModel.textBox.collectAsState().value

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .onGloballyPositioned {
                size = it.size
            }
    ) {
        drawCircle(
            color = Color(0xFFFF0000),
            radius = 3f,
            center = Offset(
                (size.width - (leftEyePosition.x)) / 1.5f + 210f,
                leftEyePosition.y / 1.5f + 40f
            )
        )
        drawCircle(
            color = Color(0xFFFF0000),
            radius = 3f,
            center = Offset(
                (size.width - (rightEyePosition.x)) / 1.5f + 130f,
                rightEyePosition.y / 1.5f + 40f
            )
        )

        drawRect(
            color = Color(0xFF00FF00),
            topLeft = Offset(
                (size.width - ((textBox?.right?.toFloat() ?: 0f) + (textBox?.left?.toFloat()
                    ?: 0f)) / 2) / 1.5f + 450f,
                (((textBox?.top?.toFloat() ?: 0f) + (textBox?.bottom?.toFloat()
                    ?: 0f)) / 2) / 1.5f - 350f
            ),
            size = Size(10f, 10f)
        )
        drawRect(
            color = Color(0xFF00FF00),
            topLeft = Offset(
                (size.width + 150 - ((textBox?.right?.toFloat() ?: 0f) + (textBox?.left?.toFloat()
                    ?: 0f)) / 2) / 1.5f + 450f,
                (((textBox?.top?.toFloat() ?: 0f) + (textBox?.bottom?.toFloat()
                    ?: 0f)) / 2) / 1.5f - 350f
            ),
            size = Size(10f, 10f)
        )
        drawRect(
            color = Color(0xFF00FF00),
            topLeft = Offset(
                (size.width - 150 - ((textBox?.right?.toFloat() ?: 0f) + (textBox?.left?.toFloat()
                    ?: 0f)) / 2) / 1.5f + 450f,
                (((textBox?.top?.toFloat() ?: 0f) + (textBox?.bottom?.toFloat()
                    ?: 0f)) / 2) / 1.5f - 350f
            ),
            size = Size(10f, 10f)
        )
        drawRect(
            color = Color(0xFF00FF00),
            topLeft = Offset(
                (size.width - ((textBox?.right?.toFloat() ?: 0f) + (textBox?.left?.toFloat()
                    ?: 0f)) / 2) / 1.5f + 450f,
                150 + (((textBox?.top?.toFloat() ?: 0f) + (textBox?.bottom?.toFloat()
                    ?: 0f)) / 2) / 1.5f - 350f
            ),
            size = Size(10f, 10f)
        )
        drawRect(
            color = Color(0xFF00FF00),
            topLeft = Offset(
                (size.width - ((textBox?.right?.toFloat() ?: 0f) + (textBox?.left?.toFloat()
                    ?: 0f)) / 2) / 1.5f + 450f,
                -150 + (((textBox?.top?.toFloat() ?: 0f) + (textBox?.bottom?.toFloat()
                    ?: 0f)) / 2) / 1.5f - 350f
            ),
            size = Size(10f, 10f)
        )
    }
}