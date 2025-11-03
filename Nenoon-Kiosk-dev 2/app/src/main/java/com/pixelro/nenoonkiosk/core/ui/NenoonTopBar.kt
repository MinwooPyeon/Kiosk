package com.pixelro.nenoonkiosk.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pixelro.nenoonkiosk.core.util.isLandscape
import com.pixelro.nenoonkiosk.ui.theme.NenoonKioskTheme
import com.pixelro.nenoonkiosk.ui.theme.titleTextStyle

enum class TopBarOrientation {
    Vertical, Horizontal
}

/**
 * NenoonTopBar - 세로/가로 겸용 상단바 컴포넌트
 *
 * @param title 상단바에 표시할 제목
 * @param orientation TopBarOrientation.Vertical / Horizontal
 * @param showBackButton 뒤로가기 버튼 표시 여부
 * @param onBackClicked 뒤로가기 클릭 이벤트
 * @param actions 오른쪽 액션 슬롯 (예: SettingsButton)
 * @param containerColor 배경색 (기본값: 흰색)
 * @param contentColor 글씨 및 아이콘 색상 (기본값: 검정)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NenoonTopBar(
    title: String,
    orientation: TopBarOrientation = if (isLandscape()) TopBarOrientation.Horizontal else TopBarOrientation.Vertical,
    showBackButton: Boolean = true,
    onBackClicked: () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
    containerColor: Color = Color.White,
    contentColor: Color = Color.Black,
) {
    val barHeight = when (orientation) {
        TopBarOrientation.Vertical -> 130.dp
        TopBarOrientation.Horizontal -> 164.dp
    }

    val actionSlotSize = when (orientation) {
        TopBarOrientation.Vertical -> 64.dp
        TopBarOrientation.Horizontal -> 72.dp
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(containerColor)
    ) {
        CenterAlignedTopAppBar(
            modifier = Modifier
                .height(barHeight)
                .padding(horizontal = 34.dp),
            title = {
                Box(
                    modifier = Modifier.fillMaxHeight(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = title,
                        style = titleTextStyle,
                        color = contentColor
                    )
                }
            },
            navigationIcon = {
                if (showBackButton) {
                    when (orientation) {
                        TopBarOrientation.Vertical -> BackButtonVertical(onClick = onBackClicked)
                        TopBarOrientation.Horizontal -> BackButtonHorizontal(onClick = onBackClicked)
                    }
                }
            },
            actions = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.size(actionSlotSize)
                ) {
                    actions()
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = containerColor,
                scrolledContainerColor = containerColor,
                titleContentColor = contentColor,
                actionIconContentColor = contentColor,
                navigationIconContentColor = contentColor,
            ),
        )
    }
}

/* ---------- PREVIEW ---------- */

@Preview(
    showBackground = true, name = "세로형 TopBar (화이트)",
    backgroundColor = 0xFFFFFFFF, widthDp = 800, heightDp = 130
)
@Composable
private fun NenoonTopBarVerticalPreview() {
    NenoonKioskTheme {
        NenoonTopBar(
            title = "세로형 탑바",
            orientation = TopBarOrientation.Vertical,
            onBackClicked = {},
            containerColor = Color.White,
            contentColor = Color.Black,
            actions = { SettingsButton(toSettingsScreen = {}) }
        )
    }
}

@Preview(
    showBackground = true, name = "가로형 TopBar (검정색)",
    backgroundColor = 0xFFFFFFFF, widthDp = 1280, heightDp = 164
)
@Composable
private fun NenoonTopBarHorizontalPreview() {
    NenoonKioskTheme {
        NenoonTopBar(
            title = "가로형 탑바",
            orientation = TopBarOrientation.Horizontal,
            onBackClicked = {},
            containerColor = Color.Black,
            contentColor = Color.White,
            actions = { SettingsButton(toSettingsScreen = {}) }
        )
    }
}
