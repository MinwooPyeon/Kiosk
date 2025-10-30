package com.pixelro.nenoonkiosk.feature.main

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation3.runtime.NavBackStack
import com.pixelro.nenoonkiosk.core.navigation.LocalNavigator
import com.pixelro.nenoonkiosk.core.navigation.Route
import ke.co.banit.idle_detector_compose.LocalIdleDetectorState
import kotlinx.coroutines.launch

/**
 * Idle 상태를 감지하여 ScreenSaver로 네비게이션하는 핸들러
 *
 * @param navBackStack 현재 네비게이션 백스택
 */
@Composable
fun IdleScreenSaverHandler(
    navBackStack: NavBackStack
) {
    val isIdle by LocalIdleDetectorState.current
    val currentRoute = navBackStack.lastOrNull()
    val navigator = LocalNavigator.current
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(isIdle) {
        if (isIdle && currentRoute !is Route.ScreenSaver) {
            // 비활성 상태이고 ScreenSaver가 아닌 경우 이동
            // Navigator의 메서드는 suspend 함수이므로 코루틴 스코프에서 실행
            coroutineScope.launch {
                navigator.navigate(Route.ScreenSaver)
            }
        }
    }
}
