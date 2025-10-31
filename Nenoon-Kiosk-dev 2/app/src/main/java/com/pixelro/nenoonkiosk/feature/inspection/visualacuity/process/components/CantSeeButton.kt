package com.pixelro.nenoonkiosk.feature.inspection.visualacuity.process.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.ui.theme.White

/**
 * "안 보임" 버튼 컴포넌트
 *
 * 시력 검사 중 란돌트 C가 보이지 않을 때 사용하는 버튼
 * 방향 선택 버튼들 아래에 넓게 표시됨
 *
 * @param onClick 버튼 클릭 시 실행할 콜백
 * @param modifier Modifier
 */
@Composable
fun CantSeeButton(
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
                    .width(350.dp)
                    .background(
                        color = White,
                        shape = RoundedCornerShape(8.dp),
                    )
                    .clickable { onClick() },
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = stringResource(R.string.visual_acuity_undefinable),
                fontSize = 60.sp,
                fontWeight = Bold
            )
        }
    }
}