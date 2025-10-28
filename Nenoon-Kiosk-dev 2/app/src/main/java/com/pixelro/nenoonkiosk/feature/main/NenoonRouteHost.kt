package com.pixelro.nenoonkiosk.feature.main

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation3.runtime.NavBackStack
import com.harang.data.model.dto.User
import com.pixelro.nenoonkiosk.core.navigation.AdminRoute
import com.pixelro.nenoonkiosk.core.navigation.DeviceConnectRoute
import com.pixelro.nenoonkiosk.core.navigation.Route
import com.pixelro.nenoonkiosk.core.navigation.SignInRoute
import com.pixelro.nenoonkiosk.core.navigation.TestRoute
import com.pixelro.nenoonkiosk.feature.auth.accountmanagement.AccountManagementRoute
import com.pixelro.nenoonkiosk.feature.auth.faceenrollment.FaceEnrollmentRoute
import com.pixelro.nenoonkiosk.feature.auth.faceidlogin.FaceIdLoginRoute
import com.pixelro.nenoonkiosk.feature.auth.faceupdate.FaceUpdateRoute
import com.pixelro.nenoonkiosk.feature.auth.idpasswordlogin.IdPasswordLoginRoute
import com.pixelro.nenoonkiosk.feature.auth.locationlogin.LocationLoginRoute
import com.pixelro.nenoonkiosk.feature.auth.login.LoginRoute
import com.pixelro.nenoonkiosk.feature.auth.qrlogin.QrLoginRoute
import com.pixelro.nenoonkiosk.feature.auth.signup.SignUpRoute
import com.pixelro.nenoonkiosk.feature.print.ResultPrintRoute
import com.pixelro.nenoonkiosk.feature.screensaver.ScreenSaverRoute
import com.pixelro.nenoonkiosk.feature.splash.SplashRoute
import com.pixelro.nenoonkiosk.feature.termsofservice.base.TermsOfServiceRoute
import com.pixelro.nenoonkiosk.feature.termsofservice.faceid.FaceIdTermsOfServiceRoute
import com.pixelro.nenoonkiosk.feature.termsofservice.signup.SignUpTermsOfServiceRoute

@Composable
fun NenoonRouteHost(
    navBackStack: NavBackStack
) {
    val currentRoute = navBackStack.lastOrNull()

    var isSignedIn by remember { mutableStateOf(false) }
    var userId by remember { mutableStateOf<String?>(null) }
    var userData by remember { mutableStateOf<User?>(null) }

    when (currentRoute) {
        // Base Routes
        is Route.Splash -> SplashRoute()
        is Route.ScreenSaver -> ScreenSaverRoute()
//        is Route.Permission -> PermissionRoute()
//        is Route.Entries -> EntriesRoute()
//        is Route.SignIn -> SignInRoute()
//        is Route.Intro -> IntroRoute()
//        is Route.Survey -> SurveyRoute()
//        is Route.SoftwareInfo -> SoftwareInfoRoute()
//        is Route.TermsOfService -> TermsOfServiceRoute()
//        is Route.ResultPrint -> ResultPrintRoute()
//        is Route.Settings -> SettingsRoute()
//        is Route.Contact -> ContactRoute()
//        is Route.Videotelephony -> VideotelephonyRoute()
//        is Route.BTDeviceManagement -> BTDeviceManagementRoute()

        // SignIn Routes
        is SignInRoute.LocationSignIn -> LocationLoginRoute(
            updateIsSignedIn = { isSignedIn = it }
        )
        is SignInRoute.UserSignIn -> LoginRoute(
            updateIsSignedIn = { isSignedIn = it }
        )
        is SignInRoute.FaceId -> FaceIdLoginRoute(
            updateIsSignedIn = { isSignedIn = it }
        )
        is SignInRoute.QR -> QrLoginRoute(
            updateIsSignedIn = { isSignedIn = it }
        )
        is SignInRoute.SignUp -> SignUpRoute()
        is SignInRoute.FaceEnrollment -> FaceEnrollmentRoute(
            userId = userId ?: "",
            accessToken = userData?.accessToken
        )
        is SignInRoute.IdPassword -> IdPasswordLoginRoute(
            updateIsSignedIn = { isSignedIn = it }
        )
//        is SignInRoute.SignUpTermsOfService -> SignUpTermsOfServiceRoute()
//        is SignInRoute.FaceIdTermsOfService -> FaceIdTermsOfServiceRoute()

        // Test Routes
//        is TestRoute.TestList -> TestListRoute()
//        is TestRoute.ExerciseList -> ExerciseListRoute()
//        is TestRoute.CategoryList -> CategoryListRoute()
//        is TestRoute.ExternalDeviceTestList -> ExternalDeviceTestListRoute()
//        is TestRoute.StrabismusTestList -> StrabismusTestListRoute()
//        is TestRoute.TestContent -> TestContentRoute()
//        is TestRoute.TestResult -> TestResultRoute()
//
//        // Device Connect Routes
//        is DeviceConnectRoute.InGripConnect -> InGripConnectRoute()
//        is DeviceConnectRoute.BPBIO320Connect -> BPBIO320ConnectRoute()
//        is DeviceConnectRoute.BP170BConnect -> BP170BConnectRoute()
//
//        // Admin Routes
//        is AdminRoute.AdminPage -> AdminPageRoute()
//        is AdminRoute.AccountManagement -> AccountManagementRoute(
//            userId = userId,
//            userData = userData,
//            isUserSignedIn = isSignedIn,
//            onSignOut = {
//                isSignedIn = false
//                userId = null
//                userData = null
//            }
//        )
//        is AdminRoute.FaceUpdate -> FaceUpdateRoute(
//            loggedInUserId = userId,
//            accessToken = userData?.accessToken
//        )
//        is AdminRoute.FaceUpdateTermsOfService -> FaceUpdateTermsOfServiceRoute()

        else -> SplashRoute()
    }
}
