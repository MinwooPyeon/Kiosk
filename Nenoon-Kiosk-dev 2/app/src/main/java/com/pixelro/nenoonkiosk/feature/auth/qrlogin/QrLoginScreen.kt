package com.pixelro.nenoonkiosk.feature.auth.qrlogin

import android.util.Log
import android.util.Size
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.google.common.util.concurrent.ListenableFuture
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.ui.PrimaryButton
import com.pixelro.nenoonkiosk.core.ui.ProgressIndicator
import com.pixelro.nenoonkiosk.core.ui.StyledText
import com.pixelro.nenoonkiosk.core.ui.TextStyle
import com.pixelro.nenoonkiosk.core.util.StringProvider
import com.pixelro.nenoonkiosk.core.util.qr.QRScannerAnalyzer
import com.pixelro.nenoonkiosk.feature.auth.SignInScreenState
import com.pixelro.nenoonkiosk.feature.auth.login.LoginViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@Composable
fun QrLoginScreen(
    updateIsSignedIn: (Boolean) -> Unit,
    loginViewModel: LoginViewModel,
    navController: NavController,
) {
    var scannedId by remember { mutableStateOf("") }
    var scannedPassword by remember { mutableStateOf("") }

    var isScanning by remember { mutableStateOf(true) }
    var signInFailed by remember { mutableStateOf(false) }
    var signInMessage by remember { mutableStateOf("") }

    val userData by loginViewModel.userData.collectAsState()
    val isUserSignedIn by loginViewModel.isUserSignedIn.collectAsState()

    val context = LocalContext.current
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
        signInMessage = StringProvider.getString(R.string.qr_sign_in_scan_instruction)
    }

    LaunchedEffect(isScanning, scannedId, scannedPassword) {
        if (!isScanning && scannedId.isNotBlank() && scannedPassword.isNotBlank()) {
            signInMessage = StringProvider.getString(R.string.qr_sign_in_login_processing)
            coroutineScope.launch(Dispatchers.Main) {
                loginViewModel.userSignIn(scannedId, scannedPassword, {}).also { success ->
                    delay(1500L)
                    if (!success) {
                        signInMessage = StringProvider.getString(R.string.qr_sign_in_invalid_qr)
                        signInFailed = true
                        isScanning = true
                    } else {
                        updateIsSignedIn(true)
                    }
                }
            }
        }
    }

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        StyledText(StringProvider.getString(R.string.qr_sign_in_title), TextStyle.Title)

        Spacer(modifier = Modifier.weight(1f))

        if (isScanning || signInFailed) {
            AndroidView(
                modifier =
                    Modifier
                        .fillMaxWidth(0.7f)
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(16.dp)),
                factory = { ctx ->
                    PreviewView(ctx).apply {
                        this.scaleType = PreviewView.ScaleType.FILL_CENTER
                        layoutParams =
                            LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT,
                            )

                        val cameraProvider = ProcessCameraProvider.getInstance(ctx)
                        cameraProviderFutureState.value = cameraProvider

                        cameraProvider.addListener({
                            val actualCameraProvider = cameraProvider.get()
                            val preview =
                                Preview.Builder().build().also {
                                    it.setSurfaceProvider(this.surfaceProvider)
                                }

                            val imageAnalysis =
                                ImageAnalysis.Builder()
                                    .setTargetResolution(Size(640, 480))
                                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                                    .build()
                                    .also {
                                        it.setAnalyzer(
                                            cameraExecutor,
                                            QRScannerAnalyzer { result ->
                                                if (isScanning) {
                                                    isScanning = false
                                                    signInFailed = false

                                                    ContextCompat.getMainExecutor(context).execute {
                                                        actualCameraProvider.unbindAll()
                                                    }

                                                    try {
                                                        val json = JSONObject(result)
                                                        scannedId = json.getString("id")
                                                        scannedPassword = json.getString("pw")
                                                        signInMessage =
                                                            StringProvider.getString(R.string.qr_sign_in_scanned_success)
                                                    } catch (e: Exception) {
                                                        signInMessage =
                                                            StringProvider.getString(R.string.qr_sign_in_invalid_qr)
                                                        isScanning = true
                                                        ContextCompat.getMainExecutor(context)
                                                            .execute {
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
                                    StringProvider.getString(R.string.qr_sign_in_camera_bind_fail),
                                    exc
                                )
                                isScanning = false
                                navController.navigate(SignInScreenState.UserSignIn.name) {
                                    popUpTo(navController.graph.startDestinationId) {
                                        inclusive = true
                                    }
                                }
                            }
                        }, ContextCompat.getMainExecutor(ctx))
                    }
                },
            )
        } else if (isUserSignedIn && userData?.name?.isNotEmpty() == true && !signInFailed) {
            StyledText(
                StringProvider.getString(
                    R.string.qr_sign_in_login_success,
                    userData?.name ?: StringProvider.getString(R.string.default_user_name),
                ),
            )
        } else {
            ProgressIndicator()
        }

        Spacer(modifier = Modifier
            .weight(1f)
            .height(40.dp))

        StyledText(
            text = if (!(isUserSignedIn && userData?.name?.isNotEmpty() == true && !signInFailed)) signInMessage else "",
            textAlign = TextAlign.Center,
            style = TextStyle.Message,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(120.dp))

        PrimaryButton(
            text = StringProvider.getString(R.string.button_back),
            onClick = {
                navController.navigate(SignInScreenState.UserSignIn.name) {
                    popUpTo(navController.graph.startDestinationId) { inclusive = true }
                }
            },
        )
    }
}
