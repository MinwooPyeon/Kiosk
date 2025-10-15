package com.pixelro.nenoonkiosk.data

import com.pixelro.nenoonkiosk.NenoonKioskApplication

object StringProviderForSawi {
    fun getString(
        id: Int,
        vararg formatArgs: Any?
    ): String {
        val context = NenoonKioskApplication.applicationContext()
        val configuration = context.resources.configuration
        return context.createConfigurationContext(configuration).getString(id, *formatArgs)
    }
}
