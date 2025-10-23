package com.pixelro.nenoonkiosk.feature.main

import android.annotation.SuppressLint
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.pixelro.nenoonkiosk.core.constants.AppConstants
import com.pixelro.nenoonkiosk.core.constants.NavConstants
import com.pixelro.nenoonkiosk.core.util.AnimationProvider
import com.pixelro.nenoonkiosk.core.util.dataprovider.TestType
import com.pixelro.nenoonkiosk.feature.auth.AccountManagementScreen
import com.pixelro.nenoonkiosk.feature.auth.FaceIdTermsOfServiceScreen
import com.pixelro.nenoonkiosk.feature.auth.FaceUpdateScreen
import com.pixelro.nenoonkiosk.feature.auth.SignInScreen
import com.pixelro.nenoonkiosk.feature.auth.SignInViewModel
import com.pixelro.nenoonkiosk.feature.categorylist.CategoryListScreen
import com.pixelro.nenoonkiosk.feature.exerciseglasses.concentration_exercise.ConcentrationExerciseContent
import com.pixelro.nenoonkiosk.feature.exerciseglasses.presbyopia_exercise.PresbyopiaExerciseContent
import com.pixelro.nenoonkiosk.feature.inspection.ExternalDeviceTestListScreen
import com.pixelro.nenoonkiosk.feature.inspection.EyeTestListScreen
import com.pixelro.nenoonkiosk.feature.inspection.PhoriaAndAniseikoniaTestListScreen
import com.pixelro.nenoonkiosk.feature.inspection.TestResultScreen
import com.pixelro.nenoonkiosk.feature.inspection.TestScreen
import com.pixelro.nenoonkiosk.feature.inspection.bloodPressure.BloodPressureTestContent
import com.pixelro.nenoonkiosk.feature.inspection.dementia.DementiaTestContent
import com.pixelro.nenoonkiosk.feature.inspection.gripStrength.GripStrengthTestContent
import com.pixelro.nenoonkiosk.feature.inspection.macular.amslergrid.AmslerGridTestContent
import com.pixelro.nenoonkiosk.feature.inspection.macular.mchart.MChartTestContent
import com.pixelro.nenoonkiosk.feature.inspection.presbyopia.PresbyopiaTestContent
import com.pixelro.nenoonkiosk.feature.inspection.visualacuity.children.ChildrenVisualAcuityTestResult
import com.pixelro.nenoonkiosk.feature.inspection.visualacuity.longdistance.LongVisualAcuityTestResult
import com.pixelro.nenoonkiosk.feature.inspection.visualacuity.shortdistance.ShortDistanceVisualAcuityTestContent
import com.pixelro.nenoonkiosk.feature.inspection.visualacuity.shortdistance.ShortVisualAcuityTestResult
import com.pixelro.nenoonkiosk.feature.intro.IntroScreen
import com.pixelro.nenoonkiosk.feature.intro.PermissionRequestScreen
import com.pixelro.nenoonkiosk.feature.intro.termsOfServiceScreen
import com.pixelro.nenoonkiosk.feature.iotdevice.BP170B.BP170BConnectionScreen
import com.pixelro.nenoonkiosk.feature.iotdevice.BPBIO320.BPBIO320ManagementScreen
import com.pixelro.nenoonkiosk.feature.iotdevice.BPBIO320.BPBIO320ViewModel
import com.pixelro.nenoonkiosk.feature.iotdevice.BTDeviceManagementScreen
import com.pixelro.nenoonkiosk.feature.iotdevice.inGrip.InGripManagmentScreen
import com.pixelro.nenoonkiosk.feature.print.ResultPrintScreen
import com.pixelro.nenoonkiosk.feature.screensaver.ScreenSaverScreen
import com.pixelro.nenoonkiosk.feature.setting.SettingsScreen
import com.pixelro.nenoonkiosk.feature.splash.SplashScreen
import com.pixelro.nenoonkiosk.feature.strabismustest.AppNavigation
import com.pixelro.nenoonkiosk.feature.survey.SurveyScreen
import com.pixelro.nenoonkiosk.feature.testcontent.ChildrenVisualAcuityTestContent
import com.pixelro.nenoonkiosk.feature.testcontent.LongDistanceVisualAcuityTestContent
import com.pixelro.nenoonkiosk.feature.undeveloped.AdminPageScreen
import com.pixelro.nenoonkiosk.feature.undeveloped.ContactScreen
import com.pixelro.nenoonkiosk.feature.undeveloped.EntriesScreen
import com.pixelro.nenoonkiosk.feature.undeveloped.ExerciseListScreen
import com.pixelro.nenoonkiosk.feature.undeveloped.SoftwareInfoScreen
import com.pixelro.nenoonkiosk.feature.undeveloped.VideoTelephonyScreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


// Route가 되어야함.
@SuppressLint("RestrictedApi")
@RequiresApi(Build.VERSION_CODES.S)
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun nenoonApp(
    viewModel: NenoonViewModel = hiltViewModel(),
    bloodPressureMonitorViewModel: BPBIO320ViewModel = hiltViewModel(),
    signInViewModel: SignInViewModel = hiltViewModel(),
    navController: NavHostController = rememberNavController(),
) {
    val selectedTest = viewModel.selectedTestType.collectAsState().value
    val isScreenSaving = viewModel.isScreenSaving.collectAsState().value

    // 광고 화면 전환 관련 코드
    LaunchedEffect(isScreenSaving) {
        bloodPressureMonitorViewModel.initializeBluetoothSDK()
        if (isScreenSaving) {
            if (navController.currentBackStackEntry?.destination?.route != NavConstants.ROUTE_SIGN_IN) {
                navController.popBackStack(
                    if (viewModel.isSignedIn.value) {
                        NavConstants.ROUTE_CATEGORY_LIST
                    } else {
                        NavConstants.ROUTE_INTRO
                    },
                    false,
                )
                navController.navigate(NavConstants.ROUTE_SCREEN_SAVER)
            }
        } else {
            navController.popBackStack(NavConstants.ROUTE_SCREEN_SAVER, true)
        }
    }

    AnimatedNavHost(
        modifier =
            Modifier
                .fillMaxSize(),
        navController = navController,
        startDestination = NavConstants.ROUTE_SPLASH,
        contentAlignment = Alignment.TopCenter,
    ) {
        /*
         * 스플래시 화면
         */
        composable(
            route = NavConstants.ROUTE_SPLASH,
            enterTransition = { AnimationProvider.enterTransition },
            exitTransition = { AnimationProvider.exitTransition },
            popEnterTransition = { AnimationProvider.popEnterTransition },
            popExitTransition = { AnimationProvider.popExitTransition },
        ) {
            BackHandler(true) {}
            LaunchedEffect(true) {
                delay(3000) // 스플래시 화면을 보여주기 위한 짧은 딜레이
                viewModel.checkPermissions() // 권한 상태 업데이트

                val allGranted =
                    viewModel.isWriteSettingsPermissionGranted.value &&
                            viewModel.isCameraPermissionGranted.value &&
                            viewModel.isBluetoothPermissionsGranted.value &&
                            viewModel.isBlueToothOn.value

                if (allGranted) {
                    // 모든 권한이 있으면 로그인 화면으로 바로 이동
                    navController.popBackStack()
                    navController.navigate(NavConstants.ROUTE_SIGN_IN)
                } else {
                    // 권한이 하나라도 없으면 권한 요청 화면으로 이동
                    navController.popBackStack()
                    navController.navigate(NavConstants.ROUTE_PERMISSION)
                }
            }
            SplashScreen()
        }

        /*
         * 기재사항 화면
         */
        composable(
            route = NavConstants.ROUTE_ENTRIES,
            enterTransition = { AnimationProvider.enterTransition },
            exitTransition = { AnimationProvider.exitTransition },
            popEnterTransition = { AnimationProvider.popEnterTransition },
            popExitTransition = { AnimationProvider.popExitTransition },
        ) {
            LaunchedEffect(true) {
                delay(2000)
                navController.popBackStack()
                navController.navigate(NavConstants.ROUTE_SIGN_IN)
            }
            EntriesScreen()
        }

        /**
         * 로그인 화면
         */
        composable(
            route = NavConstants.ROUTE_SIGN_IN,
            enterTransition = { AnimationProvider.enterTransition },
            exitTransition = { AnimationProvider.exitTransition },
            popEnterTransition = { AnimationProvider.popEnterTransition },
            popExitTransition = { AnimationProvider.popExitTransition },
        ) {
            SignInScreen(
                updateLocationSignIn = {
                    viewModel.updateIsSignedIn(it)
                },
                navController = navController,
                signInViewModel = signInViewModel,
            )
        }

        /**
         * 계정 관리 화면
         */
        composable(
            route = NavConstants.ROUTE_ACCOUNT_MANAGEMENT,
            enterTransition = { AnimationProvider.enterTransition },
            exitTransition = { AnimationProvider.exitTransition },
            popEnterTransition = { AnimationProvider.popEnterTransition },
            popExitTransition = { AnimationProvider.popExitTransition },
        ) {
            AccountManagementScreen(
                navController = navController,
                viewModel = signInViewModel,
            )
        }

        /**
         * 얼굴 (재)등록 동의서 화면
         */
        composable(
            route = NavConstants.ROUTE_FACE_UPDATE_TERMS_OF_SERVICE,
            enterTransition = { AnimationProvider.enterTransition },
            exitTransition = { AnimationProvider.exitTransition },
            popEnterTransition = { AnimationProvider.popEnterTransition },
            popExitTransition = { AnimationProvider.popExitTransition },
        ) {
            FaceIdTermsOfServiceScreen(
                onTermsAccepted = {
                    navController.popBackStack(NavConstants.ROUTE_ACCOUNT_MANAGEMENT, false)
                    navController.navigate(NavConstants.ROUTE_FACE_UPDATE)
                },
                onTermsRejected = {
                    navController.popBackStack(
                        NavConstants.ROUTE_ACCOUNT_MANAGEMENT,
                        false
                    )
                },
            )
        }

        /**
         * 얼굴 (재)등록 화면
         */
        composable(
            route = NavConstants.ROUTE_FACE_UPDATE,
            enterTransition = { AnimationProvider.enterTransition },
            exitTransition = { AnimationProvider.exitTransition },
            popEnterTransition = { AnimationProvider.popEnterTransition },
            popExitTransition = { AnimationProvider.popExitTransition },
        ) {
            FaceUpdateScreen(
                navController = navController,
                signInViewModel = signInViewModel,
            )
        }

        /*
         * 개인정보 동의서 화면 (비회원용)
         */
        composable(
            route = NavConstants.ROUTE_TERMS_OF_SERVICE,
            enterTransition = { AnimationProvider.enterTransition },
            exitTransition = { AnimationProvider.exitTransition },
            popEnterTransition = { AnimationProvider.popEnterTransition },
            popExitTransition = { AnimationProvider.popExitTransition },
        ) {
            termsOfServiceScreen(
                onTermsAccepted = {
                    navController.popBackStack(NavConstants.ROUTE_SIGN_IN, false)
                    navController.navigate(NavConstants.ROUTE_INTRO)
                },
                onTermsRejected = {
                    navController.popBackStack(NavConstants.ROUTE_SIGN_IN, false)
                },
            )
            BackHandler(true) {}
        }

        /*
         * 화면 보호기 화면
         */
        composable(
            route = NavConstants.ROUTE_SCREEN_SAVER,
            enterTransition = { AnimationProvider.enterTransition },
            exitTransition = { AnimationProvider.exitTransition },
            popEnterTransition = { AnimationProvider.popEnterTransition },
            popExitTransition = { AnimationProvider.popExitTransition },
        ) {
            ScreenSaverScreen(
                exoPlayer = viewModel.exoPlayer,
                isSignedIn = viewModel.isSignedIn.collectAsState().value,
                initializeTestDoneStatus = {
                    viewModel.initializeTestDoneStatus()
                },
            )
        }

        /*
         * 권한 확인 화면
         */
        composable(
            route = NavConstants.ROUTE_PERMISSION,
            enterTransition = { AnimationProvider.enterTransition },
            exitTransition = { AnimationProvider.exitTransition },
            popEnterTransition = { AnimationProvider.popEnterTransition },
            popExitTransition = { AnimationProvider.popExitTransition },
        ) {
            PermissionRequestScreen(
                viewModel,
                toLoginScreen = { // 권한 다 허용 되면 로그인 화면으로
                    navController.popBackStack(NavConstants.ROUTE_SPLASH, false)
                    navController.navigate(NavConstants.ROUTE_SIGN_IN)
                },
            )
            BackHandler(true) {}
        }

        /*
         * 첫 시작 화면
         */
        composable(
            route = NavConstants.ROUTE_INTRO,
            enterTransition = { AnimationProvider.enterTransition },
            exitTransition = { AnimationProvider.exitTransition },
            popEnterTransition = { AnimationProvider.popEnterTransition },
            popExitTransition = { AnimationProvider.popExitTransition },
        ) {
            IntroScreen(
                toSurveyScreen = {
                    navController.navigate(NavConstants.ROUTE_SURVEY)
                },
                toSettingsScreen = {
                    navController.navigate(NavConstants.ROUTE_SETTINGS)
                },
            )
            BackHandler(true) {}
        }

        /**
         * 문진표 작성 화면
         */
        composable(
            route = NavConstants.ROUTE_SURVEY,
            enterTransition = { AnimationProvider.enterTransition },
            exitTransition = { AnimationProvider.exitTransition },
            popEnterTransition = { AnimationProvider.popEnterTransition },
            popExitTransition = { AnimationProvider.popExitTransition },
        ) {
            SurveyScreen(
                isLoggedIn = viewModel.isSignedIn.collectAsState().value,
                toCategoryListScreen = {
                    navController.navigate(NavConstants.ROUTE_CATEGORY_LIST)
                    viewModel.initializeTestDoneStatus()
                    viewModel.updateSurveyData(it)
                },
                signInViewModel = signInViewModel,
                userData = signInViewModel.userData.collectAsState().value,
                onBack = { navController.popBackStack(NavConstants.ROUTE_INTRO, false) },
                signOut = {
                    navController.popBackStack(NavConstants.ROUTE_SIGN_IN, false)
                    signInViewModel.userSignOut()
                },
            )
        }

        /**
         * 전체 검사 목록 화면
         */
        composable(
            route = NavConstants.ROUTE_CATEGORY_LIST,
            enterTransition = { AnimationProvider.enterTransition },
            exitTransition = { AnimationProvider.exitTransition },
            popEnterTransition = { AnimationProvider.popEnterTransition },
            popExitTransition = { AnimationProvider.popExitTransition },
        ) {
            CategoryListScreen(
                pid = viewModel.locationId.collectAsState().value,
                isSignInSkipped = {
                    signInViewModel.isUserSignInSkipped()
                },
                toEyeTestScreen = {
                    navController.navigate(NavConstants.ROUTE_TEST_LIST)
                },
                toDementiaTestScreen = {
                    viewModel.updateSelectedTestType(it)
                    navController.navigate(NavConstants.ROUTE_TEST_CONTENT)
                },
                toExternalDeviceTestListScreen = {
                    navController.navigate(NavConstants.ROUTE_EXTERNAL_DEVICE_TEST_LIST)
                },
                toStrabismusTestListScreen = {
                    navController.navigate(NavConstants.ROUTE_STRABISMUS_TEST_LIST)
                },
                toIntroScreen = {
                    navController.popBackStack(NavConstants.ROUTE_INTRO, false)
                },
                toContact = {
                    navController.navigate(NavConstants.ROUTE_CONTACT)
                },
                toPrintScreen = {
                    navController.navigate(NavConstants.ROUTE_RESULT_PRINT)
                },
                toAccountManagementScreen = {
                    navController.navigate(NavConstants.ROUTE_ACCOUNT_MANAGEMENT)
                },
                toPulmonaryTestResultScreen = {
                    viewModel.pulmonaryFunctionTestResult = it
                    viewModel.updateSelectedTestType(TestType.PulmonaryFunction)
                    navController.navigate(NavConstants.ROUTE_TEST_RESULT)
                },
                toSettingsScreen = {
                    navController.navigate(NavConstants.ROUTE_SETTINGS)
                },
            )
            BackHandler(true) {}
        }

        /**
         * 눈 검사 목록 화면
         */
        composable(
            route = NavConstants.ROUTE_TEST_LIST,
            enterTransition = { AnimationProvider.enterTransition },
            exitTransition = { AnimationProvider.exitTransition },
            popEnterTransition = { AnimationProvider.popEnterTransition },
            popExitTransition = { AnimationProvider.popExitTransition },
        ) {
            EyeTestListScreen(
                checkIsTestDone = viewModel::checkIsTestDone,
                toTestScreen = {
                    viewModel.updateSelectedTestType(it)
                    navController.navigate(NavConstants.ROUTE_TEST_CONTENT)
                },
                toIntroScreen = {
                    navController.popBackStack(NavConstants.ROUTE_CATEGORY_LIST, false)
                },
                toSettingsScreen = {
                    navController.navigate(NavConstants.ROUTE_SETTINGS)
                },
                isPresbyopiaDone = viewModel.isPresbyopiaTestDone.collectAsState().value,
                isShortVisualAcuityDone = viewModel.isShortVisualAcuityTestDone.collectAsState().value,
                isAmslerGridDone = viewModel.isAmslerGridTestDone.collectAsState().value,
                isMChartDone = viewModel.isMChartTestDone.collectAsState().value,
                viewModel = viewModel,
            )
        }

        /**
         * 외부 기기 검사 목록 화면 (혈압, 약력)
         */
        composable(
            route = NavConstants.ROUTE_EXTERNAL_DEVICE_TEST_LIST,
            enterTransition = { AnimationProvider.enterTransition },
            exitTransition = { AnimationProvider.exitTransition },
            popEnterTransition = { AnimationProvider.popEnterTransition },
            popExitTransition = { AnimationProvider.popExitTransition },
        ) {
            ExternalDeviceTestListScreen(
                checkIsTestDone = viewModel::checkIsTestDone,
                toTestScreen = {
                    viewModel.updateSelectedTestType(it)
                    navController.navigate(NavConstants.ROUTE_TEST_CONTENT)
                },
                toIntroScreen = {
                    navController.popBackStack(NavConstants.ROUTE_CATEGORY_LIST, false)
                },
                toSettingsScreen = {
                    navController.navigate(NavConstants.ROUTE_SETTINGS)
                },
                isBloodPressureDone = viewModel.isBloodPressureTestDone.collectAsState().value,
                isGripStrengthDone = viewModel.isGripStrengthTestDone.collectAsState().value,
                viewModel = viewModel,
            )
        }

        /**
         * 사위, 부등상시 검사 목록 화면
         */
        composable(
            route = NavConstants.ROUTE_STRABISMUS_TEST_LIST,
            enterTransition = { AnimationProvider.enterTransition },
            exitTransition = { AnimationProvider.exitTransition },
            popEnterTransition = { AnimationProvider.popEnterTransition },
            popExitTransition = { AnimationProvider.popExitTransition },
        ) {
            PhoriaAndAniseikoniaTestListScreen(
                checkIsTestDone = viewModel::checkIsTestDone,
                toTestScreen = {
                    val route =
                        when (it) {
                            TestType.Phoria -> "strabismus_test/sawi_intro"
                            TestType.Aniseikonia -> "strabismus_test/fudo_intro"
                            else -> ""
                        }
                    if (route.isNotEmpty()) {
                        navController.navigate(route)
                    }
                },
                toIntroScreen = {
                    navController.popBackStack(NavConstants.ROUTE_CATEGORY_LIST, false)
                },
                toSettingsScreen = {
                    navController.navigate(NavConstants.ROUTE_SETTINGS)
                },
                isPhoriaDone = viewModel.isPhoriaTestDone.collectAsState().value,
                isAniseikoniaDone = viewModel.isAniseikoniaTestDone.collectAsState().value,
                viewModel = viewModel,
            )
        }

        composable("strabismus_test/{startRoute}") { backStackEntry ->
            val startRoute = backStackEntry.arguments?.getString("startRoute") ?: "sawi_intro"
            AppNavigation(
                startDestination = startRoute,
                parentNavController = navController,
            )
        }

        /**
         * 설정 화면
         */
        composable(
            route = NavConstants.ROUTE_SETTINGS,
            enterTransition = { AnimationProvider.enterTransition },
            exitTransition = { AnimationProvider.exitTransition },
            popEnterTransition = { AnimationProvider.popEnterTransition },
            popExitTransition = { AnimationProvider.popExitTransition },
        ) {
            var isNavigating by remember { mutableStateOf(false) }
            val coroutineScope = rememberCoroutineScope()

            SettingsScreen(
                viewModel = viewModel,
                isSignedIn = viewModel.isSignedIn.collectAsState().value,
                toSignInScreen = {
                    if (!isNavigating) {
                        isNavigating = true
                        navController.popBackStack()
                        navController.popBackStack()
                        navController.navigate(NavConstants.ROUTE_SIGN_IN)
                        coroutineScope.launch {
                            delay(1000)
                            isNavigating = false
                        }
                    }
                },
                toSoftwareInfoScreen = {
                    if (!isNavigating) {
                        isNavigating = true
                        navController.navigate(NavConstants.ROUTE_SOFTWARE_INFO)
                        coroutineScope.launch {
                            delay(1000)
                            isNavigating = false
                        }
                    }
                },
                signInViewModel = signInViewModel,
                onBack = {
                    if (!isNavigating) {
                        isNavigating = true
                        navController.popBackStack()
                        coroutineScope.launch {
                            delay(1000)
                            isNavigating = false
                        }
                    }
                },
            )
        }

        /**
         * 검사 화면
         */
        composable(
            route = NavConstants.ROUTE_TEST_CONTENT,
            enterTransition = { AnimationProvider.enterTransition },
            exitTransition = { AnimationProvider.exitTransition },
            popEnterTransition = { AnimationProvider.popEnterTransition },
            popExitTransition = { AnimationProvider.popExitTransition },
        ) {
            TestScreen(
                viewModel = viewModel,
                navController = navController,
                content = {
                    when (selectedTest) {
                        TestType.Presbyopia -> {
                            PresbyopiaTestContent(
                                toResultScreen = {
                                    navController.navigate(NavConstants.ROUTE_TEST_RESULT)
                                    viewModel.presbyopiaTestResult = it
                                },
                            )
                        }

                        TestType.ShortDistanceVisualAcuity -> {
                            ShortDistanceVisualAcuityTestContent(
                                toResultScreen = {
                                    navController.navigate(NavConstants.ROUTE_TEST_RESULT)
                                    viewModel.shortVisualAcuityTestResult =
                                        ShortVisualAcuityTestResult(
                                            it.leftEye,
                                            it.rightEye,
                                        )
                                },
                            )
                        }

                        TestType.LongDistanceVisualAcuity -> {
                            LongDistanceVisualAcuityTestContent(
                                toResultScreen = {
                                    navController.navigate(NavConstants.ROUTE_TEST_RESULT)
                                    viewModel.longVisualAcuityTestResult =
                                        LongVisualAcuityTestResult(
                                            it.leftEye,
                                            it.rightEye,
                                        )
                                },
                            )
                        }

                        TestType.ChildrenVisualAcuity -> {
                            ChildrenVisualAcuityTestContent(
                                toResultScreen = {
                                    navController.navigate(NavConstants.ROUTE_TEST_RESULT)
                                    viewModel.childrenVisualAcuityTestResult =
                                        ChildrenVisualAcuityTestResult(
                                            it.leftEye,
                                            it.rightEye,
                                        )
                                },
                            )
                        }

                        TestType.AmslerGrid -> {
                            AmslerGridTestContent(
                                toResultScreen = {
                                    navController.navigate(NavConstants.ROUTE_TEST_RESULT)
                                    viewModel.amslerGridTestResult = it
                                },
                            )
                        }

                        TestType.MChart -> {
                            MChartTestContent(
                                toResultScreen = {
                                    navController.navigate(NavConstants.ROUTE_TEST_RESULT)
                                    viewModel.mChartTestResult = it
                                },
                            )
                        }

                        TestType.Dementia -> {
                            DementiaTestContent(
                                toResultScreen = {
                                    navController.navigate(NavConstants.ROUTE_TEST_RESULT)
                                    viewModel.dementiaTestResult = it
                                },
                            )
                        }

                        TestType.Presbyopia_Glasses -> {
                            PresbyopiaExerciseContent(
                                toResultScreen = {
                                    navController.navigate(NavConstants.ROUTE_TEST_RESULT)
                                    viewModel.presbyopiaExerciseResult = it
                                },
                            )
                        }

                        TestType.Concentration_Glasses -> {
                            ConcentrationExerciseContent(
                                toResultScreen = {
                                    navController.navigate(NavConstants.ROUTE_TEST_RESULT)
                                    viewModel.concentrationExerciseResult = it
                                },
                            )
                        }

                        TestType.BloodPressure -> {
                            BloodPressureTestContent(
                                toResultScreen = {
                                    navController.navigate(NavConstants.ROUTE_TEST_RESULT)
                                    viewModel.bloodPressureTestResult = it
                                },
                                navController = navController,
                                isSignedIn = viewModel.isSignedIn.collectAsState().value,
                                bpbiO320ViewModel = bloodPressureMonitorViewModel,
                                signInViewModel = signInViewModel,
                            )
                        }

                        TestType.GripStrength -> {
                            GripStrengthTestContent(
                                toResultScreen = {
                                    navController.navigate(NavConstants.ROUTE_TEST_RESULT)
                                    viewModel.gripStrengthTestResult = it
                                },
                                navController = navController,
                                isSignedIn = viewModel.isSignedIn.collectAsState().value,
                                signInViewModel = signInViewModel,
                            )
                        }

                        else -> {
                            Box {
                            }
                        }
                    }
                },
            )
        }

        /**
         * 검사 결과 화면
         */
        composable(
            route = NavConstants.ROUTE_TEST_RESULT,
            enterTransition = { AnimationProvider.enterTransition },
            exitTransition = { AnimationProvider.exitTransition },
            popEnterTransition = { AnimationProvider.popEnterTransition },
            popExitTransition = { AnimationProvider.popExitTransition },
        ) {
            when (viewModel.selectedTestType.collectAsState().value) {
                TestType.Presbyopia -> viewModel.updateIsPresbyopiaTestDone(true)
                TestType.ShortDistanceVisualAcuity -> viewModel.updateIsShortVisualAcuityTestDone(
                    true
                )

                TestType.AmslerGrid -> viewModel.updateIsAmslerGridTestDone(true)
                TestType.MChart -> viewModel.updateIsMChartTestDone(true)
                TestType.BloodPressure -> viewModel.updateIsBloodPressureTestDone(true)
                TestType.GripStrength -> viewModel.updateIsGripStrengthTestDone(true)
                TestType.PulmonaryFunction -> viewModel.updateIsPulmonaryFunctionTestDone(true)
                else -> {
                }
            }
            val surveyId = viewModel.surveyId.collectAsState().value
            TestResultScreen(
                surveyId = surveyId,
                testType = viewModel.selectedTestType.collectAsState().value,
                testResult =
                    when (
                        viewModel.selectedTestType.collectAsState().value
                    ) {
                        TestType.Presbyopia -> viewModel.presbyopiaTestResult
                        TestType.ShortDistanceVisualAcuity -> viewModel.shortVisualAcuityTestResult
                        TestType.LongDistanceVisualAcuity -> viewModel.longVisualAcuityTestResult
                        TestType.ChildrenVisualAcuity -> viewModel.childrenVisualAcuityTestResult
                        TestType.AmslerGrid -> viewModel.amslerGridTestResult
                        TestType.MChart -> viewModel.mChartTestResult
                        TestType.Dementia -> viewModel.dementiaTestResult
                        TestType.Presbyopia_Glasses -> viewModel.presbyopiaExerciseResult
                        TestType.Concentration_Glasses -> viewModel.concentrationExerciseResult
                        TestType.BloodPressure -> viewModel.bloodPressureTestResult
                        TestType.GripStrength -> viewModel.gripStrengthTestResult
                        TestType.PulmonaryFunction -> viewModel.pulmonaryFunctionTestResult
                        TestType.None -> null
                        TestType.Phoria -> TODO()
                        TestType.Aniseikonia -> TODO()
                    },
                navController = navController,
                onLogout = {
                    viewModel.updateIsSignedIn(false)
                    navController.popBackStack(NavConstants.ROUTE_TERMS_OF_SERVICE, true)
                    navController.navigate(NavConstants.ROUTE_SIGN_IN)
                },
                userData = signInViewModel.userData.collectAsState().value,
            )
        }

        /**
         * 관리자 페이지 화면
         */
        composable(
            route = NavConstants.ROUTE_ADMIN_PAGE,
            enterTransition = { AnimationProvider.enterTransition },
            exitTransition = { AnimationProvider.exitTransition },
            popEnterTransition = { AnimationProvider.popEnterTransition },
            popExitTransition = { AnimationProvider.popExitTransition },
        ) {
            AdminPageScreen(
                url = AppConstants.ADMIN_PAGE_URL,
                onBack = { navController.popBackStack(NavConstants.ROUTE_SIGN_IN, false) },
            )
        }

        /**
         * 결과지 프린트 화면
         */
        composable(
            route = NavConstants.ROUTE_RESULT_PRINT,
            enterTransition = { AnimationProvider.enterTransition },
            exitTransition = { AnimationProvider.exitTransition },
            popEnterTransition = { AnimationProvider.popEnterTransition },
            popExitTransition = { AnimationProvider.popExitTransition },
        ) {
            ResultPrintScreen(
                surveyId = viewModel.surveyId.collectAsState().value,
                qrCode = signInViewModel.accountQrCode.collectAsState().value,
                navController = navController,
                userData = signInViewModel.userData.collectAsState().value,
            )
        }

        /**
         * 블루투스 기기 관리 화면
         */
        composable(
            route = NavConstants.ROUTE_BT_DEVICE_MANAGEMENT,
            enterTransition = { AnimationProvider.enterTransition },
            exitTransition = { AnimationProvider.exitTransition },
            popEnterTransition = { AnimationProvider.popEnterTransition },
            popExitTransition = { AnimationProvider.popExitTransition },
        ) {
            BTDeviceManagementScreen(
                navController = navController,
                bPBIO320ViewModel = bloodPressureMonitorViewModel,
            )
        }

        /**
         * 혈압계 연결 화면 (BPBIO320)
         */
        composable(
            route = NavConstants.ROUTE_BPBIO320_CONNECT,
            enterTransition = { AnimationProvider.enterTransition },
            exitTransition = { AnimationProvider.exitTransition },
            popEnterTransition = { AnimationProvider.popEnterTransition },
            popExitTransition = { AnimationProvider.popExitTransition },
        ) {
            BPBIO320ManagementScreen(
                navController = navController,
                viewModel = bloodPressureMonitorViewModel,
            )
        }

        /**
         * 혈압계 연결 화면 (BP170B)
         */
        composable(
            route = NavConstants.ROUTE_BP170B_CONNECT,
            enterTransition = { AnimationProvider.enterTransition },
            exitTransition = { AnimationProvider.exitTransition },
            popEnterTransition = { AnimationProvider.popEnterTransition },
            popExitTransition = { AnimationProvider.popExitTransition },
        ) {
            BP170BConnectionScreen(
                navController = navController,
            )
        }

        /**
         * 악력계 연결 화면
         */
        composable(
            route = NavConstants.ROUTE_INGRIP_CONNECT,
            enterTransition = { AnimationProvider.enterTransition },
            exitTransition = { AnimationProvider.exitTransition },
            popEnterTransition = { AnimationProvider.popEnterTransition },
            popExitTransition = { AnimationProvider.popExitTransition },
        ) {
            InGripManagmentScreen(
                navController = navController,
            )
        }

        /**
         * 소프트웨어 정보 화면 (미개발 - 개발 예정)
         */
        composable(
            route = NavConstants.ROUTE_SOFTWARE_INFO,
            enterTransition = { AnimationProvider.enterTransition },
            exitTransition = { AnimationProvider.exitTransition },
            popEnterTransition = { AnimationProvider.popEnterTransition },
            popExitTransition = { AnimationProvider.popExitTransition },
        ) {
            SoftwareInfoScreen(
                onBack = { navController.popBackStack() },
            )
        }

        /**
         * --------------------------------------------------------------------------------개발 취소----------------------------------------------------------
         */

        /**
         * 전화부 화면 (미개발)
         */
        composable(
            route = NavConstants.ROUTE_CONTACT,
            enterTransition = { AnimationProvider.enterTransition },
            exitTransition = { AnimationProvider.exitTransition },
            popEnterTransition = { AnimationProvider.popEnterTransition },
            popExitTransition = { AnimationProvider.popExitTransition },
        ) {
            ContactScreen(
                toVideoTelephonyScreen = {
                    navController.navigate(NavConstants.ROUTE_VIDEOTELEPHONY)
                },
                toIntroScreen = {
                    navController.popBackStack(NavConstants.ROUTE_INTRO, false)
                },
            )
        }

        /**
         * 영상 통화 화면 (미개발)
         */
        composable(
            route = NavConstants.ROUTE_VIDEOTELEPHONY,
            enterTransition = { AnimationProvider.enterTransition },
            exitTransition = { AnimationProvider.exitTransition },
            popEnterTransition = { AnimationProvider.popEnterTransition },
            popExitTransition = { AnimationProvider.popExitTransition },
        ) {
            VideoTelephonyScreen(
                toContactScreen = {
                    navController.popBackStack(NavConstants.ROUTE_CONTACT, false)
                },
            )
        }

        /**
         * 안경 운동 목록 화면
         */
        composable(
            route = NavConstants.ROUTE_EXERCISE_LIST,
            enterTransition = { AnimationProvider.enterTransition },
            exitTransition = { AnimationProvider.exitTransition },
            popEnterTransition = { AnimationProvider.popEnterTransition },
            popExitTransition = { AnimationProvider.popExitTransition },
        ) {
            ExerciseListScreen(
                toTestScreen = {
                    viewModel.updateSelectedTestType(it)
                    navController.navigate(NavConstants.ROUTE_TEST_CONTENT)
                },
                toIntroScreen = {
                    navController.popBackStack(NavConstants.ROUTE_INTRO, false)
                },
            )
        }
    }
}
