package com.pixelro.nenoonkiosk.feature.termsofservice.faceid

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
fun FaceIdTermsOfServiceScreen(
    state: FaceIdTosUiState,
    tableData: TermsTableData,
    onChangePersonal: (Boolean?) -> Unit,
    onClickAgree: () -> Unit,
    onClickBack: () -> Unit
) {
    val isLandscape = isLandscape()

    if (isLandscape) {
        FaceIdTermsOfServiceLandscapeLayout(
            state = state,
            tableData = tableData,
            onChangePersonal = onChangePersonal,
            onClickAgree = onClickAgree,
            onClickBack = onClickBack
        )
    } else {
        FaceIdTermsOfServicePortraitLayout(
            state = state,
            tableData = tableData,
            onChangePersonal = onChangePersonal,
            onClickAgree = onClickAgree,
            onClickBack = onClickBack
        )
    }
}

@Composable
private fun FaceIdTermsOfServicePortraitLayout(
    state: FaceIdTosUiState,
    tableData: TermsTableData,
    onChangePersonal: (Boolean?) -> Unit,
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
                FaceIdTermsOfServiceContent(
                    state = state,
                    tableData = tableData,
                    onChangePersonal = onChangePersonal,
                    onClickAgree = onClickAgree,
                    onClickBack = onClickBack
                )
            }
        }
    }
}

@Composable
private fun FaceIdTermsOfServiceLandscapeLayout(
    state: FaceIdTosUiState,
    tableData: TermsTableData,
    onChangePersonal: (Boolean?) -> Unit,
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
                FaceIdTermsOfServiceContent(
                    state = state,
                    tableData = tableData,
                    onChangePersonal = onChangePersonal,
                    onClickAgree = onClickAgree,
                    onClickBack = onClickBack
                )
            }
        }
    }
}

@Composable
private fun FaceIdTermsOfServiceContent(
    state: FaceIdTosUiState,
    tableData: TermsTableData,
    onChangePersonal: (Boolean?) -> Unit,
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
                    append(stringResource(R.string.face_id_terms_title_primary) + "\n")
                }
                withStyle(
                    SpanStyle(
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold
                    )
                ) {
                    append(stringResource(R.string.face_id_terms_title_secondary))
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
            text = stringResource(R.string.face_id_terms_description),
            style = TextStyle(fontSize = state.textSize),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(30.dp))

        // Table
        TermsTable(
            data = tableData,
            textSize = state.textSize
        )

        Spacer(Modifier.height(30.dp))

        // Consent
        ConsentRow(
            description = stringResource(R.string.face_id_checkbox_description),
            question = stringResource(R.string.face_id_checkbox_question),
            accepted = state.acceptedPersonal,
            onAcceptedChange = onChangePersonal,
            textSize = state.textSize
        )

        Spacer(Modifier.weight(1f))

        // Disclaimers
        Text(
            text = stringResource(R.string.face_id_disclaimer_1),
            fontSize = state.smallTextSize,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(10.dp))
        Text(
            text = stringResource(R.string.face_id_disclaimer_2),
            fontSize = state.smallTextSize,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(20.dp))

        // Buttons
        PrimaryButton(
            onClick = onClickAgree,
            enabled = state.acceptedPersonal == true,
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
    name = "FaceID - Portrait"
)
@Composable
private fun FaceIdTermsOfServiceScreen_Preview_Portrait() {
    FaceIdTermsOfServiceScreen(
        state = FaceIdTosUiState(
            acceptedPersonal = true,
            textSize = 20.sp,
            smallTextSize = 18.sp
        ),
        tableData = TermsTableData(
            label = "안면인식 정보 수집·이용",
            column1 = "안면 특징",
            column2 = "개인 식별/접근 제어",
            column3 = "동의 철회 시까지",
            evenly = true
        ),
        onChangePersonal = {},
        onClickAgree = {},
        onClickBack = {}
    )
}

// Landscape Preview
@Preview(
    showBackground = true,
    widthDp = 1422,
    heightDp = 888,
    name = "FaceID - Landscape"
)
@Composable
private fun FaceIdTermsOfServiceScreen_Preview_Landscape() {
    FaceIdTermsOfServiceScreen(
        state = FaceIdTosUiState(
            acceptedPersonal = null,
            textSize = 20.sp,
            smallTextSize = 18.sp
        ),
        tableData = TermsTableData(
            label = "안면인식 정보 수집·이용",
            column1 = "안면 특징",
            column2 = "개인 식별/접근 제어",
            column3 = "동의 철회 시까지",
            evenly = true
        ),
        onChangePersonal = {},
        onClickAgree = {},
        onClickBack = {}
    )
}
