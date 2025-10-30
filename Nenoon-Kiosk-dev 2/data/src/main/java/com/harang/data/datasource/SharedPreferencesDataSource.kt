package com.harang.data.datasource

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.harang.data.vo.Constants

class SharedPreferencesDataSource(
    context: Context,
) {
    private val pref: SharedPreferences =
        context.getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
    private val editor = pref.edit()
    private val gson = Gson()

    fun checkIfKeyExists(key: String): Boolean {
        return pref.contains(key)
    }

    fun removeKeyValue(key: String) {
        editor.remove(key)
        editor.commit()
    }

    private fun putStringAndCommit(
        key: String,
        string: String,
    ) {
        editor.putString(key, string)
        editor.commit()
    }

    private fun putIntAndCommit(
        key: String,
        int: Int,
    ) {
        editor.putInt(key, int)
        editor.commit()
    }

    private fun getStringAndReturn(key: String): String {
        return pref.getString(key, "") ?: ""
    }

    private fun getIntAndReturn(key: String): Int {
        return pref.getInt(key, 0)
    }

    private fun getLongAndReturn(key: String): Long {
        val value = pref.all[key]
        if (value is Long) {
            return value
        } else if (value is Int) {
            return value.toLong()
        } else {
            return pref.getLong(key, 0L)
        }
    }

    fun putString(
        key: String,
        value: String,
    ) {
        putStringAndCommit(key, value)
    }

    fun putInt(
        key: String,
        value: Int,
    ) {
        putIntAndCommit(key, value)
    }

    fun getString(key: String): String {
        return getStringAndReturn(key)
    }

    fun getInt(key: String): Int {
        return getIntAndReturn(key)
    }

    fun getLong(key: String): Long {
        return getLongAndReturn(key)
    }
}
