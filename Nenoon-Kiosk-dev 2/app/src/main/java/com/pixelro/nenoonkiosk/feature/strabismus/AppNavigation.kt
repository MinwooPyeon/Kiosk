package com.pixelro.nenoonkiosk.feature.strabismus

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.pixelro.nenoonkiosk.core.constants.NavConstants
import com.pixelro.nenoonkiosk.feature.inspection.strabismus.GlassInstructionScreen
import com.pixelro.nenoonkiosk.feature.inspection.strabismus.aniseikonia.adjustment.AniseikoniaAdjustmentScreen
import com.pixelro.nenoonkiosk.feature.inspection.strabismus.aniseikonia.intro.AniseikoniaIntroScreen
import com.pixelro.nenoonkiosk.feature.inspection.strabismus.aniseikonia.question.AniseikoniaQuestionScreen
import com.pixelro.nenoonkiosk.feature.inspection.strabismus.phoria.intro.PhoriaIntroScreen
import com.pixelro.nenoonkiosk.feature.inspection.strabismus.phoria.question.PhoriaQuestionScreen
import com.pixelro.nenoonkiosk.feature.strabismus.aniseikonia.result.AniseikoniaResultScreen
import com.pixelro.nenoonkiosk.feature.strabismus.phoria.adjustment.PhoriaAdjustmentScreen
import com.pixelro.nenoonkiosk.feature.strabismus.phoria.result.PhoriaResultScreen

@Composable
fun AppNavigation(
    startDestination: String,
    parentNavController: NavHostController,
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = startDestination,
    ) {
        composable("sawi_intro") {
            PhoriaIntroScreen(
                onStartClicked = { navController.navigate("glass_instruction/sawi") },
                onHowToClicked = { /* TODO */ },
                onBackClicked = { parentNavController.popBackStack() },
            )
        }
        composable("fudo_intro") {
            AniseikoniaIntroScreen(
                onStartClicked = { navController.navigate("glass_instruction/fudo") },
                onHowToClicked = { /* TODO */ },
                onBackClicked = { parentNavController.popBackStack() },
            )
        }
        composable("glass_instruction/{testType}") { backStackEntry ->
            val testType = backStackEntry.arguments?.getString("testType") ?: ""
            GlassInstructionScreen(
                testType = testType,
                onYesClicked = { navController.navigate("filter_instruction/$testType/true") },
                onNoClicked = { navController.navigate("filter_instruction/$testType/false") },
                onBackClicked = { navController.popBackStack() },
            )
        }
        composable("filter_instruction/{testType}/{isWearingGlasses}") { backStackEntry ->
            val testType = backStackEntry.arguments?.getString("testType") ?: ""
            val isWearingGlasses = backStackEntry.arguments?.getString("isWearingGlasses")?.toBoolean() ?: false
            FilterInstructionScreen(
                testType = testType,
                isWearingGlasses = isWearingGlasses,
                onNextClicked = {
                    if (testType == "sawi") {
                        navController.navigate("sawi_question")
                    } else {
                        navController.navigate("fudo_question")
                    }
                },
                onBackClicked = { navController.popBackStack() },
            )
        }
        composable("sawi_question") {
            PhoriaQuestionScreen(
                onNextClicked = { answer ->
                    if (answer == 2) {
                        navController.navigate("sawi_adjustment")
                    } else {
                        navController.navigate("sawi_result/$answer/false/0/0/0/0")
                    }
                },
                onBackClicked = { navController.popBackStack() },
                testType = "sawi",
            )
        }
        composable("fudo_question") {
            AniseikoniaQuestionScreen(
                onNextClicked = { answer ->
                    if (answer == 1) {
                        navController.navigate("fudo_adjustment")
                    } else {
                        navController.navigate("fudo_result/$answer/0.0")
                    }
                },
                onBackClicked = { navController.popBackStack() },
                onShowHowToClicked = { /* TODO */ },
            )
        }
        composable("sawi_adjustment") {
            PhoriaAdjustmentScreen(
                onConfirmClicked = { crosshairPosition, circlePosition ->
                    navController.navigate(
                        "sawi_result/2/true/${crosshairPosition.x}/${crosshairPosition.y}/${circlePosition.x}/${circlePosition.y}",
                    )
                },
                onBackClicked = { navController.popBackStack() },
                onShowHowToClicked = { /* TODO */ },
            )
        }
        composable("fudo_adjustment") {
            AniseikoniaAdjustmentScreen(
                onNextClicked = { answer, difference ->
                    navController.navigate("fudo_result/$answer/$difference")
                },
                onBackClicked = { navController.popBackStack() },
            )
        }
        composable("sawi_result/{answer}/{isAdjusted}/{crossX}/{crossY}/{circleX}/{circleY}") { backStackEntry ->
            PhoriaResultScreen(
                answer = backStackEntry.arguments?.getString("answer")?.toIntOrNull(),
                isAdjusted = backStackEntry.arguments?.getString("isAdjusted")?.toBoolean()
                    ?: false,
                crossX = backStackEntry.arguments?.getString("crossX")?.toFloatOrNull(),
                crossY = backStackEntry.arguments?.getString("crossY")?.toFloatOrNull(),
                circleX = backStackEntry.arguments?.getString("circleX")?.toFloatOrNull(),
                circleY = backStackEntry.arguments?.getString("circleY")?.toFloatOrNull(),
                onPrintClicked = { /* TODO */ },
                onBackToMainClicked = {
                    parentNavController.popBackStack(
                        NavConstants.ROUTE_CATEGORY_LIST,
                        false
                    )
                },
            )
        }
        composable("fudo_result/{answer}/{difference}") { backStackEntry ->
            AniseikoniaResultScreen(
                answer = backStackEntry.arguments?.getString("answer")?.toIntOrNull(),
                difference = backStackEntry.arguments?.getString("difference")?.toFloatOrNull(),
                onPrintClicked = { /* TODO */ },
                onBackToMainClicked = {
                    parentNavController.popBackStack(
                        NavConstants.ROUTE_CATEGORY_LIST,
                        false
                    )
                },
            )
        }
    }
}
