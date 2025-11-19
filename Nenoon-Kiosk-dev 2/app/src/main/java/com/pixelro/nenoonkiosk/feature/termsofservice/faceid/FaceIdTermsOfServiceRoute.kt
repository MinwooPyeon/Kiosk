package com.pixelro.nenoonkiosk.feature.termsofservice.faceid

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
import com.pixelro.nenoonkiosk.feature.termsofservice.base.TermsTableData

@Composable
fun FaceIdTermsOfServiceRoute(
    onTermsAccepted: () -> Unit,
    onTermsRejected: () -> Unit,
    viewModel: FaceIdTermsOfServiceViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val (textSize, smallTextSize) = remember {
        val sp = context.getSharedPreferences(NavConstants.PREFERENCE_NAME, Context.MODE_PRIVATE)
        val savedLanguage = sp.getString("language", "defaultLanguage")
        // 언어에 따른 폰트 크기(프로젝트 정책에 맞게 조정 가능)
        val normal = if (savedLanguage == "en") 16.sp else 20.sp
        val small  = if (savedLanguage == "en") 14.sp else 18.sp
        normal to small
    }

    val accepted by viewModel.acceptedPersonalInformationTerms

    val uiState = FaceIdTosUiState(
        acceptedPersonal = accepted,
        textSize = textSize,
        smallTextSize = smallTextSize
    )

    // 표 데이터 (균등 분할이므로 evenly=true)
    val faceIdTable = remember {
        TermsTableData(
            label = StringProvider.getString(R.string.face_id_table_label),
            column1 = StringProvider.getString(R.string.face_id_table_column1),
            column2 = StringProvider.getString(R.string.face_id_table_column2),
            column3 = StringProvider.getString(R.string.face_id_table_column3),
            evenly = true
        )
    }

    FaceIdTermsOfServiceScreen(
        state = uiState,
        tableData = faceIdTable,
        onChangePersonal = { viewModel.onPersonalInformationTermsAcceptedChange(it) },
        onClickAgree = {
            if (uiState.acceptedPersonal == true) onTermsAccepted()
        },
        onClickBack = onTermsRejected
    )
}