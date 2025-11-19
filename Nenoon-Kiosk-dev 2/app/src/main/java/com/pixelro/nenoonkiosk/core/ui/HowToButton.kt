package com.pixelro.nenoonkiosk.core.ui

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.util.StringProvider
import com.pixelro.nenoonkiosk.ui.theme.NenoonKioskTheme
import com.pixelro.nenoonkiosk.ui.theme.defaultFont

/**
 * 검사 단계 가이드 보기 버튼 컴포넌트
 *
 * @param onClick 클릭 이벤트
 */
@Composable
fun HowToButton(
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
    ) {
        Text(
            text = StringProvider.getStringComposable(R.string.common_view_test_guide),
            fontFamily = defaultFont,
            fontWeight = FontWeight.Normal,
            fontSize = 18.sp,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun HowToButtonPreview() {
    NenoonKioskTheme {
        HowToButton(onClick = {})
    }
}