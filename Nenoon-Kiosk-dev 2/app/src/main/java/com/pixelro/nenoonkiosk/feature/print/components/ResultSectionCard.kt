package com.pixelro.nenoonkiosk.feature.print.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.util.StringProvider
import com.pixelro.nenoonkiosk.ui.theme.bodyTextStyle
import com.pixelro.nenoonkiosk.ui.theme.inputTextStyle

@Composable
fun ResultSectionCard(
    title: String,
    completed: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp)
            .border(1.dp, colorResource(R.color.gray2), MaterialTheme.shapes.small)
            .padding(15.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = title,
            style = bodyTextStyle,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = if (completed)
                StringProvider.getStringComposable(R.string.result_print_screen_test_completed)
            else
                StringProvider.getStringComposable(R.string.result_print_screen_test_not_conducted),
            style = inputTextStyle,
            color = if (completed) colorResource(R.color.main) else colorResource(R.color.error)
        )
    }
}