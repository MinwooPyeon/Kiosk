package com.pixelro.nenoonkiosk.feature.admin.advertisement.sidebar

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.feature.admin.AdminTab
import com.pixelro.nenoonkiosk.feature.admin.getTitle
import com.pixelro.nenoonkiosk.ui.theme.Gray
import com.pixelro.nenoonkiosk.ui.theme.LightGray100
import com.pixelro.nenoonkiosk.ui.theme.Red
import com.pixelro.nenoonkiosk.ui.theme.White
import com.pixelro.nenoonkiosk.ui.theme.defaultFont
import com.pixelro.nenoonkiosk.ui.theme.neNoon_blue


@Composable
fun SidebarArea(
    selectedTab: AdminTab,
    onTabSelected: (AdminTab) -> Unit,
    isOutClick: () -> Unit
) {
    var showExitDialog by remember { mutableStateOf(false) }

    BoxWithConstraints(
        modifier = Modifier
            .background(Color.White)
    ) {
        val isPortrait = maxHeight > maxWidth

        if (isPortrait) {
            PortraitLayout(
                selectedTab = selectedTab,
                onTabSelected = onTabSelected,
                onExitClick = { showExitDialog = true }
            )
        } else {
            LandscapeLayout(
                selectedTab = selectedTab,
                onTabSelected = onTabSelected,
                onExitClick = { showExitDialog = true }
            )
        }

        // 나가기 확인 다이얼로그
        if (showExitDialog) {
            AlertDialog(
                onDismissRequest = { showExitDialog = false },
                title = {
                    Text(
                        text = stringResource(R.string.admin_exit_confirm_title),
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        fontFamily = defaultFont,
                        color = Color.Black
                    )
                },
                text = {
                    Text(
                        stringResource(R.string.admin_exit_confirm_message), fontSize = 22.sp,
                        fontFamily = defaultFont,
                        color = Color.Black
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showExitDialog = false
                            isOutClick()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = neNoon_blue
                        )
                    ) {
                        Text(
                            stringResource(R.string.admin_confirm),
                            fontFamily = defaultFont
                        )
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showExitDialog = false }
                    ) {
                        Text(
                            stringResource(R.string.admin_cancel), color = Gray,
                            fontFamily = defaultFont
                        )
                    }
                }
            )
        }
    }
}

@Composable
private fun PortraitLayout(
    selectedTab: AdminTab,
    onTabSelected: (AdminTab) -> Unit,
    onExitClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .drawBehind {
                drawLine(
                    color = LightGray100,
                    start = Offset(0f, size.height),
                    end = Offset(size.width, size.height),
                    strokeWidth = 1.dp.toPx()
                )
            }
            .padding(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 관리자 대시보드 텍스트
            Text(
                text = stringResource(R.string.admin_dashboard),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            LazyRow(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 28.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(AdminTab.values()) { tab ->
                    Button(
                        onClick = { onTabSelected(tab) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selectedTab == tab) neNoon_blue else Color.Transparent,
                            contentColor = if (selectedTab == tab) White else Gray
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = tab.getTitle(), fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // 관리자 나가기 버튼
            OutlinedButton(
                onClick = onExitClick,
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Red
                ),
                border = BorderStroke(1.dp, Color.Red),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = stringResource(R.string.admin_exit),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun LandscapeLayout(
    selectedTab: AdminTab,
    onTabSelected: (AdminTab) -> Unit,
    onExitClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .width(220.dp)
            .fillMaxHeight()
            .drawBehind {
                drawLine(
                    color = LightGray100,
                    start = Offset(size.width, 0f),
                    end = Offset(size.width, size.height),
                    strokeWidth = 1.dp.toPx()
                )
            }
            .padding(20.dp)
    ) {
        // 헤더
        Text(
            text = stringResource(R.string.admin_dashboard),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(bottom = 24.dp)
                .fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        // 탭 버튼
        LazyColumn(
            modifier = Modifier.weight(1f), // 필요시 높이 조절
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(AdminTab.values()) { tab ->
                Button(
                    onClick = { onTabSelected(tab) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedTab == tab) neNoon_blue else Color.Transparent,
                        contentColor = if (selectedTab == tab) White else Gray
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = tab.icon,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = tab.getTitle(), fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // 관리자 나가기 버튼
        OutlinedButton(
            onClick = onExitClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = Red
            ),
            border = BorderStroke(1.dp, Color.Red),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = stringResource(R.string.admin_exit),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF8F8F8, widthDp = 800, heightDp = 80)
@Composable
private fun SidebarAreaPortraitPreview() {
    PortraitLayout(
        selectedTab = AdminTab.AD_MANAGEMENT,
        onTabSelected = {},
        onExitClick = {}
    )
}

@Preview(showBackground = true, backgroundColor = 0xFFF8F8F8, widthDp = 220, heightDp = 1280)
@Composable
private fun SidebarAreaLandscapePreview() {
    LandscapeLayout(
        selectedTab = AdminTab.PASSWORD_MANAGEMENT,
        onTabSelected = {},
        onExitClick = {}
    )
}