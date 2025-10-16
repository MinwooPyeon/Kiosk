package com.pixelro.nenoonkiosk.feature.components

import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.constants.NavConstants

@Composable
fun PrimaryButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    text: String,
) {

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val primaryColor = colorResource(R.color.main)
    val disabledColor = colorResource(R.color.disabled)

    val context = LocalContext.current
    val sharedPreferences = remember { context.getSharedPreferences(NavConstants.PREFERENCE_NAME, Context.MODE_PRIVATE) }
    val savedLanguage = sharedPreferences.getString("language", "defaultLanguage")

    Button(
        onClick = onClick,
        enabled = enabled,
        interactionSource = interactionSource,
        modifier = modifier
            .border(
                border = BorderStroke(2.dp, primaryColor),
                shape = RoundedCornerShape(8.dp)
            )
            .fillMaxWidth()
            .height(100.dp),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = if (isPressed) Color.White else primaryColor,
            contentColor = if (!isPressed) Color.White else primaryColor,
            disabledBackgroundColor = if (isPressed) Color.White else disabledColor,
            disabledContentColor = if (!isPressed) Color.White else disabledColor
        )
    ) {
        Text(
            text = text,
            fontSize = if (savedLanguage == "en") 32.sp else 40.sp,
            fontWeight = FontWeight.Bold,
            color = if (!isPressed) Color.White else primaryColor
        )
    }
}