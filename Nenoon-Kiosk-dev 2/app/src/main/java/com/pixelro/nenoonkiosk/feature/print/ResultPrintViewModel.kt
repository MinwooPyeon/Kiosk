package com.pixelro.nenoonkiosk.feature.print

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.os.Bundle
import android.os.CancellationSignal
import android.os.ParcelFileDescriptor
import android.print.PageRange
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.print.PrintDocumentInfo
import android.print.PrintManager
import android.util.Log
import androidx.core.graphics.scale
import androidx.lifecycle.AndroidViewModel
import com.harang.data.model.AmslerTestResultAPI
import com.harang.data.model.BloodPressureTestResultAPI
import com.harang.data.model.CompoundTestResultAPI
import com.harang.data.model.DementiaTestResultAPI
import com.harang.data.model.GripStrengthTestResultAPI
import com.harang.data.model.MChartsTestResultAPI
import com.harang.data.model.PresbyopiaTestResultAPI
import com.harang.data.model.PulmonaryTestResultAPI
import com.harang.data.model.SightTestResultAPI
import com.harang.data.model.User
import com.harang.data.repository.TestResultRepository
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.util.ColorProvider
import com.pixelro.nenoonkiosk.core.util.dataprovider.CompoundTestResult
import com.pixelro.nenoonkiosk.core.util.StringProvider
import com.pixelro.nenoonkiosk.feature.inspection.macular.amslergrid.MacularDisorderType
import dagger.hilt.android.lifecycle.HiltViewModel
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class ResultPrintViewModel @Inject constructor(
    application: Application,
    private val testResultRepository: TestResultRepository
) : AndroidViewModel(application) {

    suspend fun getCompoundTestResult(token: String): List<CompoundTestResultAPI> {
        val data = testResultRepository.getCompoundTestResult(token)
        Log.d("HHHHHHHHHHH", "$data")
        return data?.data?.results?.sortedByDescending { it.createAt } ?: emptyList()
    }

    fun createDummyCompoundTestResult(): CompoundTestResultAPI {
        // Your dummy data creation logic here
        return CompoundTestResultAPI(
            tid = 12345,
            userLoginId = "dummyUser",
            userName = "Dummy User",
            createAt = "2025-08-19T11:11:14Z",
            updateAt = "2025-08-19T11:11:14Z",
            dementia = DementiaTestResultAPI(
                tid = 1,
                createAt = "2025-08-19T11:11:14Z",
                dementiaQuestion1 = "Yes",
                dementiaQuestion2 = "Yes",
                dementiaQuestion3 = "Yes",
                dementiaQuestion4 = "No",
                dementiaQuestion5 = "No"
            ),
            bloodPressure = BloodPressureTestResultAPI(
                tid = 2,
                createAt = "2025-08-19T11:11:14Z",
                systolic = 125.0,
                diastolic = 80.0,
                pulseRate = 72.0
            ),
            gripStrength = GripStrengthTestResultAPI(
                tid = 3,
                createAt = "2025-08-19T11:11:14Z",
                leftGrip = 35.5,
                rightGrip = 38.2
            ),
            eyePresbyopia = PresbyopiaTestResultAPI(
                tid = 4,
                createAt = "2025-08-19T11:11:14Z",
                distance1 = 30.5,
                distance2 = 45.0,
                distance3 = 60.2,
                distanceAvg = 45.2,
                presbyopiaAge = 52
            ),
            eyeAmsler = AmslerTestResultAPI(
                tid = 5,
                createAt = "2025-08-19T11:11:14Z",
                leftMacularLoc = "n,n,d,n,n,d,d,d,n",
                rightMacularLoc = "n,n,d,n,n,n,n,n,n"
            ),
            eyeMCharts = MChartsTestResultAPI(
                tid = 6,
                createAt = "2025-08-19T11:11:14Z",
                leftEyeVer = "1.2",
                rightEyeVer = "1.5",
                leftEyeHor = "1.0",
                rightEyeHor = "1.3"
            ),
            eyeSight = SightTestResultAPI(
                tid = 7,
                createAt = "2025-08-19T11:11:14Z",
                leftSight = "20",
                rightSight = "18"
            ),
            strabismus = null,
            pulmonary = PulmonaryTestResultAPI(
                tid = 8,
                createAt = "2025-08-19T11:11:14Z",
                pulmonaryPower = 306.0,
                pulmonaryCapacity = 7.0,
                pulmonaryAge = 24
            )
        )
    }

    // New function signature to accept a list of results
    fun printContent(context: Context, contentToPrint: List<CompoundTestResult>, qrCodeBitmap: Bitmap?, locale: String?, userData: User?) {
        printContent_v2(context, contentToPrint, qrCodeBitmap, locale, userData)
    }

    // Modified to accept a list of results
    private fun printContent_v2(context: Context, contentToPrint: List<CompoundTestResult>, qrCodeBitmap: Bitmap?, locale: String?, userData: User?) {
        val printManager = context.getSystemService(Context.PRINT_SERVICE) as PrintManager
        val jobName = StringProvider.getString(R.string.pdf_print_job_name)

        val printAdapter = object : PrintDocumentAdapter() {
            private var printAttributes: PrintAttributes? = null

            override fun onLayout(
                oldAttributes: PrintAttributes?,
                newAttributes: PrintAttributes,
                cancellationSignal: CancellationSignal?,
                callback: LayoutResultCallback,
                extras: Bundle?
            ) {
                printAttributes = newAttributes
                if (cancellationSignal?.isCanceled == true) {
                    callback.onLayoutCancelled()
                    return
                }

                this.printAttributes = newAttributes
                val builder = PrintDocumentInfo.Builder(jobName)
                    .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                    .setPageCount(1)
                    .build()
                callback.onLayoutFinished(builder, false)
            }

            override fun onWrite(
                pages: Array<PageRange>,
                destination: ParcelFileDescriptor,
                cancellationSignal: CancellationSignal?,
                callback: WriteResultCallback
            ) {
                if (cancellationSignal?.isCanceled == true) {
                    callback.onWriteCancelled()
                    return
                }

                if (pages.isEmpty()) {
                    callback.onWriteFinished(emptyArray())
                    return
                }
                var success = true
                val document = PdfDocument()
                try {
                    val outputStream = FileOutputStream(destination.fileDescriptor)

                    val A4_WIDTH = 595
                    val A4_HEIGHT = 842

                    val contentScale = 0.8f

                    val page = document.startPage(
                        PdfDocument.PageInfo.Builder(A4_WIDTH, A4_HEIGHT, pages[0].start).create()
                    )
                    val canvas = page.canvas

                    val scaledContentWidth = A4_WIDTH * contentScale
                    val translateX = (A4_WIDTH - scaledContentWidth) / 2

                    canvas.save()
                    canvas.translate(translateX, 0f)
                    canvas.scale(contentScale, contentScale)
                    // Pass the entire list to the drawing function
                    drawContent_v2(canvas,
                        contentToPrint, A4_WIDTH.toFloat(), A4_HEIGHT.toFloat(), context, qrCodeBitmap, locale, userData)
                    canvas.restore()

                    document.finishPage(page)

                    document.writeTo(outputStream)
                    outputStream.close()

                    callback.onWriteFinished(pages)
                } catch (e: IOException) {
                    success = false
                    Log.e("PrintContent", "Error writing printed document: ${e.message}", e)
                    callback.onWriteFailed(StringProvider.getString(R.string.pdf_print_write_failed, e.message))
                } finally {
                    try {
                        document.close()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                if (!success) {
                    callback.onWriteFailed(StringProvider.getString(R.string.pdf_print_failed_generic))
                }
            }
        }
        printManager.print(jobName, printAdapter, null)
    }

    @SuppressLint("DefaultLocale")
    private fun drawContent_v2(
        canvas: Canvas,
        contentToPrint: List<CompoundTestResult>,
        pageWidth: Float,
        pageHeight: Float,
        context: Context,
        qrCodeBitmap: Bitmap?,
        locale: String?,
        userData: User?,
    ) {

        val textPaint = Paint().apply {
            color = ColorProvider.getColor(R.color.black)
            textSize = 10.5f
        }
        val titlePaint = Paint().apply {
            color = ColorProvider.getColor(R.color.black)
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textSize = 12.5f
        }
        val notTestedPaint = Paint().apply {
            color = Color.GRAY
            textSize = 10.5f
            textAlign = Paint.Align.LEFT
        }
        val linePaint = Paint().apply {
            color = Color.LTGRAY
            strokeWidth = 0.5f
        }
        val infoPaint = Paint().apply {
            color = ColorProvider.getColor(R.color.black)
            textSize = if (locale == "en") 10f else 10.5f
            textAlign = Paint.Align.LEFT
        }

        val marginHorizontal = 20f
        val marginVertical = 50f
        val columnGap = 8f

        val contentWidth = pageWidth - 2 * marginHorizontal

        val offsetX = 0f

        var currentY = marginVertical

        val mainTitlePaint = Paint().apply {
            color = ColorProvider.getColor(R.color.black)
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textSize = 20f
            textAlign = Paint.Align.CENTER
        }

        val lineHeightAmplifier = if (locale == "en") 1.2f else 1.4f

        currentY += 20f
        canvas.drawText(StringProvider.getString(R.string.pdf_overall_result_title), pageWidth / 2, currentY, mainTitlePaint)
        currentY += mainTitlePaint.textSize + 20f

        val infoStartX = offsetX + marginHorizontal
        val infoSpacing = 100f

        val currentDate = LocalDate.now()
        val englishFormatter =
            if (locale == "kr") DateTimeFormatter.ofPattern("yyyy년 M월 d일", Locale.KOREA)
            else DateTimeFormatter.ofPattern("MMMM dd, yyyy", Locale.US)
        val dateString = currentDate.format(englishFormatter)

        canvas.drawText(StringProvider.getString(R.string.pdf_label_name) + "   ${userData?.name}", infoStartX, currentY, infoPaint)
        canvas.drawText(StringProvider.getString(R.string.pdf_label_date) + "   ${dateString}", infoStartX + infoSpacing * 2, currentY, infoPaint)
        currentY += infoPaint.textSize + 5f

        val logoBitmap: Bitmap? = BitmapFactory.decodeResource(context.resources,
            if (locale == "ko") R.drawable.nenoon_logo_v2_invisible
            else R.drawable.nenoon_logo_v2_invisible_en
        )
        if (logoBitmap != null) {
            val logoPaint = Paint().apply {
                alpha = 30
            }
            val logoScale = 0.20f
            val scaledLogoWidth = logoBitmap.width * logoScale
            val scaledLogoHeight = logoBitmap.height * logoScale

            val logoX = (pageWidth - scaledLogoWidth) / 2
            val logoY = (pageHeight - scaledLogoHeight) / 2

            canvas.drawBitmap(
                Bitmap.createScaledBitmap(logoBitmap, scaledLogoWidth.toInt(), scaledLogoHeight.toInt(), true),
                logoX,
                logoY,
                logoPaint
            )
        }

        qrCodeBitmap?.let { qr ->
            val qrCodeSize = 60
            val qrCodeMarginTop = marginVertical - 12f
            val qrCodeMarginRight = marginHorizontal - 12f

            val scaledQrCode = qr.scale(qrCodeSize, qrCodeSize, false)
            val qrCodeX = (pageWidth - qrCodeMarginRight - scaledQrCode.width)
            val qrCodeY = qrCodeMarginTop

            canvas.drawBitmap(scaledQrCode, qrCodeX, qrCodeY, null)

            val qrLabel = StringProvider.getString(R.string.pdf_qr_code_label)
            val qrLabelPaint = Paint().apply {
                color = Color.BLACK
                textSize = 10f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                textAlign = Paint.Align.CENTER
            }
            val qrLabelX = qrCodeX + scaledQrCode.width / 2f
            val qrLabelY = qrCodeY + scaledQrCode.height + 12f
            canvas.drawText(qrLabel, qrLabelX, qrLabelY, qrLabelPaint)
        }

        currentY += 5f

        val col1Width = contentWidth * 0.18f
        val col2Width = contentWidth * 0.25f
        val col3Width = contentWidth * 0.30f
        val col4Width = contentWidth * 0.27f

        val columnStarts = floatArrayOf(
            offsetX + marginHorizontal,
            offsetX + marginHorizontal + col1Width + columnGap,
            offsetX + marginHorizontal + col1Width + columnGap + col2Width + columnGap,
            offsetX + marginHorizontal + col1Width + columnGap + col2Width + columnGap + col3Width + columnGap
        )

        // Draw the latest test result
        val latestResult = contentToPrint.firstOrNull()
        if (latestResult != null) {
            val visualAcuityData = listOf(
                Triple(StringProvider.getString(R.string.pdf_va_range_normal), StringProvider.getString(R.string.pdf_va_interp_normal), StringProvider.getString(R.string.pdf_va_recomm_normal)),
                Triple(StringProvider.getString(R.string.pdf_va_range_mild), StringProvider.getString(R.string.pdf_va_interp_mild), StringProvider.getString(R.string.pdf_va_recomm_mild)),
                Triple(StringProvider.getString(R.string.pdf_va_range_moderate), StringProvider.getString(R.string.pdf_va_interp_moderate), StringProvider.getString(R.string.pdf_va_recomm_moderate))
            )
            currentY = drawResultRow(
                canvas,
                StringProvider.getString(R.string.pdf_visual_acuity_title),
                if (latestResult.shortVisualAcuityTestResult != null) {
                    "${StringProvider.getString(R.string.pdf_left_eye)}: ${latestResult.shortVisualAcuityTestResult.leftEye}\n${StringProvider.getString(R.string.pdf_right_eye)}: ${latestResult.shortVisualAcuityTestResult.rightEye}"
                } else {
                    StringProvider.getString(R.string.pdf_test_not_conducted)
                },
                visualAcuityData,
                columnStarts,
                col1Width, col2Width, col3Width, col4Width,
                currentY,
                titlePaint, textPaint, notTestedPaint, linePaint,
                tipText = null,
                context,
                lineHeightAmplifier = lineHeightAmplifier
            )

            // Other test results
            val bloodPressureData = listOf(
                Triple(StringProvider.getString(R.string.pdf_bp_range_normal), StringProvider.getString(R.string.pdf_bp_interp_normal), StringProvider.getString(R.string.pdf_bp_recomm_normal)),
                Triple(StringProvider.getString(R.string.pdf_bp_range_prehypertension), StringProvider.getString(R.string.pdf_bp_interp_prehypertension), StringProvider.getString(R.string.pdf_bp_recomm_prehypertension)),
                Triple(StringProvider.getString(R.string.pdf_bp_range_hypertension), StringProvider.getString(R.string.pdf_bp_interp_hypertension), StringProvider.getString(R.string.pdf_bp_recomm_hypertension))
            )

            currentY = drawResultRow(
                canvas,
                StringProvider.getString(R.string.pdf_blood_pressure_title),
                if (latestResult.bloodPressureTestResult != null) {
                    "${StringProvider.getString(R.string.pdf_systolic)}: ${latestResult.bloodPressureTestResult.systolic}mmHg\n${StringProvider.getString(R.string.pdf_diastolic)}: ${latestResult.bloodPressureTestResult.diastolic}mmHg\n${StringProvider.getString(R.string.pdf_pulse_rate)}: ${latestResult.bloodPressureTestResult.pulseRate}bpm"
                } else {
                    StringProvider.getString(R.string.pdf_test_not_conducted)
                },
                bloodPressureData,
                columnStarts,
                col1Width, col2Width, col3Width, col4Width,
                currentY,
                titlePaint, textPaint, notTestedPaint, linePaint,
                tipText = StringProvider.getString(R.string.pdf_bp_tip),
                context,
                lineHeightAmplifier = lineHeightAmplifier
            )

            val gripStrengthData = listOf(
                Triple(StringProvider.getString(R.string.pdf_gs_range_normal), StringProvider.getString(R.string.pdf_gs_interp_normal), StringProvider.getString(R.string.pdf_gs_recomm_normal)),
                Triple(StringProvider.getString(R.string.pdf_gs_range_mild), StringProvider.getString(R.string.pdf_gs_interp_mild), StringProvider.getString(R.string.pdf_gs_recomm_mild)),
                Triple(StringProvider.getString(R.string.pdf_gs_range_moderate), StringProvider.getString(R.string.pdf_gs_interp_moderate), StringProvider.getString(R.string.pdf_gs_recomm_moderate))
            )
            currentY = drawResultRow(
                canvas,
                StringProvider.getString(R.string.pdf_grip_strength_title),
                if (latestResult.gripStrengthTestResult != null) {
                    "${StringProvider.getString(R.string.pdf_left_hand)}: ${String.format("%.1f", latestResult.gripStrengthTestResult.leftGrip)}kg\n${StringProvider.getString(R.string.pdf_right_hand)}: ${String.format("%.1f", latestResult.gripStrengthTestResult.rightGrip)}kg"
                } else {
                    StringProvider.getString(R.string.pdf_test_not_conducted)
                },
                gripStrengthData,
                columnStarts,
                col1Width, col2Width, col3Width, col4Width,
                currentY,
                titlePaint, textPaint, notTestedPaint, linePaint,
                tipText = StringProvider.getString(R.string.pdf_gs_tip),
                context,
                lineHeightAmplifier = lineHeightAmplifier
            )

            val pulmonaryFunctionData = listOf(
                Triple(StringProvider.getString(R.string.pdf_pf_range_normal), StringProvider.getString(R.string.pdf_pf_interp_normal), StringProvider.getString(R.string.pdf_pf_recomm_normal)),
                Triple(StringProvider.getString(R.string.pdf_pf_range_mild), StringProvider.getString(R.string.pdf_pf_interp_mild), StringProvider.getString(R.string.pdf_pf_recomm_mild)),
                Triple(StringProvider.getString(R.string.pdf_pf_range_moderate), StringProvider.getString(R.string.pdf_pf_interp_moderate), StringProvider.getString(R.string.pdf_pf_recomm_moderate))
            )
            currentY = drawResultRow(
                canvas,
                StringProvider.getString(R.string.pdf_pulmonary_age_title),
                if (latestResult.pulmonaryFunctionTestResult != null) {
                    "${latestResult.pulmonaryFunctionTestResult.pulmonaryAge} ${StringProvider.getString(R.string.pdf_age_unit)}"
                } else {
                    StringProvider.getString(R.string.pdf_test_not_conducted)
                },
                pulmonaryFunctionData,
                columnStarts,
                col1Width, col2Width, col3Width, col4Width,
                currentY,
                titlePaint, textPaint, notTestedPaint, linePaint,
                tipText = StringProvider.getString(R.string.pdf_pf_tip),
                context,
                lineHeightAmplifier = lineHeightAmplifier
            )

            val macularDegenerationData = listOf(
                Triple(StringProvider.getString(R.string.pdf_md_range_normal), StringProvider.getString(R.string.pdf_md_interp_normal), StringProvider.getString(R.string.pdf_md_recomm_normal)),
                Triple(StringProvider.getString(R.string.pdf_md_range_mild), StringProvider.getString(R.string.pdf_md_interp_mild), StringProvider.getString(R.string.pdf_md_recomm_mild)),
                Triple(StringProvider.getString(R.string.pdf_md_range_moderate), StringProvider.getString(R.string.pdf_md_interp_moderate), StringProvider.getString(R.string.pdf_md_recomm_moderate))
            )
            currentY = drawResultRow(
                canvas,
                StringProvider.getString(R.string.pdf_macular_degeneration_title),
                if (latestResult.amslerGridTestResult != null) {
                    val hasDisorder = latestResult.amslerGridTestResult.leftEyeDisorderType.any { it != MacularDisorderType.Normal } ||
                            latestResult.amslerGridTestResult.rightEyeDisorderType.any { it != MacularDisorderType.Normal }
                    if (hasDisorder) StringProvider.getString(R.string.pdf_md_present) else StringProvider.getString(R.string.pdf_md_absent)
                } else {
                    StringProvider.getString(R.string.pdf_test_not_conducted)
                },
                macularDegenerationData,
                columnStarts,
                col1Width, col2Width, col3Width, col4Width,
                currentY,
                titlePaint, textPaint, notTestedPaint, linePaint,
                tipText = StringProvider.getString(R.string.pdf_md_tip),
                context,
                true,
                lineHeightAmplifier = lineHeightAmplifier
            )
        }

        // Draw the past three results section at the bottom
        if (contentToPrint.size > 1) {
            currentY += 6f
            drawPastResultsSummary(canvas, contentToPrint.subList(1, minOf(contentToPrint.size, 4)), pageWidth, currentY, context, locale)
        }
    }

    private fun drawPastResultsSummary(
        canvas: Canvas,
        pastResults: List<CompoundTestResult>,
        pageWidth: Float,
        startY: Float,
        context: Context,
        locale: String?,
    ) {
        var currentY = startY
        val marginHorizontal = 20f
        val columnGap = 2f

        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        val textPaint = Paint().apply {
            color = Color.BLACK
            textSize = 8f
        }
        val headerPaint = Paint(textPaint).apply {
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        val linePaint = Paint().apply {
            color = Color.DKGRAY
            strokeWidth = 0.5f
        }
        val lineHeight = textPaint.textSize * 1.2f

        // Draw headers with new adjusted widths for values
        val headerY = currentY + lineHeight
        var xOffset = marginHorizontal
        val dateWidth = 60f
        val vaWidth = 50f
        val bpWidth = 80f
        val gsWidth = 70f
        val pfWidth = 70f
        val presbyopiaWidth = 50f
        val amslerWidth = 90f
        val mchartsWidth = 80f

        canvas.drawText(StringProvider.getString(R.string.pdf_date_header), xOffset, headerY, headerPaint)
        xOffset += dateWidth + columnGap
        canvas.drawText(StringProvider.getString(R.string.pdf_va_short), xOffset, headerY, headerPaint)
        xOffset += vaWidth + columnGap
        canvas.drawText(StringProvider.getString(R.string.pdf_bp_short), xOffset, headerY, headerPaint)
        xOffset += bpWidth + columnGap
        canvas.drawText(StringProvider.getString(R.string.pdf_gs_short), xOffset, headerY, headerPaint)
        xOffset += gsWidth + columnGap
        canvas.drawText(StringProvider.getString(R.string.pdf_pf_short), xOffset, headerY, headerPaint)
        xOffset += pfWidth + columnGap
        canvas.drawText(StringProvider.getString(R.string.pdf_presbyopia_short), xOffset, headerY, headerPaint)
        xOffset += presbyopiaWidth + columnGap
        canvas.drawText(StringProvider.getString(R.string.pdf_amsler_short), xOffset, headerY, headerPaint)
        xOffset += amslerWidth + columnGap
        canvas.drawText(StringProvider.getString(R.string.pdf_mcharts_short), xOffset, headerY, headerPaint)

        currentY += lineHeight*2
        canvas.drawLine(marginHorizontal, currentY, pageWidth - marginHorizontal, currentY, linePaint)
        currentY += lineHeight

        // Draw values for each past result
        for (result in pastResults) {
            val formattedDate = result.createAt?.let {
                try {
                    dateFormat.format(SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).parse(it))
                } catch (e: Exception) {
                    it.substringBefore("T")
                }
            } ?: "N/A"

            xOffset = marginHorizontal
            canvas.drawText(formattedDate, xOffset, currentY, textPaint)
            xOffset += dateWidth + columnGap

            val vaText = result.shortVisualAcuityTestResult?.let { "L: ${it.leftEye} /  R: ${it.rightEye}" } ?: "--"
            canvas.drawText(vaText, xOffset, currentY, textPaint)
            xOffset += vaWidth + columnGap

            val bpText = result.bloodPressureTestResult?.let { "${it.systolic} / ${it.diastolic} (${it.pulseRate}bpm)" } ?: "--"
            canvas.drawText(bpText, xOffset, currentY, textPaint)
            xOffset += bpWidth + columnGap

            val gsText = result.gripStrengthTestResult?.let { "L: ${it.leftGrip} / R: ${it.rightGrip}" } ?: "--"
            canvas.drawText(gsText, xOffset, currentY, textPaint)
            xOffset += gsWidth + columnGap

            val pfText = result.pulmonaryFunctionTestResult?.let { StringProvider.getString(R.string.pdf_label_age) + "${it.pulmonaryAge}" } ?: "--"
            canvas.drawText(pfText, xOffset, currentY, textPaint)
            xOffset += pfWidth + columnGap

            val presbyopiaText = result.presbyopiaTestResult?.let { "${it.avgDistance}cm" } ?: "--"
            canvas.drawText(presbyopiaText, xOffset, currentY, textPaint)
            xOffset += presbyopiaWidth + columnGap

            // For complex results like Amsler and M-Charts, provide a summary string
            val amslerText = result.amslerGridTestResult?.let {
                val left = if (it.leftEyeDisorderType.any { type -> type == MacularDisorderType.Normal }) StringProvider.getString(R.string.pdf_normal) else StringProvider.getString(R.string.pdf_abnormal)
                val right = if (it.rightEyeDisorderType.any { type -> type == MacularDisorderType.Normal }) StringProvider.getString(R.string.pdf_normal) else StringProvider.getString(R.string.pdf_abnormal)
                "L: $left / R: $right"
            } ?: "--"
            canvas.drawText(amslerText, xOffset, currentY, textPaint)
            xOffset += amslerWidth + columnGap

            val mChartText = result.mChartTestResult?.let { "L:${it.leftEyeVertical}, ${it.leftEyeHorizontal} / R:${it.rightEyeVertical}, ${it.rightEyeHorizontal}" } ?: "--"
            canvas.drawText(mChartText, xOffset, currentY, textPaint)

            currentY += lineHeight/2
            canvas.drawLine(marginHorizontal, currentY, pageWidth - marginHorizontal, currentY, linePaint)
            currentY += lineHeight
        }
    }

    private fun drawResultRow(
        canvas: Canvas,
        title: String,
        result: String,
        data: List<Triple<String, String, String>>,
        columnStarts: FloatArray,
        col1Width: Float, col2Width: Float, col3Width: Float, col4Width: Float,
        startY: Float,
        titlePaint: Paint, textPaint: Paint, notTestedPaint: Paint, linePaint: Paint,
        tipText: String?,
        context: Context,
        drawLineAtBottom: Boolean = false,
        lineHeightAmplifier: Float = 1.4f,
    ): Float {
        var currentY = startY
        val lineHeight = textPaint.textSize * lineHeightAmplifier
        val padding = 5f
        val columnGap = 8f

        // Draw the top line of the row box
        canvas.drawLine(columnStarts[0], currentY, columnStarts[0] + col1Width + col2Width + col3Width + col4Width + 3 * 8f + padding, currentY, linePaint)

        currentY += padding

        // Draw the title
        val titleLines = splitTextIntoLines(title, titlePaint, col1Width)
        var titleLineY = currentY + titlePaint.textSize
        for (line in titleLines) {
            canvas.drawText(line, columnStarts[0], titleLineY, titlePaint)
            titleLineY += lineHeight
        }

        // Draw the result text below the title
        val resultLines = splitTextIntoLines(result, textPaint, col1Width)
        var resultLineY = titleLineY + padding
        for (line in resultLines) {
            canvas.drawText(line, columnStarts[0], resultLineY, if (result == StringProvider.getString(R.string.pdf_test_not_conducted)) notTestedPaint else textPaint)
            resultLineY += lineHeight
        }

        // Calculate the maximum height of the title and result section
        val resultSectionHeight = (titleLines.size * lineHeight) + padding + (resultLines.size * lineHeight)
        val dataColumnStartX = columnStarts[1] // Start data columns after the title/result block
        val dataColWidths = floatArrayOf(
            col2Width, // range
            col3Width, // interpretation
            col4Width  // recommendation
        )

        val headerTextPaint = Paint(textPaint).apply {
            textSize = textPaint.textSize - 1f
        }

        val dataTextPaint = Paint(textPaint).apply {
            textSize = textPaint.textSize - 1f
        }

        // Start drawing the data columns at the same vertical position as the title
        val dataHeaderY = currentY + headerTextPaint.textSize
        canvas.drawText(StringProvider.getString(R.string.pdf_range_header), dataColumnStartX, dataHeaderY, headerTextPaint)
        canvas.drawText(StringProvider.getString(R.string.pdf_interpretation_header), dataColumnStartX + dataColWidths[0] + columnGap, dataHeaderY, headerTextPaint)
        canvas.drawText(StringProvider.getString(R.string.pdf_recommendation_header), dataColumnStartX + dataColWidths[0] + dataColWidths[1] + 2 * columnGap, dataHeaderY, headerTextPaint)

        var dataSectionY = dataHeaderY + lineHeight
        val grayBackgroundPaint = Paint().apply {
            color = Color.argb(0.1f, 0f, 0f, 0f)
        }

        for (i in data.indices) {
            val item = data[i]
            val rowStartY = dataSectionY - lineHeight + padding // Adjust for header spacing

            val rangeLines = splitTextIntoLines(item.first, dataTextPaint, dataColWidths[0])
            val interpLines = splitTextIntoLines(item.second, dataTextPaint, dataColWidths[1])
            val recommLines = splitTextIntoLines(item.third, dataTextPaint, dataColWidths[2])

            val maxLines = maxOf(rangeLines.size, interpLines.size, recommLines.size)
            val currentRowHeight = maxLines * lineHeight

            if (i % 2 == 0) {
                canvas.drawRect(
                    dataColumnStartX - padding,
                    rowStartY,
                    dataColumnStartX + dataColWidths.sum() + 2 * columnGap + padding,
                    rowStartY + currentRowHeight,
                    grayBackgroundPaint
                )
            }

            for (j in 0 until maxLines) {
                val yOffset = rowStartY + dataTextPaint.textSize + j * lineHeight
                if (j < rangeLines.size) {
                    canvas.drawText(rangeLines[j], dataColumnStartX, yOffset, dataTextPaint)
                }
                if (j < interpLines.size) {
                    canvas.drawText(interpLines[j], dataColumnStartX + dataColWidths[0] + columnGap, yOffset, dataTextPaint)
                }
                if (j < recommLines.size) {
                    canvas.drawText(recommLines[j], dataColumnStartX + dataColWidths[0] + dataColWidths[1] + 2 * columnGap, yOffset, dataTextPaint)
                }
            }
            dataSectionY += currentRowHeight
        }

        // Find the greater height between the title/result block and the data block
        val finalY = maxOf(resultLineY, dataSectionY) + if (drawLineAtBottom) padding else 0f

        // Draw the bottom line of the row box
        if (drawLineAtBottom) {
            canvas.drawLine(
                columnStarts[0],
                finalY,
                columnStarts[0] + col1Width + col2Width + col3Width + col4Width + 3 * 8f + padding,
                finalY,
                linePaint
            )
        }

        return finalY
    }

    private fun splitTextIntoLines(text: String, paint: Paint, maxWidth: Float): List<String> {
        val lines = mutableListOf<String>()
        if (text.isEmpty()) {
            return lines
        }

        val words = text.split(" ")
        var currentLine = StringBuilder()

        for (word in words) {
            val parts = word.split("\n")
            for ((index, part) in parts.withIndex()) {
                if (index > 0) {
                    if (currentLine.isNotEmpty()) {
                        lines.add(currentLine.toString())
                        currentLine.clear()
                    }
                }

                val testLine = if (currentLine.isEmpty()) part else "$currentLine $part"
                val width = paint.measureText(testLine)

                if (width <= maxWidth) {
                    currentLine = if (currentLine.isEmpty()) StringBuilder(part) else currentLine.append(" ").append(part)
                } else {
                    if (currentLine.isEmpty()) {
                        var tempWord = part
                        while (paint.measureText(tempWord) > maxWidth) {
                            var splitIndex = tempWord.length - 1
                            while (splitIndex > 0 && paint.measureText(tempWord.substring(0, splitIndex)) > maxWidth) {
                                splitIndex--
                            }
                            lines.add(tempWord.substring(0, splitIndex))
                            tempWord = tempWord.substring(splitIndex)
                        }
                        currentLine = StringBuilder(tempWord)
                    } else {
                        lines.add(currentLine.toString())
                        currentLine = StringBuilder(part)
                    }
                }
            }
        }
        if (currentLine.isNotEmpty()) {
            lines.add(currentLine.toString())
        }
        return lines.filter { it.isNotEmpty() }
    }
}