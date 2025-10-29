package com.pixelro.nenoonkiosk.feature.termsofservice.faceid

import android.app.Application
import android.content.Context
import androidx.compose.ui.unit.sp
import androidx.lifecycle.AndroidViewModel
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.constants.NavConstants
import com.pixelro.nenoonkiosk.core.navigation.Navigator
import com.pixelro.nenoonkiosk.core.util.StringProvider
import com.pixelro.nenoonkiosk.feature.termsofservice.base.TermsTableData
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class FaceIdTermsOfServiceViewModel @Inject constructor(
    application: Application,
    private val navigator: Navigator
) : AndroidViewModel(application), ContainerHost<FaceIdTermsOfServiceState, Nothing> {

    override val container: Container<FaceIdTermsOfServiceState, Nothing> =
        container(FaceIdTermsOfServiceState())

    init {
        loadInitialData()
    }

    private fun loadInitialData() = intent {
        val context = getApplication<Application>()
        val sp = context.getSharedPreferences(NavConstants.PREFERENCE_NAME, Context.MODE_PRIVATE)
        val savedLanguage = sp.getString("language", "defaultLanguage")

        val textSize = if (savedLanguage == "en") 16.sp else 20.sp
        val smallTextSize = if (savedLanguage == "en") 14.sp else 18.sp

        val tableData = TermsTableData(
            label = StringProvider.getString(R.string.face_id_table_label),
            column1 = StringProvider.getString(R.string.face_id_table_column1),
            column2 = StringProvider.getString(R.string.face_id_table_column2),
            column3 = StringProvider.getString(R.string.face_id_table_column3),
            evenly = true
        )

        reduce {
            state.copy(
                textSize = textSize,
                smallTextSize = smallTextSize,
                tableData = tableData
            )
        }
    }

    fun onPersonalInformationTermsAcceptedChange(accepted: Boolean?) = intent {
        reduce {
            state.copy(acceptedPersonal = accepted)
        }
    }

    fun onClickAgree() = intent {
        if (state.acceptedPersonal == true) {
            navigator.navigateBack() // FaceEnrollment로 돌아가기
        }
    }

    fun onClickBack() = intent {
        navigator.navigateBack()
    }
}
