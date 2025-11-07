package com.pixelro.nenoonkiosk.core.ui

import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pixelro.nenoonkiosk.core.constants.NavConstants
import com.pixelro.nenoonkiosk.ui.theme.LightBlue
import com.pixelro.nenoonkiosk.ui.theme.LightGray100
import com.pixelro.nenoonkiosk.ui.theme.NenoonKioskTheme
import com.pixelro.nenoonkiosk.ui.theme.White
import com.pixelro.nenoonkiosk.ui.theme.buttonTextStyle
import com.pixelro.nenoonkiosk.ui.theme.neNoon_blue

/**
 * 보조 버튼 컴포넌트 (하늘색 배경, 파란색 텍스트)
 *
 * @param onClick 클릭 이벤트
 * @param text 버튼에 표시될 텍스트
 * @param modifier Modifier
 * @param enabled 활성화 여부
 * @param icon 버튼에 표시될 아이콘 (ImageVector, optional)
 * @param iconDrawable 버튼에 표시될 아이콘 (Drawable 리소스 ID, optional)
 * @param iconTint 아이콘 색상 (기본값: neNoon_blue)
 */
@Composable
fun SecondaryButton(
    onClick: () -> Unit,
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: ImageVector? = null,
    iconDrawable: Int? = null,
    iconTint: Color = neNoon_blue,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val primaryColor = neNoon_blue

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
                containerColor = if (isPressed) primaryColor else LightBlue,
                contentColor = if (isPressed) White else primaryColor,
                disabledContainerColor = LightGray100,
                disabledContentColor = White,
            ),
    ) {
        if (icon != null || iconDrawable != null) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                when {
                    icon != null -> {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = iconTint,
                            modifier = Modifier.size(32.dp),
                        )
                    }
                    iconDrawable != null -> {
                        Icon(
                            painter = painterResource(id = iconDrawable),
                            contentDescription = null,
                            tint = iconTint,
                            modifier = Modifier.size(32.dp),
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = text,
                    style = buttonTextStyle,
                )
            }
        } else {
            Text(
                text = text,
                style = buttonTextStyle,
            )
        }
    }
}

@Preview(showBackground = true, device = "spec:width=1280dp,height=800dp,dpi=240")
@Composable
private fun SecondaryButtonPreview1() {
    NenoonKioskTheme {
        SecondaryButton(
            onClick = {},
            text = "가로 모드",
        )
    }
}

@Preview(showBackground = true, device = "spec:width=800dp,height=1280dp,dpi=240")
@Composable
private fun SecondaryButtonPreview2() {
    NenoonKioskTheme {
        SecondaryButton(
            onClick = {},
            text = "세로 모드",
        )
    }
}

@Preview(showBackground = true, device = "spec:width=1280dp,height=800dp,dpi=240")
@Composable
private fun SecondaryButtonDisabledPreview() {
    NenoonKioskTheme {
        SecondaryButton(
            onClick = {},
            text = "비활성화",
            enabled = false,
        )
    }
}