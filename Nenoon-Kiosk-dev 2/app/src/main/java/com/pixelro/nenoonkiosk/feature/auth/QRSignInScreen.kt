package com.pixelro.nenoonkiosk.feature.auth

import android.util.Log
import android.util.Size
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import com.google.common.util.concurrent.ListenableFuture
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.ui.BackButtonHorizontal
import com.pixelro.nenoonkiosk.core.ui.NenoonTopBar
import com.pixelro.nenoonkiosk.core.ui.ProgressIndicator
import com.pixelro.nenoonkiosk.core.ui.StyledText
import com.pixelro.nenoonkiosk.core.ui.TextStyle
import com.pixelro.nenoonkiosk.core.util.isLandscape
import com.pixelro.nenoonkiosk.core.util.qr.QRScannerAnalyzer
import com.pixelro.nenoonkiosk.feature.auth.login.LoginViewModel
import com.pixelro.nenoonkiosk.ui.theme.NenoonKioskTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import androidx.camera.core.Preview as CameraPreview

@Composable
fun QRSignInScreen(
    updateIsSignedIn: (Boolean) -> Unit,
    loginViewModel: LoginViewModel,
    navController: NavController,
) {
    val scanInstruction = stringResource(R.string.qr_sign_in_scan_instruction)
    val loginProcessing = stringResource(R.string.qr_sign_in_login_processing)
    val invalidQR = stringResource(R.string.qr_sign_in_invalid_qr)
    val scannedSuccess = stringResource(R.string.qr_sign_in_scanned_success)

    var scannedId by remember { mutableStateOf("") }
    var scannedPassword by remember { mutableStateOf("") }
    var isScanning by remember { mutableStateOf(true) }
    var signInFailed by remember { mutableStateOf(false) }
    var signInMessage by remember { mutableStateOf("") }

    val userData by loginViewModel.userData.collectAsState()
    val isUserSignedIn by loginViewModel.isUserSignedIn.collectAsState()

    val coroutineScope = rememberCoroutineScope()
    val lifecycleOwner = LocalLifecycleOwner.current

    val cameraExecutor: ExecutorService = remember { Executors.newSingleThreadExecutor() }
    val cameraProviderFutureState =
        remember { mutableStateOf<ListenableFuture<ProcessCameraProvider>?>(null) }

    DisposableEffect(Unit) {
        signInFailed = false
        isScanning = true
        onDispose {
            cameraProviderFutureState.value?.get()?.unbindAll()
            cameraExecutor.shutdown()
        }
    }

    LaunchedEffect(Unit) {
        signInMessage = scanInstruction
    }

    LaunchedEffect(isScanning, scannedId, scannedPassword) {
        if (!isScanning && scannedId.isNotBlank() && scannedPassword.isNotBlank()) {
            signInMessage = loginProcessing
            coroutineScope.launch(Dispatchers.Main) {
                loginViewModel.userSignIn(scannedId, scannedPassword, {}).also { success ->
                    delay(1500L)
                    if (!success) {
                        signInMessage = invalidQR
                        signInFailed = true
                        isScanning = true
                    } else {
                        updateIsSignedIn(true)
                    }
                }
            }
        }
    }

    QRSignInContent(
        isScanning = isScanning,
        signInFailed = signInFailed,
        isUserSignedIn = isUserSignedIn,
        userName = userData?.name,
        signInMessage = signInMessage,
        onQRScanned = { id, password ->
            scannedId = id
            scannedPassword = password
            isScanning = false
            signInFailed = false
            signInMessage = scannedSuccess
        },
        onInvalidQR = {
            signInMessage = invalidQR
            isScanning = true
        },
        onCameraBindFail = {
            isScanning = false
            navController.navigate(SignInScreenState.UserSignIn.name) {
                popUpTo(navController.graph.startDestinationId) { inclusive = true }
            }
        },
        onBackClick = {
            navController.navigate(SignInScreenState.UserSignIn.name) {
                popUpTo(navController.graph.startDestinationId) { inclusive = true }
            }
        },
        cameraProviderFutureState = cameraProviderFutureState,
        cameraExecutor = cameraExecutor,
        lifecycleOwner = lifecycleOwner,
        coroutineScope = coroutineScope
    )
}

@Composable
private fun QRSignInContent(
    isScanning: Boolean,
    signInFailed: Boolean,
    isUserSignedIn: Boolean,
    userName: String?,
    signInMessage: String,
    onQRScanned: (String, String) -> Unit,
    onInvalidQR: () -> Unit,
    onCameraBindFail: () -> Unit,
    onBackClick: () -> Unit,
    cameraProviderFutureState: MutableState<ListenableFuture<ProcessCameraProvider>?>,
    cameraExecutor: ExecutorService,
    lifecycleOwner: LifecycleOwner,
    coroutineScope: CoroutineScope
) {
    val isLandscape = isLandscape()

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        NenoonTopBar(
            title = stringResource(R.string.qr_sign_in_title),
            showBackButton = false
        )

        if (isLandscape) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                QRSignInLayout(
                    isScanning = isScanning,
                    signInFailed = signInFailed,
                    isUserSignedIn = isUserSignedIn,
                    userName = userName,
                    signInMessage = signInMessage,
                    onQRScanned = onQRScanned,
                    onInvalidQR = onInvalidQR,
                    onCameraBindFail = onCameraBindFail,
                    onBackClick = onBackClick,
                    cameraProviderFutureState = cameraProviderFutureState,
                    cameraExecutor = cameraExecutor,
                    lifecycleOwner = lifecycleOwner,
                    coroutineScope = coroutineScope,
                    isLandscapeMode = true,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 40.dp, vertical = 16.dp)
                )
            }
        } else {
            QRSignInLayout(
                isScanning = isScanning,
                signInFailed = signInFailed,
                isUserSignedIn = isUserSignedIn,
                userName = userName,
                signInMessage = signInMessage,
                onQRScanned = onQRScanned,
                onInvalidQR = onInvalidQR,
                onCameraBindFail = onCameraBindFail,
                onBackClick = onBackClick,
                cameraProviderFutureState = cameraProviderFutureState,
                cameraExecutor = cameraExecutor,
                lifecycleOwner = lifecycleOwner,
                coroutineScope = coroutineScope,
                isLandscapeMode = false,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(40.dp)
            )
        }
    }
}

@Composable
private fun QRSignInLayout(
    isScanning: Boolean,
    signInFailed: Boolean,
    isUserSignedIn: Boolean,
    userName: String?,
    signInMessage: String,
    onQRScanned: (String, String) -> Unit,
    onInvalidQR: () -> Unit,
    onCameraBindFail: () -> Unit,
    onBackClick: () -> Unit,
    cameraProviderFutureState: MutableState<ListenableFuture<ProcessCameraProvider>?>,
    cameraExecutor: ExecutorService,
    lifecycleOwner: LifecycleOwner,
    coroutineScope: CoroutineScope,
    isLandscapeMode: Boolean,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isInPreview = LocalInspectionMode.current
    val defaultUserName = stringResource(R.string.default_user_name)
    val loginSuccess =
        stringResource(R.string.qr_sign_in_login_success, userName ?: defaultUserName)
    val cameraWidthFraction = if (isLandscapeMode) 0.35f else 0.7f

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        if (isScanning || signInFailed) {
            CameraPreviewArea(
                isInPreview = isInPreview,
                isScanning = isScanning,
                cameraWidthFraction = cameraWidthFraction,
                onQRScanned = onQRScanned,
                onInvalidQR = onInvalidQR,
                onCameraBindFail = onCameraBindFail,
                cameraProviderFutureState = cameraProviderFutureState,
                cameraExecutor = cameraExecutor,
                lifecycleOwner = lifecycleOwner
            )
        } else if (isUserSignedIn && userName?.isNotEmpty() == true && !signInFailed) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(cameraWidthFraction)
                    .aspectRatio(1f),
                contentAlignment = Alignment.Center
            ) {
                StyledText(text = loginSuccess, textAlign = TextAlign.Center)
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth(cameraWidthFraction)
                    .aspectRatio(1f),
                contentAlignment = Alignment.Center
            ) {
                ProgressIndicator()
            }
        }

        Spacer(modifier = Modifier.height(if (isLandscapeMode) 24.dp else 32.dp))

        StyledText(
            text = if (!(isUserSignedIn && userName?.isNotEmpty() == true && !signInFailed)) signInMessage else "",
            textAlign = TextAlign.Center,
            style = TextStyle.Message,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(if (isLandscapeMode) 24.dp else 60.dp))

        BackButtonHorizontal(
            onClick = onBackClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(if (isLandscapeMode) 70.dp else 80.dp)
                .shadow(
                    elevation = 2.dp,
                    shape = RoundedCornerShape(12.dp)
                )
        )

        Spacer(modifier = Modifier.weight(if (isLandscapeMode) 0.5f else 1f))
    }
}

@Composable
private fun CameraPreviewArea(
    isInPreview: Boolean,
    isScanning: Boolean,
    cameraWidthFraction: Float,
    onQRScanned: (String, String) -> Unit,
    onInvalidQR: () -> Unit,
    onCameraBindFail: () -> Unit,
    cameraProviderFutureState: MutableState<ListenableFuture<ProcessCameraProvider>?>,
    cameraExecutor: ExecutorService,
    lifecycleOwner: LifecycleOwner
) {
    val context = LocalContext.current

    if (isInPreview) {
        Box(
            modifier = Modifier
                .fillMaxWidth(cameraWidthFraction)
                .aspectRatio(1f)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
        )
    } else {
        AndroidView(
            modifier = Modifier
                .fillMaxWidth(cameraWidthFraction)
                .aspectRatio(1f)
                .clip(RoundedCornerShape(16.dp)),
            factory = { ctx ->
                PreviewView(ctx).apply {
                    this.scaleType = PreviewView.ScaleType.FILL_CENTER
                    layoutParams = LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT,
                    )

                    val cameraProvider = ProcessCameraProvider.getInstance(ctx)
                    cameraProviderFutureState.value = cameraProvider

                    cameraProvider.addListener({
                        val actualCameraProvider = cameraProvider.get()
                        val preview = CameraPreview.Builder().build().also {
                            it.setSurfaceProvider(this.surfaceProvider)
                        }

                        val imageAnalysis = ImageAnalysis.Builder()
                            .setTargetResolution(Size(640, 480))
                            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                            .build()
                            .also {
                                it.setAnalyzer(
                                    cameraExecutor,
                                    QRScannerAnalyzer { result ->
                                        if (isScanning) {
                                            ContextCompat.getMainExecutor(context).execute {
                                                actualCameraProvider.unbindAll()
                                            }

                                            try {
                                                val json = JSONObject(result)
                                                val id = json.getString("id")
                                                val pw = json.getString("pw")
                                                onQRScanned(id, pw)
                                            } catch (e: Exception) {
                                                onInvalidQR()
                                                ContextCompat.getMainExecutor(context).execute {
                                                    actualCameraProvider.bindToLifecycle(
                                                        lifecycleOwner,
                                                        CameraSelector.DEFAULT_FRONT_CAMERA,
                                                        preview,
                                                        it,
                                                    )
                                                }
                                            }
                                        }
                                    },
                                )
                            }

                        try {
                            actualCameraProvider.unbindAll()
                            actualCameraProvider.bindToLifecycle(
                                lifecycleOwner,
                                CameraSelector.DEFAULT_FRONT_CAMERA,
                                preview,
                                imageAnalysis,
                            )
                        } catch (exc: Exception) {
                            Log.e(
                                "CAMERA_BIND",
                                context.getString(R.string.qr_sign_in_camera_bind_fail),
                                exc
                            )
                            onCameraBindFail()
                        }
                    }, ContextCompat.getMainExecutor(ctx))
                }
            },
        )
    }
}

@Preview(
    showBackground = true,
    widthDp = 1920,
    backgroundColor = 0xFFFFFFFF,
    heightDp = 1080,
    name = "Landscape - 32 inch Full HD"
)
@Composable
fun QRSignInScreen_Preview_32InchFullHD() {
    val scanInstruction = stringResource(R.string.qr_sign_in_scan_instruction)

    NenoonKioskTheme {
        QRSignInContent(
            isScanning = true,
            signInFailed = false,
            isUserSignedIn = false,
            userName = null,
            signInMessage = scanInstruction,
            onQRScanned = { _, _ -> },
            onInvalidQR = { },
            onCameraBindFail = { },
            onBackClick = { },
            cameraProviderFutureState = remember { mutableStateOf(null) },
            cameraExecutor = remember { Executors.newSingleThreadExecutor() },
            lifecycleOwner = LocalLifecycleOwner.current,
            coroutineScope = rememberCoroutineScope()
        )
    }
}

@Preview(
    showBackground = true,
    widthDp = 1280,
    backgroundColor = 0xFFFFFFFF,
    heightDp = 800,
    name = "Landscape - Standard Tablet"
)
@Composable
fun QRSignInScreen_Preview_Landscape() {
    val scanInstruction = stringResource(R.string.qr_sign_in_scan_instruction)

    NenoonKioskTheme {
        QRSignInContent(
            isScanning = true,
            signInFailed = false,
            isUserSignedIn = false,
            userName = null,
            signInMessage = scanInstruction,
            onQRScanned = { _, _ -> },
            onInvalidQR = { },
            onCameraBindFail = { },
            onBackClick = { },
            cameraProviderFutureState = remember { mutableStateOf(null) },
            cameraExecutor = remember { Executors.newSingleThreadExecutor() },
            lifecycleOwner = LocalLifecycleOwner.current,
            coroutineScope = rememberCoroutineScope()
        )
    }
}

@Preview(
    showBackground = true,
    widthDp = 800,
    backgroundColor = 0xFFFFFFFF,
    heightDp = 1280,
    name = "Portrait - Standard Tablet"
)
@Composable
fun QRSignInScreen_Preview_Portrait() {
    val scanInstruction = stringResource(R.string.qr_sign_in_scan_instruction)

    NenoonKioskTheme {
        QRSignInContent(
            isScanning = true,
            signInFailed = false,
            isUserSignedIn = false,
            userName = null,
            signInMessage = scanInstruction,
            onQRScanned = { _, _ -> },
            onInvalidQR = { },
            onCameraBindFail = { },
            onBackClick = { },
            cameraProviderFutureState = remember { mutableStateOf(null) },
            cameraExecutor = remember { Executors.newSingleThreadExecutor() },
            lifecycleOwner = LocalLifecycleOwner.current,
            coroutineScope = rememberCoroutineScope()
        )
    }
}
