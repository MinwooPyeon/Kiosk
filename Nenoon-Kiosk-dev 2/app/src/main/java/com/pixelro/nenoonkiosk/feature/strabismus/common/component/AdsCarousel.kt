package com.pixelro.nenoonkiosk.feature.strabismus.common.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.pixelro.nenoonkiosk.core.ui.Advertisement

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AdsCarousel(
    isSenior: Boolean,
    pagerState: androidx.compose.foundation.pager.PagerState
) {
    if (isSenior) return

    HorizontalPager(
        contentPadding = PaddingValues(start = 40.dp, top = 20.dp, end = 40.dp, bottom = 20.dp),
        pageSpacing = 40.dp,
        state = pagerState
    ) { page ->
        Advertisement(page)
    }
}