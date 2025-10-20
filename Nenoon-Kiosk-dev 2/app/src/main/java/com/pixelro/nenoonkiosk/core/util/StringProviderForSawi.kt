package com.pixelro.nenoonkiosk.core.util

import com.pixelro.nenoonkiosk.app.NenoonKioskApplication

object StringProviderForSawi {
    fun getString(
        id: Int,
        vararg formatArgs: Any?
    ): String {
        val context = NenoonKioskApplication.Companion.applicationContext()
        val configuration = context.resources.configuration
        return context.createConfigurationContext(configuration).getString(id, *formatArgs)
    }
}