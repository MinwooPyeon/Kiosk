package com.pixelro.nenoonkiosk.core.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.pixelro.nenoonkiosk.app.NenoonKioskApplication

object StringProvider {
    // 기존 방식 - ViewModel이나 일반 코드에서 사용
    fun getString(
        id: Int,
        vararg formatArgs: Any?,
    ): String {
        val context = NenoonKioskApplication.Companion.applicationContext()
        val configuration = context.resources.configuration
        return context.createConfigurationContext(configuration).getString(id, *formatArgs)
    }

    // Composable 전용 - Preview에서도 작동
    @Composable
    fun getStringComposable(
        id: Int,
        vararg formatArgs: Any?,
    ): String {
        val context = LocalContext.current
        return context.getString(id, *formatArgs)
    }
}
