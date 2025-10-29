package com.pixelro.nenoonkiosk.feature.inspection.dementia.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.util.StringProvider

@Composable
fun WebContainer(
    onBack: () -> Unit,
    content: @Composable () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color(0xff144AAE)),
    ) {
        Box(
            modifier = Modifier
                .padding(start = 40.dp, top = 20.dp, end = 40.dp, bottom = 20.dp)
                .fillMaxWidth()
                .height(40.dp),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxHeight()
                    .clickable { onBack() },
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Image(
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .width(28.dp),
                    painter = painterResource(id = R.drawable.icon_back_white),
                    contentDescription = "",
                )
                Text(
                    text = StringProvider.getStringComposable(R.string.back),
                    fontSize = 24.sp,
                    color = Color(0xffffffff),
                )
            }
        }
        content()
    }
}