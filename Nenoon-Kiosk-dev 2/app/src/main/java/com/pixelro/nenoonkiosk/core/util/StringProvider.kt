package com.pixelro.nenoonkiosk.core.util

import com.pixelro.nenoonkiosk.app.NenoonKioskApplication

object StringProvider {
    fun getString(
        id: Int,
        s: String? = ""
    ): String {
        return NenoonKioskApplication.Companion.applicationContext().
            createConfigurationContext(
                NenoonKioskApplication.Companion.applicationContext()
            .resources.configuration).getString(id, s)
    }
}