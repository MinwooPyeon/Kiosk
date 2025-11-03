package com.pixelro.nenoonkiosk.core.util

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration


/***
    세로이면 true, 가로이면 false
 ***/
@Composable
fun isPortrait(): Boolean =
    LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT