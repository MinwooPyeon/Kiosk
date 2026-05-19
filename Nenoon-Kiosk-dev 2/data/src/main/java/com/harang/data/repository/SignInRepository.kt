package com.harang.data.repository

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.harang.data.datasource.SharedPreferencesDataSource
import com.harang.data.datasource.SignInRemoteDataSource
import com.harang.data.model.dto.User
import com.harang.data.model.dto.response.SendLocationSignInDataResponse
import com.harang.data.util.ErrorCode
import com.harang.data.util.Result
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
    suspend fun locationSignIn(id: String, pw: String): SendLocationSignInDataResponse? {
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
    ): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                val response = remoteDataSource.userSignUp(id, pw, name, email, pid, vector, qrUrl)
                val errorCode = response?.data?.get(Constants.ApiField.CODE) as? Int
                val accessToken = response?.data?.get(Constants.ApiField.ACCESS_TOKEN) as? String

                when {
                    errorCode == Constants.ApiErrorCode.DUPLICATE_ID ->
                        Result.Error(ErrorCode.DUPLICATE_ID, "이미 사용 중인 아이디입니다.")
                    errorCode == Constants.ApiErrorCode.INVALID_CREDENTIALS ->
                        Result.Error(ErrorCode.INVALID_CREDENTIALS, "잘못된 인증 정보입니다.")
                    accessToken != null ->
                        Result.Success(accessToken)
                    else ->
                        Result.Error(ErrorCode.UNKNOWN, "회원가입에 실패했습니다.")
                }
            } catch (e: Exception) {
                Log.e(TAG, "userSignUp failed", e)
                Result.Error(ErrorCode.NETWORK_ERROR, e.message ?: "")
            }
        }
    }

    suspend fun userSignIn(id: String, pw: String): Result<User> {
        return withContext(Dispatchers.IO) {
            try {
                val response = remoteDataSource.userSignIn(id, pw)
                val accessToken = response?.data?.get(Constants.ApiField.ACCESS_TOKEN) as? String
                val refreshToken = response?.data?.get(Constants.ApiField.REFRESH_TOKEN) as? String

                if (!accessToken.isNullOrBlank() && !refreshToken.isNullOrBlank()) {
                    Result.Success(User(accessToken = accessToken, refreshToken = refreshToken))
                } else {
                    Result.Error(ErrorCode.INVALID_CREDENTIALS, "아이디 또는 비밀번호를 확인해주세요.")
                }
            } catch (e: Exception) {
                Log.e(TAG, "userSignIn failed", e)
                Result.Error(ErrorCode.NETWORK_ERROR, e.message ?: "")
            }
        }
    }

    suspend fun userSignInWithFace(vector: String, threshold: Double): Result<User> {
        return withContext(Dispatchers.IO) {
            try {
                val response = remoteDataSource.userSignInWithFace(vector, threshold)
                val accessToken = response?.data?.get(Constants.ApiField.ACCESS_TOKEN) as? String
                val refreshToken = response?.data?.get(Constants.ApiField.REFRESH_TOKEN) as? String

                if (!accessToken.isNullOrBlank() && !refreshToken.isNullOrBlank()) {
                    Result.Success(User(accessToken = accessToken, refreshToken = refreshToken))
                } else {
                    Result.Error(ErrorCode.INVALID_CREDENTIALS, "얼굴 인증에 실패했습니다.")
                }
            } catch (e: Exception) {
                Log.e(TAG, "userSignInWithFace failed", e)
                Result.Error(ErrorCode.NETWORK_ERROR, e.message ?: "")
            }
        }
    }

    suspend fun getUserProfile(token: String): Result<User> {
        return withContext(Dispatchers.IO) {
            try {
                val profile = remoteDataSource.getUserProfile(token)
                if (profile != null) {
                    Result.Success(
                        User(
                            name = profile.data["name"] as? String,
                            id = profile.data["loginId"] as? String,
                            email = profile.data["email"] as? String,
                            age = profile.data["age"] as? Double,
                            gender = profile.data["gender"] as? String,
                            surveyId = "${profile.data["surveyId"] as? Double}",
                        )
                    )
                } else {
                    Result.Error(ErrorCode.UNKNOWN, "프로필을 불러올 수 없습니다.")
                }
            } catch (e: Exception) {
                Log.e(TAG, "getUserProfile failed", e)
                Result.Error(ErrorCode.NETWORK_ERROR, e.message ?: "")
            }
        }
    }

    suspend fun getQrUrl(token: String): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                val response = remoteDataSource.getQrUrl(token)
                val url = response?.data?.get(Constants.ApiField.QR_URL) as? String
                val success = response?.data?.get(Constants.ApiField.SUCCESS) as? Boolean

                if (success == true && url != null) {
                    Result.Success(url)
                } else {
                    Result.Error(ErrorCode.UNKNOWN, "QR URL을 불러올 수 없습니다.")
                }
            } catch (e: Exception) {
                Log.e(TAG, "getQrUrl failed", e)
                Result.Error(ErrorCode.NETWORK_ERROR, e.message ?: "")
            }
        }
    }

    suspend fun updateQrCode(qrCode: File?): Result<String> {
        if (qrCode == null) return Result.Error(ErrorCode.UNKNOWN, "QR 파일이 없습니다.")
        return withContext(Dispatchers.IO) {
            try {
                val requestFile = qrCode.asRequestBody("image/jpeg".toMediaTypeOrNull())
                val qrCodePart = MultipartBody.Part.createFormData("file", qrCode.name, requestFile)
                val url = remoteDataSource.updateQrCode(qrCodePart)?.data?.get(Constants.ApiField.QR_URL) as? String

                if (url != null) {
                    Result.Success(url)
                } else {
                    Result.Error(ErrorCode.UNKNOWN, "QR 업데이트에 실패했습니다.")
                }
            } catch (e: Exception) {
                Log.e(TAG, "updateQrCode failed", e)
                Result.Error(ErrorCode.NETWORK_ERROR, e.message ?: "")
            }
        }
    }

    suspend fun getQrCode(filename: String): Result<Bitmap> {
        return withContext(Dispatchers.IO) {
            try {
                val bytes = remoteDataSource.getQrCode(filename)?.bytes()
                if (bytes != null) {
                    Result.Success(BitmapFactory.decodeByteArray(bytes, 0, bytes.size))
                } else {
                    Result.Error(ErrorCode.UNKNOWN, "QR 이미지를 불러올 수 없습니다.")
                }
            } catch (e: Exception) {
                Log.e(TAG, "getQrCode failed", e)
                Result.Error(ErrorCode.NETWORK_ERROR, e.message ?: "")
            }
        }
    }

    suspend fun userUpdateFace(token: String, vector: String): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val response = remoteDataSource.userUpdateFace(token, vector)
                if (response != null) {
                    Result.Success(Unit)
                } else {
                    Result.Error(ErrorCode.UNKNOWN, "얼굴 정보 업데이트에 실패했습니다.")
                }
            } catch (e: Exception) {
                Log.e(TAG, "userUpdateFace failed", e)
                Result.Error(ErrorCode.NETWORK_ERROR, e.message ?: "")
            }
        }
    }

    fun getLocationId(): Int {
        return sharedPreferencesDataSource.getInt(Constants.PREF_LOCATION_ID)
    }

    companion object {
        private const val TAG = "SignInRepository"
    }
}
