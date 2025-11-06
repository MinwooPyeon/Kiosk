package com.pixelro.nenoonkiosk.core.manager

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.util.Log
import com.mangoslab.nemonicsdk.NPrintInfo
import com.mangoslab.nemonicsdk.NPrinter
import com.mangoslab.nemonicsdk.constants.NPrinterType
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.util.StringProvider

/**
 * 사시 검사 결과 출력 헬퍼
 */
object StrabismusPrintHelper {

    private const val PAPER_WIDTH = 384
    private const val PADDING = 10f

    fun printPhoriaResult(
        context: Context,
        hTitle: String,
        hResult: String,
        hDesc: String,
        vTitle: String,
        vResult: String,
        vDesc: String
    ) {
        val bitmap =
            createPhoriaResultBitmap(context, hTitle, hResult, hDesc, vTitle, vResult, vDesc)
        printBitmap(bitmap)
    }

    fun printAniseikoniaResult(
        context: Context,
        retinalTitle: String,
//        retinalResult: String,
        retinalDescription: String,
        opinionTitle: String,
        opinionResult: String,
        opinionDescription: String
    ) {
        val bitmap = createAniseikoniaResultBitmap(
            context,
            retinalTitle,
            retinalDescription,
            opinionTitle,
            opinionResult,
            opinionDescription
        )
        printBitmap(bitmap)
    }

    private fun createPhoriaResultBitmap(
        context: Context,
        hTitle: String,
        hResult: String,
        hDesc: String,
        vTitle: String,
        vResult: String,
        vDesc: String
    ): Bitmap {
        val title = StringProvider.getString(R.string.sawi_result_title)
        val disclaimer = StringProvider.getString(R.string.sawi_result_general_disclaimer)

        val textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.BLACK
            textSize = 18f
        }

        val titlePaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.BLACK
            textSize = 24f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }

        val disclaimerPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.GRAY
            textSize = 16f
        }

        val resultText = """[$hTitle]
$hResult
$hDesc

[$vTitle]
$vResult
$vDesc
        """.trimIndent()

        val titleLayout = StaticLayout.Builder.obtain(
            title,
            0,
            title.length,
            titlePaint,
            PAPER_WIDTH - 2 * PADDING.toInt()
        )
            .setAlignment(Layout.Alignment.ALIGN_CENTER)
            .build()

        val resultLayout = StaticLayout.Builder.obtain(
            resultText,
            0,
            resultText.length,
            textPaint,
            PAPER_WIDTH - 2 * PADDING.toInt()
        )
            .build()

        val disclaimerLayout = StaticLayout.Builder.obtain(
            disclaimer,
            0,
            disclaimer.length,
            disclaimerPaint,
            PAPER_WIDTH - 2 * PADDING.toInt()
        )
            .build()

        val bitmapHeight = titleLayout.height + resultLayout.height + disclaimerLayout.height + 100
        val bitmap = Bitmap.createBitmap(PAPER_WIDTH, bitmapHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawColor(Color.WHITE)

        canvas.save()
        canvas.translate(PADDING, 20f)
        titleLayout.draw(canvas)
        canvas.restore()

        canvas.save()
        canvas.translate(PADDING, titleLayout.height + 40f)
        resultLayout.draw(canvas)
        canvas.restore()

        canvas.save()
        canvas.translate(PADDING, titleLayout.height + resultLayout.height + 60f)
        disclaimerLayout.draw(canvas)
        canvas.restore()

        return bitmap
    }

    private fun createAniseikoniaResultBitmap(
        context: Context,
        retinalTitle: String,
        retinalDescription: String,
        opinionTitle: String,
        opinionResult: String,
        opinionDescription: String
    ): Bitmap {
        val title = StringProvider.getString(R.string.fudo_result_title)
        val disclaimer = StringProvider.getString(R.string.sawi_result_general_disclaimer)

        val textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.BLACK
            textSize = 18f
        }

        val titlePaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.BLACK
            textSize = 24f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }

        val disclaimerPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.GRAY
            textSize = 16f
        }

        val resultText = """[$retinalTitle]
$retinalDescription

[$opinionTitle]
$opinionResult
$opinionDescription
        """.trimIndent()

        val titleLayout = StaticLayout.Builder.obtain(
            title,
            0,
            title.length,
            titlePaint,
            PAPER_WIDTH - 2 * PADDING.toInt()
        )
            .setAlignment(Layout.Alignment.ALIGN_CENTER)
            .build()

        val resultLayout = StaticLayout.Builder.obtain(
            resultText,
            0,
            resultText.length,
            textPaint,
            PAPER_WIDTH - 2 * PADDING.toInt()
        )
            .build()

        val disclaimerLayout = StaticLayout.Builder.obtain(
            disclaimer,
            0,
            disclaimer.length,
            disclaimerPaint,
            PAPER_WIDTH - 2 * PADDING.toInt()
        )
            .build()

        val bitmapHeight = titleLayout.height + resultLayout.height + disclaimerLayout.height + 100
        val bitmap = Bitmap.createBitmap(PAPER_WIDTH, bitmapHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawColor(Color.WHITE)

        canvas.save()
        canvas.translate(PADDING, 20f)
        titleLayout.draw(canvas)
        canvas.restore()

        canvas.save()
        canvas.translate(PADDING, titleLayout.height + 40f)
        resultLayout.draw(canvas)
        canvas.restore()

        canvas.save()
        canvas.translate(PADDING, titleLayout.height + resultLayout.height + 60f)
        disclaimerLayout.draw(canvas)
        canvas.restore()

        return bitmap
    }


    private fun printBitmap(bitmap: Bitmap) {
        val printerInfo = PrinterManager.getPrinterInfo()
        val printerType = printerInfo.first
        val printerMacAddress = printerInfo.second
        val nPrinterController = PrinterManager.getPrinterController()

        if (printerMacAddress.isEmpty() || nPrinterController == null) {
            Log.e("StrabismusPrintHelper", "Printer not configured or controller is null.")
            return
        }

        try {
            nPrinterController.print(
                NPrintInfo(
                    NPrinter(
                        printerType ?: NPrinterType.NEMONIC_MIP201,
                        "Printer",
                        printerMacAddress
                    ), bitmap
                ).apply {
                    copies = 1
                    isEnableDither = true
                })
            Log.d("StrabismusPrintHelper", "Print command sent successfully.")
        } catch (e: Exception) {
            Log.e("StrabismusPrintHelper", "Error during print command: ${'$'}e.message")
        }
    }
}