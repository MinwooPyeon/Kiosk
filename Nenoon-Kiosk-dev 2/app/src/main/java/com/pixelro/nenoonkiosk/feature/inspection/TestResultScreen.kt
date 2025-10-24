package com.pixelro.nenoonkiosk.feature.inspection

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.speech.tts.TextToSpeech
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.harang.data.model.User
import com.mangoslab.nemonicsdk.NPrintInfo
import com.mangoslab.nemonicsdk.NPrinter
import com.mangoslab.nemonicsdk.constants.NPrinterType
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.constants.DebugConstants
import com.pixelro.nenoonkiosk.core.constants.GlobalValue
import com.pixelro.nenoonkiosk.core.constants.NavConstants
import com.pixelro.nenoonkiosk.core.manager.PrinterManager
import com.pixelro.nenoonkiosk.core.util.StringProvider
import com.pixelro.nenoonkiosk.core.util.TTS
import com.pixelro.nenoonkiosk.core.util.dataprovider.TestType
import com.pixelro.nenoonkiosk.feature.exerciseglasses.concentration_exercise.ConcentrationExerciseResult
import com.pixelro.nenoonkiosk.feature.exerciseglasses.concentration_exercise.ConcentrationExerciseResultContent
import com.pixelro.nenoonkiosk.feature.exerciseglasses.presbyopia_exercise.PresbyopiaExerciseResult
import com.pixelro.nenoonkiosk.feature.exerciseglasses.presbyopia_exercise.PresbyopiaExerciseResultContent
import com.pixelro.nenoonkiosk.feature.inspection.bloodPressure.BloodPressureTestResult
import com.pixelro.nenoonkiosk.feature.inspection.bloodPressure.BloodPressureTestResultContent
import com.pixelro.nenoonkiosk.feature.inspection.dementia.DementiaTestResult
import com.pixelro.nenoonkiosk.feature.inspection.dementia.DementiaTestResultContent
import com.pixelro.nenoonkiosk.feature.inspection.gripStrength.GripStrengthTestResult
import com.pixelro.nenoonkiosk.feature.inspection.gripStrength.GripStrengthTestResultContent
import com.pixelro.nenoonkiosk.feature.inspection.macular.amslergrid.AmslerGridTestResult
import com.pixelro.nenoonkiosk.feature.inspection.macular.amslergrid.AmslerGridTestResultContent
import com.pixelro.nenoonkiosk.feature.inspection.macular.mchart.MChartTestResult
import com.pixelro.nenoonkiosk.feature.inspection.macular.mchart.MChartTestResultContent
import com.pixelro.nenoonkiosk.feature.inspection.presbyopia.PresbyopiaTestResult
import com.pixelro.nenoonkiosk.feature.inspection.presbyopia.PresbyopiaTestResultContent
import com.pixelro.nenoonkiosk.feature.inspection.pulmonaryFunction.PulmonaryFunctionTestResult
import com.pixelro.nenoonkiosk.feature.inspection.pulmonaryFunction.PulmonaryFunctionTestResultTestResultContent
import com.pixelro.nenoonkiosk.feature.inspection.result.AiComment
import com.pixelro.nenoonkiosk.feature.inspection.result.TestResultUtil.textAsBitmap
import com.pixelro.nenoonkiosk.feature.inspection.result.TestResultViewModel
import com.pixelro.nenoonkiosk.feature.inspection.visualacuity.children.ChildrenVisualAcuityTestResult
import com.pixelro.nenoonkiosk.feature.inspection.visualacuity.longdistance.LongVisualAcuityTestResult
import com.pixelro.nenoonkiosk.feature.inspection.visualacuity.shortdistance.ShortDistanceVisualAcuityTestResultContent
import com.pixelro.nenoonkiosk.feature.inspection.visualacuity.shortdistance.ShortVisualAcuityTestResult
import com.pixelro.nenoonkiosk.feature.screen.TypewriterText
import com.pixelro.nenoonkiosk.feature.testresultcontent.ChildrenVisualAcuityTestResultContent
import com.pixelro.nenoonkiosk.feature.testresultcontent.LongDistanceVisualAcuityTestResultContent
import kotlinx.coroutines.delay

@Composable
fun TestResultScreen(
    surveyId: Long,
    testType: TestType,
    testResult: Any?,
    navController: NavHostController,
    testResultViewModel: TestResultViewModel = hiltViewModel(),
    onLogout: () -> Unit,
    userData: User?,
) {
    var showLoading by remember { mutableStateOf(true) }

    fun navBack() {
        when (testType) {
            TestType.Dementia, TestType.PulmonaryFunction -> navController.popBackStack(NavConstants.ROUTE_CATEGORY_LIST, false)
            TestType.GripStrength, TestType.BloodPressure -> navController.popBackStack(NavConstants.ROUTE_EXTERNAL_DEVICE_TEST_LIST, false)
            else -> navController.popBackStack(NavConstants.ROUTE_TEST_LIST, false)
        }
    }

    BackHandler(enabled = true) {
        navBack()
    }

    LaunchedEffect(Unit) {
        TTS.tts.stop()
        TTS.speechTTS(StringProvider.getString(R.string.tts_end), TextToSpeech.QUEUE_ADD)
        delay(2000)
        showLoading = false
        testResultViewModel.sendResultToServer(
            surveyId = surveyId,
            testType = testType,
            testResult = testResult,
            token = userData?.accessToken,
        )
    }

    val context = LocalContext.current

    val sharedPreferences =
        remember { context.getSharedPreferences(NavConstants.PREFERENCE_NAME, Context.MODE_PRIVATE) }
    val savedLanguage =
        sharedPreferences.getString("language", "defaultLanguage")

    fun printResult(
        testType: TestType,
        testResult: Any?,
    ) {
        var printerInfo = PrinterManager.getPrinterInfo()
        var printerType = printerInfo.first
        var printerMacAddress = printerInfo.second
        var nPrinterController = PrinterManager.getPrinterController()

        if (printerMacAddress.isEmpty() || nPrinterController == null) {
            Toast.makeText(context, StringProvider.getString(R.string.not_exist_print), Toast.LENGTH_SHORT).show()
            Log.d("TestResultScreen", "Printer MAC Address is empty")
            return
        }

        val printer =
            NPrinter(printerType ?: NPrinterType.NEMONIC_MIP201, "Printer", printerMacAddress)
        Log.d(
            "TestResultScreen",
            "Printer Info: ${printer.getType()}, ${printer.getName()}, ${printer.getMacAddress()}, ${nPrinterController.printerStatus}",
        )

        try {
            nPrinterController.connectDelay = 2000 // 2 seconds delay
            Log.d("TestResultScreen", "Custom Connect Delay set to 2000 ms")

            val resources = context.resources
            val logoImg =
                Bitmap.createScaledBitmap(
                    BitmapFactory.decodeResource(resources, R.drawable.pixelro_logo_black),
                    240,
                    80,
                    false,
                )
            val qrImg =
                Bitmap.createScaledBitmap(
                    BitmapFactory.decodeResource(resources, R.drawable.qrcode_home_en),
                    80,
                    80,
                    false,
                )
            val bm = textAsBitmap(testType, testResult, logoImg, qrImg)
            val nPrintWidth = 576
            val nPaperHeight = ((bm.height.toFloat() / bm.width.toFloat()) * 576).toInt()

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

    Column(
        modifier =
            when (testType) {
                TestType.Presbyopia_Glasses,
                TestType.Concentration_Glasses,
                -> {
                    Modifier
                        .fillMaxSize()
                        .background(color = Color(0xff000000))
                }

                else -> {
                    Modifier.fillMaxSize()
                }
            },
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        val printString = testResultViewModel.printString.collectAsState().value
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
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text =
                    when (testType) {
                        TestType.Presbyopia ->
                            StringProvider.getString(
                                R.string.presbyopia_result_title,
                            )
                        TestType.ShortDistanceVisualAcuity ->
                            StringProvider.getString(
                                R.string.short_visual_acuity_result_title,
                            )
                        TestType.LongDistanceVisualAcuity ->
                            StringProvider.getString(
                                R.string.long_visual_acuity_result_title,
                            )
                        TestType.ChildrenVisualAcuity ->
                            StringProvider.getString(
                                R.string.children_visual_acuity_result_title,
                            )
                        TestType.AmslerGrid ->
                            StringProvider.getString(
                                R.string.amsler_grid_result_title,
                            )
                        TestType.MChart ->
                            StringProvider.getString(
                                R.string.mchart_result_title,
                            )
                        TestType.Dementia ->
                            StringProvider.getString(
                                R.string.dementia_result_title,
                            )
                        TestType.Presbyopia_Glasses ->
                            StringProvider.getString(
                                R.string.presbyopia_glasses_result_title,
                            )
                        TestType.Concentration_Glasses ->
                            StringProvider.getString(
                                R.string.concentration_glasses_result_title,
                            )
                        TestType.PulmonaryFunction ->
                            StringProvider.getString(
                                R.string.pulmonary_function_test_result,
                            )
                        TestType.GripStrength -> StringProvider.getString(R.string.grip_strength_result)
                        TestType.BloodPressure -> StringProvider.getString(R.string.blood_pressure_result)
                        else -> {
                            "None TestResultScreen"
                        }
                    },
                color =
                    when (testType) {
                        TestType.Presbyopia_Glasses,
                        TestType.Concentration_Glasses,
                        -> Color(0xffffffff)

                        else -> Color(0xff000000)
                    },
                fontSize = 24.sp,
                fontWeight = FontWeight.Medium,
            )
        }
        Spacer(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(color = Color(0xffebebeb)),
        )

        if (showLoading) {
            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .background(color = Color.White),
                contentAlignment = Alignment.Center,
            ) {
                Image(
                    painter =
                        painterResource(
                            if (savedLanguage == "ko") {
                                R.drawable.loading_icon
                            } else {
                                R.drawable.loading_icon_en
                            },
                        ),
                    contentDescription =
                        StringProvider.getString(
                            R.string.tts_wait_for_result,
                        ),
                    modifier =
                        Modifier
                            .size(600.dp),
                )
            }
        } else {
            when (testType) {
                TestType.Presbyopia -> {
                    PresbyopiaTestResultContent(
                        testResult = testResult as PresbyopiaTestResult,
                    )
                }

                TestType.ShortDistanceVisualAcuity -> {
                    ShortDistanceVisualAcuityTestResultContent(
                        testResult = testResult as ShortVisualAcuityTestResult,
                        navController = navController,
                    )
                }

                TestType.LongDistanceVisualAcuity -> {
                    LongDistanceVisualAcuityTestResultContent(
                        testResult = testResult as LongVisualAcuityTestResult,
                        navController = navController,
                    )
                }

                TestType.ChildrenVisualAcuity -> {
                    ChildrenVisualAcuityTestResultContent(
                        testResult = testResult as ChildrenVisualAcuityTestResult,
                        navController = navController,
                    )
                }

                TestType.AmslerGrid -> {
                    AmslerGridTestResultContent(
                        testResult = testResult as AmslerGridTestResult,
                        navController = navController,
                    )
                }

                TestType.MChart -> {
                    MChartTestResultContent(
                        testResult = testResult as MChartTestResult,
                        navController = navController,
                    )
                }

                TestType.Dementia -> {
                    DementiaTestResultContent(
                        testResult = testResult as DementiaTestResult,
                        navController = navController,
                    )
                }

                TestType.Presbyopia_Glasses -> {
                    PresbyopiaExerciseResultContent(
                        testResult = testResult as PresbyopiaExerciseResult,
                        navController = navController,
                    )
                }

                TestType.Concentration_Glasses -> {
                    ConcentrationExerciseResultContent(
                        testResult = testResult as ConcentrationExerciseResult,
                        navController = navController,
                    )
                }

                TestType.GripStrength -> {
                    GripStrengthTestResultContent(
                        testResult = testResult as GripStrengthTestResult,
                        navController = navController,
                    )
                }

                TestType.BloodPressure -> {
                    BloodPressureTestResultContent(
                        testResult = testResult as BloodPressureTestResult,
                        navController = navController,
                    )
                }

                TestType.PulmonaryFunction -> {
                    val result = testResult as PulmonaryFunctionTestResult
                    PulmonaryFunctionTestResultTestResultContent(
                        testResult = result,
                    )
                }

                else -> {
                    Text("None TestResultScreen")
                }
            }

            if (testResult != null) {
                TypewriterText(
                    text = AiComment(testType = testType, testResult = testResult),
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
                )
                Spacer(modifier = Modifier.height(40.dp))
            }

            Spacer(modifier = Modifier.weight(1f))
            when (testType) {
                TestType.Presbyopia_Glasses,
                TestType.Concentration_Glasses,
                -> {
                    Box(
                        modifier =
                            Modifier
                                .fillMaxSize(),
                        contentAlignment = Alignment.BottomCenter,
                    ) {
                        Row(
                            modifier =
                                Modifier
                                    .padding(bottom = 200.dp)
                                    .height(100.dp),
                        ) {
                            Box(
                                modifier =
                                    Modifier
                                        .padding(
                                            start = 40.dp,
                                            end = 40.dp,
                                            bottom = 20.dp,
                                        )
                                        .width(340.dp)
                                        .height(120.dp)
                                        .clip(shape = RoundedCornerShape(8.dp))
                                        .background(
                                            color = Color(0xffffffff),
                                            shape = RoundedCornerShape(8.dp),
                                        )
                                        .clickable {
                                            navBack()
                                        },
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    text =
                                        StringProvider.getString(
                                            R.string.retest,
                                        ),
                                    fontSize = 30.sp,
                                    fontWeight = FontWeight.Bold,
                                )
                            }

                            Box(
                                modifier =
                                    Modifier
                                        .padding(
                                            start = 40.dp,
                                            end = 40.dp,
                                            bottom = 20.dp,
                                        )
                                        .width(340.dp)
                                        .height(120.dp)
                                        .clip(shape = RoundedCornerShape(8.dp))
                                        .background(
                                            color = Color(0xffffffff),
                                            shape = RoundedCornerShape(8.dp),
                                        )
                                        .clickable {
                                            navBack()
                                        },
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    text =
                                        StringProvider.getString(
                                            R.string.to_beginning,
                                        ),
                                    fontSize = 30.sp,
                                    fontWeight = FontWeight.Bold,
                                )
                            }

                            Box(
                                modifier =
                                    Modifier
                                        .padding(
                                            start = 40.dp,
                                            end = 40.dp,
                                        )
                                        .width(340.dp)
                                        .height(120.dp)
                                        .clip(shape = RoundedCornerShape(8.dp))
                                        .background(
                                            color = Color(0xffffffff),
                                            shape = RoundedCornerShape(8.dp),
                                        )
                                        .clickable {
                                            onLogout()
                                        },
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    text =
                                        StringProvider.getString(
                                            R.string.settings_signout,
                                        ),
                                    fontSize = 30.sp,
                                    fontWeight = FontWeight.Bold,
                                )
                            }
                        }
                    }
                }
                else -> {
                    Text(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(start = 40.dp, end = 40.dp),
                        text =
                            buildAnnotatedString {
                                withStyle(style = SpanStyle(color = Color(0xff999999), fontSize = 16.sp)) {
                                    append(
                                        StringProvider.getString(
                                            R.string.test_list_screen_warning1,
                                        ),
                                    )
                                }
                                withStyle(style = SpanStyle(color = Color(0xffff0000), fontSize = 16.sp, fontWeight = FontWeight.Bold)) {
                                    append(
                                        StringProvider.getString(
                                            R.string.test_list_screen_warning2,
                                        ),
                                    )
                                }
                                withStyle(style = SpanStyle(color = Color(0xff999999), fontSize = 16.sp)) {
                                    append(
                                        StringProvider.getString(
                                            R.string.test_list_screen_warning3,
                                        ),
                                    )
                                }
                            },
                    )
                    Box(
                        modifier =
                            Modifier
                                .padding(bottom = 40.dp)
                                .fillMaxSize(),
                        contentAlignment = Alignment.BottomCenter,
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            if (DebugConstants.USE_NEMONIC_PRINTER) {
                                Box(
                                    modifier =
                                        Modifier
                                            .padding(start = 40.dp, end = 40.dp, bottom = 20.dp)
                                            .fillMaxWidth()
                                            .clip(shape = RoundedCornerShape(8.dp))
                                            .border(
                                                border = BorderStroke(1.dp, Color(0xffc3c3c3)),
                                                shape = RoundedCornerShape(8.dp),
                                            )
                                            .clickable {
//                                            coroutineScope.launch {
//                                                bluetoothAdapter.startDiscovery()
                                                printResult(
                                                    testType = testType,
                                                    testResult = testResult,
                                                )
//                                            }
                                            },
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Image(
                                            modifier = Modifier.width(28.dp),
                                            painter = painterResource(id = R.drawable.icon_print),
                                            contentDescription = "",
                                        )
                                        Text(
                                            modifier = Modifier.padding(20.dp),
                                            text =
                                                StringProvider.getString(
                                                    R.string.result_button1_print,
                                                ),
                                            fontSize = 24.sp,
                                            fontWeight = FontWeight.Medium,
                                        )
                                    }
                                }
                            }
                            Box(
                                modifier =
                                    Modifier
                                        .padding(start = 40.dp, end = 40.dp, bottom = 20.dp)
                                        .fillMaxWidth()
                                        .clip(shape = RoundedCornerShape(8.dp))
                                        .border(
                                            border = BorderStroke(1.dp, Color(0xffc3c3c3)),
                                            shape = RoundedCornerShape(8.dp),
                                        )
                                        .clickable {
                                            navBack()
                                        },
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    modifier = Modifier.padding(20.dp),
                                    text =
                                        StringProvider.getString(
                                            R.string.result_dementia_back,
                                        ),
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Medium,
                                )
                            }
                            Box(
                                modifier =
                                    Modifier
                                        .padding(start = 40.dp, end = 40.dp)
                                        .fillMaxWidth()
                                        .clip(shape = RoundedCornerShape(8.dp))
                                        .border(
                                            border = BorderStroke(1.dp, Color(0xffc3c3c3)),
                                            shape = RoundedCornerShape(8.dp),
                                        )
                                        .clickable {
                                            onLogout()
                                        },
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    modifier = Modifier.padding(20.dp),
                                    text =
                                        StringProvider.getString(
                                            R.string.settings_signout,
                                        ),
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Medium,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
