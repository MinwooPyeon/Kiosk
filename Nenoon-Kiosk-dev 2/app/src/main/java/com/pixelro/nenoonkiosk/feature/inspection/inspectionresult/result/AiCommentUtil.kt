package com.pixelro.nenoonkiosk.feature.inspection.inspectionresult.result

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.util.ColorProvider
import com.pixelro.nenoonkiosk.core.util.StringProvider
import com.pixelro.nenoonkiosk.feature.inspection.InspectionType
import com.pixelro.nenoonkiosk.feature.inspection.bloodPressure.BloodPressureTestResult
import com.pixelro.nenoonkiosk.feature.inspection.dementia.DementiaInspectionResult
import com.pixelro.nenoonkiosk.feature.inspection.macular.amslergrid.AmslerGridTestResult
import com.pixelro.nenoonkiosk.feature.inspection.macular.amslergrid.MacularDisorderType
import com.pixelro.nenoonkiosk.feature.inspection.macular.mchart.MChartTestResult
import com.pixelro.nenoonkiosk.feature.inspection.presbyopia.PresbyopiaInspectionResult
import com.pixelro.nenoonkiosk.feature.inspection.visualacuity.shortdistance.ShortVisualAcuityTestResult

fun aiComment(
    testType: InspectionType,
    testResult: Any,
): AnnotatedString {
    val red = Color(ColorProvider.getColor(R.color.error))
    val orange = Color(ColorProvider.getColor(R.color.warning))
    val blue = Color(ColorProvider.getColor(R.color.main))

    return when (testType) {
        InspectionType.ShortDistanceVisualAcuity -> {
            try {
                val parsedResult = testResult as ShortVisualAcuityTestResult

                if (parsedResult.leftEye >= 5 && parsedResult.rightEye >= 5) {
                    buildAnnotatedString {
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = blue)) {
                            append(
                                StringProvider.getString(
                                    R.string.visual_acuity_result_normal_heading,
                                ) + "\n",
                            )
                        }
                        append(
                            StringProvider.getString(
                                R.string.visual_acuity_result_normal_body,
                            ),
                        )
                    }
                } else {
                    buildAnnotatedString {
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = red)) {
                            append(
                                StringProvider.getString(
                                    R.string.visual_acuity_result_abnormal_heading,
                                ) + "\n",
                            )
                        }
                        append(
                            StringProvider.getString(
                                R.string.visual_acuity_result_abnormal_body,
                            ),
                        )
                    }
                }
            } catch (e: ClassCastException) {
                AnnotatedString("")
            }
        }

        InspectionType.Presbyopia -> {
            try {
                val parsedResult = testResult as PresbyopiaInspectionResult

                when (parsedResult.firstDistance.toInt() == 25 || parsedResult.secondDistance.toInt() == 25 || parsedResult.thirdDistance.toInt() == 25) {
                    true ->
                        buildAnnotatedString {
                            withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = blue)) {
                                append(
                                    StringProvider.getString(
                                        R.string.presbyopia_result_description1_normal,
                                    ),
                                )
                            }
                        }
                    false ->
                        buildAnnotatedString {
                            withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = red)) {
                                append(
                                    StringProvider.getString(
                                        R.string.presbyopia_result_description1_abnormal,
                                    ),
                                )
                            }
                        }
                }
            } catch (e: ClassCastException) {
                AnnotatedString("")
            }
        }

        InspectionType.AmslerGrid -> {
            return try {
                val parsedResult = testResult as AmslerGridTestResult

                if ((parsedResult.leftEyeDisorderType + parsedResult.rightEyeDisorderType)
                        .map { it == MacularDisorderType.Normal }.all { it }
                ) {
                    buildAnnotatedString {
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = blue)) {
                            append(
                                StringProvider.getString(
                                    R.string.amsler_result_normal_heading,
                                ) + "\n",
                            )
                        }
                        append(
                            StringProvider.getString(
                                R.string.amsler_result_normal_body,
                            ),
                        )
                    }
                } else {
                    buildAnnotatedString {
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = red)) {
                            append(
                                StringProvider.getString(
                                    R.string.amsler_result_abnormal_heading,
                                ) + "\n",
                            )
                        }
                        append(
                            StringProvider.getString(
                                R.string.amsler_result_abnormal_body,
                            ),
                        )
                    }
                }
            } catch (e: ClassCastException) {
                AnnotatedString("")
            }
        }

        InspectionType.MChart -> {
            try {
                val parsedResult = testResult as MChartTestResult

                if (parsedResult.leftEyeVertical == 0 &&
                    parsedResult.rightEyeVertical == 0 &&
                    parsedResult.leftEyeHorizontal == 0 &&
                    parsedResult.rightEyeHorizontal == 0
                ) {
                    buildAnnotatedString {
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = blue)) {
                            append(
                                StringProvider.getString(
                                    R.string.mchart_result_normal_heading,
                                ) + "\n",
                            )
                        }
                        append(
                            StringProvider.getString(
                                R.string.mchart_result_normal_body,
                            ),
                        )
                    }
                } else {
                    buildAnnotatedString {
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = red)) {
                            append(
                                StringProvider.getString(
                                    R.string.mchart_result_abnormal_heading,
                                ) + "\n",
                            )
                        }
                        append(
                            StringProvider.getString(
                                R.string.mchart_result_abnormal_body,
                            ),
                        )
                    }
                }
            } catch (e: ClassCastException) {
                AnnotatedString("")
            }
        }

        InspectionType.BloodPressure -> {
            try {
                val parsedResult = testResult as BloodPressureTestResult

                if (parsedResult.systolic < 120 && parsedResult.diastolic < 80) {
                    buildAnnotatedString {
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = blue)) {
                            append(
                                StringProvider.getString(
                                    R.string.bp_result_normal_heading,
                                ) + "\n",
                            )
                        }
                        append(
                            StringProvider.getString(
                                R.string.bp_result_normal_body,
                            ),
                        )
                    }
                } else if ((parsedResult.systolic in 120..129 && parsedResult.diastolic < 80) ||
                    parsedResult.systolic in 130..139 ||
                    parsedResult.diastolic in 80..89
                ) {
                    buildAnnotatedString {
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = orange)) {
                            append(
                                StringProvider.getString(
                                    R.string.bp_result_pre_hypertension_heading,
                                ) + "\n",
                            )
                        }
                        append(
                            StringProvider.getString(
                                R.string.bp_result_pre_hypertension_body,
                            ) + "\n\n",
                        )
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = blue)) { append("✓ ") }
                        append(
                            StringProvider.getString(
                                R.string.bp_result_pre_hypertension_tip1,
                            ) + "\n",
                        )
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = blue)) { append("✓ ") }
                        append(
                            StringProvider.getString(
                                R.string.bp_result_pre_hypertension_tip2,
                            ) + "\n",
                        )
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = blue)) { append("✓ ") }
                        append(
                            StringProvider.getString(
                                R.string.bp_result_pre_hypertension_tip3,
                            ) + "\n",
                        )
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = blue)) { append("✓ ") }
                        append(
                            StringProvider.getString(
                                R.string.bp_result_pre_hypertension_tip4,
                            ),
                        )
                    }
                } else {
                    buildAnnotatedString {
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = red)) {
                            append(
                                StringProvider.getString(
                                    R.string.bp_result_hypertension_heading,
                                ) + "\n",
                            )
                        }
                        append(
                            StringProvider.getString(
                                R.string.bp_result_hypertension_body,
                            ),
                        )
                    }
                }
            } catch (e: ClassCastException) {
                AnnotatedString("")
            }
        }

        InspectionType.GripStrength -> {
            buildAnnotatedString {
                append(
                    StringProvider.getString(
                        R.string.grip_strength_m_30s_label,
                    ) + ": ",
                )
                withStyle(SpanStyle(color = blue)) {
                    append(
                        StringProvider.getString(
                            R.string.grip_strength_m_30s_value,
                        ) + "\n",
                    )
                }
                append(
                    StringProvider.getString(
                        R.string.grip_strength_m_40_50s_label,
                    ) + ": ",
                )
                withStyle(SpanStyle(color = blue)) {
                    append(
                        StringProvider.getString(
                            R.string.grip_strength_m_40_50s_value,
                        ) + "\n",
                    )
                }
                append(
                    StringProvider.getString(
                        R.string.grip_strength_m_60s_label,
                    ) + ": ",
                )
                withStyle(SpanStyle(color = blue)) {
                    append(
                        StringProvider.getString(
                            R.string.grip_strength_m_60s_value,
                        ) + "\n",
                    )
                }
                append(
                    StringProvider.getString(
                        R.string.grip_strength_w_30s_label,
                    ) + ": ",
                )
                withStyle(SpanStyle(color = blue)) {
                    append(
                        StringProvider.getString(
                            R.string.grip_strength_w_30s_value,
                        ) + "\n",
                    )
                }
                append(
                    StringProvider.getString(
                        R.string.grip_strength_w_40_50s_label,
                    ) + ": ",
                )
                withStyle(SpanStyle(color = blue)) {
                    append(
                        StringProvider.getString(
                            R.string.grip_strength_w_40_50s_value,
                        ) + "\n",
                    )
                }
                append(
                    StringProvider.getString(
                        R.string.grip_strength_w_60s_label,
                    ) + ": ",
                )
                withStyle(SpanStyle(color = blue)) {
                    append(
                        StringProvider.getString(
                            R.string.grip_strength_w_60s_value,
                        ) + "\n\n",
                    )
                }
                append(
                    StringProvider.getString(
                        R.string.grip_strength_result_body,
                    ),
                )
            }
        }

        InspectionType.Dementia -> {
            try {
                val parsedResult = testResult as DementiaInspectionResult

                when {
                    parsedResult.countActiveScore() < 6 ->
                        buildAnnotatedString {
                            withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = blue)) {
                                append(
                                    StringProvider.getString(
                                        R.string.dementia_result_wording3,
                                    ),
                                )
                            }
                        }

                    parsedResult.countActiveScore() >= 6 ->
                        buildAnnotatedString {
                            withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = blue)) {
                                append(
                                    StringProvider.getString(
                                        R.string.dementia_result_wording4,
                                    ),
                                )
                            }
                        }

                    else -> AnnotatedString("")
                }
            } catch (e: ClassCastException) {
                AnnotatedString("")
            }
        }

        else -> AnnotatedString("")
    }
}
