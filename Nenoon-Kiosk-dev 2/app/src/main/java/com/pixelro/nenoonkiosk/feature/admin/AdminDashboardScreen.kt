package com.pixelro.nenoonkiosk.feature.admin

import android.R.attr.contentDescription
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.RemoveCircleOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pixelro.nenoonkiosk.ui.theme.Green
import com.pixelro.nenoonkiosk.ui.theme.LightGray
import com.pixelro.nenoonkiosk.ui.theme.LightGreen
import com.pixelro.nenoonkiosk.ui.theme.LightYellow
import com.pixelro.nenoonkiosk.ui.theme.Red
import com.pixelro.nenoonkiosk.ui.theme.White
import com.pixelro.nenoonkiosk.ui.theme.Yellow200
import com.pixelro.nenoonkiosk.ui.theme.neNoon_blue

@Composable
fun AdminDashboardScreen() {
    var selectedTab by remember { mutableStateOf(AdminTab.AD_MANAGEMENT) }

    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(White)
    ) {
        // 좌측 사이드바
        AdminSidebar(
            selectedTab = selectedTab,
            onTabSelected = { selectedTab = it }
        )

        // 메인 컨텐츠 영역
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(White)
        ) {
            when (selectedTab) {
                AdminTab.AD_MANAGEMENT -> AdManagementContent()
                // 다른 탭들 추가 가능
            }
        }
    }
}

enum class AdminTab(val title: String, val icon: ImageVector) {
    AD_MANAGEMENT("광고 관리", Icons.Default.Image)
}

@Composable
fun AdminSidebar(
    selectedTab: AdminTab,
    onTabSelected: (AdminTab) -> Unit
) {
    Column(
        modifier = Modifier
            .width(200.dp)
            .fillMaxHeight()
            .background(Color.White)
            .drawBehind {
                drawLine(
                    color = LightGray,
                    start = Offset(size.width, 0f),
                    end = Offset(size.width, size.height),
                    strokeWidth = 1.dp.toPx()
                )
            }
            .padding(20.dp)

    ) {
        // 헤더
        Text(
            text = "관리자 대시보드",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 24.dp).fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        // 탭 버튼
        AdminTab.values().forEach { tab ->
            Button(
                onClick = { onTabSelected(tab) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedTab == tab) neNoon_blue else Color.Transparent,
                    contentColor = if (selectedTab == tab) Color.White else Color.Black
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
                    Text(text = tab.title, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // 관리자 나가기 버튼
        OutlinedButton(
            onClick = { },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = Red
            ),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color.Red),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = "관리자 나가기",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun AdManagementContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {

        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 4.dp)){
            // 헤더
            Text(
                text = "광고 이미지 관리",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
            )

            Spacer(modifier = Modifier.width(8.dp))

            Icon(
                imageVector = Icons.Default.Build,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
        }

        Text(
            text = "광고에 사용할 이미지를 관리하고 선택하세요",
            fontSize = 16.sp,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // 좌측: 광고 위치 선택
            AdLocationSelection(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            )

            // 우측: 광고 이미지 목록
            AdImageList(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            )
        }
    }
}

@Composable
fun AdLocationSelection(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 광고 위치 선택 섹션
        AdLocationOptions()

        // 광고 위치 미리보기 섹션
        AdLocationPreview()
    }
}

@Composable
fun AdLocationOptions() {
    Column(
        modifier = Modifier
            .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(12.dp))
            .padding(24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "광고 위치를 선택해주세요",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 검사리스트 화면
        AdLocationOption(
            title = "검사리스트 화면",
            subtitle = "혈압, 눈, 사시 등",
            badge = "이미지",
            badgeColor = LightGreen,
            badgeTextColor = Green
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 스크린세이버
        AdLocationOption(
            title = "스크린세이버",
            subtitle = "터치하세요 화면",
            badge = "동영상",
            badgeColor = LightYellow,
            badgeTextColor = Yellow200
        )
    }
}

@Composable
fun AdLocationPreview() {
    Column(
        modifier = Modifier
            .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(12.dp))
            .padding(24.dp)
    ) {
        Text(
            text = "광고 위치",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // 미리보기 이미지 영역
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFFF5F5F5)),
            contentAlignment = Alignment.Center
        ) {
            // 실제로는 이미지를 표시
            Text(
                text = "광고 위치 미리보기",
                color = Color.Gray
            )
        }
    }
}

@Composable
fun AdLocationOption(
    title: String,
    subtitle: String,
    badge: String,
    badgeColor: Color,
    badgeTextColor: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(12.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = false,
            onClick = { }
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = Color.Gray
            )
        }

        Box(
            modifier = Modifier
                .background(badgeColor, RoundedCornerShape(4.dp))
                .padding(horizontal = 12.dp, vertical = 4.dp)
        ) {
            Text(
                text = badge,
                fontSize = 12.sp,
                color = badgeTextColor,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun AdImageList(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(12.dp))
            .padding(24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "광고 이미지 목록",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Button(
                onClick = { },
                colors = ButtonDefaults.buttonColors(
                    containerColor = neNoon_blue
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("추가")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 이미지 목록
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(7) { index ->
                AdImageItem(
                    fileName = "사진이름.jpg",
                    onDelete = { },
                    onReorder = { }
                )
            }
        }
    }
}

@Composable
fun AdImageItem(
    fileName: String,
    imageUri: String? = null,
    onDelete: () -> Unit,
    onReorder: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(8.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 삭제 버튼 (왼쪽)
        IconButton(
            onClick = onDelete,
            modifier = Modifier.size(40.dp)
        ) {
            Icon(
                imageVector = Icons.Default.RemoveCircleOutline,
                contentDescription = "삭제",
                tint = Color.Gray
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // 이미지 썸네일과 파일명 (수직 배치)
        Column(
            modifier = Modifier.weight(1f)
        ) {
            // 이미지 썸네일
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFE0E0E0)),
                contentAlignment = Alignment.Center
            ) {
                if (imageUri != null) {
//                    AsyncImage(
//                        model = imageUri,
//                        contentDescription = fileName,
//                        modifier = Modifier.fillMaxSize(),
//                        contentScale = ContentScale.Crop
//                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Image,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 파일명
            Text(
                text = fileName,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // 순서 변경 버튼 (오른쪽)
        IconButton(
            onClick = onReorder,
            modifier = Modifier.size(40.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Menu,
                contentDescription = "순서 변경",
                tint = Color.Gray
            )
        }
    }
}


@Preview(showBackground = true, widthDp = 1280, heightDp = 800)
@Composable
fun AdminDashboardScreenPreview() {
    AdminDashboardScreen()
}