package com.pixelro.nenoonkiosk.core.util

import androidx.core.graphics.toColorInt
import com.pixelro.nenoonkiosk.app.NenoonKioskApplication

object ColorProvider {
    fun getColor(
        id: Int
    ): Int {
        return NenoonKioskApplication.Companion.applicationContext().
            createConfigurationContext(
                NenoonKioskApplication.Companion.applicationContext()
            .resources.configuration).getString(id).toColorInt()
    }
}