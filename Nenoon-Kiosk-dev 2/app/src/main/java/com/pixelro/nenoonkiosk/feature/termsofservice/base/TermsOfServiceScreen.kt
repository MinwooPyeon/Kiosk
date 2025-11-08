package com.pixelro.nenoonkiosk.feature.termsofservice.base

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.boundsInParent
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.ui.PrimaryButton
import com.pixelro.nenoonkiosk.core.ui.SecondaryButton
import com.pixelro.nenoonkiosk.feature.termsofservice.components.ConsentRow
import com.pixelro.nenoonkiosk.feature.termsofservice.components.TermsTable
import com.pixelro.nenoonkiosk.feature.termsofservice.components.TosDescription
import com.pixelro.nenoonkiosk.feature.termsofservice.components.TosTitle

@Composable
fun TermsOfServiceScreen(
    state: TermsOfServiceUiState,
    personalTable: TermsTableData,
    sensitiveTable: TermsTableData,
    onChangePersonal: (Boolean?) -> Unit,
    onChangeSensitive: (Boolean?) -> Unit,
    onClickAgree: () -> Unit,
    onClickBack: () -> Unit
) {
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()
    var sensitiveTableY by remember { mutableFloatStateOf(0f) }
    var agreeButtonY by remember { mutableFloatStateOf(0f) }
    
    LaunchedEffect(state.acceptedPersonal) {
        if (state.acceptedPersonal != null && sensitiveTableY > 0) {
            coroutineScope.launch {
                scrollState.animateScrollTo(sensitiveTableY.toInt())
            }
        }
    }

    LaunchedEffect(state.acceptedSensitive) {
        if (state.acceptedSensitive != null && agreeButtonY > 0) {
            coroutineScope.launch {
                scrollState.animateScrollTo(agreeButtonY.toInt())
            }
        }
    }
    
    Scaffold { paddingValues ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(40.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(30.dp))
                TosTitle()
                Spacer(Modifier.height(30.dp))
                TosDescription(textSize = state.textSize)

                Spacer(Modifier.height(30.dp))
                TermsTable(
                    data = personalTable,
                    textSize = state.textSize
                )
                Spacer(Modifier.height(16.dp))
                ConsentRow(
                    description = stringResource(R.string.personal_info_checkbox_description),
                    question = stringResource(R.string.personal_info_checkbox_question),
                    accepted = state.acceptedPersonal,
                    onAcceptedChange = onChangePersonal,
                    textSize = state.textSize
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 18.dp), color = Color(0x1F000000))

                Box(
                    modifier = Modifier.onGloballyPositioned { coordinates ->
                        sensitiveTableY = coordinates.boundsInParent().top
                    }
                ) {
                    TermsTable(
                        data = sensitiveTable,
                        textSize = state.textSize
                    )
                }
                Spacer(Modifier.height(16.dp))
                ConsentRow(
                    description = stringResource(R.string.sensitive_info_checkbox_description),
                    question = stringResource(R.string.sensitive_info_checkbox_question),
                    accepted = state.acceptedSensitive,
                    onAcceptedChange = onChangeSensitive,
                    textSize = state.textSize
                )

                Spacer(Modifier.height(40.dp))

                PrimaryButton(
                    onClick = onClickAgree,
                    enabled = state.acceptedPersonal == true && state.acceptedSensitive == true,
                    text = stringResource(R.string.button_agree),
                    modifier = Modifier.onGloballyPositioned { coordinates ->
                        agreeButtonY = coordinates.boundsInParent().top
                    }
                )
                Spacer(Modifier.height(20.dp))
                SecondaryButton(
                    onClick = onClickBack,
                    text = stringResource(R.string.back),
                    iconDrawable = R.drawable.icon_back_black,
                )
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 888, heightDp = 1422, name = "All Accepted", apiLevel = 34)
@Composable
private fun TermsOfServiceScreen_Preview_AllAccepted() {
    TermsOfServiceScreen(
        state = TermsOfServiceUiState(
            acceptedPersonal = true,
            acceptedSensitive = true,
            textSize = 20.sp
        ),
        personalTable = TermsTableData(
            label = "개인정보 수집·이용",
            column1 = "성명/연락처",
            column2 = "본인확인 및 상담",
            column3 = "1년 보관"
        ),
        sensitiveTable = TermsTableData(
            label = "민감정보 수집·이용",
            column1 = "건강정보",
            column2 = "맞춤 서비스 제공",
            column3 = "동의 철회시까지"
        ),
        onChangePersonal = {},
        onChangeSensitive = {},
        onClickAgree = {},
        onClickBack = {}
    )
}

@Preview(showBackground = true, widthDp = 888, heightDp = 1422, name = "Partial / Korean", apiLevel = 34)
@Composable
private fun TermsOfServiceScreen_Preview_Partial() {
    TermsOfServiceScreen(
        state = TermsOfServiceUiState(
            acceptedPersonal = true,
            acceptedSensitive = null,
            textSize = 20.sp
        ),
        personalTable = TermsTableData(
            label = "개인정보 수집·이용",
            column1 = "성명/연락처",
            column2 = "본인확인 및 상담",
            column3 = "1년 보관"
        ),
        sensitiveTable = TermsTableData(
            label = "민감정보 수집·이용",
            column1 = "건강정보",
            column2 = "맞춤 서비스 제공",
            column3 = "동의 철회시까지"
        ),
        onChangePersonal = {},
        onChangeSensitive = {},
        onClickAgree = {},
        onClickBack = {}
    )
}

@Preview(showBackground = true, widthDp = 1422, heightDp = 1155, name = "Horizontal", apiLevel = 34)
@Composable
private fun TermsOfServiceScreen_Preview_Horizontal() {
    TermsOfServiceScreen(
        state = TermsOfServiceUiState(
            acceptedPersonal = true,
            acceptedSensitive = true,
            textSize = 20.sp
        ),
        personalTable = TermsTableData(
            label = "개인정보 수집·이용",
            column1 = "성명/연락처",
            column2 = "본인확인 및 상담",
            column3 = "1년 보관"
        ),
        sensitiveTable = TermsTableData(
            label = "민감정보 수집·이용",
            column1 = "건강정보",
            column2 = "맞춤 서비스 제공",
            column3 = "동의 철회시까지"
        ),
        onChangePersonal = {},
        onChangeSensitive = {},
        onClickAgree = {},
        onClickBack = {}
    )
}
