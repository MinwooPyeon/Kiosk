package com.pixelro.nenoonkiosk.feature.main

import SettingsRoute
import android.os.Build
import androidx.annotation.OptIn
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.navigation3.runtime.NavBackStack
import com.harang.data.model.dto.User
import com.pixelro.nenoonkiosk.core.constants.DebugConstants
import com.pixelro.nenoonkiosk.core.navigation.AdminRoute
import com.pixelro.nenoonkiosk.core.navigation.Route
import com.pixelro.nenoonkiosk.core.navigation.SignInRoute
import com.pixelro.nenoonkiosk.core.navigation.TermsOfServiceRoute
import com.pixelro.nenoonkiosk.core.navigation.InspectionRoute
import com.pixelro.nenoonkiosk.feature.auth.accountmanagement.AccountManagementRoute
import com.pixelro.nenoonkiosk.feature.auth.faceenrollment.FaceEnrollmentRoute
import com.pixelro.nenoonkiosk.feature.auth.faceidlogin.FaceIdLoginRoute
import com.pixelro.nenoonkiosk.feature.auth.idpasswordlogin.IdPasswordLoginRoute
import com.pixelro.nenoonkiosk.feature.auth.locationlogin.LocationLoginRoute
import com.pixelro.nenoonkiosk.feature.auth.login.LoginRoute
import com.pixelro.nenoonkiosk.feature.auth.qrlogin.QrLoginRoute
import com.pixelro.nenoonkiosk.feature.auth.signup.SignUpRoute
import com.pixelro.nenoonkiosk.feature.categorylist.CategoryListRoute
import com.pixelro.nenoonkiosk.feature.intro.IntroRoute
import com.pixelro.nenoonkiosk.feature.permission.PermissionRoute
import com.pixelro.nenoonkiosk.feature.screensaver.ScreenSaverRoute
import com.pixelro.nenoonkiosk.feature.splash.SplashRoute
import com.pixelro.nenoonkiosk.feature.survey.SurveyRoute
import com.pixelro.nenoonkiosk.feature.termsofservice.base.TermsOfServiceRoute
import com.pixelro.nenoonkiosk.feature.termsofservice.faceid.FaceIdTermsOfServiceRoute
import com.pixelro.nenoonkiosk.feature.termsofservice.signup.SignUpTermsOfServiceRoute

@RequiresApi(Build.VERSION_CODES.S)
@OptIn(UnstableApi::class)
@Composable
fun NenoonRouteHost(
    navBackStack: NavBackStack,
    exoPlayer: ExoPlayer,
) {
    val currentRoute = navBackStack.lastOrNull()

    var isSignedIn by remember { mutableStateOf(false) }
    var userId by remember { mutableStateOf<String?>(null) }
    var userData by remember { mutableStateOf<User?>(null) }

    when (currentRoute) {
        is Route.Splash -> SplashRoute()
        is Route.ScreenSaver -> ScreenSaverRoute(
            exoPlayer = exoPlayer,
            isSignedIn = isSignedIn
        )

        is Route.Permission -> PermissionRoute()
        is Route.Intro -> IntroRoute()
        is Route.Survey -> SurveyRoute(
            userData = userData
        )

        is Route.Settings -> SettingsRoute()

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

        is TermsOfServiceRoute.SignUp -> SignUpTermsOfServiceRoute()
        is TermsOfServiceRoute.FaceId -> FaceIdTermsOfServiceRoute()
        is TermsOfServiceRoute.Base -> TermsOfServiceRoute()

        is InspectionRoute.CategoryList -> CategoryListRoute(
            pid = DebugConstants.PLACEHOLDER_PID,
            isSignInSkipped = isSignedIn,
        )

        is AdminRoute.AccountManagement -> AccountManagementRoute(
            userId = userId,
            userData = userData,
            isUserSignedIn = isSignedIn,
            onSignOut = {
                isSignedIn = false
                userId = null
                userData = null
            }
        )

        else -> SplashRoute()
    }
}