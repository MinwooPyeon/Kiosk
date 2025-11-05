package com.pixelro.nenoonkiosk.feature.inspection.inspectionresult

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.speech.tts.TextToSpeech
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.graphics.scale
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.harang.data.model.dto.User
import com.mangoslab.nemonicsdk.NPrintInfo
import com.mangoslab.nemonicsdk.NPrinter
import com.mangoslab.nemonicsdk.constants.NPrinterType
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.constants.DebugConstants
import com.pixelro.nenoonkiosk.core.constants.NavConstants
import com.pixelro.nenoonkiosk.core.manager.PrinterManager
import com.pixelro.nenoonkiosk.core.util.StringProvider
import com.pixelro.nenoonkiosk.core.util.TTS
import com.pixelro.nenoonkiosk.feature.inspection.InspectionType
import com.pixelro.nenoonkiosk.feature.inspection.bloodPressure.result.BloodPressureInspectionResult
import com.pixelro.nenoonkiosk.feature.inspection.bloodPressure.result.BloodPressureInspectionResultContent
import com.pixelro.nenoonkiosk.feature.inspection.dementia.result.DementiaInspectionResultRoute
import com.pixelro.nenoonkiosk.feature.undeveloped.exerciseglasses.concentration_exercise.ConcentrationExerciseResult
import com.pixelro.nenoonkiosk.feature.undeveloped.exerciseglasses.concentration_exercise.ConcentrationExerciseResultContent
import com.pixelro.nenoonkiosk.feature.undeveloped.exerciseglasses.presbyopia_exercise.PresbyopiaExerciseResult
import com.pixelro.nenoonkiosk.feature.undeveloped.exerciseglasses.presbyopia_exercise.PresbyopiaExerciseResultContent
import com.pixelro.nenoonkiosk.feature.inspection.gripStrength.result.GripStrengthInspectionResultContent
import com.pixelro.nenoonkiosk.feature.inspection.gripStrength.result.GripStrengthInspectionResultContract
import com.pixelro.nenoonkiosk.feature.inspection.inspectionresult.result.InspectionResultUtil.textAsBitmap
import com.pixelro.nenoonkiosk.feature.inspection.inspectionresult.result.aiComment
import com.pixelro.nenoonkiosk.feature.inspection.macular.amslergrid.AmslerGridTestResult
import com.pixelro.nenoonkiosk.feature.inspection.macular.amslergrid.result.AmslerGridTestResultContent
import com.pixelro.nenoonkiosk.feature.inspection.macular.mchart.result.MChartInspectionResult
import com.pixelro.nenoonkiosk.feature.inspection.macular.mchart.result.MChartInspectionResultContent
import com.pixelro.nenoonkiosk.feature.inspection.presbyopia.PresbyopiaInspectionResult
import com.pixelro.nenoonkiosk.feature.inspection.presbyopia.result.PresbyopiaInspectionResultContent
import com.pixelro.nenoonkiosk.feature.inspection.visualacuity.result.children.ChildrenVisualAcuityInspectionResult
import com.pixelro.nenoonkiosk.feature.inspection.visualacuity.result.longdistance.LongVisualAcuityInspectionResult
import com.pixelro.nenoonkiosk.feature.inspection.visualacuity.result.shortdistance.ShortDistanceVisualAcuityInspectionResultContent
import com.pixelro.nenoonkiosk.feature.inspection.visualacuity.result.shortdistance.ShortVisualAcuityInspectionResult
import com.pixelro.nenoonkiosk.feature.undeveloped.testresultcontent.ChildrenVisualAcuityInspectionResultContent
import com.pixelro.nenoonkiosk.feature.undeveloped.testresultcontent.LongDistanceVisualAcuityTestResultContent
import kotlinx.coroutines.delay

@Composable
fun InspectionResultRoute(
    surveyId: Long,
    testType: InspectionType,
    testResult: Any?,
    navController: NavHostController,
    onLogout: () -> Unit,
    userData: User?,
    viewModel: InspectionResultViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    var showLoading by remember { mutableStateOf(true) }

    val sharedPreferences = remember {
        context.getSharedPreferences(NavConstants.PREFERENCE_NAME, Context.MODE_PRIVATE)
    }
    val savedLanguage = sharedPreferences.getString("language", "defaultLanguage")

    // 뒤로가기 라우팅
    val navBack: () -> Unit = remember(testType, navController) {
        {
            when (testType) {
                InspectionType.Dementia ->
                    navController.popBackStack(NavConstants.ROUTE_CATEGORY_LIST, false)
                InspectionType.GripStrength, InspectionType.BloodPressure ->
                    navController.popBackStack(NavConstants.ROUTE_EXTERNAL_DEVICE_TEST_LIST, false)
                else ->
                    navController.popBackStack(NavConstants.ROUTE_TEST_LIST, false)
            }
        }
    }
    BackHandler(enabled = true) { navBack() }

    // TTS + 서버전송
    LaunchedEffect(surveyId, testType, testResult, userData?.accessToken) {
        TTS.tts.stop()
        TTS.speechTTS(StringProvider.getString(R.string.tts_end), TextToSpeech.QUEUE_ADD)
        delay(2000)
        showLoading = false
        viewModel.sendResultToServer(
            surveyId = surveyId,
            testType = testType,
            testResult = testResult,
            token = userData?.accessToken,
        )
    }

    // 프린트 핸들러
    val onPrint: () -> Unit = remember(testType, testResult) {
        {
            if (!DebugConstants.USE_NEMONIC_PRINTER) return@remember
            doPrint(context = context, testType = testType, testResult = testResult)
        }
    }

    // 결과 콘텐츠 람다 (기존 디자인 그대로 사용)
    val resultContent: @Composable () -> Unit = remember(testType, testResult) {
        {
            when (testType) {
                InspectionType.Presbyopia -> {
                    PresbyopiaInspectionResultContent(testResult =  testResult as PresbyopiaInspectionResult)
                }
                InspectionType.ShortDistanceVisualAcuity -> {
                    ShortDistanceVisualAcuityInspectionResultContent(
                        inspectionResult = testResult as ShortVisualAcuityInspectionResult,
                        navController = navController,
                    )
                }
                InspectionType.LongDistanceVisualAcuity -> {
                    LongDistanceVisualAcuityTestResultContent(
                        testResult = testResult as LongVisualAcuityInspectionResult,
                        navController = navController,
                    )
                }
                InspectionType.ChildrenVisualAcuity -> {
                    ChildrenVisualAcuityInspectionResultContent(
                        testResult = testResult as ChildrenVisualAcuityInspectionResult,
                        navController = navController,
                    )
                }
                InspectionType.AmslerGrid -> {
                    AmslerGridTestResultContent(
                        testResult = testResult as AmslerGridTestResult,
                    )
                }
                InspectionType.MChart -> {
                    MChartInspectionResultContent(
                        testResult = testResult as MChartInspectionResult,
                    )
                }
                InspectionType.Dementia -> {
                    DementiaInspectionResultRoute(
                        testResult = testResult as com.pixelro.nenoonkiosk.feature.inspection.dementia.DementiaInspectionResult,
                    )
                }
                InspectionType.Presbyopia_Glasses -> {
                    PresbyopiaExerciseResultContent(
                        testResult = testResult as PresbyopiaExerciseResult,
                        navController = navController,
                    )
                }
                InspectionType.Concentration_Glasses -> {
                    ConcentrationExerciseResultContent(
                        testResult = testResult as ConcentrationExerciseResult,
                        navController = navController,
                    )
                }
                InspectionType.GripStrength -> {
                    GripStrengthInspectionResultContent(
                        testResult = testResult as GripStrengthInspectionResultContract,
                    )
                }
                InspectionType.BloodPressure -> {
                    BloodPressureInspectionResultContent(
                        testResult = testResult as BloodPressureInspectionResult,
                    )
                }
                else -> {
                    Text("None TestResultScreen")
                }
            }
        }
    }

    val isDarkBg = remember(testType) {
        testType == InspectionType.Presbyopia_Glasses || testType == InspectionType.Concentration_Glasses
    }

    val titleText = remember(testType) { titleFor(testType) }

    val aiCommentText = remember(testType, testResult) {
        if (testResult != null) aiComment(testType = testType, testResult = testResult) else null
    }

    InspectionResultScreen(
        titleText = titleText,
        isDarkBackground = isDarkBg,
        showLoading = showLoading,
        savedLanguage = savedLanguage,
        resultContent = resultContent,
        aiCommentText = aiCommentText,
        printEnabled = DebugConstants.USE_NEMONIC_PRINTER,
        onPrint = onPrint,
        onBack = navBack,
        onLogout = onLogout,
    )
}

private fun doPrint(
    context: Context,
    testType: InspectionType,
    testResult: Any?,
) {
    val (printerType, printerMacAddress) = PrinterManager.getPrinterInfo()
    val nPrinterController = PrinterManager.getPrinterController()

    if (printerMacAddress.isEmpty() || nPrinterController == null) {
        Toast.makeText(context, StringProvider.getString(R.string.not_exist_print), Toast.LENGTH_SHORT).show()
        Log.d("TestResultScreen", "Printer MAC Address is empty or controller is null")
        return
    }

    val printer = NPrinter(printerType ?: NPrinterType.NEMONIC_MIP201, "Printer", printerMacAddress)
    Log.d(
        "TestResultScreen",
        "Printer Info: ${printer.getType()}, ${printer.getName()}, ${printer.getMacAddress()}, ${nPrinterController.printerStatus}",
    )

    try {
        nPrinterController.connectDelay = 2000 // 2 seconds

        val resources = context.resources
        val logoImg = BitmapFactory.decodeResource(resources, R.drawable.pixelro_logo_black)
            .scale(240, 80, false)
        val qrImg =
            BitmapFactory.decodeResource(resources, R.drawable.qrcode_home_en).scale(80, 80, false)
        val bm: Bitmap = textAsBitmap(testType, testResult, logoImg, qrImg)

        nPrinterController.print(
            NPrintInfo(printer, bm).apply {
                copies = 1
                isEnableDither = true
            },
        )
        Log.d("TestResultScreen", "Print command sent successfully.")
    } catch (e: Exception) {
        Log.e("TestResultScreen", "Error during print command: ${e.message}")
    }
}