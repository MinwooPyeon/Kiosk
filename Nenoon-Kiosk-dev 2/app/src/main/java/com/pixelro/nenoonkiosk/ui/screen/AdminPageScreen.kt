package com.pixelro.nenoonkiosk.ui.screen

import android.content.Context
import android.graphics.Bitmap
import android.view.ViewGroup
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.pixelro.nenoonkiosk.NenoonKioskApplication
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.constants.AppConstants
import com.pixelro.nenoonkiosk.constants.NavConstants
import com.pixelro.nenoonkiosk.data.GlobalValue
import com.pixelro.nenoonkiosk.data.StringProvider
import com.pixelro.nenoonkiosk.ui.components.ProgressIndicator
import java.util.Locale

@Composable
fun AdminPageScreen(
    modifier: Modifier = Modifier,
    url: String,
    onBack: () -> Unit,
) {
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    val sharedPreferences = remember { context.getSharedPreferences(NavConstants.PREFERENCE_NAME, Context.MODE_PRIVATE) }
    val savedLanguage = sharedPreferences.getString("language", "defaultLanguage")
    val urlWithLocale = url + if (AppConstants.ADMIN_PAGE_LOCALE_PARAMETER.containsKey((savedLanguage))) {
        "?${AppConstants.ADMIN_PAGE_LOCALE_PARAMETER[savedLanguage]}"
    } else ""

    Column(
        modifier = modifier.fillMaxSize(),
    ) {
        Box(
            modifier = Modifier
                .padding(
                    start = 40.dp,
                    top = (GlobalValue.statusBarPadding + 20).dp,
                    end = 40.dp,
                    bottom = 20.dp
                )
                .fillMaxWidth()
                .height(40.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.CenterStart
            ) {
                Image(
                    modifier = Modifier
                        .width(32.dp)
                        .clickable { onBack() },
                    painter = painterResource(id = R.drawable.close_button_black),
                    contentDescription = ""
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = StringProvider.getString(R.string.admin_page),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(
            modifier = Modifier
                .padding(bottom = 5.dp, start = 5.dp, end = 5.dp)
                .fillMaxWidth()
                .height(1.dp)
                .background(
                    color = Color(0xff000000)
                )
        )

        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { webviewContext ->
                WebView(webviewContext).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    settings.javaScriptEnabled = true
                    settings.setSupportZoom(true)
                    settings.builtInZoomControls = true
                    settings.displayZoomControls = false

                    webViewClient = object : WebViewClient() {
                        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                            super.onPageStarted(view, url, favicon)
                            isLoading = true
                            errorMessage = null
                            // Re-apply the locale when the page starts loading
                            setAppLocale(Locale(savedLanguage ?: ""))
                        }

                        override fun onPageFinished(view: WebView?, url: String?) {
                            super.onPageFinished(view, url)
                            isLoading = false
                            // Re-apply the locale after the page has finished loading as a fallback
                            setAppLocale(Locale(savedLanguage ?: ""))
                        }

                        override fun onReceivedError(
                            view: WebView?,
                            request: WebResourceRequest?,
                            error: WebResourceError?
                        ) {
                            super.onReceivedError(view, request, error)
                            isLoading = false
                            errorMessage = StringProvider.getString(
                                R.string.admin_loading_error,
                                error?.description.toString() ?: "Unknown error"
                            )
                            // Re-apply the locale on error as well
                            setAppLocale(Locale(savedLanguage ?: ""))
                        }
                    }
                    loadUrl(urlWithLocale)
                }
            },
        )

        if (isLoading) {
            ProgressIndicator()
        }

        errorMessage?.let { message ->
            Text(text = message)
        }
    }
}

// Helper function to re-apply the locale.
private fun setAppLocale(locale: Locale) {
    NenoonKioskApplication.applicationContext().resources.configuration.setLocale(locale)
}
