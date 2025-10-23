package com.pixelro.nenoonkiosk.feature.strabismustest

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.util.StringProvider
import com.pixelro.nenoonkiosk.ui.theme.NenoonKioskTheme
import com.pixelro.nenoonkiosk.ui.theme.neNoon_blue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FudoIntroScreen(
    onStartClicked: () -> Unit,
    onHowToClicked: () -> Unit,
    onBackClicked: () -> Unit,
) {
    var showHowToDialog by remember { mutableStateOf(false) }

    if (showHowToDialog) {
        FudoHowToDialog(onDismissRequest = { showHowToDialog = false })
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("") },
                navigationIcon = {
                    IconButton(onClick = onBackClicked) {
                        Icon(
                            painterResource(R.drawable.icon_back_black),
                            contentDescription = StringProvider.getString(R.string.common_back_content_desc),
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White),
            )
        },
    ) { paddingValues ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .padding(paddingValues)
                    .padding(16.dp),
        ) {
            Text(
                text = StringProvider.getString(R.string.fudo_intro_title),
                fontSize = 70.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 16.dp, start = 16.dp),
            )
            Text(
                text = StringProvider.getString(R.string.fudo_intro_description),
                fontSize = 32.sp,
                color = Color.Gray,
                modifier = Modifier.padding(top = 40.dp, start = 16.dp, end = 16.dp),
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { showHowToDialog = true },
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(96.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
            ) {
                Text(StringProvider.getString(R.string.common_view_test_guide), fontSize = 36.sp, color = neNoon_blue)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onStartClicked,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(96.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = neNoon_blue),
            ) {
                Text(StringProvider.getString(R.string.common_start_test), fontSize = 36.sp, color = Color.White)
            }
        }
    }
}

@Preview(showBackground = true, device = "spec:width=1280dp,height=800dp,dpi=240")
@Composable
fun FudoIntroScreenPreview() {
    NenoonKioskTheme {
        FudoIntroScreen({}, {}, {})
    }
}
