package com.pixelro.nenoonkiosk.core.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.util.StringProvider

@Composable
fun GlassesExerciseSelectionButton(
    modifier: Modifier,
    title: String,
    onClickMethod: () -> Unit,
    painter: Painter,
) {
    Card(
        elevation = CardDefaults.cardElevation(0.dp),
        modifier =
            modifier
                .padding(start = 40.dp, end = 40.dp)
                .fillMaxWidth()
                .border(
                    border = BorderStroke(1.dp, Color(0xffe5f0f5)),
                    shape = RoundedCornerShape(8.dp),
                ),
        colors =
            CardDefaults.cardColors(
                containerColor = Color(0xFFFFFFFF),
            ),
    ) {
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(
                        color = Color(0xffe5f0f5),
                    )
                    .clickable {
                        onClickMethod()
                    },
            contentAlignment = Alignment.TopCenter,
        ) {
            Text(
                text = title,
                modifier =
                    Modifier
                        .padding(start = 28.dp, top = 30.dp),
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xff000000),
            )
            Image(
                modifier =
                    Modifier
                        .padding(top = 80.dp)
                        .align(Alignment.Center),
                painter =
                    if (title ==
                        StringProvider.getString(
                            R.string.presbyopia_glasses,
                        )
                    ) {
                        painterResource(id = R.drawable.presbyopiaglasses_2)
                    } else {
                        painterResource(id = R.drawable.concentrationglasses)
                    },
                contentDescription = "",
            )
        }
    }
}
