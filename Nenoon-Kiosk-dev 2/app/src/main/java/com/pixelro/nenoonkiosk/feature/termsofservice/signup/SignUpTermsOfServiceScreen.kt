package com.pixelro.nenoonkiosk.feature.termsofservice.signup

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import com.pixelro.nenoonkiosk.core.ui.PrimaryButton
import com.pixelro.nenoonkiosk.core.util.StringProvider
import com.pixelro.nenoonkiosk.feature.termsofservice.base.TermsTableData
import com.pixelro.nenoonkiosk.feature.termsofservice.components.ConsentRow
import com.pixelro.nenoonkiosk.feature.termsofservice.components.TermsTable

@Composable
fun SignUpTermsOfServiceScreen(
    state: SignUpTermsOfServiceState,
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
            Column(
                modifier = Modifier.fillMaxSize(),
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
                            append(StringProvider.getStringComposable(R.string.signup_terms_title_primary) + "\n")
                        }
                        withStyle(
                            SpanStyle(
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold
                            )
                        ) {
                            append(StringProvider.getStringComposable(R.string.signup_terms_title_secondary))
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
                    text = StringProvider.getStringComposable(R.string.signup_terms_description),
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
                    description = StringProvider.getStringComposable(R.string.signup_personal_info_checkbox_description),
                    question = StringProvider.getStringComposable(R.string.signup_personal_info_checkbox_question),
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
                    description = StringProvider.getStringComposable(R.string.signup_sensitive_info_checkbox_description),
                    question = StringProvider.getStringComposable(R.string.signup_sensitive_info_checkbox_question),
                    accepted = state.acceptedSensitive,
                    onAcceptedChange = onChangeSensitive,
                    textSize = state.textSize
                )

                Spacer(Modifier.weight(1f))

                PrimaryButton(
                    onClick = onClickAgree,
                    enabled = state.acceptedPersonal == true && state.acceptedSensitive == true,
                    text = StringProvider.getStringComposable(R.string.button_agree)
                )
                Spacer(Modifier.height(20.dp))
                PrimaryButton(
                    onClick = onClickBack,
                    text = StringProvider.getStringComposable(R.string.back)
                )
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 888, heightDp = 1422, name = "SignUp TOS - Accepted")
@Composable
private fun SignUpTermsOfServiceScreen_Preview_Accepted() {
    SignUpTermsOfServiceScreen(
        state = SignUpTermsOfServiceState(
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

@Preview(showBackground = true, widthDp = 888, heightDp = 1422, name = "SignUp TOS - Partial")
@Composable
private fun SignUpTermsOfServiceScreen_Preview_Partial() {
    SignUpTermsOfServiceScreen(
        state = SignUpTermsOfServiceState(
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
