package com.pixelro.nenoonkiosk.feature.inspection.visualacuity.process.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.ui.theme.White

/**
 * 시력 검사 방향 선택 버튼 컴포넌트
 *
 * 란돌트 C의 방향을 선택하는 버튼. 흰색 박스 안에 방향 이미지를 표시
 *
 * @param direction 표시할 방향 (2~7: 6가지 방향)
 * @param onClick 버튼 클릭 시 실행할 콜백
 * @param modifier Modifier
 */
@Composable
fun DirectionSelectionButton(
    direction: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .padding(10.dp),
    ) {
        Box(
            modifier =
                Modifier
                    .height(100.dp)
                    .width(100.dp)
                    .background(
                        color = White,
                        shape = RoundedCornerShape(8.dp),
                    )
                    .clickable { onClick() },
            contentAlignment = Alignment.Center,
        ) {
            Image(
                modifier =
                    Modifier
                        .padding(10.dp)
                        .height(100.dp),
                imageVector =
                    ImageVector.vectorResource(
                        id = getDirectionDrawableId(direction),
                    ),
                contentDescription = "Direction $direction",
            )
        }
    }
}

/**
 * 방향 값에 해당하는 drawable ID를 반환
 *
 * @param direction 방향 (2~7)
 * @return Drawable resource ID
 */
private fun getDirectionDrawableId(direction: Int): Int {
    return when (direction) {
        2 -> R.drawable.two
        3 -> R.drawable.three
        4 -> R.drawable.four
        5 -> R.drawable.five
        6 -> R.drawable.six
        else -> R.drawable.seven
    }
}