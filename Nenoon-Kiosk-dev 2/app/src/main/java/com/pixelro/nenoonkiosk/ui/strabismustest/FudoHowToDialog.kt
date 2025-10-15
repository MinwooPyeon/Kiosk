package com.pixelro.nenoonkiosk.ui.strabismustest

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.strabismustest.ui.theme.neNoon_blue
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.data.StringProvider

@Composable
fun FudoHowToDialog(onDismissRequest: () -> Unit) {
    Dialog(onDismissRequest = onDismissRequest) {
        Column(
            modifier = Modifier
                .background(Color.White)
                .padding(24.dp)
        ) {
            Text(
                text = StringProvider.getString(R.string.fudo_howto_title),
                fontSize = 30.sp,
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(bottom = 16.dp)
            )
            HowToStep(
                step = StringProvider.getString(R.string.common_step1),
                instruction = StringProvider.getString(R.string.fudo_howto_step1_desc)
            )
            HowToStep(
                step = StringProvider.getString(R.string.common_step2),
                instruction = StringProvider.getString(R.string.fudo_howto_step2_desc)
            )
            HowToStep(
                step = StringProvider.getString(R.string.common_step3),
                instruction = StringProvider.getString(R.string.fudo_howto_step3_desc)
            )
            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = onDismissRequest,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(96.dp),
                colors = ButtonDefaults.buttonColors(containerColor = neNoon_blue),
                shape = RoundedCornerShape( 12.dp)
            ) {
                Text(StringProvider.getString(R.string.common_confirm), fontSize = 36.sp, color = Color.White)
            }
        }
    }
}

@Composable
fun HowToStep(step: String, instruction: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = step,
            fontSize = 22.sp,
            color = neNoon_blue,
            modifier = Modifier.padding(end = 16.dp)
        )
        Text(
            text = instruction,
            fontSize = 24.sp,
            color = Color.Black,
            modifier = Modifier.weight(1f)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun FudoHowToDialogPreview() {
    FudoHowToDialog { }
}