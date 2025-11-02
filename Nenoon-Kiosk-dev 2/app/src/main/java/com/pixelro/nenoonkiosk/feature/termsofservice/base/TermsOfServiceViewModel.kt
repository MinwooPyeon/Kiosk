package com.pixelro.nenoonkiosk.feature.termsofservice.base

import android.app.Application
import android.content.Context
import androidx.compose.ui.unit.sp
import androidx.lifecycle.AndroidViewModel
import com.pixelro.nenoonkiosk.core.navigation.Navigator
import com.pixelro.nenoonkiosk.core.navigation.Route
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class TermsOfServiceViewModel @Inject constructor(
    application: Application,
    private val navigator: Navigator
) : AndroidViewModel(application),
    ContainerHost<TermsOfServiceUiState, Nothing> {

    override val container: Container<TermsOfServiceUiState, Nothing> =
        container(TermsOfServiceUiState())

    fun loadLanguageBasedTextSize() = intent {
        val context = getApplication<Application>()
        val sp = context.getSharedPreferences(NavConstants.PREFERENCE_NAME, Context.MODE_PRIVATE)
        val savedLanguage = sp.getString("language", "defaultLanguage")
        val textSize = if (savedLanguage == "en") 16.sp else 20.sp

        reduce {
            state.copy(textSize = textSize)
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
            navigator.navigate(Route.Intro)
        }
    }

    fun onClickBack() = intent {
        navigator.navigateBack()
    }
}
