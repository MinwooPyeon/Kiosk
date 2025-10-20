package com.pixelro.nenoonkiosk.feature.strabismustest

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.util.StringProvider

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Header(
    title: String,
    showHowToButton: Boolean,
    onBackClicked: () -> Unit,
    onHowToClicked: () -> Unit,
    backButtonText: String? = null
) {
    TopAppBar(
        title = { Text(text = title, color = Color.Black) },
        navigationIcon = {
            if (backButtonText != null) {
                // TextButton을 사용하여 텍스트가 잘리지 않도록 합니다.
                TextButton(
                    onClick = onBackClicked,
                ) {
                    Text(StringProvider.getString(R.string.common_back_arrow), fontSize = 48.sp, color = Color.Black)
                }
            }
        },
        actions = {
            if (showHowToButton) {
                Button(onClick = onHowToClicked) {
                    Text(text = StringProvider.getString(R.string.common_view_test_guide))
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.White,
            scrolledContainerColor = Color.White,
            titleContentColor = Color.Black,
            actionIconContentColor = Color.Black,
            navigationIconContentColor = Color.Black
        )
    )
}
