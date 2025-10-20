package com.pixelro.nenoonkiosk.feature.categorylist

import android.app.Activity
import android.content.Intent
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.util.TTS
import com.pixelro.nenoonkiosk.core.util.StringProvider
import com.pixelro.nenoonkiosk.core.util.dataprovider.TestType
import com.pixelro.nenoonkiosk.core.ui.Logo
import com.pixelro.nenoonkiosk.core.ui.TestSelectionButton
import com.pixelro.nenoonkiosk.feature.inspection.pulmonaryFunction.PulmonaryFunctionTestResult
import kotlinx.coroutines.delay

//치매, 눈 검사 검사
@OptIn(ExperimentalFoundationApi::class, ExperimentalAnimationApi::class)
@Composable
fun CategoryListScreen(
    pid: Int,
    isSignInSkipped: () -> Boolean,
    toEyeTestScreen: () -> Unit,
    toContact: () -> Unit,
    toDementiaTestScreen: (TestType) -> Unit,
    toExternalDeviceTestListScreen: () -> Unit,
    toStrabismusTestListScreen: () -> Unit,
    toIntroScreen: () -> Unit,
    toPrintScreen: () -> Unit,
    toAccountManagementScreen: () -> Unit,
    toPulmonaryTestResultScreen: (PulmonaryFunctionTestResult) -> Unit,
    toSettingsScreen: () -> Unit,
) {
    val context = LocalContext.current
    val pagerState = rememberPagerState(
        initialPage = 50000
    )
    val isDescriptionShowing = remember { mutableStateOf(true) }
    LaunchedEffect(true) {
//        exoPlayer.release()
        TTS.tts.stop()
        while (true) {
            delay(5000)
            pagerState.animateScrollToPage(
                page = (pagerState.currentPage + 1),
                animationSpec = tween(1000)
            )
            for (i in 1..3) {
                isDescriptionShowing.value = false
                delay(250)
                isDescriptionShowing.value = true
                delay(250)
            }
        }
    }
    rememberInfiniteTransition(label = "")

    val activityResultLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val isGood = result.data?.getBooleanExtra("isGood", false)
            val lungAge = result.data?.getDoubleExtra("lungAge", -1.0)
            val capacity = result.data?.getDoubleExtra("capacity", -1.0)
            val capacityPredict = result.data?.getDoubleExtra("capacityPredict", -1.0)
            val power = result.data?.getDoubleExtra("power", -1.0)
            val powerPredict = result.data?.getDoubleExtra("powerPredict", -1.0)
            val deviceID = result.data?.getStringExtra("deviceID")

            Log.d("CallTestApp", "Received result: $isGood, $lungAge, $capacity, $capacityPredict, $power, $powerPredict, $deviceID")
            val result = PulmonaryFunctionTestResult()
            isGood?.let { result.pulmonaryStatus = it }
            capacity?.let{ result.pulmonaryCapacity = it }
            power?.let{ result.pulmonaryPower = it }
            lungAge?.let{result.pulmonaryAge = it.toInt()}
            toPulmonaryTestResultScreen(result)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                color = Color(0xff1d71e1)
            ),
    ) {

        Spacer(modifier = Modifier.weight(1f))

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Logo(true)
        }

        Spacer(modifier = Modifier.weight(1f))

        TestSelectionButton(
            modifier = Modifier
                .height(100.dp),
            title1 = StringProvider.getString(
                R.string.blood_pressure_and_grip_strength),
            title2 = "",
            onClickMethod = {
                toExternalDeviceTestListScreen()
            },
            alignment = Alignment.Center,
            isDone = false,
            isSenior = false,
            time = 0,
            icon = R.drawable.blood_pressure_and_grip_strength_icon
        )
        Spacer(
            modifier = Modifier
                .height(20.dp)
        )
        TestSelectionButton(
            modifier = Modifier
                .height(100.dp),
            title1 = StringProvider.getString(R.string.eye_test, ),
            title2 = "",
            onClickMethod = {
                toEyeTestScreen()
            },
            alignment = Alignment.Center,
            isDone = false,
            isSenior = false,
            time = 0,
            icon = R.drawable.eye_test_icon
        )
        Spacer(
            modifier = Modifier
                .height(20.dp)
        )
        TestSelectionButton(
            modifier = Modifier
                .height(100.dp),
            title1 = StringProvider.getString(
                R.string.pulmonary_function_test),
            title2 = "",
            onClickMethod = {
                val intent = Intent("android.intent.action.BREATHINGS").apply {
                    putExtra("id", pid.toString())
                    putExtra("height", 0)
                    putExtra("birthday", 0)
                    putExtra("weight", 0)
                    putExtra("gender", "m")
                }
                activityResultLauncher.launch(intent)
            },
            alignment = Alignment.Center,
            isDone = false,
            isSenior = false,
            time = 0,
            icon = R.drawable.lung_age_icon
        )
        Spacer(
            modifier = Modifier
                .height(20.dp)
        )
        TestSelectionButton(
            modifier = Modifier
                .height(100.dp),
            title1 = StringProvider.getString(R.string.cross_eye_test),
            title2 = "",
            onClickMethod = {
                toStrabismusTestListScreen()
            },
            alignment = Alignment.Center,
            isDone = false,
            isSenior = false,
            time = 0,
            icon = R.drawable.cross_eye_icon,
            enabled = true,
        )
        Spacer(
            modifier = Modifier
                .height(20.dp)
        )
        TestSelectionButton(
            modifier = Modifier
                .height(100.dp),
            title1 = StringProvider.getString(R.string.dementia_test, ),
            title2 = "",
            onClickMethod = {
                toDementiaTestScreen(TestType.Dementia)
            },
            alignment = Alignment.Center,
            isDone = false,
            isSenior = false,
            time = 0,
            icon = R.drawable.dementia_icon
        )
        Spacer(
            modifier = Modifier
                .height(40.dp)
        )
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(40.dp),
        horizontalArrangement = Arrangement.End
    ) {
        if (!isSignInSkipped()) {
            Card(
                modifier = Modifier
                    .size(100.dp)
                    .clip(shape = RoundedCornerShape(8.dp))
                    .border(
                        border = BorderStroke(1.dp, Color(0xffc3c3c3)),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .clickable { toPrintScreen() },
                backgroundColor = Color(0xffffffff)
            ) {
                Icon(
                    modifier = Modifier.width(60.dp).padding(20.dp),
                    painter = painterResource(id = R.drawable.icon_print),
                    contentDescription = ""
                )
            }

            Spacer(
                modifier = Modifier
                    .width(20.dp)
            )
        }

        Card(
            modifier = Modifier
                .size(100.dp)
                .clip(shape = RoundedCornerShape(8.dp))
                .border(
                    border = BorderStroke(1.dp, Color(0xffc3c3c3)),
                    shape = RoundedCornerShape(8.dp)
                )
                .clickable { toAccountManagementScreen() },
            backgroundColor = Color(0xffffffff)
        ) {
            Icon(
                modifier = Modifier.width(60.dp).padding(20.dp),
                painter = painterResource(id = R.drawable.account_icon),
                contentDescription = ""
            )
        }
    }
}

@Preview
@Composable
fun CategoryListScreenPreview() {
    CategoryListScreen(
        pid = -1,
        isSignInSkipped = { false },
        toEyeTestScreen = {},
        toContact = {},
        toDementiaTestScreen = {},
        toExternalDeviceTestListScreen = {},
        toStrabismusTestListScreen = {},
        toIntroScreen = {},
        toPrintScreen = {},
        toPulmonaryTestResultScreen = {},
        toSettingsScreen = {},
        toAccountManagementScreen = {},
    )
}