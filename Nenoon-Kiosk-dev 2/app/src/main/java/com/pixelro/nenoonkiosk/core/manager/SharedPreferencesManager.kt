package com.pixelro.nenoonkiosk.core.manager

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.harang.data.model.dto.GetCompoundTestResult
import com.harang.data.model.dto.User
import com.pixelro.nenoonkiosk.app.NenoonKioskApplication
import com.pixelro.nenoonkiosk.core.constants.NavConstants
import java.lang.reflect.Type

object SharedPreferencesManager {
    private const val PREFERENCE_NAME = NavConstants.PREFERENCE_NAME
    private const val KEY_REGISTERED_FACE_EMBEDDINGS = "registered_face_embeddings"
    private const val KEY_COMPOUND_TEST_RESULT_PREFIX = "compound_test_result_"
    private const val KEY_USER_ACCOUNTS = "user_accounts"
    private const val KEY_BLOOD_PRESSURE_MONITOR_TYPE = "blood_pressure_monitor_type"
    private const val KEY_LANGUAGE = "language"

    enum class BloodPressureMonitorType {
        BPBIO320,
        BP170B,
    }

    private val pref by lazy {
        NenoonKioskApplication.Companion.applicationContext().getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)
    }
    private val editor by lazy { pref.edit() }

    private val gson = Gson()

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
        return pref.getLong(key, 0)
    }

    fun putLanguage(languageCode: String) {
        editor.putString(KEY_LANGUAGE, languageCode)
        editor.commit()
    }

    fun getLanguage(): String {
        return pref.getString(KEY_LANGUAGE, "ko") ?: "ko"
    }

    fun putBloodPressureMonitorType(type: BloodPressureMonitorType) {
        editor.putString(KEY_BLOOD_PRESSURE_MONITOR_TYPE, type.name)
        editor.commit()
    }

    fun getBloodPressureMonitorType(): BloodPressureMonitorType {
        return when (pref.getString(KEY_BLOOD_PRESSURE_MONITOR_TYPE, "") ?: "") {
            BloodPressureMonitorType.BPBIO320.name -> BloodPressureMonitorType.BPBIO320
            BloodPressureMonitorType.BP170B.name -> BloodPressureMonitorType.BP170B
            else -> BloodPressureMonitorType.BPBIO320
        }
    }

    fun putUserAccount(
        id: String,
        password: String,
        name: String,
        email: String?,
    ): Boolean {
        val users = getAllUserAccounts().toMutableMap()

        if (users.containsKey(id)) {
            Log.d("SharedPreferencesManager", "User with ID '$id' already exists. Cannot register.")
            return false
        }

        val newUser = User(id = id, password = password, name = name, email = email)

        users[id] = newUser

        val jsonString = gson.toJson(users)
        editor.putString(KEY_USER_ACCOUNTS, jsonString)
        editor.apply()

        Log.d("SharedPreferencesManager", "User '$id' registered successfully.")
        return true
    }

    fun checkUserAccount(
        id: String,
        password: String,
    ): User? {
        val users = getAllUserAccounts()

        val user = users[id]

        if (user != null) {
            if (user.password == password) {
                Log.d("SharedPreferencesManager", "User '$id' authenticated successfully.")
                return user
            } else {
                Log.d("SharedPreferencesManager", "Incorrect password for user '$id'.")
            }
        } else {
            Log.d("SharedPreferencesManager", "User with ID '$id' not found.")
        }
        return user
    }

    fun checkUserAccount(id: String): User? {
        val users = getAllUserAccounts()

        val user = users[id]

        if (user != null) {
            Log.d("SharedPreferencesManager", "User '$id' authenticated successfully.")
        } else {
            Log.d("SharedPreferencesManager", "User with ID '$id' not found.")
        }
        return user
    }

    private fun getAllUserAccounts(): Map<String, User> {
        val jsonString = pref.getString(KEY_USER_ACCOUNTS, null)
        if (jsonString.isNullOrBlank()) {
            return emptyMap()
        }
        return try {
            val type: Type = object : TypeToken<Map<String, User>>() {}.type
            gson.fromJson(jsonString, type) ?: emptyMap()
        } catch (e: Exception) {
            Log.e("SharedPreferencesManager", "Error parsing user accounts from JSON.", e)
            emptyMap()
        }
    }

    fun putRegisteredFaceEmbeddings(embeddings: Map<String, FloatArray>) {
        val jsonString = gson.toJson(embeddings)
        Log.d("SharedPreferencesManager", "Saving embeddings JSON: '$jsonString'")
        editor.putString(KEY_REGISTERED_FACE_EMBEDDINGS, jsonString)
        editor.apply()
    }

    fun getRegisteredFaceEmbeddings(): Map<String, FloatArray> {
        val jsonString = pref.getString(KEY_REGISTERED_FACE_EMBEDDINGS, null)

        Log.d("SharedPreferencesManager", "Attempting to retrieve embeddings. Raw JSON: '${jsonString ?: "null"}'")

        if (jsonString.isNullOrBlank()) {
            Log.d("SharedPreferencesManager", "No registered embeddings found (JSON string is null or blank). Returning empty map.")
            return emptyMap()
        }

        return try {
            val type: Type = object : TypeToken<Map<String, List<Double>>>() {}.type
            val rawMap: Map<String, List<Double>>? = gson.fromJson(jsonString, type)

            if (rawMap == null) {
                Log.e("SharedPreferencesManager", "Gson.fromJson returned null for raw embeddings map. JSON: '$jsonString'")
                emptyMap()
            } else {
                val result = mutableMapOf<String, FloatArray>()
                rawMap.forEach { (userId, doubleList) ->
                    val floatArray = doubleList.map { it.toFloat() }.toFloatArray()
                    result[userId] = floatArray
                }
                Log.d("SharedPreferencesManager", "Successfully parsed ${result.size} embeddings from JSON.")
                result
            }
        } catch (e: Exception) {
            Log.e("SharedPreferencesManager", "Failed to convert JSON string to Map<String, FloatArray> for embeddings.", e)
            Log.e("SharedPreferencesManager", "Problematic JSON string: '$jsonString'", e)
            emptyMap()
        }
    }

    fun putCompoundTestResult(
        userId: String,
        result: GetCompoundTestResult,
    ) {
        val jsonString = gson.toJson(result)
        editor.putString(KEY_COMPOUND_TEST_RESULT_PREFIX + userId, jsonString)
        editor.apply()
    }

    fun getCompoundTestResult(userId: String): GetCompoundTestResult {
        val jsonString = pref.getString(KEY_COMPOUND_TEST_RESULT_PREFIX + userId, null)
        return if (jsonString.isNullOrBlank()) {
            GetCompoundTestResult(null, null, null)
        } else {
            gson.fromJson(jsonString, GetCompoundTestResult::class.java)
        }
    }

    fun updatePresbyopiaResult(
        firstDistance: Float?,
        secondDistance: Float?,
        thirdDistance: Float?,
        avgDistance: Float?,
        age: Int?,
    ) {
//        val currentResult = getCompoundTestResult(AppConstants.DEFAULT_USER_ID)
//        val updatedResult = currentResult.copy(
//            presbyopia_firstDistance = firstDistance,
//            presbyopia_secondDistance = secondDistance,
//            presbyopia_thirdDistance = thirdDistance,
//            presbyopia_avgDistance = avgDistance,
//            presbyopia_age = age
//        )
//        putCompoundTestResult(AppConstants.DEFAULT_USER_ID, updatedResult)
    }

    fun updateVisualAcuityResult(
        leftEye: Int?,
        rightEye: Int?,
    ) {
//        val currentResult = getCompoundTestResult(AppConstants.DEFAULT_USER_ID)
//        val updatedResult = currentResult.copy(
//            shortVisualAcuity_LeftEye = leftEye,
//            shortVisualAcuity_RightEye = rightEye
//        )
//        putCompoundTestResult(AppConstants.DEFAULT_USER_ID, updatedResult)
    }

    fun updateAmslerGridResult(
        leftMacularLoc: String?,
        rightMacularLoc: String?,
    ) {
//        val currentResult = getCompoundTestResult(AppConstants.DEFAULT_USER_ID)
//        val updatedResult = currentResult.copy(
//            amslerGrid_leftMacularLoc = leftMacularLoc,
//            amslerGrid_rightMacularLoc = rightMacularLoc
//        )
//        putCompoundTestResult(AppConstants.DEFAULT_USER_ID, updatedResult)
    }

    fun updateMChartResult(
        leftEyeVertical: Float?,
        rightEyeVertical: Float?,
        leftEyeHorizontal: Float?,
        rightEyeHorizontal: Float?,
    ) {
//        val currentResult = getCompoundTestResult(AppConstants.DEFAULT_USER_ID)
//        val updatedResult = currentResult.copy(
//            mChart_leftEyeVertical = leftEyeVertical,
//            mChart_rightEyeVertical = rightEyeVertical,
//            mChart_leftEyeHorizontal = leftEyeHorizontal,
//            mChart_rightEyeHorizontal = rightEyeHorizontal
//        )
//        putCompoundTestResult(AppConstants.DEFAULT_USER_ID, updatedResult)
    }

    fun updateBloodPressureResult(
        systolic: Float?,
        diastolic: Float?,
        pulseRate: Float?,
    ) {
//        val currentResult = getCompoundTestResult(AppConstants.DEFAULT_USER_ID)
//        val updatedResult = currentResult.copy(
//            bloodPressure_systolic = systolic,
//            bloodPressure_diastolic = diastolic,
//            bloodPressure_pulseRate = pulseRate
//        )
//        putCompoundTestResult(AppConstants.DEFAULT_USER_ID, updatedResult)
    }

    fun updateGripStrengthResult(
        leftGrip: Float?,
        rightGrip: Float?,
    ) {
//        val currentResult = getCompoundTestResult(AppConstants.DEFAULT_USER_ID)
//        val updatedResult = currentResult.copy(
//            gripStrength_leftGrip = leftGrip,
//            gripStrength_rightGrip = rightGrip
//        )
//        putCompoundTestResult(AppConstants.DEFAULT_USER_ID, updatedResult)
    }

    fun updateDementiaResult(
        q1: String?,
        q2: String?,
        q3: String?,
        q4: String?,
        q5: String?,
        q6: String?,
        q7: String?,
        q8: String?,
        q9: String?,
        q10: String?,
        q11: String?,
        q12: String?,
        q13: String?,
        q14: String?,
    ) {
//        val currentResult = getCompoundTestResult(AppConstants.DEFAULT_USER_ID)
//        val updatedResult = currentResult.copy(
//            dementia_question1 = q1, dementia_question2 = q2, dementia_question3 = q3,
//            dementia_question4 = q4, dementia_question5 = q5, dementia_question6 = q6,
//            dementia_question7 = q7, dementia_question8 = q8, dementia_question9 = q9,
//            dementia_question10 = q10, dementia_question11 = q11, dementia_question12 = q12,
//            dementia_question13 = q13, dementia_question14 = q14
//        )
//        putCompoundTestResult(AppConstants.DEFAULT_USER_ID, updatedResult)
    }

    fun updatePulmonaryFunctionResult(
        pulmonaryPower: Double?,
        pulmonaryCapacity: Double?,
        pulmonaryAge: Int?,
    ) {
//        val currentResult = getCompoundTestResult(AppConstants.DEFAULT_USER_ID)
//        val updatedResult = currentResult.copy(
//            pulmonaryFunction_pulmonaryPower = pulmonaryPower,
//            pulmonaryFunction_pulmonaryCapacity = pulmonaryCapacity,
//            pulmonaryFunction_pulmonaryAge = pulmonaryAge,
//        )
//        putCompoundTestResult(AppConstants.DEFAULT_USER_ID, updatedResult)
    }

    fun clearCompoundTestResult(userId: String) {
        editor.remove(KEY_COMPOUND_TEST_RESULT_PREFIX + userId)
        editor.apply()
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
