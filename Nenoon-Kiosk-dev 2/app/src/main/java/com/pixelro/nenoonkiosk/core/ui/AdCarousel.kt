package com.pixelro.nenoonkiosk.core.ui

import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.harang.data.db.entity.AdImageEntity
import kotlinx.coroutines.delay

/**
 * 광고 자동 슬라이드 캐러셀
 * @param adImages 표시할 광고 이미지 리스트
 * @param autoScrollInterval 자동 스크롤 간격 (밀리초)
 * @param onPageChange 페이지 변경 시 콜백 (옵션)
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AdCarousel(
    adImages: List<AdImageEntity> = emptyList(),
    modifier: Modifier = Modifier,
    autoScrollInterval: Long = 5000L,
    onPageChange: (() -> Unit)? = null
) {

    // 광고 개수에 맞는 initialPage 계산 (양방향 스크롤을 위해 중간 지점에서 시작)
    val initialPage = remember(adImages.size) {
        if (adImages.isEmpty()) 0
        else {
            val baseNumber = Int.MAX_VALUE / 2
            (baseNumber / adImages.size) * adImages.size
        }
    }

    // PagerState 생성 (광고 개수가 변경되면 재생성)
    val pagerState = key(adImages.size) {
        rememberPagerState(
            initialPage = initialPage,
            initialPageOffsetFraction = 0f,
            pageCount = { Int.MAX_VALUE }
        )
    }

    // 자동 스크롤
    LaunchedEffect(adImages.size) {
        if (adImages.isEmpty()) return@LaunchedEffect

        while (true) {
            delay(autoScrollInterval)
            pagerState.animateScrollToPage(
                page = pagerState.currentPage + 1,
                animationSpec = tween(1000)
            )
            onPageChange?.invoke()
        }
    }

    // HorizontalPager
    if (adImages.isNotEmpty()) {
        HorizontalPager(
            contentPadding = PaddingValues(0.dp),
            state = pagerState,
            modifier = modifier
        ) { page ->
            Advertisement(adImages[page % adImages.size])
        }
    }
}