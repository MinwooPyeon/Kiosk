package com.pixelro.nenoonkiosk.feature.categorylist

import android.app.Activity
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card

import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.ui.InspectionSelectionButton
import com.pixelro.nenoonkiosk.core.ui.Logo
import com.pixelro.nenoonkiosk.core.util.TTS
import com.pixelro.nenoonkiosk.core.util.dataprovider.TestType
import com.pixelro.nenoonkiosk.feature.inspection.pulmonaryFunction.PulmonaryFunctionTestResult
import com.pixelro.nenoonkiosk.ui.theme.neNoon_blue
import kotlinx.coroutines.delay

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
    val isPreview = LocalInspectionMode.current
    val coroutineScope = rememberCoroutineScope()

    // Preview-safe infinite animation
    val isDescriptionShowing = remember { mutableStateOf(true) }
    LaunchedEffect(!isPreview) {
        if (!isPreview) {
            TTS.tts.stop()
            while (true) {
                delay(5000)
                for (i in 1..3) {
                    isDescriptionShowing.value = false
                    delay(250)
                    isDescriptionShowing.value = true
                    delay(250)
                }
            }
        }
    }

    // Safe launcher (dummy in preview)
    val activityResultLauncher =
        if (!isPreview) {
            rememberLauncherForActivityResult(
                contract = ActivityResultContracts.StartActivityForResult(),
            ) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val isGood = result.data?.getBooleanExtra("isGood", false)
                    val lungAge = result.data?.getDoubleExtra("lungAge", -1.0)
                    val capacity = result.data?.getDoubleExtra("capacity", -1.0)
                    val power = result.data?.getDoubleExtra("power", -1.0)

                    val pulmonaryResult = PulmonaryFunctionTestResult().apply {
                        isGood?.let { pulmonaryStatus = it }
                        capacity?.let { pulmonaryCapacity = it }
                        power?.let { pulmonaryPower = it }
                        lungAge?.let { pulmonaryAge = it.toInt() }
                    }
                    toPulmonaryTestResultScreen(pulmonaryResult)
                }
            }
        } else null

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(neNoon_blue),
    ) {
        Spacer(modifier = Modifier.weight(1f))
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Logo(true)
        }
        Spacer(modifier = Modifier.weight(1f))

        // Test buttons
        val testButtons = listOf(
            Triple(
                R.string.blood_pressure_and_grip_strength,
                R.drawable.blood_pressure_and_grip_strength_icon
            ) {
                toExternalDeviceTestListScreen()
            },
            Triple(R.string.eye_test, R.drawable.eye_test_icon) {
                toEyeTestScreen()
            },
            Triple(R.string.pulmonary_function_test, R.drawable.lung_age_icon) {
                if (!isPreview) {
                    val intent = Intent("android.intent.action.BREATHINGS").apply {
                        putExtra("id", pid.toString())
                        putExtra("height", 0)
                        putExtra("birthday", 0)
                        putExtra("weight", 0)
                        putExtra("gender", "m")
                    }
                    activityResultLauncher?.launch(intent)
                }
            },
            Triple(R.string.cross_eye_test, R.drawable.cross_eye_icon) {
                toStrabismusTestListScreen()
            },
            Triple(R.string.dementia_test, R.drawable.dementia_icon) {
                toDementiaTestScreen(TestType.Dementia)
            }
        )

        testButtons.forEachIndexed { index, (titleId, icon, onClick) ->
            InspectionSelectionButton(
                modifier = Modifier.height(100.dp),
                title1 = stringResource(id = titleId),
                title2 = "",
                onClickMethod = onClick,
                alignment = Alignment.Center,
                isDone = false,
                isSenior = false,
                time = 0,
                icon = icon
            )
            Spacer(modifier = Modifier.height(if (index == testButtons.lastIndex) 40.dp else 20.dp))
        }
    }

    // Top buttons
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
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { toPrintScreen() },
                backgroundColor = Color.White
            ) {
                Icon(
                    modifier = Modifier
                        .width(60.dp)
                        .padding(20.dp),
                    painter = painterResource(id = R.drawable.icon_print),
                    contentDescription = ""
                )
            }
            Spacer(modifier = Modifier.width(20.dp))
        }

        Card(
            modifier = Modifier
                .size(100.dp)
                .clip(RoundedCornerShape(8.dp))
                .clickable { toAccountManagementScreen() },
            backgroundColor = Color.White
        ) {
            Icon(
                modifier = Modifier
                    .width(60.dp)
                    .padding(20.dp),
                painter = painterResource(id = R.drawable.account_icon),
                contentDescription = ""
            )
        }
    }
}

@Preview(
    showBackground = true, name = "CategoryListScreen Preview Safe", widthDp = 800,
    heightDp = 1280, apiLevel = 34
)
@Composable
fun CategoryListScreenPreview() {
    CategoryListScreen(
        pid = 0,
        isSignInSkipped = { false },
        toEyeTestScreen = {},
        toContact = {},
        toDementiaTestScreen = {},
        toExternalDeviceTestListScreen = {},
        toStrabismusTestListScreen = {},
        toIntroScreen = {},
        toPrintScreen = {},
        toAccountManagementScreen = {},
        toPulmonaryTestResultScreen = {},
        toSettingsScreen = {}
    )
}

