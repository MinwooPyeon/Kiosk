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
    data object Entries : Route

    @Serializable
    data object SignIn : Route

    @Serializable
    data object Intro : Route

    @Serializable
    data object Survey : Route

    @Serializable
    data object SoftwareInfo : Route

    @Serializable
    data object TermsOfService : Route

    @Serializable
    data object ResultPrint : Route

    @Serializable
    data object Settings : Route

    @Serializable
    data object Contact : Route

    @Serializable
    data object Videotelephony : Route

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

    @Serializable
    data object SignUpTermsOfService : SignInRoute

    @Serializable
    data object FaceIdTermsOfService : SignInRoute
}

sealed interface TestRoute : Route {
    @Serializable
    data object TestList : TestRoute

    @Serializable
    data object ExerciseList : TestRoute

    @Serializable
    data object CategoryList : TestRoute

    @Serializable
    data object ExternalDeviceTestList : TestRoute

    @Serializable
    data object StrabismusTestList : TestRoute

    @Serializable
    data object TestContent : TestRoute

    @Serializable
    data object TestResult : TestRoute
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

    @Serializable
    data object FaceUpdateTermsOfService : AdminRoute
}
