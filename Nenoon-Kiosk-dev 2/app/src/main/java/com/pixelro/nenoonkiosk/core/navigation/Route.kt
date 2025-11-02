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
    @Serializable
    data object CategoryList : InspectionRoute

    @Serializable
    data object ExternalDeviceInspectionList : InspectionRoute

    @Serializable
    data object StrabismusInspectionList : InspectionRoute

    @Serializable
    data object DementiaInspection : InspectionRoute

    @Serializable
    data object EyeInspectionList : InspectionRoute

    // 개별 시력 검사 Route 추가
    @Serializable
    data object Presbyopia : InspectionRoute

    @Serializable
    data object ShortVisualAcuity : InspectionRoute

    @Serializable
    data object AmslerGrid : InspectionRoute

    @Serializable
    data object MChart : InspectionRoute

    @Serializable
    data object InspectionResult : InspectionRoute

    @Serializable
    data object BloodPressure: InspectionRoute

    @Serializable
    data object GripStrength: InspectionRoute
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
