package com.pixelro.nenoonkiosk.feature.termsofservice.signup

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.ui.BackButtonHorizontal
import com.pixelro.nenoonkiosk.core.ui.PrimaryButton
import com.pixelro.nenoonkiosk.core.util.isLandscape
import com.pixelro.nenoonkiosk.feature.termsofservice.base.TermsTableData
import com.pixelro.nenoonkiosk.feature.termsofservice.components.ConsentRow
import com.pixelro.nenoonkiosk.feature.termsofservice.components.TermsTable

@Composable
fun SignUpTermsOfServiceScreen(
    state: SignUpTosUiState,
    personalTable: TermsTableData,
    sensitiveTable: TermsTableData,
    onChangePersonal: (Boolean?) -> Unit,
    onChangeSensitive: (Boolean?) -> Unit,
    onClickAgree: () -> Unit,
    onClickBack: () -> Unit
) {
    val isLandscape = isLandscape()

    if (isLandscape) {
        SignUpTermsOfServiceLandscapeLayout(
            state = state,
            personalTable = personalTable,
            sensitiveTable = sensitiveTable,
            onChangePersonal = onChangePersonal,
            onChangeSensitive = onChangeSensitive,
            onClickAgree = onClickAgree,
            onClickBack = onClickBack
        )
    } else {
        SignUpTermsOfServicePortraitLayout(
            state = state,
            personalTable = personalTable,
            sensitiveTable = sensitiveTable,
            onChangePersonal = onChangePersonal,
            onChangeSensitive = onChangeSensitive,
            onClickAgree = onClickAgree,
            onClickBack = onClickBack
        )
    }
}

@Composable
private fun SignUpTermsOfServicePortraitLayout(
    state: SignUpTosUiState,
    personalTable: TermsTableData,
    sensitiveTable: TermsTableData,
    onChangePersonal: (Boolean?) -> Unit,
    onChangeSensitive: (Boolean?) -> Unit,
    onClickAgree: () -> Unit,
    onClickBack: () -> Unit
) {
    Scaffold { paddingValues ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(40.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                SignUpTermsOfServiceContent(
                    state = state,
                    personalTable = personalTable,
                    sensitiveTable = sensitiveTable,
                    onChangePersonal = onChangePersonal,
                    onChangeSensitive = onChangeSensitive,
                    onClickAgree = onClickAgree,
                    onClickBack = onClickBack
                )
            }
        }
    }
}

@Composable
private fun SignUpTermsOfServiceLandscapeLayout(
    state: SignUpTosUiState,
    personalTable: TermsTableData,
    sensitiveTable: TermsTableData,
    onChangePersonal: (Boolean?) -> Unit,
    onChangeSensitive: (Boolean?) -> Unit,
    onClickAgree: () -> Unit,
    onClickBack: () -> Unit
) {
    val scrollState = rememberScrollState()

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
                SignUpTermsOfServiceContent(
                    state = state,
                    personalTable = personalTable,
                    sensitiveTable = sensitiveTable,
                    onChangePersonal = onChangePersonal,
                    onChangeSensitive = onChangeSensitive,
                    onClickAgree = onClickAgree,
                    onClickBack = onClickBack
                )
            }
        }
    }
}

@Composable
private fun SignUpTermsOfServiceContent(
    state: SignUpTosUiState,
    personalTable: TermsTableData,
    sensitiveTable: TermsTableData,
    onChangePersonal: (Boolean?) -> Unit,
    onChangeSensitive: (Boolean?) -> Unit,
    onClickAgree: () -> Unit,
    onClickBack: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(30.dp))

        // Title
        Text(
            text = buildAnnotatedString {
                withStyle(
                    SpanStyle(
                        color = Color(0xff1d71e1),
                        fontSize = 40.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                ) {
                    append(stringResource(R.string.signup_terms_title_primary) + "\n")
                }
                withStyle(
                    SpanStyle(
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                ) {
                    append(stringResource(R.string.signup_terms_title_secondary))
                }
            },
            style = TextStyle(
                textAlign = TextAlign.Center,
                lineHeight = 40.sp
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(30.dp))

        // Description
        Text(
            text = stringResource(R.string.signup_terms_description),
            style = TextStyle(fontSize = state.textSize),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(30.dp))

        // Personal Information
        TermsTable(
            data = personalTable,
            textSize = state.textSize
        )
        Spacer(Modifier.height(30.dp))
        ConsentRow(
            description = stringResource(R.string.signup_personal_info_checkbox_description),
            question = stringResource(R.string.signup_personal_info_checkbox_question),
            accepted = state.acceptedPersonal,
            onAcceptedChange = onChangePersonal,
            textSize = state.textSize
        )

        Spacer(Modifier.height(30.dp))

        // Sensitive Information
        TermsTable(
            data = sensitiveTable,
            textSize = state.textSize
        )
        Spacer(Modifier.height(30.dp))
        ConsentRow(
            description = stringResource(R.string.signup_sensitive_info_checkbox_description),
            question = stringResource(R.string.signup_sensitive_info_checkbox_question),
            accepted = state.acceptedSensitive,
            onAcceptedChange = onChangeSensitive,
            textSize = state.textSize
        )

        Spacer(Modifier.weight(1f))

        PrimaryButton(
            onClick = onClickAgree,
            enabled = state.acceptedPersonal == true && state.acceptedSensitive == true,
            text = stringResource(R.string.button_agree)
        )
        Spacer(Modifier.height(20.dp))
        BackButtonHorizontal(
            onClick = onClickBack,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

// Portrait Preview
@Preview(
    showBackground = true,
    widthDp = 888,
    heightDp = 1422,
    name = "SignUp - Portrait"
)
@Composable
private fun SignUpTermsOfServiceScreen_Preview_Portrait() {
    SignUpTermsOfServiceScreen(
        state = SignUpTosUiState(
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
            column3 = "동의 철회 시까지"
        ),
        onChangePersonal = {},
        onChangeSensitive = {},
        onClickAgree = {},
        onClickBack = {}
    )
}

// Landscape Preview
@Preview(
    showBackground = true,
    widthDp = 1422,
    heightDp = 888,
    name = "SignUp - Landscape"
)
@Composable
private fun SignUpTermsOfServiceScreen_Preview_Landscape() {
    SignUpTermsOfServiceScreen(
        state = SignUpTosUiState(
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
            column3 = "동의 철회 시까지"
        ),
        onChangePersonal = {},
        onChangeSensitive = {},
        onClickAgree = {},
        onClickBack = {}
    )
}
