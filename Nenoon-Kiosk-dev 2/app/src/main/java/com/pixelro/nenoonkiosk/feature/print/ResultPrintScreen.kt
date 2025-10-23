package com.pixelro.nenoonkiosk.feature.print

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.harang.data.model.User
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.constants.AppConstants
import com.pixelro.nenoonkiosk.core.constants.DebugConstants
import com.pixelro.nenoonkiosk.core.constants.GlobalValue
import com.pixelro.nenoonkiosk.core.constants.NavConstants
import com.pixelro.nenoonkiosk.core.manager.SharedPreferencesManager
import com.pixelro.nenoonkiosk.core.ui.PrimaryButton
import com.pixelro.nenoonkiosk.core.util.StringProvider
import com.pixelro.nenoonkiosk.core.util.dataprovider.CompoundTestResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun ResultPrintScreen(
    surveyId: Long,
    navController: NavController,
    qrCode: Bitmap?,
    userData: User?,
    viewModel: ResultPrintViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var compoundTestResults by remember { mutableStateOf<List<CompoundTestResult>>(emptyList()) }
    val sharedPreferences = remember { context.getSharedPreferences(NavConstants.PREFERENCE_NAME, Context.MODE_PRIVATE) }
    val locale = sharedPreferences.getString("language", "defaultLanguage")

    LaunchedEffect(Unit) {
        coroutineScope.launch(Dispatchers.IO) {
            if (AppConstants.MANAGE_USERS_INTERNALLY) {
                val compoundResultFromPrefs = SharedPreferencesManager.getCompoundTestResult(AppConstants.DEFAULT_USER_ID)
                compoundTestResults = emptyList()
            } else if (DebugConstants.PRINTER_USE_DUMMY_DATA) {
                compoundTestResults = listOf(CompoundTestResult(viewModel.createDummyCompoundTestResult()))
            } else {
                val results = viewModel.getCompoundTestResult(userData?.accessToken ?: "")
                compoundTestResults = results.map { CompoundTestResult(it) }
            }
        }
    }

    // Get the latest result to display on the screen
    val latestResult = compoundTestResults.firstOrNull()

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
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
                        contentDescription = "",
                    )
                }
                Box(
                    modifier =
                        Modifier
                            .fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = StringProvider.getString(id = R.string.result_print_screen_result_title),
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
                modifier =
                    Modifier
                        .padding(40.dp)
                        .fillMaxWidth(),
            ) {
                Row(
                    modifier =
                        Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                ) {
                    latestResult?.let { result ->
                        Column(
                            verticalArrangement = Arrangement.Top,
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.weight(1f),
                        ) {
                            ResultSection(title = StringProvider.getString(id = R.string.result_print_screen_visual_acuity_test), content = {
                                Text(
                                    text = if (result.shortVisualAcuityTestResult != null) StringProvider.getString(R.string.result_print_screen_test_completed) else StringProvider.getString(R.string.result_print_screen_test_not_conducted),
                                    fontSize = 24.sp,
                                    color = if (result.shortVisualAcuityTestResult != null) colorResource(R.color.main) else colorResource(R.color.error),
                                )
                            })

                            ResultSection(title = StringProvider.getString(id = R.string.result_print_screen_presbyopia_test), content = {
                                Text(
                                    text = if (result.presbyopiaTestResult != null) StringProvider.getString(R.string.result_print_screen_test_completed) else StringProvider.getString(R.string.result_print_screen_test_not_conducted),
                                    fontSize = 24.sp,
                                    color = if (result.presbyopiaTestResult != null) colorResource(R.color.main) else colorResource(R.color.error),
                                )
                            })

                            ResultSection(title = StringProvider.getString(id = R.string.result_print_screen_amsler_grid_test), content = {
                                Text(
                                    text = if (result.amslerGridTestResult != null) StringProvider.getString(R.string.result_print_screen_test_completed) else StringProvider.getString(R.string.result_print_screen_test_not_conducted),
                                    fontSize = 24.sp,
                                    color = if (result.amslerGridTestResult != null) colorResource(R.color.main) else colorResource(R.color.error),
                                )
                            })

                            ResultSection(title = StringProvider.getString(id = R.string.result_print_screen_m_chart_test), content = {
                                Text(
                                    text = if (result.mChartTestResult != null) StringProvider.getString(R.string.result_print_screen_test_completed) else StringProvider.getString(R.string.result_print_screen_test_not_conducted),
                                    fontSize = 24.sp,
                                    color = if (result.mChartTestResult != null) colorResource(R.color.main) else colorResource(R.color.error),
                                )
                            })

//                            ResultSection(title = StringProvider.getString(id = R.string.phoria_test), content = {
//                                Text(
//                                    text = if (result.sawiTestResult != null) StringProvider.getString(R.string.result_print_screen_test_completed) else StringProvider.getString(R.string.result_print_screen_test_not_conducted),
//                                    fontSize = 24.sp,
//                                    color = if (result.sawiTestResult != null) colorResource(R.color.main) else colorResource(R.color.error)
//                                )
//                            })
                        }

                        Spacer(modifier = Modifier.width(20.dp))

                        Column(
                            verticalArrangement = Arrangement.Top,
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.weight(1f),
                        ) {
                            ResultSection(title = StringProvider.getString(id = R.string.result_print_screen_blood_pressure_test), content = {
                                Text(
                                    text = if (result.bloodPressureTestResult != null) StringProvider.getString(R.string.result_print_screen_test_completed) else StringProvider.getString(R.string.result_print_screen_test_not_conducted),
                                    fontSize = 24.sp,
                                    color = if (result.bloodPressureTestResult != null) colorResource(R.color.main) else colorResource(R.color.error),
                                )
                            })

                            ResultSection(title = StringProvider.getString(id = R.string.result_print_screen_grip_strength_test), content = {
                                Text(
                                    text = if (result.gripStrengthTestResult != null) StringProvider.getString(R.string.result_print_screen_test_completed) else StringProvider.getString(R.string.result_print_screen_test_not_conducted),
                                    fontSize = 24.sp,
                                    color = if (result.gripStrengthTestResult != null) colorResource(R.color.main) else colorResource(R.color.error),
                                )
                            })

                            ResultSection(title = StringProvider.getString(id = R.string.result_print_screen_dementia_survey), content = {
                                Text(
                                    text = if (result.dementiaTestResult != null) StringProvider.getString(R.string.result_print_screen_test_completed) else StringProvider.getString(R.string.result_print_screen_test_not_conducted),
                                    fontSize = 24.sp,
                                    color = if (result.dementiaTestResult != null) colorResource(R.color.main) else colorResource(R.color.error),
                                )
                            })

                            ResultSection(title = StringProvider.getString(id = R.string.result_print_screen_pulmonary_function_test), content = {
                                Text(
                                    text = if (result.pulmonaryFunctionTestResult != null) StringProvider.getString(R.string.result_print_screen_test_completed) else StringProvider.getString(R.string.result_print_screen_test_not_conducted),
                                    fontSize = 24.sp,
                                    color = if (result.pulmonaryFunctionTestResult != null) colorResource(R.color.main) else colorResource(R.color.error),
                                )
                            })

//                            ResultSection(title = StringProvider.getString(id = R.string.aniseikonia_test), content = {
//                                Text(
//                                    text = if (result.fudoTestResult != null) StringProvider.getString(R.string.result_print_screen_test_completed) else StringProvider.getString(R.string.result_print_screen_test_not_conducted),
//                                    fontSize = 24.sp,
//                                    color = if (result.fudoTestResult != null) colorResource(R.color.main) else colorResource(R.color.error)
//                                )
//                            })
                        }
                    } ?: run {
                        Text(
                            text = StringProvider.getString(id = R.string.result_print_screen_loading_results),
                            fontSize = 28.sp,
                            textAlign = TextAlign.Center,
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 50.dp),
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
                PrimaryButton(
                    onClick = {
                        coroutineScope.launch(Dispatchers.IO) {
                            // Pass the entire list of results to the print function
                            if (compoundTestResults.isNotEmpty()) {
                                viewModel.printContent(context, compoundTestResults, qrCode, locale, userData)
                            }
                        }
                    },
                    text = StringProvider.getString(id = R.string.result_print_screen_print_button),
                )
            }
        }
    }
}

@Composable
private fun ResultSection(
    title: String,
    content: @Composable () -> Unit,
) {
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp)
                .border(1.dp, colorResource(R.color.gray2), MaterialTheme.shapes.small)
                .padding(15.dp),
        horizontalAlignment = Alignment.Start,
    ) {
        Text(
            text = title,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp),
        )
        content()
    }
}
