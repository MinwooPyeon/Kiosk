package com.pixelro.nenoonkiosk.feature.termsofservice.base

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.util.StringProvider
import org.orbitmvi.orbit.compose.collectAsState

@Composable
fun TermsOfServiceRoute(
    viewModel: TermsOfServiceViewModel = hiltViewModel(),
) {
    val state by viewModel.collectAsState()

    // 언어 기반 폰트 크기 로드
    LaunchedEffect(Unit) {
        viewModel.loadLanguageBasedTextSize()
    }

    // 표에 들어갈 데이터 구성
    val personalTable = remember {
        TermsTableData(
            label = StringProvider.getString(R.string.personal_info_table_label),
            column1 = StringProvider.getString(R.string.personal_info_table_column1),
            column2 = StringProvider.getString(R.string.personal_info_table_column2),
            column3 = StringProvider.getString(R.string.personal_info_table_column3),
            evenly = false,
        )
    }

    val sensitiveTable = remember {
        TermsTableData(
            label = StringProvider.getString(R.string.sensitive_info_table_label),
            column1 = StringProvider.getString(R.string.sensitive_info_table_column1),
            column2 = StringProvider.getString(R.string.sensitive_info_table_column2),
            column3 = StringProvider.getString(R.string.sensitive_info_table_column3),
            evenly = false,
        )
    }

    TermsOfServiceScreen(
        state = state,
        personalTable = personalTable,
        sensitiveTable = sensitiveTable,
        onChangePersonal = { viewModel.onPersonalInformationTermsAcceptedChange(it) },
        onChangeSensitive = { viewModel.onSensitiveInformationTermsAcceptedChange(it) },
        onClickAgree = { viewModel.onClickAgree() },
        onClickBack = { viewModel.onClickBack() },
    )
}
