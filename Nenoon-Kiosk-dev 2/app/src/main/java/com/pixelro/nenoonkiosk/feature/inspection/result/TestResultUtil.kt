package com.pixelro.nenoonkiosk.feature.inspection.result

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.manager.SharedPreferencesManager
import com.pixelro.nenoonkiosk.core.util.StringProvider
import com.pixelro.nenoonkiosk.core.util.dataprovider.TestType
import com.pixelro.nenoonkiosk.feature.inspection.bloodPressure.BloodPressureTestResult
import com.pixelro.nenoonkiosk.feature.inspection.dementia.DementiaTestResult
import com.pixelro.nenoonkiosk.feature.inspection.gripStrength.GripStrengthTestResult
import com.pixelro.nenoonkiosk.feature.inspection.macular.amslergrid.AmslerGridTestResult
import com.pixelro.nenoonkiosk.feature.inspection.macular.amslergrid.MacularDisorderType
import com.pixelro.nenoonkiosk.feature.inspection.macular.mchart.MChartTestResult
import com.pixelro.nenoonkiosk.feature.inspection.presbyopia.PresbyopiaTestResult
import com.pixelro.nenoonkiosk.feature.inspection.pulmonaryFunction.PulmonaryFunctionTestResult
import com.pixelro.nenoonkiosk.feature.inspection.visualacuity.shortdistance.ShortVisualAcuityTestResult

// TODO STR

object TestResultUtil {
    fun textAsBitmap(
        testType: TestType,
        testResult: Any?,
        logoImg: Bitmap,
        qrImg: Bitmap,
    ): Bitmap {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.color = 0xff000000.toInt()
        paint.textSize = 40f
        paint.textAlign = Paint.Align.CENTER
        val width = 600
        val baseline = -paint.ascent()
        when (testType) {
            TestType.Presbyopia -> {
                testResult as PresbyopiaTestResult
                val image = Bitmap.createBitmap(width, 400, Bitmap.Config.ARGB_8888)
                val canvas = Canvas(image)
                canvas.drawARGB(255, 255, 255, 255)

                canvas.drawBitmap(logoImg, 360f, 320f, null)

//                canvas.drawBitmap(qrImg, 0f, 0f, null)

//                canvas.drawText("조절력 검사", 300f, baseline, paint)
                canvas.drawText(
                    StringProvider.getString(
                        R.string.presbyopia_name2,
                    ),
                    300f,
                    baseline,
                    paint,
                )

                paint.typeface = Typeface.DEFAULT_BOLD
                if (testResult.firstDistance.toInt() == 25 && testResult.secondDistance.toInt() == 25 && testResult.thirdDistance.toInt() == 25) {
                    canvas.drawText(
                        StringProvider.getString(
                            R.string.presbyopia_result_description1_normal,
                        ),
                        300f,
                        baseline + 160f,
                        paint,
                    )
                    canvas.drawText(
                        StringProvider.getString(
                            R.string.presbyopia_result_description,
                        ),
                        300f,
                        baseline + 200f,
                        paint,
                    )
                } else {
                    when (testResult.firstDistance.toInt() == 25 || testResult.secondDistance.toInt() == 25 || testResult.thirdDistance.toInt() == 25) {
                        true -> {
                            canvas.drawText(
                                StringProvider.getString(
                                    R.string.presbyopia_result_description1_normal,
                                ),
                                300f,
                                baseline + 160f,
                                paint,
                            )
                            canvas.drawText(
                                StringProvider.getString(
                                    R.string.presbyopia_result_description2_eye_age,
                                ) + " " + (testResult.age - 2) + " ~ " + (testResult.age + 2) +
                                    StringProvider.getString(
                                        R.string.presbyopia_result_description2_abnormal,
                                    ) +
                                    StringProvider.getString(
                                        R.string.presbyopia_result_description3_abnormal,
                                    ),
                                300f,
                                baseline + 200f,
                                paint,
                            )
                        }

                        false -> {
                            canvas.drawText(
                                StringProvider.getString(
                                    R.string.presbyopia_result_description_abnormal_1,
                                ),
                                300f,
                                baseline + 160f,
                                paint,
                            )
                            canvas.drawText(
                                StringProvider.getString(
                                    R.string.presbyopia_result_description_abnormal_2,
                                ),
                                300f,
                                baseline + 200f,
                                paint,
                            )
                            canvas.drawText(
                                StringProvider.getString(
                                    R.string.presbyopia_result_description2_eye_age,
                                ) + " " + (testResult.age - 2) + " ~ " + (testResult.age + 2) +
                                    StringProvider.getString(
                                        R.string.presbyopia_result_description2_abnormal,
                                    ) +
                                    StringProvider.getString(
                                        R.string.presbyopia_result_description3_abnormal,
                                    ),
                                300f,
                                baseline + 240f,
                                paint,
                            )
                        }
                    }
                }
                paint.typeface = Typeface.DEFAULT

                paint.textAlign = Paint.Align.LEFT
//            canvas.drawText(printName, 0f, baseline + 320f, paint)
//            canvas.drawText("☎0000-0000", 0f, baseline + 360f, paint)
//                canvas.drawText("UAE Hospital", 0f, baseline + 320f, paint)
//                canvas.drawText("+82-31-8182-9290", 0f, baseline + 360f, paint)
                return image!!
            }

            TestType.ShortDistanceVisualAcuity -> {
                testResult as ShortVisualAcuityTestResult
                val image = Bitmap.createBitmap(width, 400, Bitmap.Config.ARGB_8888)
                val canvas = Canvas(image)
                canvas.drawARGB(255, 255, 255, 255)

                canvas.drawBitmap(logoImg, 360f, 320f, null)

//                canvas.drawBitmap(qrImg, 0f, 0f, null)

                canvas.drawText(
                    StringProvider.getString(
                        R.string.short_visual_acuity_name2,
                    ),
                    300f,
                    baseline,
                    paint,
                )

                canvas.drawText(
                    StringProvider.getString(
                        R.string.printer_left_eye,
                    ),
                    150f,
                    baseline + 60f,
                    paint,
                )
                canvas.drawText(
                    StringProvider.getString(
                        R.string.printer_right_eye,
                    ),
                    450f,
                    baseline + 60f,
                    paint,
                )

                paint.typeface = Typeface.DEFAULT_BOLD

                if (SharedPreferencesManager.getString("language") == "ko") {
                    canvas.drawText((testResult.leftEye / 10f).toString(), 150f, baseline + 190f, paint)
                    canvas.drawText((testResult.rightEye / 10f).toString(), 450f, baseline + 190f, paint)
                }
                if (SharedPreferencesManager.getString("language") == "en") {
                    canvas.drawText("20/" + (200 / testResult.leftEye).toString(), 150f, baseline + 190f, paint)
                    canvas.drawText("20/" + (200 / testResult.rightEye).toString(), 450f, baseline + 190f, paint)
                }
                if (SharedPreferencesManager.getString("language") == "zh") {
                    canvas.drawText((4 + testResult.leftEye / 10f).toString(), 150f, baseline + 190f, paint)
                    canvas.drawText((4 + testResult.rightEye / 10f).toString(), 450f, baseline + 190f, paint)
                }
                if (SharedPreferencesManager.getString("language") == "ja") {
                    canvas.drawText((testResult.leftEye / 10f).toString(), 150f, baseline + 190f, paint)
                    canvas.drawText((testResult.rightEye / 10f).toString(), 450f, baseline + 190f, paint)
                }
//                canvas.drawText((testResult.leftEye / 10f).toString(), 150f, baseline + 190f, paint)
//                canvas.drawText((testResult.rightEye / 10f).toString(), 450f, baseline + 190f, paint)
                paint.typeface = Typeface.DEFAULT

                paint.textAlign = Paint.Align.LEFT
                canvas.drawLine(300f, 100f, 300f, 300f, paint)
//            canvas.drawText(printName, 0f, baseline + 320f, paint)
//            canvas.drawText("☎0000-0000", 0f, baseline + 360f, paint)
//                canvas.drawText("UAE Hospital", 0f, baseline + 320f, paint)
//                canvas.drawText("+82-31-8182-9290", 0f, baseline + 360f, paint)
                return image!!
            }

            TestType.LongDistanceVisualAcuity -> {
                val image = Bitmap.createBitmap(width, 400, Bitmap.Config.ARGB_8888)
                val canvas = Canvas(image)
                canvas.drawARGB(255, 255, 255, 255)

                canvas.drawBitmap(logoImg, 360f, 320f, null)

//                canvas.drawBitmap(qrImg, 0f, 0f, null)

                canvas.drawText("원거리 시력 검사", 300f, baseline, paint)

                canvas.drawText("좌안", 150f, baseline + 60f, paint)
                canvas.drawText("우안", 450f, baseline + 60f, paint)

                paint.typeface = Typeface.DEFAULT_BOLD
                canvas.drawText("0.6 난시", 150f, baseline + 190f, paint)
                canvas.drawText("1.0 정상", 450f, baseline + 190f, paint)
                paint.typeface = Typeface.DEFAULT

                paint.textAlign = Paint.Align.LEFT
                canvas.drawLine(300f, 100f, 300f, 300f, paint)
//            canvas.drawText(printName, 0f, baseline + 320f, paint)
//            canvas.drawText("☎0000-0000", 0f, baseline + 360f, paint)

                return image!!
            }

            TestType.ChildrenVisualAcuity -> {
                val image = Bitmap.createBitmap(width, 400, Bitmap.Config.ARGB_8888)
                val canvas = Canvas(image)
                canvas.drawARGB(255, 255, 255, 255)

                canvas.drawBitmap(logoImg, 360f, 320f, null)

//                canvas.drawBitmap(qrImg, 0f, 0f, null)

                canvas.drawText("어린이 시력 검사", 300f, baseline, paint)

                canvas.drawText("좌안", 150f, baseline + 60f, paint)
                canvas.drawText("우안", 450f, baseline + 60f, paint)

                paint.typeface = Typeface.DEFAULT_BOLD
                canvas.drawText("0.6 난시", 150f, baseline + 190f, paint)
                canvas.drawText("1.0 정상", 450f, baseline + 190f, paint)
                paint.typeface = Typeface.DEFAULT

                paint.textAlign = Paint.Align.LEFT
                canvas.drawLine(300f, 100f, 300f, 300f, paint)
//            canvas.drawText(printName, 0f, baseline + 320f, paint)
//            canvas.drawText("☎0000-0000", 0f, baseline + 360f, paint)
                return image!!
            }

            TestType.AmslerGrid -> {
                testResult as AmslerGridTestResult
                val image = Bitmap.createBitmap(width, 500, Bitmap.Config.ARGB_8888)
                val canvas = Canvas(image)
                canvas.drawARGB(255, 255, 255, 255)

                canvas.drawBitmap(logoImg, 360f, 420f, null)

//                canvas.drawBitmap(qrImg, 0f, 0f, null)

                canvas.drawText(
                    StringProvider.getString(
                        R.string.amsler_grid_name,
                    ),
                    300f,
                    baseline,
                    paint,
                )

                canvas.drawText(
                    StringProvider.getString(
                        R.string.printer_left_eye,
                    ),
                    150f,
                    baseline + 60f,
                    paint,
                )
                canvas.drawText(
                    StringProvider.getString(
                        R.string.printer_right_eye,
                    ),
                    450f,
                    baseline + 60f,
                    paint,
                )

                paint.style = Paint.Style.STROKE
                paint.strokeWidth = 4f

                canvas.drawRect(RectF(30f, 140f, 110f, 220f), paint)
                canvas.drawRect(RectF(110f, 140f, 190f, 220f), paint)
                canvas.drawRect(RectF(190f, 140f, 270f, 220f), paint)
                canvas.drawRect(RectF(30f, 220f, 110f, 300f), paint)
                canvas.drawRect(RectF(110f, 220f, 190f, 300f), paint)
                canvas.drawRect(RectF(190f, 220f, 270f, 300f), paint)
                canvas.drawRect(RectF(30f, 300f, 110f, 380f), paint)
                canvas.drawRect(RectF(110f, 300f, 190f, 380f), paint)
                canvas.drawRect(RectF(190f, 300f, 270f, 380f), paint)

                canvas.drawRect(RectF(330f, 140f, 410f, 220f), paint)
                canvas.drawRect(RectF(410f, 140f, 490f, 220f), paint)
                canvas.drawRect(RectF(490f, 140f, 570f, 220f), paint)
                canvas.drawRect(RectF(330f, 220f, 410f, 300f), paint)
                canvas.drawRect(RectF(410f, 220f, 490f, 300f), paint)
                canvas.drawRect(RectF(490f, 220f, 570f, 300f), paint)
                canvas.drawRect(RectF(330f, 300f, 410f, 380f), paint)
                canvas.drawRect(RectF(410f, 300f, 490f, 380f), paint)
                canvas.drawRect(RectF(490f, 300f, 570f, 380f), paint)

                paint.style = Paint.Style.FILL
                paint.typeface = Typeface.DEFAULT_BOLD
                if (testResult.leftEyeDisorderType[0] != MacularDisorderType.Normal) {
                    canvas.drawText(
                        StringProvider.getString(
                            R.string.printer_problem,
                        ),
                        70f,
                        baseline + 160f,
                        paint,
                    )
                }
                if (testResult.leftEyeDisorderType[1] != MacularDisorderType.Normal) {
                    canvas.drawText(
                        StringProvider.getString(
                            R.string.printer_problem,
                        ),
                        150f,
                        baseline + 160f,
                        paint,
                    )
                }
                if (testResult.leftEyeDisorderType[2] != MacularDisorderType.Normal) {
                    canvas.drawText(
                        StringProvider.getString(
                            R.string.printer_problem,
                        ),
                        230f,
                        baseline + 160f,
                        paint,
                    )
                }
                if (testResult.leftEyeDisorderType[3] != MacularDisorderType.Normal) {
                    canvas.drawText(
                        StringProvider.getString(
                            R.string.printer_problem,
                        ),
                        70f,
                        baseline + 240f,
                        paint,
                    )
                }
                if (testResult.leftEyeDisorderType[4] != MacularDisorderType.Normal) {
                    canvas.drawText(
                        StringProvider.getString(
                            R.string.printer_problem,
                        ),
                        150f,
                        baseline + 240f,
                        paint,
                    )
                }
                if (testResult.leftEyeDisorderType[5] != MacularDisorderType.Normal) {
                    canvas.drawText(
                        StringProvider.getString(
                            R.string.printer_problem,
                        ),
                        230f,
                        baseline + 240f,
                        paint,
                    )
                }
                if (testResult.leftEyeDisorderType[6] != MacularDisorderType.Normal) {
                    canvas.drawText(
                        StringProvider.getString(
                            R.string.printer_problem,
                        ),
                        70f,
                        baseline + 320f,
                        paint,
                    )
                }
                if (testResult.leftEyeDisorderType[7] != MacularDisorderType.Normal) {
                    canvas.drawText(
                        StringProvider.getString(
                            R.string.printer_problem,
                        ),
                        150f,
                        baseline + 320f,
                        paint,
                    )
                }
                if (testResult.leftEyeDisorderType[8] != MacularDisorderType.Normal) {
                    canvas.drawText(
                        StringProvider.getString(
                            R.string.printer_problem,
                        ),
                        230f,
                        baseline + 320f,
                        paint,
                    )
                }

                if (testResult.rightEyeDisorderType[0] != MacularDisorderType.Normal) {
                    canvas.drawText(
                        StringProvider.getString(
                            R.string.printer_problem,
                        ),
                        370f,
                        baseline + 160f,
                        paint,
                    )
                }
                if (testResult.rightEyeDisorderType[1] != MacularDisorderType.Normal) {
                    canvas.drawText(
                        StringProvider.getString(
                            R.string.printer_problem,
                        ),
                        450f,
                        baseline + 160f,
                        paint,
                    )
                }
                if (testResult.rightEyeDisorderType[2] != MacularDisorderType.Normal) {
                    canvas.drawText(
                        StringProvider.getString(
                            R.string.printer_problem,
                        ),
                        530f,
                        baseline + 160f,
                        paint,
                    )
                }
                if (testResult.rightEyeDisorderType[3] != MacularDisorderType.Normal) {
                    canvas.drawText(
                        StringProvider.getString(
                            R.string.printer_problem,
                        ),
                        370f,
                        baseline + 240f,
                        paint,
                    )
                }
                if (testResult.rightEyeDisorderType[4] != MacularDisorderType.Normal) {
                    canvas.drawText(
                        StringProvider.getString(
                            R.string.printer_problem,
                        ),
                        450f,
                        baseline + 240f,
                        paint,
                    )
                }
                if (testResult.rightEyeDisorderType[5] != MacularDisorderType.Normal) {
                    canvas.drawText(
                        StringProvider.getString(
                            R.string.printer_problem,
                        ),
                        530f,
                        baseline + 240f,
                        paint,
                    )
                }
                if (testResult.rightEyeDisorderType[6] != MacularDisorderType.Normal) {
                    canvas.drawText(
                        StringProvider.getString(
                            R.string.printer_problem,
                        ),
                        370f,
                        baseline + 320f,
                        paint,
                    )
                }
                if (testResult.rightEyeDisorderType[7] != MacularDisorderType.Normal) {
                    canvas.drawText(
                        StringProvider.getString(
                            R.string.printer_problem,
                        ),
                        450f,
                        baseline + 320f,
                        paint,
                    )
                }
                if (testResult.rightEyeDisorderType[8] != MacularDisorderType.Normal) {
                    canvas.drawText(
                        StringProvider.getString(
                            R.string.printer_problem,
                        ),
                        530f,
                        baseline + 320f,
                        paint,
                    )
                }
                paint.typeface = Typeface.DEFAULT

                paint.textAlign = Paint.Align.LEFT
                paint.strokeWidth = 1f
                canvas.drawLine(300f, 100f, 300f, 400f, paint)
//            canvas.drawText(printName, 0f, baseline + 420f, paint)
//            canvas.drawText("☎0000-0000", 0f, baseline + 460f, paint)
//                canvas.drawText("UAE Hospital", 0f, baseline + 420f, paint)
//                canvas.drawText("+82-31-8182-9290", 0f, baseline + 460f, paint)
                return image!!
            }

            TestType.MChart -> {
                testResult as MChartTestResult
                val image = Bitmap.createBitmap(width, 400, Bitmap.Config.ARGB_8888)
                val canvas = Canvas(image)
                canvas.drawARGB(255, 255, 255, 255)

                canvas.drawBitmap(logoImg, 360f, 320f, null)

//                canvas.drawBitmap(qrImg, 0f, 0f, null)

                canvas.drawText(
                    StringProvider.getString(
                        R.string.mchart_name,
                    ),
                    300f,
                    baseline,
                    paint,
                )

                canvas.drawText(
                    StringProvider.getString(
                        R.string.printer_left_eye,
                    ),
                    150f,
                    baseline + 60f,
                    paint,
                )
                canvas.drawText(
                    StringProvider.getString(
                        R.string.printer_right_eye,
                    ),
                    450f,
                    baseline + 60f,
                    paint,
                )

                paint.typeface = Typeface.DEFAULT_BOLD
                canvas.drawText(
                    when (testResult.leftEyeVertical) {
                        0 ->
                            StringProvider.getString(
                                R.string.printer_horizontal_normal,
                            )
                        else ->
                            StringProvider.getString(
                                R.string.printer_horizontal_problem,
                            )
                    },
                    150f,
                    baseline + 170f,
                    paint,
                )
                canvas.drawText(
                    when (testResult.leftEyeHorizontal) {
                        0 ->
                            StringProvider.getString(
                                R.string.printer_vertical_normal,
                            )
                        else ->
                            StringProvider.getString(
                                R.string.printer_vertical_problem,
                            )
                    },
                    150f,
                    baseline + 210f,
                    paint,
                )
                canvas.drawText(
                    when (testResult.rightEyeVertical) {
                        0 ->
                            StringProvider.getString(
                                R.string.printer_horizontal_normal,
                            )
                        else ->
                            StringProvider.getString(
                                R.string.printer_horizontal_problem,
                            )
                    },
                    450f,
                    baseline + 170f,
                    paint,
                )
                canvas.drawText(
                    when (testResult.rightEyeHorizontal) {
                        0 ->
                            StringProvider.getString(
                                R.string.printer_vertical_normal,
                            )
                        else ->
                            StringProvider.getString(
                                R.string.printer_vertical_problem,
                            )
                    },
                    450f,
                    baseline + 210f,
                    paint,
                )
                paint.typeface = Typeface.DEFAULT

                paint.textAlign = Paint.Align.LEFT
                canvas.drawLine(300f, 100f, 300f, 300f, paint)
//            canvas.drawText(printName, 0f, baseline + 320f, paint)
//            canvas.drawText("☎0000-0000", 0f, baseline + 360f, paint)
//                canvas.drawText("UAE Hospital", 0f, baseline + 320f, paint)
//                canvas.drawText("+82-31-8182-9290", 0f, baseline + 360f, paint)
                return image!!
            }

            TestType.GripStrength -> {
                testResult as GripStrengthTestResult
                val image = Bitmap.createBitmap(width, 400, Bitmap.Config.ARGB_8888)
                val canvas = Canvas(image)
                canvas.drawARGB(255, 255, 255, 255)

                canvas.drawBitmap(logoImg, 360f, 320f, null)

                canvas.drawText(
                    StringProvider.getString(
                        R.string.test_predescription_grip_strength_title1,
                    ),
                    300f,
                    baseline,
                    paint,
                )

                canvas.drawText(
                    StringProvider.getString(
                        R.string.test_result_left,
                    ),
                    150f,
                    baseline + 60f,
                    paint,
                )
                canvas.drawText(
                    StringProvider.getString(
                        R.string.test_result_right,
                    ),
                    450f,
                    baseline + 60f,
                    paint,
                )

                paint.typeface = Typeface.DEFAULT_BOLD

                canvas.drawText("${testResult.leftGrip}kg", 150f, baseline + 190f, paint)
                canvas.drawText("${testResult.rightGrip}kg", 450f, baseline + 190f, paint)

                paint.typeface = Typeface.DEFAULT

                paint.textAlign = Paint.Align.LEFT
                canvas.drawLine(300f, 100f, 300f, 300f, paint)
                return image!!
            }

            TestType.BloodPressure -> {
                testResult as BloodPressureTestResult
                val image = Bitmap.createBitmap(width, 400, Bitmap.Config.ARGB_8888)
                val canvas = Canvas(image)
                canvas.drawARGB(255, 255, 255, 255)

                canvas.drawBitmap(logoImg, 360f, 320f, null)

                canvas.drawText(
                    StringProvider.getString(
                        (R.string.test_predescription_blood_pressure_title1),
                    ),
                    300f,
                    baseline,
                    paint,
                )

                paint.textAlign = Paint.Align.LEFT

                canvas.drawText(
                    StringProvider.getString(
                        R.string.blood_pressure_monitor_systolic,
                    ) + " : ${testResult.systolic}mmHg",
                    20f,
                    baseline + 80f,
                    paint,
                )
                canvas.drawText(
                    StringProvider.getString(
                        R.string.blood_pressure_monitor_diastolic,
                    ) + " : ${testResult.diastolic}mmHg",
                    20f,
                    baseline + 150f,
                    paint,
                )
                canvas.drawText(
                    StringProvider.getString(
                        R.string.blood_pressure_monitor_heart_rate,
                    ) + " : ${testResult.pulseRate}bpm",
                    20f,
                    baseline + 220f,
                    paint,
                )

                return image!!
            }

            TestType.Dementia -> {
                testResult as DementiaTestResult
                val image = Bitmap.createBitmap(width, 400, Bitmap.Config.ARGB_8888)
                val canvas = Canvas(image)
                canvas.drawARGB(255, 255, 255, 255)

                canvas.drawBitmap(logoImg, 360f, 320f, null)

                canvas.drawText(
                    StringProvider.getString(
                        (R.string.dementia_test),
                    ),
                    300f,
                    baseline,
                    paint,
                )

                paint.textAlign = Paint.Align.LEFT

                canvas.drawText(
                    StringProvider.getString(R.string.test_result_my_result) + ": " + testResult.countActiveScore().toString(),
                    20f,
                    baseline + 150f,
                    paint,
                )

                return image!!
            }

            TestType.PulmonaryFunction -> {
                testResult as PulmonaryFunctionTestResult
                val image = Bitmap.createBitmap(width, 400, Bitmap.Config.ARGB_8888)
                val canvas = Canvas(image)
                canvas.drawARGB(255, 255, 255, 255)

                canvas.drawBitmap(logoImg, 360f, 320f, null)

                canvas.drawText(
                    StringProvider.getString(
                        (R.string.pulmonary_function_test_result),
                    ),
                    300f,
                    baseline,
                    paint,
                )

                paint.textAlign = Paint.Align.LEFT

                val roundedPulmonaryCapacity = String.format("%.1f", testResult.pulmonaryCapacity)
                val roundedPulmonaryPower = String.format("%.1f", testResult.pulmonaryPower)

                canvas.drawText(
                    StringProvider.getString(
                        R.string.pulmonary_capacity_label,
                    ) + " : ${roundedPulmonaryCapacity}L",
                    20f,
                    baseline + 80f,
                    paint,
                )
                canvas.drawText(
                    StringProvider.getString(
                        R.string.pulmonary_power_label,
                    ) + " : ${roundedPulmonaryPower}F",
                    20f,
                    baseline + 150f,
                    paint,
                )
                canvas.drawText(
                    StringProvider.getString(
                        R.string.pulmonary_age_label,
                    ) + " : ${testResult.pulmonaryAge}",
                    20f,
                    baseline + 220f,
                    paint,
                )

                return image!!

//                testResult as PulmonaryFunctionTestResult
//                val image = Bitmap.createBitmap(width, 400, Bitmap.Config.ARGB_8888)
//                val canvas = Canvas(image)
//                canvas.drawARGB(255, 255, 255, 255)
//                canvas.drawBitmap(logoImg, 360f, 320f, null)
//
//                canvas.drawText(StringProvider.getString(R.string.pulmonary_function_test), 300f, baseline, paint)
//
//                if(SharedPreferencesManager.getString("language") != "ko") {
//                    paint.textSize = 28.0f;
//                }
//                canvas.drawText(StringProvider.getString(R.string.pulmonary_capacity_label), 100f, baseline + 60f, paint)
//                canvas.drawText(StringProvider.getString(R.string.pulmonary_power_label), 300f, baseline + 60f, paint)
//                canvas.drawText(StringProvider.getString(R.string.pulmonary_age_label), 500f, baseline + 60f, paint)
//
//                paint.textSize = 40.0f;
//
//                paint.typeface = Typeface.DEFAULT_BOLD
//                canvas.drawText(String.format("%.1fL", testResult.pulmonaryCapacity), 100f, baseline + 190f, paint)
//                canvas.drawText(String.format("%.1f", testResult.pulmonaryPower), 300f, baseline + 190f, paint)
//                canvas.drawText(String.format(StringProvider.getString(R.string.pulmonary_age_short_format), testResult.pulmonaryAge.toString()), 500f, baseline + 190f, paint)
//                paint.typeface = Typeface.DEFAULT
//
//                paint.textAlign = Paint.Align.LEFT
//                canvas.drawLine(200f, 100f, 200f, 300f, paint)
//                canvas.drawLine(400f, 100f, 400f, 300f, paint)
//                return image!!
            }

            else -> {
                val image = Bitmap.createBitmap(width, 400, Bitmap.Config.ARGB_8888)
                val canvas = Canvas(image)
                return image!!
            }
        }
    }

    fun formatQrCode(
        qrImg: Bitmap,
        logoImg: Bitmap,
    ): Bitmap {
        val outputWidth = 300
        val outputHeight = 300

        val resultBitmap = Bitmap.createBitmap(600, 400, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(resultBitmap)
        canvas.drawARGB(255, 255, 255, 255)

        val destRect = RectF(150f, 0f, 450f, 300f)
        canvas.drawBitmap(qrImg, null, destRect, null)

        canvas.drawBitmap(logoImg, 360f, 320f, null)

        return resultBitmap
    }
}
