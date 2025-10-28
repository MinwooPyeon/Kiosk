package com.pixelro.nenoonkiosk.feature.main

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.rememberNavBackStack
import com.pixelro.nenoonkiosk.core.navigation.AdminRoute
import com.pixelro.nenoonkiosk.core.navigation.DeviceConnectRoute
import com.pixelro.nenoonkiosk.core.navigation.LaunchedNavigator
import com.pixelro.nenoonkiosk.core.navigation.Route
import com.pixelro.nenoonkiosk.core.navigation.SignInRoute
import com.pixelro.nenoonkiosk.core.navigation.TestRoute
import com.pixelro.nenoonkiosk.feature.splash.SplashRoute
import com.pixelro.nenoonkiosk.feature.auth.locationlogin.LocationLoginScreen
import com.pixelro.nenoonkiosk.feature.auth.login.LoginScreen
import com.pixelro.nenoonkiosk.feature.auth.faceidlogin.FaceIdLoginScreen
import com.pixelro.nenoonkiosk.feature.auth.qrlogin.QrLoginScreen
import com.pixelro.nenoonkiosk.feature.auth.signup.SignUpScreen
import com.pixelro.nenoonkiosk.feature.auth.faceenrollment.FaceEnrollmentScreen
import com.pixelro.nenoonkiosk.feature.auth.idpasswordlogin.IdPasswordLoginScreen
import com.pixelro.nenoonkiosk.feature.screensaver.ScreenSaverRoute
import com.pixelro.nenoonkiosk.feature.termsofservice.base.TermsOfServiceRoute
import com.pixelro.nenoonkiosk.feature.termsofservice.signup.SignUpTermsOfServiceRoute
import com.pixelro.nenoonkiosk.feature.termsofservice.faceid.FaceIdTermsOfServiceRoute

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun NenoonRouteHost(navBackStack: NavBackStack) {
    val currentRoute = remember(navBackStack.entries) {
        navBackStack.entries.lastOrNull()?.key ?: Route.Splash
    }

    when (currentRoute) {
        is Route.Splash -> SplashRoute()
        is Route.Permission -> PermissionRoute()

        // SignIn 전체 네비게이션 통합
        is Route.SignIn -> {
            val signInNavBackStack = rememberNavBackStack(SignInRoute.LocationSignIn)
            LaunchedNavigator(navBackStack = signInNavBackStack)

            val signInRoute = remember(signInNavBackStack.entries) {
                signInNavBackStack.entries.lastOrNull()?.key ?: SignInRoute.LocationSignIn
            }

            when (signInRoute) {
                is SignInRoute.LocationSignIn -> LocationLoginScreen()
                is SignInRoute.UserSignIn -> LoginScreen()
                is SignInRoute.FaceId -> FaceIdLoginScreen()
                is SignInRoute.QR -> QrLoginScreen()
                is SignInRoute.SignUp -> SignUpScreen()
                is SignInRoute.FaceEnrollment -> FaceEnrollmentScreen()
                is SignInRoute.IdPassword -> IdPasswordLoginScreen()
                is SignInRoute.SignUpTermsOfService -> SignUpTermsOfServiceRoute()
                is SignInRoute.FaceIdTermsOfService -> FaceIdTermsOfServiceRoute()
                else -> LocationLoginScreen()
            }
        }

        is Route.Intro -> IntroRoute()
        is Route.Survey -> SurveyRoute()
        is Route.ScreenSaver -> ScreenSaverRoute()
        is Route.Settings -> SettingsRoute()
        is Route.TermsOfService -> TermsOfServiceRoute()
        is Route.ResultPrint -> ResultPrintRoute()
        is Route.SoftwareInfo -> SoftwareInfoRoute()
        is Route.Contact -> ContactRoute()
        is Route.Videotelephony -> VideoTelephonyRoute()
        is Route.BTDeviceManagement -> BTDeviceManagementRoute()

        is TestRoute.CategoryList -> CategoryListRoute()
        is TestRoute.TestList -> EyeTestListRoute()
        is TestRoute.ExternalDeviceTestList -> ExternalDeviceTestListRoute()
        is TestRoute.StrabismusTestList -> StrabismusTestListRoute()
        is TestRoute.TestContent -> TestContentRoute()
        is TestRoute.TestResult -> TestResultRoute()
        is TestRoute.ExerciseList -> ExerciseListRoute()

        is DeviceConnectRoute.InGripConnect -> InGripConnectRoute()
        is DeviceConnectRoute.BPBIO320Connect -> BPBIO320ConnectRoute()
        is DeviceConnectRoute.BP170BConnect -> BP170BConnectRoute()

        is AdminRoute.AdminPage -> AdminPageRoute()
        is AdminRoute.AccountManagement -> AccountManagementRoute()
        is AdminRoute.FaceUpdate -> FaceUpdateRoute()
        is AdminRoute.FaceUpdateTermsOfService -> FaceUpdateTermsOfServiceRoute()

        else -> SplashRoute()
    }
}
