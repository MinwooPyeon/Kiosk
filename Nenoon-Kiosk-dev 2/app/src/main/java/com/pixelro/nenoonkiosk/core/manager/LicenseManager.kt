package com.pixelro.nenoonkiosk.core.manager

import android.content.Context
import android.provider.Settings
import android.util.Log
import androidx.core.content.edit
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import java.security.MessageDigest
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 라이선스 인증 관리자
 *
 * 보안 레이어:
 * - Android Keystore: 하드웨어 수준 키 저장 (TEE)
 * - AES-256-GCM: 강력한 암호화 + 무결성 검증
 * - 기기 ID 바인딩: 복제 방지
 * - R8 난독화: 코드 보호
 * - 재시도 제한: Brute-force 방지
 * - Constant-time 비교: 타이밍 공격 방지
 */
@Singleton
class LicenseManager @Inject constructor(
    private val context: Context
) {
    companion object {
        private const val TAG = "LicenseManager"
        private const val PREFS_NAME = "license_secure_prefs"
        private const val KEY_LICENSE_HASH = "license_hash"
        private const val KEY_DEVICE_ID = "device_id"
        private const val KEY_ACTIVATED_TIME = "activated_time"
        private const val KEY_IS_ACTIVATED = "is_activated"
        private const val KEY_FAILED_ATTEMPTS = "failed_attempts"
        private const val KEY_LOCKOUT_UNTIL = "lockout_until"

        // 재시도 제한
        private const val MAX_ATTEMPTS = 5
        private const val LOCKOUT_DURATION_MS = 30 * 60 * 1000L  // 30분

        // 암호화된 비밀번호 (문자열 은닉)
        // 다중 XOR 키로 강화된 암호화
        // "1234" 를 KEYS로 암호화한 결과
        private val ENC_PWD = byteArrayOf(0x4B, 0x0D, 0xA2.toByte(), 0x59)
        private val KEYS = intArrayOf(0x7A, 0x3F, 0x91, 0x6D)

        // SHA-256 Salt (레인보우 테이블 공격 방지)
        private const val HASH_SALT = "nenoon_kiosk_secure_v1_"

        /**
         * 런타임에 비밀번호 복호화
         * R8 난독화 + 다중 키 XOR 암호화로 보호
         */
        private fun getPassword(): String {
            return ENC_PWD.mapIndexed { index, byte ->
                val decrypted = (byte.toInt() and 0xFF) xor KEYS[index % KEYS.size]
                decrypted.toChar()
            }.joinToString("")
        }

        /**
         * Constant-time 문자열 비교 (타이밍 공격 방지)
         */
        private fun constantTimeEquals(a: String, b: String): Boolean {
            if (a.length != b.length) return false

            var result = 0
            for (i in a.indices) {
                result = result or (a[i].code xor b[i].code)
            }
            return result == 0
        }
    }

    // Keystore 마스터 키 (하드웨어 TEE에 저장)
    private val masterKey: MasterKey by lazy {
        MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
    }

    // 복제 감지 플래그 (Toast 표시용)
    private var _wasClonedDetected = false

    // EncryptedSharedPreferences (Keystore 키 사용)
    private val encryptedPrefs by lazy {
        try {
            EncryptedSharedPreferences.create(
                context,
                PREFS_NAME,
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        } catch (e: Exception) {
            // AEADBadTagException = 다른 기기의 Keystore로 암호화된 데이터 (복제 감지!)
            if (e is javax.crypto.AEADBadTagException || e.cause is javax.crypto.AEADBadTagException) {
                _wasClonedDetected = true
            }

            // Fallback: 기존 암호화된 파일 삭제 후 재생성
            context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit {
                clear()
            }

            EncryptedSharedPreferences.create(
                context,
                PREFS_NAME,
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        }
    }

    /**
     * 기기 고유 ID 가져오기 (Android ID)
     * - 공장 초기화 시 변경됨
     * - 앱마다 다름 (Android 8.0+)
     * - 권한 불필요
     */
    private fun getDeviceId(): String {
        val androidId = Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ANDROID_ID
        )

        // Android ID가 없으면 보안상 실행 불가
        if (androidId.isNullOrBlank()) {
            throw SecurityException("Device ID unavailable")
        }

        return androidId
    }

    /**
     * SHA-256 해시 생성 (Salt 포함)
     */
    private fun sha256(input: String): String {
        val salted = HASH_SALT + input
        return MessageDigest.getInstance("SHA-256")
            .digest(salted.toByteArray())
            .joinToString("") { "%02x".format(it) }
    }

    /**
     * 라이선스 활성화 (최초 인증)
     *
     * @param password 비밀번호 (기업에 전달된 비밀번호)
     * @return 인증 성공 여부
     */
    fun activateLicense(password: String): Boolean {
        if (password.isBlank()) {
            return false
        }

        // 1. 잠금 상태 확인 (Brute-force 방지)
        val lockoutUntil = encryptedPrefs.getLong(KEY_LOCKOUT_UNTIL, 0L)
        val currentTime = System.currentTimeMillis()

        if (currentTime < lockoutUntil) {
            return false
        }

        // 2. Constant-time 비교 (타이밍 공격 방지)
        val expectedPassword = getPassword()
        val isPasswordCorrect = constantTimeEquals(password, expectedPassword)

        if (!isPasswordCorrect) {
            // 실패 횟수 증가
            val currentFailedAttempts = encryptedPrefs.getInt(KEY_FAILED_ATTEMPTS, 0)
            val failedAttempts = currentFailedAttempts + 1

            encryptedPrefs.edit {
                putInt(KEY_FAILED_ATTEMPTS, failedAttempts)
            }

            // 최대 시도 횟수 초과 시 잠금
            if (failedAttempts >= MAX_ATTEMPTS) {
                val lockoutTime = currentTime + LOCKOUT_DURATION_MS

                encryptedPrefs.edit {
                    putLong(KEY_LOCKOUT_UNTIL, lockoutTime)
                    putInt(KEY_FAILED_ATTEMPTS, 0)  // 초기화
                }
            }

            return false
        }

        // 3. 인증 성공 - 라이선스 활성화
        return try {
            val deviceId = getDeviceId()
            val combined = "$password-$deviceId"
            val hash = sha256(combined)

            // Keystore 키로 암호화되어 저장
            encryptedPrefs.edit {
                putString(KEY_LICENSE_HASH, hash)
                putString(KEY_DEVICE_ID, deviceId)
                putLong(KEY_ACTIVATED_TIME, currentTime)
                putBoolean(KEY_IS_ACTIVATED, true)
                putInt(KEY_FAILED_ATTEMPTS, 0)  // 실패 횟수 초기화
                putLong(KEY_LOCKOUT_UNTIL, 0L)  // 잠금 해제
            }

            true
        } catch (e: Exception) {
            false
        }
    }

    /**
     * 라이선스 검증 (비밀번호 확인)
     *
     * @param password 입력한 비밀번호
     * @return 검증 성공 여부
     */
    fun verifyPassword(password: String): Boolean {
        if (password.isBlank()) {
            return false
        }

        return try {
            val deviceId = getDeviceId()
            val combined = "$password-$deviceId"
            val inputHash = sha256(combined)

            val storedHash = encryptedPrefs.getString(KEY_LICENSE_HASH, null) ?: return false

            // Constant-time 비교 (타이밍 공격 방지)
            constantTimeEquals(inputHash, storedHash)
        } catch (e: Exception) {
            false
        }
    }

    /**
     * 라이선스 유효성 확인
     *
     * @return 라이선스 유효 여부
     */
    fun isLicenseValid(): Boolean {
        return try {
            // 1. 활성화 여부 확인
            val isActivated = encryptedPrefs.getBoolean(KEY_IS_ACTIVATED, false)
            if (!isActivated) {
                return false
            }

            // 2. 기기 ID 바인딩 확인
            val storedDeviceId = encryptedPrefs.getString(KEY_DEVICE_ID, null)
            val currentDeviceId = getDeviceId()

            if (storedDeviceId != currentDeviceId) {
                clearLicense()
                return false
            }

            // 3. 라이선스 해시 존재 확인
            val hash = encryptedPrefs.getString(KEY_LICENSE_HASH, null)
            if (hash == null) {
                return false
            }

            true
        } catch (e: Exception) {
            // 복호화 실패 = 다른 기기의 Keystore로 암호화된 데이터
            clearLicense()
            false
        }
    }

    /**
     * 기기 ID 불일치 여부 확인 (Toast 표시용)
     * @return true if device was cloned to different device
     */
    fun wasDeviceCloned(): Boolean {
        // 1. Keystore 복호화 실패로 감지된 경우
        if (_wasClonedDetected) {
            return true
        }

        // 2. Device ID 불일치로 감지된 경우
        return try {
            val isActivated = encryptedPrefs.getBoolean(KEY_IS_ACTIVATED, false)
            if (!isActivated) {
                return false
            }

            val storedDeviceId = encryptedPrefs.getString(KEY_DEVICE_ID, null)
            val currentDeviceId = getDeviceId()

            storedDeviceId != null && storedDeviceId != currentDeviceId
        } catch (e: Exception) {
            false
        }
    }

    /**
     * 라이선스 정보 초기화
     */
    fun clearLicense() {
        try {
            encryptedPrefs.edit {
                clear()
            }
        } catch (e: Exception) {
            // Silent fail (정보 노출 방지)
        }
    }

    /**
     * 라이선스 활성화 시각 가져오기
     */
    fun getActivatedTime(): Long {
        return try {
            encryptedPrefs.getLong(KEY_ACTIVATED_TIME, 0L)
        } catch (e: Exception) {
            0L
        }
    }

    /**
     * 디버그 정보 (개발용)
     * Release 빌드에서는 비활성화됨
     */
    fun getDebugInfo(): String {
        // Release 빌드에서는 정보 노출 방지
        // ApplicationInfo.FLAG_DEBUGGABLE로 확인
        val isDebuggable = (context.applicationInfo.flags and android.content.pm.ApplicationInfo.FLAG_DEBUGGABLE) != 0

        if (!isDebuggable) {
            return "Debug info not available in release build"
        }

        return try {
            val deviceId = getDeviceId()
            val isActivated = encryptedPrefs.getBoolean(KEY_IS_ACTIVATED, false)
            val activatedTime = getActivatedTime()
            val storedDeviceId = encryptedPrefs.getString(KEY_DEVICE_ID, "none")

            """
            Device ID: $deviceId
            Stored Device ID: $storedDeviceId
            Is Activated: $isActivated
            Activated Time: $activatedTime
            Device Match: ${deviceId == storedDeviceId}
            """.trimIndent()
        } catch (e: Exception) {
            "Error: ${e.message}"
        }
    }

    /**
     * 잠금 상태 확인 (UI 표시용)
     * @return 잠금 해제까지 남은 시간 (밀리초), 0이면 잠금 없음
     */
    fun getLockoutTimeRemaining(): Long {
        val lockoutUntil = encryptedPrefs.getLong(KEY_LOCKOUT_UNTIL, 0L)
        val currentTime = System.currentTimeMillis()
        val remaining = lockoutUntil - currentTime
        return if (remaining > 0) remaining else 0L
    }
}