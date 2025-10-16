package com.pixelro.nenoonkiosk.feature.screen

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.pixelro.nenoonkiosk.NenoonViewModel
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.TTS
import com.pixelro.nenoonkiosk.constants.NavConstants
import com.pixelro.nenoonkiosk.constants.GlobalValue
import com.pixelro.nenoonkiosk.util.StringProvider
import com.pixelro.nenoonkiosk.util.dataprovider.TestType

//검사할 때 뷰
@SuppressLint("NewApi")
@Composable
fun TestScreen(
    viewModel: NenoonViewModel,
    navController: NavHostController,
    content: @Composable () -> Unit
) {

    val selectedTestType = viewModel.selectedTestType.collectAsState().value

    BackHandler(enabled = true) {
        when (selectedTestType) {
            TestType.Dementia -> navController.popBackStack(NavConstants.ROUTE_CATEGORY_LIST, false)
            TestType.GripStrength, TestType.BloodPressure -> navController.popBackStack(NavConstants.ROUTE_EXTERNAL_DEVICE_TEST_LIST, false)
            else -> navController.popBackStack(NavConstants.ROUTE_TEST_LIST, false)
        }
        viewModel.resetScreenSaverTimer()
    }
    val systemUiController = rememberSystemUiController()
    DisposableEffect(true) {
        viewModel.updateScreenSaverTimerValue(2147483647)
        systemUiController.systemBarsDarkContentEnabled = false
        onDispose {
            viewModel.updateScreenSaverTimerValue(60)
            viewModel.resetScreenSaverTimer()
            systemUiController.systemBarsDarkContentEnabled = true
        }
    }
    val context = LocalContext.current
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    color = when (selectedTestType) {
                        TestType.Dementia, TestType.GripStrength, TestType.BloodPressure -> Color(0xffffffff)
                        else -> Color(0xff000000)
                    }
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            /**
             * 상단 바
             */
            Box(
                modifier = Modifier
                    .padding(
                        start = 40.dp,
                        top = (GlobalValue.statusBarPadding + 20).dp,
                        end = 40.dp,
                        bottom = 20.dp
                    )
                    .fillMaxWidth()
                    .height(40.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Image(
                        modifier = Modifier
                            .width(32.dp)
                            .clickable {
                                when (selectedTestType) {
                                    TestType.Dementia -> {
                                        navController.popBackStack(NavConstants.ROUTE_CATEGORY_LIST, false)
                                    }
                                    TestType.GripStrength, TestType.BloodPressure -> {
                                        navController.popBackStack(NavConstants.ROUTE_EXTERNAL_DEVICE_TEST_LIST, false)
                                    }
                                    TestType.Presbyopia_Glasses, TestType.Concentration_Glasses -> {
                                        navController.popBackStack(NavConstants.ROUTE_EXERCISE_LIST, false)
                                    }
                                    else -> {
                                        TTS.tts.stop()
                                        navController.popBackStack(NavConstants.ROUTE_TEST_LIST, false)
                                    }
                                }
                            },
                        painter =when (selectedTestType) {
                            TestType.Dementia, TestType.GripStrength, TestType.BloodPressure -> {
                                painterResource(id = R.drawable.close_button_black)
                            }
                            else -> painterResource(id = R.drawable.close_button)
                        },
                        contentDescription = ""
                    )
                }
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = when (selectedTestType) {
                            TestType.Presbyopia -> StringProvider.getString(
                                R.string.presbyopia_name,
                                
                            )
                            TestType.ShortDistanceVisualAcuity -> StringProvider.getString(
                                R.string.short_visual_acuity_name2,
                                
                            )
                            TestType.LongDistanceVisualAcuity -> StringProvider.getString(
                                R.string.long_visual_acuity_name,
                                
                            )
                            TestType.ChildrenVisualAcuity -> StringProvider.getString(
                                R.string.children_visual_acuity_name,
                                
                            )
                            TestType.AmslerGrid -> StringProvider.getString(
                                R.string.amsler_grid_name,
                                
                            )
                            TestType.MChart -> StringProvider.getString(
                                R.string.mchart_name,
                                
                            )
                            TestType.Dementia -> StringProvider.getString(
                                R.string.dementia_title,
                                
                            )
                            TestType.Presbyopia_Glasses -> StringProvider.getString(
                                R.string.presbyopia_glasses,
                                
                            )
                            TestType.Concentration_Glasses -> StringProvider.getString(
                                R.string.concentration_glasses_title,
                                
                            )
                            TestType.GripStrength -> StringProvider.getString(R.string.grip_strength_test)
                            TestType.BloodPressure -> StringProvider.getString(R.string.blood_pressure_test)
                            else -> ""
                        },
                        color = when (selectedTestType) {
                            TestType.Dementia, TestType.GripStrength, TestType.BloodPressure -> Color(0xff000000)
                            else -> Color(0xffffffff)
                        },
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            Spacer(
                modifier = Modifier
                    .padding(bottom = 5.dp, start = 5.dp, end = 5.dp)
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(
                        color = Color(0xff000000)
                    )
            )
            /**
             * 검사 내용
             */
            content()
        }
    }
}