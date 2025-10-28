package com.pixelro.nenoonkiosk.feature.print.model

import androidx.compose.runtime.Composable
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.util.StringProvider

data class PrintStrings(
    val pageTitle: String,
    val loading: String,
    val print: String,
    val completed: String,
    val notConducted: String,
    val visualAcuity: String,
    val presbyopia: String,
    val amsler: String,
    val mChart: String,
    val bloodPressure: String,
    val gripStrength: String,
    val dementia: String,
    val pulmonary: String
)

@Composable
fun rememberStrings() = PrintStrings(
    pageTitle = StringProvider.getString(R.string.result_print_screen_result_title),
    loading = StringProvider.getString(R.string.result_print_screen_loading_results),
    print = StringProvider.getString(R.string.result_print_screen_print_button),
    completed = StringProvider.getString(R.string.result_print_screen_test_completed),
    notConducted = StringProvider.getString(R.string.result_print_screen_test_not_conducted),
    visualAcuity = StringProvider.getString(R.string.result_print_screen_visual_acuity_test),
    presbyopia = StringProvider.getString(R.string.result_print_screen_presbyopia_test),
    amsler = StringProvider.getString(R.string.result_print_screen_amsler_grid_test),
    mChart = StringProvider.getString(R.string.result_print_screen_m_chart_test),
    bloodPressure = StringProvider.getString(R.string.result_print_screen_blood_pressure_test),
    gripStrength = StringProvider.getString(R.string.result_print_screen_grip_strength_test),
    dementia = StringProvider.getString(R.string.result_print_screen_dementia_survey),
    pulmonary = StringProvider.getString(R.string.result_print_screen_pulmonary_function_test),
)