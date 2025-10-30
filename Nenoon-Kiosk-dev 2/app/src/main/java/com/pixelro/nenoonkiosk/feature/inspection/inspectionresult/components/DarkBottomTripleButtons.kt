package com.pixelro.nenoonkiosk.feature.inspection.inspectionresult.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.pixelro.nenoonkiosk.R

@Composable
fun DarkBottomTripleButtons(
    onRetest: () -> Unit,
    onToBeginning: () -> Unit,
    onLogout: () -> Unit,
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        )  {
            BottomWhiteButton(textRes = R.string.retest, onClick = onRetest)
            BottomWhiteButton(textRes = R.string.to_beginning, onClick = onToBeginning)
            BottomWhiteButton(textRes = R.string.settings_signout, onClick = onLogout)
        }
    }
}