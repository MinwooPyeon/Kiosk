package com.pixelro.nenoonkiosk.feature.termsofservice.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.TextUnit
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.util.StringProvider

@Composable
fun TosDescription(textSize: TextUnit) {
    Text(
        text = StringProvider.getStringComposable(R.string.terms_of_service_description),
        style = TextStyle(fontSize = textSize),
        modifier = Modifier.fillMaxWidth()
    )
}