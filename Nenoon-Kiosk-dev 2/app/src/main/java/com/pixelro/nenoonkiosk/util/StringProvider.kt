package com.pixelro.nenoonkiosk.util

import com.pixelro.nenoonkiosk.NenoonKioskApplication

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