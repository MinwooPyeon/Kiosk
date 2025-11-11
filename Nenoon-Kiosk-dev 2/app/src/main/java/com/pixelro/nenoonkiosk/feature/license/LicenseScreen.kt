package com.pixelro.nenoonkiosk.feature.license

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.ui.theme.Black
import com.pixelro.nenoonkiosk.ui.theme.DarkGray
import com.pixelro.nenoonkiosk.ui.theme.Gray
import com.pixelro.nenoonkiosk.ui.theme.Green
import com.pixelro.nenoonkiosk.ui.theme.Red
import com.pixelro.nenoonkiosk.ui.theme.White
import com.pixelro.nenoonkiosk.ui.theme.Yellow200


/**
 * 라이선스 인증 화면
 * - 숫자 키패드로 비밀번호 입력
 * - Keystore + AES-256-GCM으로 안전하게 저장
 * - 기기 ID 바인딩으로 복제 방지
 */
@Composable
fun LicenseScreen(
    password: String,
    isLoading: Boolean,
    errorMessage: String?,
    onNumberClick: (String) -> Unit,
    onBackspaceClick: () -> Unit,
    onClearClick: () -> Unit,
    onAuthenticateClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Black),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .width(500.dp)
                .padding(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = DarkGray
            ),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(32.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 아이콘
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Lock",
                    modifier = Modifier.size(48.dp),
                    tint = Green
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 제목
                Text(
                    text = stringResource(R.string.license_title),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = White
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = stringResource(R.string.license_description),
                    fontSize = 14.sp,
                    color = Gray
                )

                Spacer(modifier = Modifier.height(24.dp))

                // 비밀번호 표시
                PasswordDisplay(password = password)

                Spacer(modifier = Modifier.height(8.dp))

                // 에러 메시지
                if (errorMessage != null) {
                    Text(
                        text = errorMessage!!,
                        color = Red,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 숫자 키패드
                NumberKeypad(
                    onNumberClick = onNumberClick,
                    onBackspaceClick = onBackspaceClick,
                    onClearClick = onClearClick
                )

                Spacer(modifier = Modifier.height(24.dp))

                // 인증 버튼
                Button(
                    onClick = onAuthenticateClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    enabled = !isLoading && password.isNotEmpty(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Green
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = White
                        )
                    } else {
                        Text(
                            text = stringResource(R.string.license_authenticate),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

/**
 * 프리뷰
 */
@Preview(showBackground = true, widthDp = 800, heightDp = 1280)
@Composable
private fun LicenseScreenPreview() {
    LicenseScreen(
        password = "",
        isLoading = false,
        errorMessage = null,
        onNumberClick = {},
        onBackspaceClick = {},
        onClearClick = {},
        onAuthenticateClick = {}
    )
}

@Preview(showBackground = true, widthDp = 800, heightDp = 1280)
@Composable
private fun LicenseScreenWithErrorPreview() {
    LicenseScreen(
        password = "12",
        isLoading = false,
        errorMessage = "인증 실패. 비밀번호를 확인하세요",
        onNumberClick = {},
        onBackspaceClick = {},
        onClearClick = {},
        onAuthenticateClick = {}
    )
}

/**
 * 비밀번호 표시 영역
 */
@Composable
private fun PasswordDisplay(password: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .border(2.dp, Green, RoundedCornerShape(8.dp))
            .background(DarkGray, RoundedCornerShape(8.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = if (password.isEmpty()) stringResource(R.string.license_password_placeholder) else "•".repeat(password.length),
            fontSize = 24.sp,
            color = if (password.isEmpty()) Gray else White,
            letterSpacing = 4.sp
        )
    }
}

/**
 * 숫자 키패드
 */
@Composable
private fun NumberKeypad(
    onNumberClick: (String) -> Unit,
    onBackspaceClick: () -> Unit,
    onClearClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 1, 2, 3
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            NumberButton("1", Modifier.weight(1f)) { onNumberClick("1") }
            NumberButton("2", Modifier.weight(1f)) { onNumberClick("2") }
            NumberButton("3", Modifier.weight(1f)) { onNumberClick("3") }
        }

        // 4, 5, 6
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            NumberButton("4", Modifier.weight(1f)) { onNumberClick("4") }
            NumberButton("5", Modifier.weight(1f)) { onNumberClick("5") }
            NumberButton("6", Modifier.weight(1f)) { onNumberClick("6") }
        }

        // 7, 8, 9
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            NumberButton("7", Modifier.weight(1f)) { onNumberClick("7") }
            NumberButton("8", Modifier.weight(1f)) { onNumberClick("8") }
            NumberButton("9", Modifier.weight(1f)) { onNumberClick("9") }
        }

        // C, 0, ←
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = onClearClick,
                modifier = Modifier
                    .weight(1f)
                    .height(64.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Red
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("C", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }

            NumberButton("0", Modifier.weight(1f)) { onNumberClick("0") }

            Button(
                onClick = onBackspaceClick,
                modifier = Modifier
                    .weight(1f)
                    .height(64.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Yellow200
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Backspace,
                    contentDescription = "Backspace",
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}

/**
 * 숫자 버튼
 */
@Composable
private fun NumberButton(
    number: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(64.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Gray
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = number,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = White
        )
    }
}