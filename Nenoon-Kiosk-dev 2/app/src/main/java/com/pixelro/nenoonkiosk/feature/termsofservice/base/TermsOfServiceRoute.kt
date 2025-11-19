package com.pixelro.nenoonkiosk.feature.termsofservice.base

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.constants.NavConstants
import com.pixelro.nenoonkiosk.core.util.StringProvider

@Composable
fun TermsOfServiceRoute(
    onTermsAccepted: () -> Unit,
    onTermsRejected: () -> Unit,
    viewModel: TermsOfServiceViewModel = hiltViewModel(),
) {
    val acceptedPersonal by viewModel.acceptedPersonalInformationTerms
    val acceptedSensitive by viewModel.acceptedSensitiveInformationTerms

    val context = LocalContext.current
    // 언어 기반 폰트 크기 결정은 화면 로직 밖에서 한 번만 계산
    val textSize = remember {
        val sp = context.getSharedPreferences(NavConstants.PREFERENCE_NAME, Context.MODE_PRIVATE)
        val savedLanguage = sp.getString("language", "defaultLanguage")
        if (savedLanguage == "en") 16.sp else 20.sp
    }

    val uiState = TermsOfServiceUiState(
        acceptedPersonal = acceptedPersonal,
        acceptedSensitive = acceptedSensitive,
        textSize = textSize
    )

    // 표에 들어갈 데이터 구성
    val personalTable = remember {
        TermsTableData(
            label = StringProvider.getString(R.string.personal_info_table_label),
            column1 = StringProvider.getString(R.string.personal_info_table_column1),
            column2 = StringProvider.getString(R.string.personal_info_table_column2),
            column3 = StringProvider.getString(R.string.personal_info_table_column3),
            evenly = false
        )
    }
    val sensitiveTable = remember {
        TermsTableData(
            label = StringProvider.getString(R.string.sensitive_info_table_label),
            column1 = StringProvider.getString(R.string.sensitive_info_table_column1),
            column2 = StringProvider.getString(R.string.sensitive_info_table_column2),
            column3 = StringProvider.getString(R.string.sensitive_info_table_column3),
            evenly = false
        )
    }

    TermsOfServiceScreen(
        state = uiState,
        personalTable = personalTable,
        sensitiveTable = sensitiveTable,
        onChangePersonal = { viewModel.onPersonalInformationTermsAcceptedChange(it) },
        onChangeSensitive = { viewModel.onSensitiveInformationTermsAcceptedChange(it) },
        onClickAgree = {
            if (uiState.acceptedPersonal == true && uiState.acceptedSensitive == true) {
                onTermsAccepted()
            }
        },
        onClickBack = onTermsRejected
    )
}