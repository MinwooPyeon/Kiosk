package com.pixelro.nenoonkiosk.feature.termsofservice.base

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TermsOfServiceViewModel
@Inject
constructor() : ViewModel() {
    private val _acceptedPersonalInformationTerms = mutableStateOf<Boolean?>(null)
    val acceptedPersonalInformationTerms: State<Boolean?> = _acceptedPersonalInformationTerms

    private val _acceptedSensitiveInformationTerms = mutableStateOf<Boolean?>(null)
    val acceptedSensitiveInformationTerms: State<Boolean?> = _acceptedSensitiveInformationTerms

    fun onPersonalInformationTermsAcceptedChange(newValue: Boolean?) {
        _acceptedPersonalInformationTerms.value = newValue
    }

    fun onSensitiveInformationTermsAcceptedChange(newValue: Boolean?) {
        _acceptedSensitiveInformationTerms.value = newValue
    }
}