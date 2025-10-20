package com.pixelro.nenoonkiosk.core.constants

object AppConstants {

    // API endpoint
    const val BASE_API_ADDRESS = "https://pixelro.nenoons.com:8080/"

    // 관리자 페이지 URL
    const val ADMIN_PAGE_URL = "http://172.30.1.40:3000/login"
    val ADMIN_PAGE_LOCALE_PARAMETER = mapOf(
        "ko" to "lang=ko",
        "en" to "lang=en",
    )

    // 사용자 기본 이메일 (회원가입 시 이메일 작성하지 않은 경우 사용되는 이메일)
    const val DEFAULT_EMAIL = "info@pixelro.com"

    // 비회원 사용자용 기본 계정
    const val DEFAULT_USER_ID = "default_user"

    // 미등록 기기용 기본 ID
    const val DEFAULT_LOCATION_ID = 1

    // 키오스크 모드 강제종료 패스워드
    const val KIOSK_MODE_EXIT_PASSWORD = "admin"

    // 사용자 계정 정보를 기기 내에서만 저장 및 관리하는 설정. 전시, 시연 등 환경에서만 사용 권장.
    const val MANAGE_USERS_INTERNALLY = false

    // 오프라인 혹은 연결이 원활하지 않은 상황에서 데이터 저장을 포기하고 로그인 없이는 사용이 가능한 모드
    const val ALLOW_OFFLINE_BYPASS_FOR_SIGN_IN_SKIP = true

    const val APP_VERSION = "0.0"

    const val FACE_ID_MAX_ATTEMPTS = 10
    const val FACE_ID_INTERVAL = 2000L
    const val FACE_ID_THRESHOLD = 0.85
}