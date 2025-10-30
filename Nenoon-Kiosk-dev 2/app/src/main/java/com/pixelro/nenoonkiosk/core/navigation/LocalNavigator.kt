package com.pixelro.nenoonkiosk.core.navigation

import androidx.compose.runtime.compositionLocalOf

val LocalNavigator = compositionLocalOf<Navigator> {
    error("No Navigator provided")
}
