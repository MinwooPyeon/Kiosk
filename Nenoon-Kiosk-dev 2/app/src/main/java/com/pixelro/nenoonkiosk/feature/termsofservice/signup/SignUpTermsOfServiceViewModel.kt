package com.pixelro.nenoonkiosk.feature.termsofservice.signup

import android.app.Application
import android.content.Context
import androidx.compose.ui.unit.sp
import androidx.lifecycle.AndroidViewModel
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.constants.NavConstants
import com.pixelro.nenoonkiosk.core.navigation.Navigator
import com.pixelro.nenoonkiosk.core.navigation.SignInRoute
import com.pixelro.nenoonkiosk.core.util.StringProvider
import com.pixelro.nenoonkiosk.feature.termsofservice.base.TermsTableData
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class SignUpTermsOfServiceViewModel @Inject constructor(
    application: Application,
    private val navigator: Navigator
) : AndroidViewModel(application), ContainerHost<SignUpTermsOfServiceState, Nothing> {

    override val container: Container<SignUpTermsOfServiceState, Nothing> =
        container(SignUpTermsOfServiceState())

    init {
        loadInitialData()
    }

    private fun loadInitialData() = intent {
        val context = getApplication<Application>()
        val sp = context.getSharedPreferences(NavConstants.PREFERENCE_NAME, Context.MODE_PRIVATE)
        val savedLanguage = sp.getString("language", "defaultLanguage")
        val textSize = if (savedLanguage == "en") 16.sp else 20.sp

        val personalTable = TermsTableData(
            label = StringProvider.getString(R.string.signup_personal_info_table_label),
            column1 = StringProvider.getString(R.string.signup_personal_info_table_column1),
            column2 = StringProvider.getString(R.string.signup_personal_info_table_column2),
            column3 = StringProvider.getString(R.string.signup_personal_info_table_column3),
            evenly = false
        )

        val sensitiveTable = TermsTableData(
            label = StringProvider.getString(R.string.signup_sensitive_info_table_label),
            column1 = StringProvider.getString(R.string.signup_sensitive_info_table_column1),
            column2 = StringProvider.getString(R.string.signup_sensitive_info_table_column2),
            column3 = StringProvider.getString(R.string.signup_sensitive_info_table_column3),
            evenly = false
        )

        reduce {
            state.copy(
                textSize = textSize,
                personalTable = personalTable,
                sensitiveTable = sensitiveTable
            )
        }
    }

    fun onPersonalInformationTermsAcceptedChange(accepted: Boolean?) = intent {
        reduce {
            state.copy(acceptedPersonal = accepted)
        }
    }

    fun onSensitiveInformationTermsAcceptedChange(accepted: Boolean?) = intent {
        reduce {
            state.copy(acceptedSensitive = accepted)
        }
    }

    fun onClickAgree() = intent {
        if (state.acceptedPersonal == true && state.acceptedSensitive == true) {
            navigator.navigate(SignInRoute.SignUp)
        }
    }

    fun onClickBack() = intent {
        navigator.navigateBack()
    }
}
