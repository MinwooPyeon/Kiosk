package com.pixelro.nenoonkiosk.feature.termsofservice.faceid

import androidx.compose.runtime.Composable
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import org.orbitmvi.orbit.compose.collectAsState

@Composable
fun FaceIdTermsOfServiceRoute(
    viewModel: FaceIdTermsOfServiceViewModel = hiltViewModel()
) {
    val state = viewModel.collectAsState().value

    // SideEffect 없음 - Navigator가 직접 처리

    if (state.tableData != null) {
        FaceIdTermsOfServiceScreen(
            state = FaceIdTermsOfServiceState(
                acceptedPersonal = state.acceptedPersonal,
                textSize = state.textSize,
                smallTextSize = state.smallTextSize
            ),
            tableData = state.tableData,
            onChangePersonal = { viewModel.onPersonalInformationTermsAcceptedChange(it) },
            onClickAgree = { viewModel.onClickAgree() },
            onClickBack = { viewModel.onClickBack() }
        )
    }
}
