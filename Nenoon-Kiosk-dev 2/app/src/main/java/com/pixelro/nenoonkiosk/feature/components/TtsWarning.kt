package com.pixelro.nenoonkiosk.feature.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.util.StringProvider
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

@Composable
fun TtsWarning(
    active: MutableStateFlow<Boolean>,
    duration: Long = 2000L
) {

    val showDialog by active.collectAsState()

    LaunchedEffect(showDialog) {
        if (showDialog) {
            delay(duration)
            active.update { false }
        }
    }

    if (showDialog) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0,0,0,63))
        ) {
            Text(
                modifier = Modifier
                    .padding(start = 40.dp, end = 40.dp, bottom = 360.dp)
                    .border(
                        border = BorderStroke(2.dp, Color(0xFF000000)),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .background(
                        color = Color(0xFFFFFFFF),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(20.dp)
                    .fillMaxWidth(),
                text = buildAnnotatedString {
                    append(
                        StringProvider.getString(
                            R.string.dialog_description2_announcement1
                        )
                    )
                    withStyle(
                        style = SpanStyle(
                            color = Color(0xff1d71e1),
                            fontWeight = FontWeight.Bold
                        )
                    ) {
                        append(
                            StringProvider.getString(
                                R.string.dialog_description2_announcement2
                            )
                        )
                    }
                    append(
                        StringProvider.getString(
                            R.string.dialog_description2_announcement3
                        )
                    )
                },
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }
    }
}