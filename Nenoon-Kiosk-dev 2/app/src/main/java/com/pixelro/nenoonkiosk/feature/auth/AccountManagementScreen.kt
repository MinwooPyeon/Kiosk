package com.pixelro.nenoonkiosk.feature.auth

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.constants.AppConstants
import com.pixelro.nenoonkiosk.core.constants.GlobalValue
import com.pixelro.nenoonkiosk.core.constants.NavConstants
import com.pixelro.nenoonkiosk.core.ui.PrimaryButton
import com.pixelro.nenoonkiosk.core.ui.ProgressIndicator
import com.pixelro.nenoonkiosk.core.ui.StyledText
import com.pixelro.nenoonkiosk.core.ui.TextStyle
import com.pixelro.nenoonkiosk.core.util.StringProvider
import com.pixelro.nenoonkiosk.feature.auth.login.LoginViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun AccountManagementScreen(
    navController: NavController,
    viewModel: LoginViewModel,
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val isUserSignedIn by viewModel.isUserSignedIn.collectAsState()
    val userData by viewModel.userData.collectAsState()

    var localQrCodeBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var showProgressIndicator by remember { mutableStateOf(true) }
    var isQrPrintButtonEnabled by remember { mutableStateOf(true) }

    fun generateQrCodeBitmapInternal(
        userId: String,
        password: String,
    ) {
        coroutineScope.launch {
            try {
                val qrData = "ID:$userId,PW:$password"

                val writer = QRCodeWriter()
                val bitMatrix =
                    withContext(Dispatchers.Default) {
                        writer.encode(qrData, BarcodeFormat.QR_CODE, 512, 512)
                    }

                val width = bitMatrix.width
                val height = bitMatrix.height
                val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)

                for (x in 0 until width) {
                    for (y in 0 until height) {
                        bitmap.setPixel(x, y, if (bitMatrix[x, y]) android.graphics.Color.BLACK else android.graphics.Color.WHITE)
                    }
                }
                localQrCodeBitmap = bitmap
                showProgressIndicator = false
            } catch (e: Exception) {
                Log.e("AccountManagementScreen", "Error generating QR code bitmap: ${e.message}")
                localQrCodeBitmap = null
            }
        }
    }

    LaunchedEffect(Unit) {
        val currentUserId = userData?.id
        val currentUserPassword = userData?.password

        if (!AppConstants.MANAGE_USERS_INTERNALLY) {
            localQrCodeBitmap = viewModel.getQrCode()
            showProgressIndicator = false
        } else if (isUserSignedIn && !currentUserId.isNullOrBlank() && !currentUserPassword.isNullOrBlank()) {
            generateQrCodeBitmapInternal(currentUserId, currentUserPassword)
        } else {
            showProgressIndicator = false
        }
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize(),
        ) {
            Box(
                modifier =
                    Modifier
                        .padding(
                            start = 40.dp,
                            top = (GlobalValue.statusBarPadding + 20).dp,
                            end = 40.dp,
                            bottom = 20.dp,
                        )
                        .fillMaxWidth()
                        .height(40.dp),
            ) {
                Box(
                    modifier =
                        Modifier
                            .fillMaxSize(),
                    contentAlignment = Alignment.CenterStart,
                ) {
                    Image(
                        modifier =
                            Modifier
                                .width(32.dp)
                                .clickable { navController.popBackStack(NavConstants.ROUTE_CATEGORY_LIST, false) },
                        painter = painterResource(id = R.drawable.close_button_black),
                        contentDescription =
                            StringProvider.getString(
                                R.string.close_button_description,
                            ),
                    )
                }
                Box(
                    modifier =
                        Modifier
                            .fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text =
                            StringProvider.getString(
                                R.string.account_management_title,
                            ),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Medium,
                    )
                }
            }

            Spacer(
                modifier =
                    Modifier
                        .padding(start = 5.dp, end = 5.dp)
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(
                            color = Color(0xff000000),
                        ),
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(40.dp),
            ) {
                Spacer(modifier = Modifier.weight(1f))

                if (showProgressIndicator) {
                    ProgressIndicator()
                } else if (!viewModel.isUserSignInSkipped() && isUserSignedIn) {
                    StyledText(
                        text =
                            StringProvider.getString(
                                R.string.qr_code_user_name,
                                userData?.name ?: StringProvider.getString(R.string.default_user_name),
                            ),
                    )

                    if (localQrCodeBitmap != null) {
                        Image(
                            bitmap = localQrCodeBitmap!!.asImageBitmap(),
                            contentDescription =
                                StringProvider.getString(
                                    R.string.qr_code_image_description,
                                ),
                            modifier =
                                Modifier
                                    .size(400.dp)
                                    .padding(top = 40.dp),
                        )
                    } else {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier =
                                Modifier
                                    .size(400.dp),
                        ) {
                            ProgressIndicator()
                        }
                    }
                } else {
                    StyledText(
                        text =
                            StringProvider.getString(
                                R.string.not_signed_in_message,
                            ),
                        style = TextStyle.Error,
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                PrimaryButton(
                    onClick = {
                        val currentUserId = userData?.id
                        val currentUserPassword = userData?.password

                        if (isUserSignedIn) {
                            if (AppConstants.MANAGE_USERS_INTERNALLY && !currentUserId.isNullOrBlank() && !currentUserPassword.isNullOrBlank()) {
                                viewModel.generateAndPrintQrCode(currentUserId, currentUserPassword)

                                isQrPrintButtonEnabled = false
                                coroutineScope.launch {
                                    delay(10000) // 10 seconds
                                    isQrPrintButtonEnabled = true
                                }
                            } else if (!AppConstants.MANAGE_USERS_INTERNALLY && localQrCodeBitmap != null) {
                                viewModel.printQrCode(localQrCodeBitmap)

                                isQrPrintButtonEnabled = false
                                coroutineScope.launch {
                                    delay(10000) // 10 seconds
                                    isQrPrintButtonEnabled = true
                                }
                            }
                        }
                    },
                    text =
                        StringProvider.getString(
                            R.string.qr_code_print_button,
                        ),
                    enabled = !viewModel.isUserSignInSkipped() && isQrPrintButtonEnabled,
                )
                Spacer(modifier = Modifier.height(20.dp))

                PrimaryButton(
                    onClick = {
                        Log.d(
                            "AccountManagementScreen",
                            "User signed in: $isUserSignedIn, User ID: ${userData?.id}",
                        )
                        if (isUserSignedIn && userData?.id != null) {
                            navController.navigate(NavConstants.ROUTE_FACE_UPDATE_TERMS_OF_SERVICE)
                        }
                    },
                    text =
                        StringProvider.getString(
                            R.string.face_enroll_button_text,
                        ),
                    enabled = !viewModel.isUserSignInSkipped(),
                )
                Spacer(modifier = Modifier.height(20.dp))

                PrimaryButton(
                    onClick = {
                        viewModel.userSignOut()
                        showProgressIndicator = true
                        localQrCodeBitmap = null
                        navController.navigate(NavConstants.ROUTE_SIGN_IN) {
                            popUpTo(navController.graph.startDestinationId) { inclusive = false }
                            launchSingleTop = true
                        }
                    },
                    text =
                        if (!viewModel.isUserSignInSkipped()) {
                            StringProvider.getString(
                                R.string.settings_signout,
                            )
                        } else {
                            StringProvider.getString(R.string.signin)
                        },
                )
            }
        }
    }
}
