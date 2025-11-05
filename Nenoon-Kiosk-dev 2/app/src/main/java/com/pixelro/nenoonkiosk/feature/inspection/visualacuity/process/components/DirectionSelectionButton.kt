package com.pixelro.nenoonkiosk.feature.inspection.visualacuity.process.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.ui.theme.White

@Composable
fun DirectionSelectionButton(
    direction: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.padding(10.dp),
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    color = White,
                    shape = RoundedCornerShape(8.dp),
                )
                .clickable { onClick() },
            contentAlignment = Alignment.Center,
        ) {
            Image(
                modifier = Modifier
                    .padding(10.dp)
                    .size(50.dp),
                imageVector = ImageVector.vectorResource(
                    id = getDirectionDrawableId(direction),
                ),
                contentDescription = "Direction $direction",
            )
        }
    }
}

private fun getDirectionDrawableId(direction: Int): Int {
    return when (direction) {
        2 -> R.drawable.two
        3 -> R.drawable.three
        4 -> R.drawable.four
        5 -> R.drawable.five
        6 -> R.drawable.six
        else -> R.drawable.seven
    }
}
