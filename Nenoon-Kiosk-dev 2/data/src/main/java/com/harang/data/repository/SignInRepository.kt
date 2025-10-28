package com.harang.data.repository

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.harang.data.datasource.SharedPreferencesDataSource
import com.harang.data.datasource.SignInRemoteDataSource
import com.harang.data.model.dto.User
import com.harang.data.model.dto.response.SendLocationSignInDataResponse
import com.harang.data.vo.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class SignInRepository(
    private val remoteDataSource: SignInRemoteDataSource,
    private val sharedPreferencesDataSource: SharedPreferencesDataSource,
) {
    suspend fun locationSignIn(
        id: String,
        pw: String,
    ): SendLocationSignInDataResponse? {
        return withContext(Dispatchers.IO) {
            remoteDataSource.locationSignIn(id, pw)
        }
    }

    suspend fun updateLocationId(locationId: Int) {
        withContext(Dispatchers.IO) {
            sharedPreferencesDataSource.putInt(Constants.PREF_LOCATION_ID, locationId)
        }
    }

    suspend fun updateScreenSaverVideoURI(uri: String) {
        withContext(Dispatchers.IO) {
            sharedPreferencesDataSource.putString(Constants.PREF_VIDEO_URI, uri)
        }
    }

    suspend fun locationSignOut() {
        withContext(Dispatchers.IO) {
            sharedPreferencesDataSource.removeKeyValue(Constants.PREF_LOCATION_ID)
            sharedPreferencesDataSource.removeKeyValue(Constants.PREF_VIDEO_URI)
        }
    }

    suspend fun userSignUp(
        id: String,
        pw: String,
        name: String,
        email: String?,
        pid: Long,
        vector: String?,
        qrUrl: String?,
    ): String? {
        return withContext(Dispatchers.IO) {
            try {
                val temp = remoteDataSource.userSignUp(id, pw, name, email, pid, vector, qrUrl)
                val errorCode = temp?.data?.get("code") as Int?
                if (errorCode != null) {
                    when (errorCode) {
                        10301 -> null
                        10103 -> null
                        else -> null
                    }
                } else if (temp == null) {
                    null
                } else {
                    temp.data["accessToken"] as String?
                }
            } catch (e: Exception) {
                Log.e("SignInRepository", e.toString())
                null
            }
        }
    }

    suspend fun updateQrCode(qrCode: File?): String? {
        if (qrCode == null) return null
        return withContext(Dispatchers.IO) {
            try {
                val requestFile = qrCode.asRequestBody("image/jpeg".toMediaTypeOrNull())

                val qrCodePart =
                    MultipartBody.Part.createFormData(
                        "file",
                        qrCode.name,
                        requestFile,
                    )

                remoteDataSource.updateQrCode(qrCodePart)?.data?.get("qrUrl") as String
            } catch (e: Exception) {
                Log.e("SignInRepository", e.toString())
                null
            }
        }
    }

    suspend fun getQrUrl(token: String): String? {
        return withContext(Dispatchers.IO) {
            try {
                val temp = remoteDataSource.getQrUrl(token)
                val url = temp?.data?.get("qrUrl") as String?
                val success = temp?.data?.get("success") as Boolean?
                if (success == null || !success || url == null) {
                    null
                } else {
                    url
                }
            } catch (e: Exception) {
                Log.e("SignInRepository", e.toString())
                null
            }
        }
    }

    suspend fun getQrCode(filename: String): Bitmap? {
        return withContext(Dispatchers.IO) {
            try {
                val bytes = remoteDataSource.getQrCode(filename)?.bytes()
                if (bytes != null) {
                    BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                } else {
                    null
                }
            } catch (e: Exception) {
                Log.e("SignInRepository", e.toString())
                null
            }
        }
    }

    suspend fun getUserProfile(token: String): User? {
        return withContext(Dispatchers.IO) {
            try {
                val profile = remoteDataSource.getUserProfile(token)
                if (profile != null) {
                    User(
                        name = profile.data["name"] as String?,
                        id = profile.data["loginId"] as String?,
                        email = profile.data["email"] as String?,
                        age = profile.data["age"] as Double?,
                        gender = profile.data["gender"] as String?,
                        surveyId = "${profile.data["surveyId"] as Double?}",
                    )
                } else {
                    null
                }
            } catch (e: Exception) {
                Log.e("SignInRepository", e.toString())
                null
            }
        }
    }

    suspend fun userSignIn(
        id: String,
        pw: String,
    ): User? {
        return withContext(Dispatchers.IO) {
            try {
                val temp = remoteDataSource.userSignIn(id, pw)
                val accessToken = temp?.data?.get("accessToken") as String?
                val refreshToken = temp?.data?.get("refreshToken") as String?
                if (accessToken.isNullOrBlank() || refreshToken.isNullOrBlank()) {
                    null
                } else {
                    User(accessToken = accessToken, refreshToken = refreshToken)
                }
            } catch (e: Exception) {
                Log.e("SignInRepository", e.toString())
                null
            }
        }
    }

    suspend fun userUpdateFace(
        token: String,
        vector: String,
    ): Boolean {
        return withContext(Dispatchers.IO) {
            val data = remoteDataSource.userUpdateFace(token, vector)
            data != null
        }
    }

    suspend fun userSignInWithFace(
        vector: String,
        threshold: Double,
    ): User? {
        return withContext(Dispatchers.IO) {
            try {
                val temp = remoteDataSource.userSignInWithFace(vector, threshold)
                val accessToken = temp?.data?.get("accessToken") as String
                val refreshToken = temp?.data?.get("refreshToken") as String
                if (accessToken.isBlank() || refreshToken.isBlank()) {
                    null
                } else {
                    User(accessToken = accessToken, refreshToken = refreshToken)
                }
            } catch (e: Exception) {
                Log.e("SignInRepository", e.toString())
                null
            }
        }
    }

    fun getLocationId(): Int {
        return sharedPreferencesDataSource.getInt(Constants.PREF_LOCATION_ID)
    }
}
