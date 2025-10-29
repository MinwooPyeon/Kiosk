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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.ui.InspectionSelectionButton
import com.pixelro.nenoonkiosk.core.ui.Logo
import com.pixelro.nenoonkiosk.core.util.TTS
import com.pixelro.nenoonkiosk.feature.inspection.pulmonaryFunction.PulmonaryFunctionTestResult
import com.pixelro.nenoonkiosk.ui.theme.neNoon_blue

@OptIn(ExperimentalFoundationApi::class, ExperimentalAnimationApi::class)
@Composable
fun CategoryListRoute(
    pid: Int,
    isSignInSkipped: Boolean,
    viewModel: CategoryListViewModel = hiltViewModel()
) {
    val isPreview = LocalInspectionMode.current

    LaunchedEffect(pid) {
        viewModel.setPid(pid)
    }

    val activityResultLauncher = if (!isPreview) {
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
                viewModel.navigateToPulmonaryTestResult(pulmonaryResult)
            }
        }
    } else null

    LaunchedEffect(!isPreview) {
        if (!isPreview) {
            viewModel.startDescriptionAnimation()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.container.sideEffectFlow.collect { sideEffect ->
            when (sideEffect) {
                is CategoryListSideEffect.StopTts -> {
                    TTS.tts.stop()
                }

                is CategoryListSideEffect.LaunchPulmonaryTest -> {
                    val intent = Intent("android.intent.action.BREATHINGS").apply {
                        putExtra("id", sideEffect.pid.toString())
                        putExtra("height", sideEffect.height)
                        putExtra("birthday", sideEffect.birthday)
                        putExtra("weight", sideEffect.weight)
                        putExtra("gender", sideEffect.gender)
                    }
                    activityResultLauncher?.launch(intent)
                }
            }
        }
    }

    CategoryListScreen(
        isSignInSkipped = isSignInSkipped,
        onEyeTestClick = viewModel::navigateToEyeTest,
        onExternalDeviceTestClick = viewModel::navigateToExternalDeviceTestList,
        onPulmonaryTestClick = { viewModel.navigateToPulmonaryTest(pid) },
        onStrabismusTestClick = viewModel::navigateToStrabismusTestList,
        onDementiaTestClick = viewModel::navigateToDementiaTest,
        onPrintClick = viewModel::navigateToPrint,
        onAccountManagementClick = viewModel::navigateToAccountManagement
    )
}


@OptIn(ExperimentalFoundationApi::class, ExperimentalAnimationApi::class)
@Composable
internal fun CategoryListScreen(
    isSignInSkipped: Boolean,
    onEyeTestClick: () -> Unit,
    onExternalDeviceTestClick: () -> Unit,
    onPulmonaryTestClick: () -> Unit,
    onStrabismusTestClick: () -> Unit,
    onDementiaTestClick: () -> Unit,
    onPrintClick: () -> Unit,
    onAccountManagementClick: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
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

            val testButtons = listOf(
                Triple(
                    R.string.blood_pressure_and_grip_strength,
                    R.drawable.blood_pressure_and_grip_strength_icon,
                    onExternalDeviceTestClick
                ),
                Triple(R.string.eye_test, R.drawable.eye_test_icon, onEyeTestClick),
                Triple(
                    R.string.cross_eye_test,
                    R.drawable.cross_eye_icon,
                    onStrabismusTestClick
                ),
                Triple(R.string.dementia_test, R.drawable.dementia_icon, onDementiaTestClick)
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
                Spacer(
                    modifier = Modifier.height(
                        if (index == testButtons.lastIndex) 40.dp else 20.dp
                    )
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(40.dp),
            horizontalArrangement = Arrangement.End
        ) {
            if (!isSignInSkipped) {
                Card(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onPrintClick() },
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
                    .clickable { onAccountManagementClick() },
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
}

@Preview(
    showBackground = true,
    name = "CategoryListScreen Preview",
    widthDp = 800,
    heightDp = 1280,
    apiLevel = 34
)
@Composable
fun CategoryListScreenPreview() {
    CategoryListScreen(
        isSignInSkipped = false,
        onEyeTestClick = {},
        onExternalDeviceTestClick = {},
        onPulmonaryTestClick = {},
        onStrabismusTestClick = {},
        onDementiaTestClick = {},
        onPrintClick = {},
        onAccountManagementClick = {}
    )
}