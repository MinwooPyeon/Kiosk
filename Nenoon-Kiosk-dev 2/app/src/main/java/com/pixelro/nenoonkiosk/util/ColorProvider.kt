package com.pixelro.nenoonkiosk.util

import androidx.core.graphics.toColorInt
import com.pixelro.nenoonkiosk.NenoonKioskApplication

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