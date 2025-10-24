package com.pixelro.nenoonkiosk.feature.auth

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.pixelro.nenoonkiosk.core.constants.NavConstants
import com.pixelro.nenoonkiosk.feature.auth.login.LoginViewModel
import com.pixelro.nenoonkiosk.feature.auth.login.LoginScreen

enum class SignInScreenState {
    LocationSignIn,
    UserSignIn,
    FaceId,
    SignUpTermsOfService,
    FaceIdTermsOfService,
    QR,
    SignUp,
    FaceEnrollment,
    IdPassword,
}

@Composable
fun SignInScreen(
    updateLocationSignIn: (Boolean) -> Unit,
    loginViewModel: LoginViewModel,
    navController: NavController,
) {
    val signInNavController = rememberNavController()
    val isLocationSignedIn by loginViewModel.isLocationSignedIn.collectAsState()
    var isFaceIdTermsOfServiceAccepted by remember { mutableStateOf(false) }

    NavHost(
        navController = signInNavController,
        startDestination = SignInScreenState.LocationSignIn.name,
    ) {
        composable(
            SignInScreenState.LocationSignIn.name,
        ) {
            LaunchedEffect(Unit) {
                isFaceIdTermsOfServiceAccepted = false
                if (isLocationSignedIn) {
                    signInNavController.navigate(SignInScreenState.UserSignIn.name)
                }
            }
            LocationSignInScreen(
                updateIsSignedIn = updateLocationSignIn,
                loginViewModel = loginViewModel,
                signInNavController = signInNavController,
                navController = navController,
            )
            BackHandler(true) {}
        }
        composable(
            SignInScreenState.UserSignIn.name,
        ) {
            LaunchedEffect(Unit) {
                isFaceIdTermsOfServiceAccepted = false
                loginViewModel.resetUserData()
            }
            LoginScreen(
                loginViewModel = loginViewModel,
                signInNavController = signInNavController,
                navController = navController,
            )
            BackHandler(true) {}
        }
        composable(
            SignInScreenState.SignUp.name,
        ) {
            UserSignUpScreen(
                updateIsSignedIn = { navController.navigate(NavConstants.ROUTE_INTRO) },
                loginViewModel = loginViewModel,
                toFaceEnrollmentScreen = {
                    if (isFaceIdTermsOfServiceAccepted) {
                        signInNavController.navigate(SignInScreenState.FaceEnrollment.name)
                    } else {
                        signInNavController.navigate(SignInScreenState.FaceIdTermsOfService.name)
                    }
                },
                navController = signInNavController,
            )
        }
        composable(
            SignInScreenState.SignUpTermsOfService.name,
        ) {
            signUpTermsOfServiceScreen(
                onTermsAccepted = {
                    signInNavController.popBackStack(SignInScreenState.UserSignIn.name, false)
                    signInNavController.navigate(SignInScreenState.SignUp.name)
                },
                onTermsRejected = { signInNavController.popBackStack(SignInScreenState.UserSignIn.name, false) },
            )
        }
        composable(
            SignInScreenState.FaceIdTermsOfService.name,
        ) {
            FaceIdTermsOfServiceScreen(
                onTermsAccepted = {
                    signInNavController.popBackStack(SignInScreenState.SignUp.name, false)
                    signInNavController.navigate(SignInScreenState.FaceEnrollment.name)
                    isFaceIdTermsOfServiceAccepted = true
                },
                onTermsRejected = { signInNavController.popBackStack(SignInScreenState.UserSignIn.name, false) },
            )
        }
        composable(
            SignInScreenState.QR.name,
        ) {
            QRSignInScreen(
                updateIsSignedIn = { navController.navigate(NavConstants.ROUTE_INTRO) },
                loginViewModel = loginViewModel,
                navController = signInNavController,
            )
        }
        composable(
            SignInScreenState.FaceId.name,
        ) {
            FaceIdSignInScreen(
                updateIsSignedIn = { success ->
                    if (success) {
                        navController.navigate(NavConstants.ROUTE_INTRO)
                    } else {
                        signInNavController.popBackStack(SignInScreenState.UserSignIn.name, false)
                    }
                },
                loginViewModel = loginViewModel,
                navController = signInNavController,
            )
        }
        composable(
            SignInScreenState.FaceEnrollment.name,
        ) {
            FaceEnrollmentScreen(
                loginViewModel = loginViewModel,
                navController = signInNavController,
            )
        }
        composable(
            SignInScreenState.IdPassword.name,
        ) {
            IdPasswordSignInScreen(
                updateIsSignedIn = { navController.navigate(NavConstants.ROUTE_INTRO) },
                loginViewModel = loginViewModel,
                navController = signInNavController,
            )
        }
    }
}
