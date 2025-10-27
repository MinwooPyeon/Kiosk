package com.pixelro.nenoonkiosk.feature.termsofservice.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.util.StringProvider
import com.pixelro.nenoonkiosk.feature.termsofservice.base.TermsTableData

@Composable
fun TermsTable(
    data: TermsTableData,
    textSize: TextUnit
) {
    val strokeWidthDp = 1.dp
    val cellPaddingDp = 8.dp
    val density = LocalDensity.current
    val strokeWidthPx = with(density) { strokeWidthDp.toPx() }

    var totalHeight by remember { mutableFloatStateOf(0f) }
    var headerHeight by remember { mutableFloatStateOf(0f) }
    var totalWidth by remember { mutableFloatStateOf(0f) }

    Text(
        text = data.label,
        style = TextStyle(fontSize = textSize, fontWeight = FontWeight.SemiBold),
        modifier = Modifier.fillMaxWidth()
    )

    Spacer(Modifier.height(8.dp))

    Column(
        modifier = Modifier
            .onGloballyPositioned {
                totalHeight = it.size.height.toFloat()
                totalWidth = it.size.width.toFloat()
            }
            .drawWithContent {
                drawContent()
                // header 아래 가로선
                drawLine(
                    color = Color(0xff000000),
                    start = Offset(0f, headerHeight),
                    end = Offset(totalWidth, headerHeight),
                    strokeWidth = strokeWidthPx,
                )
                // 세로선 2개(1/4, 3/4) 혹은 균등(1/3, 2/3)
                val x1 = totalWidth * if (data.evenly) 0.33f else 0.25f
                val x2 = totalWidth * if (data.evenly) 0.67f else 0.75f
                drawLine(Color(0xff000000), Offset(x1, 0f), Offset(x1, totalHeight), strokeWidthPx)
                drawLine(Color(0xff000000), Offset(x2, 0f), Offset(x2, totalHeight), strokeWidthPx)
            }
            .fillMaxWidth()
            .border(1.dp, Color.Black, RectangleShape)
            .background(Color.White)
    ) {
        // Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xffebebeb))
                .onGloballyPositioned { headerHeight = it.size.height.toFloat() }
        ) {
            TableHeaderCell(
                text = StringProvider.getStringComposable(R.string.table_header_item),
                textSize = textSize,
                padding = cellPaddingDp,
                modifier = Modifier.weight(1F)
            )
            TableHeaderCell(
                text = StringProvider.getStringComposable(R.string.table_header_purpose),
                textSize = textSize,
                padding = cellPaddingDp,
                modifier = Modifier.weight(weight = if (data.evenly) 1f else 2f)
            )
            TableHeaderCell(
                text = StringProvider.getStringComposable(R.string.table_header_period),
                textSize = textSize,
                padding = cellPaddingDp,
                modifier = Modifier.weight(1F)
            )
        }

        // Body
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            TableBodyCell(
                data.column1, textSize, cellPaddingDp,
                modifier = Modifier.weight(1F)
            )
            TableBodyCell(
                data.column2, textSize, cellPaddingDp,
                modifier = Modifier.weight(
                    weight = if (data.evenly) 1f else 2f
                ),
            )
            TableBodyCell(
                data.column3, textSize, cellPaddingDp,
                modifier = Modifier.weight(1F)
            )
        }
    }
}