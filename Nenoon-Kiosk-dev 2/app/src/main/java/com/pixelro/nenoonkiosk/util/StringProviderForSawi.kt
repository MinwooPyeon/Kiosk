package com.pixelro.nenoonkiosk.util

import com.pixelro.nenoonkiosk.NenoonKioskApplication

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