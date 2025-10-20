package com.pixelro.nenoonkiosk.feature.strabismustest

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.util.StringProvider
import com.pixelro.nenoonkiosk.ui.theme.neNoon_blue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SawiIntroScreen(
    onStartClicked: () -> Unit,
    onHowToClicked: () -> Unit,
    onBackClicked: () -> Unit
) {
    var showHowToDialog by remember { mutableStateOf(false) }

    if (showHowToDialog) {
        SawiHowToDialog(onDismissRequest = { showHowToDialog = false })
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White),
                navigationIcon = {
                    TextButton(
                        onClick = onBackClicked,
                    ) {
                        Text(StringProvider.getString(R.string.common_back_arrow), fontSize = 48.sp, color = Color.Black)
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text(
                text = StringProvider.getString(R.string.sawi_intro_title),
                fontSize = 70.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(start = 16.dp, top = 16.dp)
            )
            Spacer(modifier = Modifier.height(80.dp))
            Text(
                text = StringProvider.getString(R.string.sawi_intro_description),
                fontSize = 32.sp,
                color = Color.DarkGray,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.weight(1f))
            Button(
                onClick = { showHowToDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(96.dp)
                    .padding(bottom = 16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape( 12.dp)
            ) {
                Text(StringProvider.getString(R.string.common_view_test_guide), fontSize = 36.sp, color = neNoon_blue)
            }
            Button(
                onClick = onStartClicked,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(96.dp),
                colors = ButtonDefaults.buttonColors(containerColor = neNoon_blue),
                shape = RoundedCornerShape( 12.dp)
            ) {
                Text(StringProvider.getString(R.string.common_start_test), fontSize = 36.sp, color = Color.White)
            }
        }
    }
}
