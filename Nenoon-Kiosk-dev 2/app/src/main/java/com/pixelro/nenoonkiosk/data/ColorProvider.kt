package com.pixelro.nenoonkiosk.data

import androidx.core.graphics.toColorInt
import com.pixelro.nenoonkiosk.NenoonKioskApplication
import com.pixelro.nenoonkiosk.R
import java.util.Locale

object ColorProvider {
    fun getColor(
        id: Int
    ): Int {
        return NenoonKioskApplication.applicationContext().
            createConfigurationContext(NenoonKioskApplication.applicationContext()
            .resources.configuration).getString(id).toColorInt()
    }
}