package com.pixelro.nenoonkiosk.feature.inspection.dementia.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.util.StringProvider
import com.pixelro.nenoonkiosk.feature.inspection.dementia.DementiaAnswer

@Composable
fun AnswerRow(
    selected: DementiaAnswer?,
    onClickYes: () -> Unit,
    onClickNo: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        AnswerCard(
            text = StringProvider.getStringComposable(R.string.yes),
            selected = selected == DementiaAnswer.Yes,
            onClick = onClickYes,
            biasPaddingStart = 0.dp
        )
        AnswerCard(
            text = StringProvider.getStringComposable(R.string.no),
            selected = selected == DementiaAnswer.No,
            onClick = onClickNo,
            biasPaddingStart = 20.dp
        )
    }
}
