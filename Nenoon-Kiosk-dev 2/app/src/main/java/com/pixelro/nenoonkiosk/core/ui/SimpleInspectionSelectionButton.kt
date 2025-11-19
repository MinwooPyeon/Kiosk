package com.pixelro.nenoonkiosk.core.ui


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.ui.theme.Black
import com.pixelro.nenoonkiosk.ui.theme.Gray
import com.pixelro.nenoonkiosk.ui.theme.LightGray100
import com.pixelro.nenoonkiosk.ui.theme.LightGray400
import com.pixelro.nenoonkiosk.ui.theme.LightGray500
import com.pixelro.nenoonkiosk.ui.theme.NenoonKioskTheme
import com.pixelro.nenoonkiosk.ui.theme.White
import com.pixelro.nenoonkiosk.ui.theme.neNoon_blue

/**
 * SimpleInspectionCard
 * 검사 항목 카드 (title 1개만 표시)
 *
 * @param title 검사명 (예: "혈압 검사")
 * @param time 소요시간 (예: 2)
 * @param onClick 클릭 이벤트
 * @param modifier Modifier
 */
@Composable
fun SimpleInspectionSelectionButton(
    title: String,
    time: Int,
    onClick: () -> Unit,
    isDone: Boolean,
    modifier: Modifier = Modifier
) {
    // 그라데이션: isDone일 때만 적용
    val gradientColors = if (isDone) {
        listOf(LightGray500, LightGray400)
    } else { neNoon_blue
        listOf(White, White)
    }


    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(
                border = BorderStroke(1.dp, LightGray100),
                shape = RoundedCornerShape(8.dp),
            )
            .shadow(
                elevation = 3.dp,
                shape = RoundedCornerShape(8.dp),
                ambientColor = Black.copy(alpha = 0.25f),
                spotColor = Black.copy(alpha = 0.2f)
            )
            .clickable { onClick() },
        shape = RoundedCornerShape(8.dp),
        backgroundColor = White,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(colors = gradientColors))
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 왼쪽: 검사명
            Text(
                text = title,
                fontSize = 28.sp,
                lineHeight = 26.sp,
                fontWeight = FontWeight.Bold,
                color = Black,
                textAlign = TextAlign.Start,
                modifier = Modifier.weight(1f)
            )

            // 오른쪽: 소요 시간 + 아이콘
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(start = 4.dp)
            ) {
                Column(horizontalAlignment = Alignment.End) {

                    Row() {
                        Text(
                            text = stringResource(R.string.box_approximate),
                            fontSize = 18.sp,
                            lineHeight = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Gray
                        )
                        Text(
                            text = " ",
                            fontSize = 18.sp,
                            lineHeight = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Gray
                        )
                        Text(
                            text = time.toString(),
                            fontSize = 18.sp,
                            lineHeight = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Gray
                        )
                        Text(
                            text = stringResource(R.string.box_minute),
                            fontSize = 18.sp,
                            lineHeight = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Gray
                        )
                    }
                }

                Icon(
                    painter = painterResource(id = R.drawable.icon_back_black),
                    contentDescription = null,
                    tint = neNoon_blue,
                    modifier = Modifier
                        .size(32.dp)
                        .rotate(180f)
                )
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 530, heightDp = 270)
@Composable
fun SimpleInspectionCardPreviewIsNotDone() {
    NenoonKioskTheme {
        Box(
            modifier = Modifier
                .background(Color(0xFFFFFFFF))
                .padding(32.dp)
        ) {
            SimpleInspectionSelectionButton(
                title = "혈압 검사",
                time = 2,
                isDone = false,
                onClick = {}
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 525, heightDp = 270)
@Composable
fun SimpleInspectionCardPreviewIsDone() {
    NenoonKioskTheme {
        Box(
            modifier = Modifier
                .background(Color(0xFFFFFFFF))
                .padding(32.dp)
        ) {
            SimpleInspectionSelectionButton(
                title = "혈압 검사",
                time = 2,
                isDone = true,
                onClick = {}
            )
        }
    }
}