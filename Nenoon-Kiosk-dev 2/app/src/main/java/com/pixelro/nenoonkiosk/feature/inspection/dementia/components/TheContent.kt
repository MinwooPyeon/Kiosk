package com.pixelro.nenoonkiosk.feature.inspection.dementia.components

import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.runtime.Composable
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun TheContent(mUrl: String) {
    AndroidView(
        factory = { ctx ->
            WebView(ctx).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                )
                webViewClient = WebViewClient()
                loadUrl(mUrl)
            }
        },
        update = { it.loadUrl(mUrl) }
    )
}