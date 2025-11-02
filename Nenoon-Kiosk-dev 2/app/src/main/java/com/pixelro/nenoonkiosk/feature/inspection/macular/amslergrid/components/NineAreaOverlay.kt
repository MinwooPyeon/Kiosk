package com.pixelro.nenoonkiosk.feature.inspection.macular.amslergrid.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.pixelro.nenoonkiosk.feature.inspection.macular.amslergrid.MacularDisorderType

@Composable
fun NineAreaOverlay(
    areas: List<MacularDisorderType>,
    onPress: (Offset) -> Unit,
) {
    Column(
        modifier = Modifier
            .padding(40.dp)
            .width(600.dp)
            .height(600.dp)
            .pointerInput(Unit) {
                detectTapGestures(onPress = { onPress(it) })
            },
    ) {
        for (i in 0..2) {
            Row(modifier = Modifier.weight(1f)) {
                for (j in (i * 3)..(i * 3 + 2)) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .background(
                                color = when (areas.getOrNull(j)) {
                                    MacularDisorderType.Normal, null -> Color(0x00000000)
                                    else -> Color(0x550000ff)
                                },
                            ),
                    )
                }
            }
        }
    }
}