package com.harang.data.vo

object Constants {
    const val SHARED_PREFERENCES_NAME = "nenoon_kiosk_shared_preferences"

    const val PREF_LOCATION_ID = "locationId"
    const val PREF_VIDEO_URI = "screenSaverVideoURI"

    object ApiField {
        const val ACCESS_TOKEN = "accessToken"
        const val REFRESH_TOKEN = "refreshToken"
        const val QR_URL = "qrUrl"
        const val SUCCESS = "success"
        const val CODE = "code"
    }

    object ApiErrorCode {
        const val DUPLICATE_ID = 10301
        const val INVALID_CREDENTIALS = 10103
    }
}
