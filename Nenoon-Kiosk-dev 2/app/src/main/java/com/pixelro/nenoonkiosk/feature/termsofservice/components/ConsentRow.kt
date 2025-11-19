package com.pixelro.nenoonkiosk.feature.termsofservice.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.util.StringProvider

@Composable
fun ConsentRow(
    description: String,
    question: String,
    accepted: Boolean?,
    onAcceptedChange: (Boolean?) -> Unit,
    textSize: TextUnit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = description, style = TextStyle(fontSize = textSize))
        Spacer(Modifier.height(8.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = question,
                style = TextStyle(fontSize = textSize),
                modifier = Modifier.weight(1f)
            )
            // 동의
            Text(
                text = StringProvider.getStringComposable(R.string.checkbox_agree),
                color = Color(0xff1d71e1),
                style = TextStyle(fontSize = textSize)
            )
            Checkbox(
                checked = accepted == true,
                onCheckedChange = { onAcceptedChange(true) },
                colors = CheckboxDefaults.colors(
                    checkedColor = Color(0xff1d71e1),
                    uncheckedColor = Color(0xff1d71e1),
                    checkmarkColor = Color.White
                )
            )
            Spacer(Modifier.width(12.dp))
            // 비동의
            Text(
                text = StringProvider.getStringComposable(R.string.checkbox_disagree),
                color = Color(0xff1d71e1),
                style = TextStyle(fontSize = textSize)
            )
            Checkbox(
                checked = accepted == false,
                onCheckedChange = { onAcceptedChange(false) },
                colors = CheckboxDefaults.colors(
                    checkedColor = Color(0xff1d71e1),
                    uncheckedColor = Color(0xff1d71e1),
                    checkmarkColor = Color.White
                )
            )
        }
    }
}