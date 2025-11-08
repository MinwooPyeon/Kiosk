package com.pixelro.nenoonkiosk.feature.strabismus.common.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.ui.SimpleInspectionSelectionButton

@Composable
fun InspectionList(
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
        SimpleInspectionSelectionButton(
            modifier = Modifier.fillMaxWidth().weight(1f),
            title = stringResource(R.string.phoria_test),
            onClick = onStartPhoria,
            isDone = isPhoriaDone,
            time = 2
        )
        Spacer(Modifier.height(20.dp))
        SimpleInspectionSelectionButton(
            modifier = Modifier.fillMaxWidth().weight(1f),
            title = stringResource(R.string.aniseikonia_test),
            onClick = onStartAniseikonia,
            isDone = isAniseikoniaDone,
            time = 3
        )
        Spacer(Modifier.height(20.dp))
    }
}
