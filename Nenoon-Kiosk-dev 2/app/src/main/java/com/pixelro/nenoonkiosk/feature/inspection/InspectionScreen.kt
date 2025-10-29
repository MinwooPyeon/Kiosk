package com.pixelro.nenoonkiosk.feature.inspection

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.util.StringProvider.getStringComposable
import com.pixelro.nenoonkiosk.feature.inspection.components.InspectionDivider
import com.pixelro.nenoonkiosk.feature.inspection.components.InspectionTopBar

@Composable
fun InspectionScreen(
    testType: InspectionType,
    onClickClose: () -> Unit,
    content: @Composable () -> Unit,
) {
    val isLight = testType in setOf(InspectionType.Dementia, InspectionType.GripStrength, InspectionType.BloodPressure)
    val bgColor = if (isLight) Color(0xFFFFFFFF) else Color(0xFF000000)
    val titleColor = if (isLight) Color(0xFF000000) else Color(0xFFFFFFFF)
    val closePainter =
        if (isLight) painterResource(R.drawable.close_button_black)
        else painterResource(R.drawable.close_button)
    val titleFor : String = when(testType){
        InspectionType.Presbyopia -> getStringComposable(R.string.presbyopia_name)
        InspectionType.ShortDistanceVisualAcuity -> getStringComposable(R.string.short_visual_acuity_name2)
        InspectionType.LongDistanceVisualAcuity -> getStringComposable(R.string.long_visual_acuity_name)
        InspectionType.ChildrenVisualAcuity -> getStringComposable(R.string.children_visual_acuity_name)
        InspectionType.AmslerGrid -> getStringComposable(R.string.amsler_grid_name)
        InspectionType.MChart -> getStringComposable(R.string.mchart_name)
        InspectionType.Dementia -> getStringComposable(R.string.dementia_title)
        InspectionType.Presbyopia_Glasses -> getStringComposable(R.string.presbyopia_glasses)
        InspectionType.Concentration_Glasses -> getStringComposable(R.string.concentration_glasses_title)
        InspectionType.GripStrength -> getStringComposable(R.string.grip_strength_test)
        InspectionType.BloodPressure -> getStringComposable(R.string.blood_pressure_test)
        else -> ""
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        InspectionTopBar(
            title = titleFor,
            titleColor = titleColor,
            closePainterId = closePainter,
            onClickClose = onClickClose
        )
        InspectionDivider()
        Box(modifier = Modifier.fillMaxSize()) {
            content()
        }
    }
}

@Composable
private fun FakeContent() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("검사 컨텐츠 미리보기", color = Color(0xFF888888))
    }
}

@Preview(showBackground = true, widthDp = 900, heightDp = 1400, name = "Dark Test Screen", apiLevel = 34)
@Composable
private fun Preview_TestScreen_Dark() {
    InspectionScreen(
        testType = InspectionType.MChart,
        onClickClose = {},
        content = { FakeContent() }
    )
}

@Preview(showBackground = true, widthDp = 900, heightDp = 1400, name = "Light Test Screen", apiLevel = 34)
@Composable
private fun Preview_TestScreen_Light() {
    InspectionScreen(
        testType = InspectionType.Dementia,
        onClickClose = {},
        content = { FakeContent() }
    )
}
