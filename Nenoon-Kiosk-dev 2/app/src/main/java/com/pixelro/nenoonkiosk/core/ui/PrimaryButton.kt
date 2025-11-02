package com.pixelro.nenoonkiosk.core.ui

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.ui.theme.LightGray
import com.pixelro.nenoonkiosk.ui.theme.NenoonKioskTheme
import com.pixelro.nenoonkiosk.ui.theme.buttonTextStyle
import com.pixelro.nenoonkiosk.ui.theme.neNoon_blue

/**
 * 기본 버튼 컴포넌트
 *
 * @param onClick 클릭 이벤트
 * @param modifier Modifier
 * @param enabled 활성화 여부
 * @param text 버튼에 표시될 텍스트
 */
@Composable
fun PrimaryButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    text: String,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val primaryColor = neNoon_blue
    val disabledColor = LightGray

    val context = LocalContext.current
    val sharedPreferences = remember { context.getSharedPreferences(NavConstants.PREFERENCE_NAME, Context.MODE_PRIVATE) }
    val savedLanguage = sharedPreferences.getString("language", "defaultLanguage")

    Button(
        onClick = onClick,
        enabled = enabled,
        interactionSource = interactionSource,
        modifier =
            modifier
                .border(
                    border = BorderStroke(2.dp, primaryColor),
                    shape = RoundedCornerShape(10.dp),
                )
                .fillMaxWidth()
                .height(100.dp),
        shape = RoundedCornerShape(10.dp),
        colors =
            ButtonDefaults.buttonColors(
                backgroundColor = if (isPressed) Color.White else primaryColor,
                contentColor = if (!isPressed) Color.White else primaryColor,
                disabledBackgroundColor = if (isPressed) Color.White else disabledColor,
                disabledContentColor = if (!isPressed) Color.White else disabledColor,
            ),
    ) {
        Text(
            text = text,
            style = buttonTextStyle,
            color = if (!isPressed) Color.White else primaryColor,
        )
    }
}

@Preview(showBackground = true, device = "spec:width=1280dp,height=800dp,dpi=240")
@Composable
private fun PrimaryButtonPreview1() {
    NenoonKioskTheme {
        PrimaryButton(
            onClick = {},
            text = "가로 모드",
        )
    }
}

@Preview(showBackground = true, device = "spec:width=800dp,height=1280dp,dpi=240")
@Composable
private fun PrimaryButtonPreview2() {
    NenoonKioskTheme {
        PrimaryButton(
            onClick = {},
            text = "세로 모드",
        )
    }
}

@Preview(showBackground = true, device = "spec:width=1280dp,height=800dp,dpi=240")
@Composable
private fun PrimaryButtonDisabledPreview() {
    NenoonKioskTheme {
        PrimaryButton(
            onClick = {},
            text = "비활성화",
            enabled = false,
        )
    }
}
