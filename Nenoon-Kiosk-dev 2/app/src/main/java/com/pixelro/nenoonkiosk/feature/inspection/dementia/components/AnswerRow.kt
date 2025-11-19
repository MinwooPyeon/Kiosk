package com.pixelro.nenoonkiosk.feature.inspection.dementia.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.feature.inspection.dementia.DementiaViewModel


@Composable
fun AnswerRow(
    selected: DementiaViewModel.DementiaAnswer?,
    onClickYes: () -> Unit,
    onClickNo: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterHorizontally)
    ) {
        AnswerCard(
            text = stringResource(R.string.yes),
            selected = selected == DementiaViewModel.DementiaAnswer.Yes,
            onClick = onClickYes,
        )
        AnswerCard(
            text = stringResource(R.string.no),
            selected = selected == DementiaViewModel.DementiaAnswer.No,
            onClick = onClickNo,
        )
    }
}