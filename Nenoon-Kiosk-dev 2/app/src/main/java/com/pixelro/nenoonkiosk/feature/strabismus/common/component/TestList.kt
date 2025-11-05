package com.pixelro.nenoonkiosk.feature.strabismus.common.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.ui.InspectionSelectionButton

@Composable
fun TestList(
    isSenior: Boolean,
    isPhoriaDone: Boolean,
    isAniseikoniaDone: Boolean,
    onStartPhoria: () -> Unit,
    onStartAniseikonia: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 40.dp)
    ) {
        InspectionSelectionButton(
            modifier = Modifier.fillMaxWidth().weight(1f),
            title1 = stringResource(R.string.phoria_test),
            title2 = "",
            onClickMethod = onStartPhoria,
            alignment = Alignment.CenterStart,
            isDone = isPhoriaDone,
            isSenior = isSenior,
            time = 2
        )
        Spacer(Modifier.height(20.dp))
        InspectionSelectionButton(
            modifier = Modifier.fillMaxWidth().weight(1f),
            title1 = stringResource(R.string.aniseikonia_test),
            title2 = "",
            onClickMethod = onStartAniseikonia,
            alignment = Alignment.CenterStart,
            isDone = isAniseikoniaDone,
            isSenior = isSenior,
            time = 3
        )
        Spacer(Modifier.height(20.dp))
    }
}
