package com.pixelro.nenoonkiosk.feature.categorylist

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pixelro.nenoonkiosk.core.constants.NavConstants
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class CategoryListViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val prefs = context.getSharedPreferences(NavConstants.PREFERENCE_NAME, Context.MODE_PRIVATE)

    private val _language = MutableStateFlow(prefs.getString("language", "ko") ?: "ko")
    val language: StateFlow<String> = _language

    private val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
        if (key == "language") {
            _language.value = prefs.getString("language", "ko") ?: "ko"
        }
    }

    init {
        prefs.registerOnSharedPreferenceChangeListener(listener)
    }

    override fun onCleared() {
        prefs.unregisterOnSharedPreferenceChangeListener(listener)
    }
}
