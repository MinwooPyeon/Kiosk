package com.pixelro.nenoonkiosk.feature.strabismus.common.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.harang.data.db.entity.AdImageEntity
import com.pixelro.nenoonkiosk.core.ui.Advertisement

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AdsCarousel(
    adImages: List<AdImageEntity>,
    isSenior: Boolean,
    pagerState: PagerState
) {
    if (isSenior || adImages.isEmpty()) return

    HorizontalPager(
        contentPadding = PaddingValues(start = 40.dp, top = 20.dp, end = 40.dp, bottom = 20.dp),
        pageSpacing = 40.dp,
        state = pagerState
    ) { page ->
        Advertisement(adImages[page % adImages.size])
    }
}