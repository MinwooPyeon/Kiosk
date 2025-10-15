package com.pixelro.nenoonkiosk.data

import com.pixelro.nenoonkiosk.NenoonKioskApplication

object StringProvider {
    fun getString(
        id: Int,
        s: String? = ""
    ): String {
        return NenoonKioskApplication.applicationContext().
            createConfigurationContext(NenoonKioskApplication.applicationContext()
            .resources.configuration).getString(id, s)
    }
}