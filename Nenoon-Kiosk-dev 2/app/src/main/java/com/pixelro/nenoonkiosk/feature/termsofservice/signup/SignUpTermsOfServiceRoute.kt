package com.pixelro.nenoonkiosk.feature.termsofservice.signup

import androidx.compose.runtime.Composable
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import org.orbitmvi.orbit.compose.collectAsState

@Composable
fun SignUpTermsOfServiceRoute(
    viewModel: SignUpTermsOfServiceViewModel = hiltViewModel()
) {
    val state = viewModel.collectAsState().value

    // SideEffect 수집 완전 제거 - Navigator가 직접 처리

    if (state.personalTable != null && state.sensitiveTable != null) {
        SignUpTermsOfServiceScreen(
            state = SignUpTermsOfServiceState(
                acceptedPersonal = state.acceptedPersonal,
                acceptedSensitive = state.acceptedSensitive,
                textSize = state.textSize
            ),
            personalTable = state.personalTable,
            sensitiveTable = state.sensitiveTable,
            onChangePersonal = { viewModel.onPersonalInformationTermsAcceptedChange(it) },
            onChangeSensitive = { viewModel.onSensitiveInformationTermsAcceptedChange(it) },
            onClickAgree = { viewModel.onClickAgree() },
            onClickBack = { viewModel.onClickBack() }
        )
    }
}
