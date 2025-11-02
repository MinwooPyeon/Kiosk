package com.pixelro.nenoonkiosk.core.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

sealed interface Route : NavKey {
    @Serializable
    data object Splash : Route

    @Serializable
    data object ScreenSaver : Route

    @Serializable
    data object Permission : Route

    @Serializable
    data object Intro : Route

    @Serializable
    data object Survey : Route

    @Serializable
    data object ResultPrint : Route

    @Serializable
    data object Settings : Route

    @Serializable
    data object BTDeviceManagement : Route
}

sealed interface SignInRoute : Route {
    @Serializable
    data object LocationSignIn : SignInRoute

    @Serializable
    data object UserSignIn : SignInRoute

    @Serializable
    data object FaceId : SignInRoute

    @Serializable
    data object QR : SignInRoute

    @Serializable
    data object SignUp : SignInRoute

    @Serializable
    data object FaceEnrollment : SignInRoute

    @Serializable
    data object IdPassword : SignInRoute
}

sealed interface InspectionRoute : Route {
    // 전체 카테고리
    @Serializable
    data object CategoryList : InspectionRoute

    // 혈압, 악력
    @Serializable
    data object ExternalDeviceInspectionList : InspectionRoute

    // 사시
    @Serializable
    data object StrabismusInspectionList : InspectionRoute

    // 치매
    @Serializable
    data object DementiaInspection : InspectionRoute

    // 시력검사 루트
    @Serializable
    data object EyeInspectionList : InspectionRoute

    // 검사 결과
    @Serializable
    data object InspectionResult : InspectionRoute
}

sealed interface DeviceConnectRoute : Route {
    @Serializable
    data object InGripConnect : DeviceConnectRoute

    @Serializable
    data object BPBIO320Connect : DeviceConnectRoute

    @Serializable
    data object BP170BConnect : DeviceConnectRoute
}

sealed interface AdminRoute : Route {
    @Serializable
    data object AdminPage : AdminRoute

    @Serializable
    data object AccountManagement : AdminRoute

    @Serializable
    data object FaceUpdate : AdminRoute
}

sealed interface TermsOfServiceRoute : Route {

    @Serializable
    data object FaceUpdate : TermsOfServiceRoute

    @Serializable
    data object SignUp : TermsOfServiceRoute

    @Serializable
    data object FaceId : TermsOfServiceRoute

    @Serializable
    data object Base : TermsOfServiceRoute
}
