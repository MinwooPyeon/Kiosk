package com.pixelro.nenoonkiosk.feature.admin.advertisement

import com.harang.data.db.entity.LocationEntity
import com.pixelro.nenoonkiosk.R

/**
 * LocationEntity의 name을 기반으로 제목의 stringResource ID를 반환합니다.
 */
fun LocationEntity.getTitleRes(): Int {
    return when (name) {
        "TEST_LIST_SCREEN" -> R.string.admin_test_list_screen
        "SCREENSAVER" -> R.string.admin_screensaver
        else -> R.string.admin_test_list_screen
    }
}

/**
 * LocationEntity의 name을 기반으로 부제목의 stringResource ID를 반환합니다.
 */
fun LocationEntity.getSubtitleRes(): Int {
    return when (name) {
        "TEST_LIST_SCREEN" -> R.string.admin_test_list_subtitle
        "SCREENSAVER" -> R.string.admin_screensaver_subtitle
        else -> R.string.admin_test_list_subtitle
    }
}