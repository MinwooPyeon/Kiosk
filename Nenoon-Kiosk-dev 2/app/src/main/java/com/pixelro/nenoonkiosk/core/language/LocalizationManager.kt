package com.pixelro.nenoonkiosk.core.language

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

object LocalizationManager {
    private const val PREFS_NAME = "LocalizationPrefs"
    private const val LANGUAGE_CODE_KEY = "language_code"
    private const val DEFAULT_LANGUAGE = "ko"

    private fun getSharedPreferences(context: Context): SharedPreferences{
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }
    fun saveLanguageCode(context: Context, languageCode: String){
        getSharedPreferences(context).edit() { putString(LANGUAGE_CODE_KEY, languageCode) }
    }
}