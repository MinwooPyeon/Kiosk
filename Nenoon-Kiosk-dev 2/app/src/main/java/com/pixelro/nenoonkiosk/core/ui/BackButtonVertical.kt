package com.pixelro.nenoonkiosk.core.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.ui.theme.LightBlue
import com.pixelro.nenoonkiosk.ui.theme.NenoonKioskTheme
import com.pixelro.nenoonkiosk.ui.theme.neNoon_blue

/**
 * 세로 모드 뒤로가기 버튼 컴포넌트
 *
 * @param onClick 클릭 이벤트
 * @param modifier Modifier
 */
@Composable
fun BackButtonVertical(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Button(
        onClick = onClick,
        modifier =
            modifier
                .size(70.dp),
        shape = RoundedCornerShape(10.dp),
        colors =
            ButtonDefaults.buttonColors(
                containerColor = LightBlue,
                contentColor = neNoon_blue,
            ),
        contentPadding = PaddingValues(0.dp),
    ) {
        Icon(
            painter = painterResource(id = R.drawable.icon_back_black),
            contentDescription = "back",
            tint = neNoon_blue,
            modifier = Modifier.size(32.dp),
        )
    }
}

@Preview(showBackground = true, device = "spec:width=800dp,height=1280dp,dpi=240")
@Composable
private fun BackButtonVerticalPreview() {
    NenoonKioskTheme {
        BackButtonVertical(onClick = {})
    }
}