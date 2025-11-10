package com.pixelro.nenoonkiosk.feature.auth

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Surface
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.createBitmap
import androidx.core.graphics.set
import androidx.navigation.NavController
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.constants.AppConstants
import com.pixelro.nenoonkiosk.core.constants.NavConstants
import com.pixelro.nenoonkiosk.core.ui.NenoonTopBar
import com.pixelro.nenoonkiosk.core.ui.TopBarOrientation
import com.pixelro.nenoonkiosk.core.util.isLandscape
import com.pixelro.nenoonkiosk.feature.auth.component.ActionButtons
import com.pixelro.nenoonkiosk.feature.auth.component.QrCodeContent
import com.pixelro.nenoonkiosk.feature.auth.login.LoginViewModel
import com.pixelro.nenoonkiosk.ui.theme.NenoonKioskTheme
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
                val bitMatrix = withContext(Dispatchers.Default) {
                    writer.encode(qrData, BarcodeFormat.QR_CODE, 512, 512)
                }

                val width = bitMatrix.width
                val height = bitMatrix.height
                val bitmap = createBitmap(width, height, Bitmap.Config.RGB_565)

                for (x in 0 until width) {
                    for (y in 0 until height) {
                        bitmap[x, y] =
                            if (bitMatrix[x, y]) android.graphics.Color.BLACK else android.graphics.Color.WHITE
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

    val isLandscape = isLandscape()

    AccountManagementContent(
        isLandscape = isLandscape,
        showProgressIndicator = showProgressIndicator,
        isUserSignedIn = isUserSignedIn,
        isUserSignInSkipped = viewModel.isUserSignInSkipped(),
        userName = userData?.name,
        qrCodeBitmap = localQrCodeBitmap,
        isQrPrintButtonEnabled = isQrPrintButtonEnabled,
        onBackClick = { navController.popBackStack(NavConstants.ROUTE_CATEGORY_LIST, false) },
        onPrintClick = {
            val currentUserId = userData?.id
            val currentUserPassword = userData?.password

            if (isUserSignedIn) {
                if (AppConstants.MANAGE_USERS_INTERNALLY && !currentUserId.isNullOrBlank() && !currentUserPassword.isNullOrBlank()) {
                    viewModel.generateAndPrintQrCode(currentUserId, currentUserPassword)
                    isQrPrintButtonEnabled = false
                    coroutineScope.launch {
                        delay(10000)
                        isQrPrintButtonEnabled = true
                    }
                } else if (!AppConstants.MANAGE_USERS_INTERNALLY && localQrCodeBitmap != null) {
                    viewModel.printQrCode(localQrCodeBitmap)
                    isQrPrintButtonEnabled = false
                    coroutineScope.launch {
                        delay(10000)
                        isQrPrintButtonEnabled = true
                    }
                }
            }
        },
        onFaceEnrollClick = {
            Log.d("AccountManagementScreen", "User signed in: $isUserSignedIn, User ID: ${userData?.id}")
            if (isUserSignedIn && userData?.id != null) {
                navController.navigate(NavConstants.ROUTE_FACE_UPDATE_TERMS_OF_SERVICE)
            }
        },
        onSignOutClick = {
            viewModel.userSignOut()
            showProgressIndicator = true
            localQrCodeBitmap = null
            navController.navigate(NavConstants.ROUTE_SIGN_IN) {
                popUpTo(navController.graph.startDestinationId) { inclusive = false }
                launchSingleTop = true
            }
        }
    )
}

@Composable
private fun AccountManagementContent(
    isLandscape: Boolean,
    showProgressIndicator: Boolean,
    isUserSignedIn: Boolean,
    isUserSignInSkipped: Boolean,
    userName: String?,
    qrCodeBitmap: Bitmap?,
    isQrPrintButtonEnabled: Boolean,
    onBackClick: () -> Unit,
    onPrintClick: () -> Unit,
    onFaceEnrollClick: () -> Unit,
    onSignOutClick: () -> Unit
) {
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize(),
        ) {
            NenoonTopBar(
                title = stringResource(id = R.string.account_management_title),
                orientation = if (isLandscape) TopBarOrientation.Horizontal else TopBarOrientation.Vertical,
                showBackButton = true,
                onBackClicked = onBackClick,
                containerColor = Color.White,
                contentColor = Color.Black
            )

            if (isLandscape) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(40.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .weight(0.5f)
                            .fillMaxSize()
                    ) {
                        QrCodeContent(
                            showProgressIndicator = showProgressIndicator,
                            isUserSignedIn = isUserSignedIn,
                            isUserSignInSkipped = isUserSignInSkipped,
                            userName = userName,
                            qrCodeBitmap = qrCodeBitmap
                        )
                    }

                    Spacer(modifier = Modifier.width(40.dp))

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .weight(0.5f)
                            .fillMaxSize()
                    ) {
                        ActionButtons(
                            isUserSignInSkipped = isUserSignInSkipped,
                            isQrPrintButtonEnabled = isQrPrintButtonEnabled,
                            onPrintClick = onPrintClick,
                            onFaceEnrollClick = onFaceEnrollClick,
                            onSignOutClick = onSignOutClick,
                            modifier = Modifier.fillMaxWidth(0.7f)
                        )
                    }
                }
            } else {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(40.dp),
                ) {
                    Spacer(modifier = Modifier.weight(1f))

                    QrCodeContent(
                        showProgressIndicator = showProgressIndicator,
                        isUserSignedIn = isUserSignedIn,
                        isUserSignInSkipped = isUserSignInSkipped,
                        userName = userName,
                        qrCodeBitmap = qrCodeBitmap
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    ActionButtons(
                        isUserSignInSkipped = isUserSignInSkipped,
                        isQrPrintButtonEnabled = isQrPrintButtonEnabled,
                        onPrintClick = onPrintClick,
                        onFaceEnrollClick = onFaceEnrollClick,
                        onSignOutClick = onSignOutClick
                    )
                }
            }
        }
    }
}

@Preview(
    showBackground = true,
    widthDp = 800,
    heightDp = 1280,
    name = "AccountManagement - Portrait"
)
@Composable
private fun AccountManagementScreen_Preview_Portrait() {
    NenoonKioskTheme {
        AccountManagementContent(
            isLandscape = false,
            showProgressIndicator = false,
            isUserSignedIn = true,
            isUserSignInSkipped = false,
            userName = "홍길동",
            qrCodeBitmap = null,
            isQrPrintButtonEnabled = true,
            onBackClick = {},
            onPrintClick = {},
            onFaceEnrollClick = {},
            onSignOutClick = {}
        )
    }
}

@Preview(
    showBackground = true,
    widthDp = 1422,
    heightDp = 888,
    name = "AccountManagement - Landscape"
)
@Composable
private fun AccountManagementScreen_Preview_Landscape() {
    NenoonKioskTheme {
        AccountManagementContent(
            isLandscape = true,
            showProgressIndicator = false,
            isUserSignedIn = true,
            isUserSignInSkipped = false,
            userName = "홍길동",
            qrCodeBitmap = null,
            isQrPrintButtonEnabled = true,
            onBackClick = {},
            onPrintClick = {},
            onFaceEnrollClick = {},
            onSignOutClick = {}
        )
    }
}

@Preview(
    showBackground = true,
    widthDp = 800,
    heightDp = 1280,
    name = "AccountManagement - Not Signed In"
)
@Composable
private fun AccountManagementScreen_Preview_NotSignedIn() {
    NenoonKioskTheme {
        AccountManagementContent(
            isLandscape = false,
            showProgressIndicator = false,
            isUserSignedIn = false,
            isUserSignInSkipped = true,
            userName = null,
            qrCodeBitmap = null,
            isQrPrintButtonEnabled = false,
            onBackClick = {},
            onPrintClick = {},
            onFaceEnrollClick = {},
            onSignOutClick = {}
        )
    }
}
