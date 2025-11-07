package com.pixelro.nenoonkiosk.core.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
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
import com.pixelro.nenoonkiosk.ui.theme.LightGray100
import com.pixelro.nenoonkiosk.ui.theme.LightGray400
import com.pixelro.nenoonkiosk.ui.theme.LightGray500
import com.pixelro.nenoonkiosk.ui.theme.NenoonKioskTheme
import com.pixelro.nenoonkiosk.ui.theme.White
import com.pixelro.nenoonkiosk.ui.theme.neNoon_blue

/**
 * DualInspectionSelectionButton
 * 검사 항목 카드 (두 줄 title)
 *
 * @param title1 첫 줄 (예: "암슬러 차트 검사")
 * @param title2 둘째 줄 (예: "(황반 변성 검사)")
 * @param time 소요시간 (예: 2)
 * @param isDone 완료 여부
 * @param onClick 클릭 이벤트
 */
@Composable
fun TwoLineInspectionSelectionButton(
    title1: String,
    title2: String,
    time: Int,
    isDone: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // 그라데이션: isDone일 때만 적용
    val gradientColors = if (isDone) {
        listOf(LightGray500, LightGray400)
    } else { neNoon_blue
        listOf(White, White)
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.Transparent)
    ) {
        Card(
            modifier = modifier
                .fillMaxSize()
                .heightIn(min = this.minHeight)
                .border(
                    border = BorderStroke(1.dp, LightGray100),
                    shape = RoundedCornerShape(8.dp)
                )
                .shadow(
                    elevation = 3.dp,
                    shape = RoundedCornerShape(8.dp),
                    ambientColor = Black.copy(alpha = 0.25f),
                    spotColor = Black.copy(alpha = 0.2f)
                )
                .clickable { onClick() },
            shape = RoundedCornerShape(8.dp),
            backgroundColor = White
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(brush = Brush.verticalGradient(colors = gradientColors))
                    .padding(horizontal = 28.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 왼쪽: 두 줄 제목
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .wrapContentHeight(),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = title1,
                        fontSize = 30.sp,
                        lineHeight = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        textAlign = TextAlign.Start,
                        modifier = Modifier.padding(0.dp)
                    )
                    Text(
                        text = title2,
                        fontSize = 26.sp,
                        lineHeight = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = neNoon_blue,
                        textAlign = TextAlign.Start
                    )
                }

                // 오른쪽: 소요 시간 + 아이콘
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(start = 4.dp)
                ) {
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = stringResource(R.string.box_time_required),
                            fontSize = 18.sp,
                            color = Black
                        )
                        Row {
                            Text(
                                text = stringResource(R.string.box_approximate),
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = Black
                            )
                            Text(
                                text = " ",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = Black
                            )
                            Text(
                                text = time.toString(),
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = Black
                            )
                            Text(
                                text = stringResource(R.string.box_minute),
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = Black
                            )
                        }
                    }

                    Icon(
                        painter = painterResource(id = R.drawable.icon_back_black),
                        contentDescription = null,
                        tint = neNoon_blue,
                        modifier = Modifier
                            .size(42.dp)
                            .rotate(180f)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 540, heightDp = 270)
@Composable
fun DualInspectionSelectionButtonPreview() {
    NenoonKioskTheme {
        Box(
            modifier = Modifier
                .background(Color.White)
                .padding(32.dp)
        ) {
            TwoLineInspectionSelectionButton(
                title1 = "암슬러 차트 검사",
                title2 = "(황반 변성 검사)",
                time = 2,
                isDone = false,
                onClick = {}
            )
        }
    }
}


@Preview(showBackground = true, widthDp = 540, heightDp = 270)
@Composable
fun DualInspectionSelectionButtonPreviewIsDone() {
    NenoonKioskTheme {
        Box(
            modifier = Modifier
                .background(Color.White)
                .padding(32.dp)
        ) {
            TwoLineInspectionSelectionButton(
                title1 = "암슬러 차트 검사",
                title2 = "(황반 변성 검사)",
                time = 2,
                isDone = true,
                onClick = {}
            )
        }
    }
}

