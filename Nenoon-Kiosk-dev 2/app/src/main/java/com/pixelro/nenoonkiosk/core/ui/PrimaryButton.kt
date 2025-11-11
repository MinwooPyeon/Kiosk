package com.pixelro.nenoonkiosk.core.ui

import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pixelro.nenoonkiosk.core.constants.NavConstants
import com.pixelro.nenoonkiosk.ui.theme.LightGray100
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
 * @param icon 버튼에 표시될 아이콘 (ImageVector, optional)
 * @param iconDrawable 버튼에 표시될 아이콘 (Drawable 리소스 ID, optional)
 */
@Composable
fun PrimaryButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    text: String,
    icon: ImageVector? = null,
    iconDrawable: Int? = null,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val primaryColor = neNoon_blue
    val disabledColor = LightGray100

    val context = LocalContext.current
    val sharedPreferences = remember { context.getSharedPreferences(NavConstants.PREFERENCE_NAME, Context.MODE_PRIVATE) }

    Button(
        onClick = onClick,
        enabled = enabled,
        interactionSource = interactionSource,
        modifier =
            modifier
                .fillMaxWidth()
                .height(if (icon != null || iconDrawable != null) 380.dp else 100.dp),
        shape = RoundedCornerShape(10.dp),
        colors =
            ButtonDefaults.buttonColors(
                backgroundColor = if (isPressed) Color.White else primaryColor,
                contentColor = if (!isPressed) Color.White else primaryColor,
                disabledBackgroundColor = if (isPressed) Color.White else disabledColor,
                disabledContentColor = if (!isPressed) Color.White else disabledColor,
            ),
    ) {
        if (icon != null || iconDrawable != null) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(vertical = 24.dp, horizontal = 12.dp)
                    .fillMaxWidth()
            ) {
                when {
                    icon != null -> {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = if (!isPressed) Color.White else primaryColor,
                            modifier = Modifier.size(100.dp),
                        )
                    }
                    iconDrawable != null -> {
                        Icon(
                            painter = painterResource(id = iconDrawable),
                            contentDescription = null,
                            tint = if (!isPressed) Color.White else primaryColor,
                            modifier = Modifier.size(100.dp),
                        )
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = text,
                    style = buttonTextStyle,
                    color = if (!isPressed) Color.White else primaryColor,
                    textAlign = TextAlign.Center,
                    maxLines = 3,
                    overflow = TextOverflow.Visible,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        } else {
            Text(
                text = text,
                style = buttonTextStyle,
                color = if (!isPressed) Color.White else primaryColor,
                textAlign = TextAlign.Center,
            )
        }
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
