package com.pixelro.nenoonkiosk.feature.print.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pixelro.nenoonkiosk.feature.print.ResultSummary

@Composable
fun ResultsGrid(
    summaries: List<ResultSummary>
) {
    // 2열 레이아웃: 왼쪽 4개, 오른쪽 4개
    Row(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            summaries.take(4).forEach { item ->
                ResultSectionCard(
                    title = item.label,
                    completed = item.completed
                )
            }
        }
        Spacer(Modifier.width(20.dp))
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            summaries.drop(4).take(4).forEach { item ->
                ResultSectionCard(
                    title = item.label,
                    completed = item.completed
                )
            }
        }
    }
}