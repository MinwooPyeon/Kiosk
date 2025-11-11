package com.pixelro.nenoonkiosk.feature.admin.entrypoint

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.ui.theme.Black
import com.pixelro.nenoonkiosk.ui.theme.neNoon_blue

@Composable
fun AdminPasswordDialog(
    onDismiss: () -> Unit,
    onPasswordConfirm: (String) -> Unit,
    correctPassword: String,
    modifier: Modifier = Modifier
) {
    Dialog(
        onDismissRequest = { }, // 배경 클릭으로 닫히지 않도록 빈 람다
        properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
    ) {
        AdminPasswordDialogContent(
            onDismiss = onDismiss,
            onPasswordConfirm = onPasswordConfirm,
            correctPassword = correctPassword,
            modifier = modifier
        )
    }
}

@Composable
private fun AdminPasswordDialogContent(
    onDismiss: () -> Unit,
    onPasswordConfirm: (String) -> Unit,
    correctPassword: String,
    modifier: Modifier = Modifier
) {
    var password by remember { mutableStateOf("") }
    val context = LocalContext.current

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = Color.White
    ) {
        Column(
            modifier = Modifier
                .padding(32.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // 제목과 X 버튼
            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.admin_password),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.align(Alignment.Center)
                )

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "닫기",
                        tint = Black
                    )
                }
            }

            // 비밀번호 입력 필드 (숫자만 입력)
            OutlinedTextField(
                value = password,
                onValueChange = { newValue ->
                    // 숫자만 입력 허용
                    if (newValue.all { it.isDigit() }) {
                        password = newValue
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                textStyle = TextStyle(
                    color = Color.Black,
                    fontSize = 16.sp
                ),
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.NumberPassword
                ),
                singleLine = true,
                shape = RoundedCornerShape(8.dp),
                colors = TextFieldDefaults.colors(
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    disabledTextColor = Color.Black,
                    cursorColor = Color.Black,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    disabledContainerColor = Color.White,
                    focusedIndicatorColor = neNoon_blue,
                    unfocusedIndicatorColor = Color.Gray,
                    disabledIndicatorColor = Color.Gray
                )
            )

            // 확인 버튼
            Button(
                onClick = {
                    if (password.isNotEmpty()) {
                        if (password == correctPassword) {
                            onPasswordConfirm(password)
                        } else {
                            Toast.makeText(
                                context,
                                context.getString(R.string.signin_vm_validation_password_mismatch),
                                Toast.LENGTH_SHORT
                            ).show()
                            password = "" // 비밀번호 필드 초기화
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
                    .size(width = 200.dp, height = 48.dp),
                enabled = password.isNotEmpty(),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = neNoon_blue,
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = stringResource(R.string.common_confirm),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AdminPasswordDialogPreview() {
    MaterialTheme {
        AdminPasswordDialogContent(
            onDismiss = {},
            onPasswordConfirm = {},
            correctPassword = "1234"
        )
    }
}