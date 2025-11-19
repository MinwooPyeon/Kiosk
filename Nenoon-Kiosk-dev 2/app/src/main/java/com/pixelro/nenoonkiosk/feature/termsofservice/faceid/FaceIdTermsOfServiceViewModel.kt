package com.pixelro.nenoonkiosk.feature.termsofservice.faceid

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FaceIdTermsOfServiceViewModel @Inject constructor() : ViewModel() {
    private val _acceptedPersonalInformationTerms = mutableStateOf<Boolean?>(null)
    val acceptedPersonalInformationTerms: State<Boolean?> = _acceptedPersonalInformationTerms

    fun onPersonalInformationTermsAcceptedChange(newValue: Boolean?) {
        _acceptedPersonalInformationTerms.value = newValue
    }
}