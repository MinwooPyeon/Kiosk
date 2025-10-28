package com.pixelro.nenoonkiosk.core.ui

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pixelro.nenoonkiosk.ui.theme.NenoonKioskTheme
import com.pixelro.nenoonkiosk.ui.theme.titleTextStyle

/**
 * 세로 모드 상단 바 컴포넌트
 *
 * @param title 상단 바에 표시될 제목
 * @param showBackButton 뒤로가기 버튼 표시 여부
 * @param onBackClicked 뒤로가기 버튼 클릭 이벤트
 * @param actions 상단 바 우측에 추가될 액션 버튼들
 * @param containerColor 상단 바 배경색
 * @param contentColor 상단 바 텍스트 및 아이콘 색상
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBarVertical(
    title: String,
    showBackButton: Boolean,
    onBackClicked: () -> Unit,
    actions: @Composable RowScope.() -> Unit = {},
    containerColor: Color = Color.White,
    contentColor: Color = Color.Black,
) {
    CenterAlignedTopAppBar(
        modifier = Modifier.padding(horizontal = 18.dp, vertical = 34.dp),
        title = { Text(text = title, style = titleTextStyle, color = contentColor) },
        navigationIcon = {
            if (showBackButton) {
                BackButtonVertical(onClick = onBackClicked)
            }
        },
        actions = actions,
        colors =
            TopAppBarDefaults.topAppBarColors(
                containerColor = containerColor,
                scrolledContainerColor = containerColor,
                titleContentColor = contentColor,
                actionIconContentColor = contentColor,
                navigationIconContentColor = contentColor,
            ),
    )
}

@Preview(showBackground = true, device = "spec:width=800dp,height=1280dp,dpi=240")
@Composable
private fun HeaderPreview() {
    NenoonKioskTheme {
        TopBarVertical(
            title = "검사 제목",
            showBackButton = true,
            onBackClicked = {},
            actions = {
                HowToButton(onClick = {})
            },
        )
    }
}