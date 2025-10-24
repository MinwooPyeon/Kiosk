package com.pixelro.nenoonkiosk.core.navigation.internal.navigator

import androidx.navigation3.runtime.NavKey
import com.pixelro.nenoonkiosk.core.navigation.Navigator
import dagger.hilt.android.scopes.ActivityRetainedScoped
import jakarta.inject.Inject
import kotlinx.coroutines.channels.Channel

@ActivityRetainedScoped
class NavigatorImpl @Inject constructor() : Navigator, InternalNavigator {
    override val channel = Channel<InternalRoute>(Channel.BUFFERED)

    override suspend fun navigate(
        route: NavKey,
        saveState: Boolean,
        launchSingleTop: Boolean
    ) {
        channel.send(
            InternalRoute.Navigate(
                route = route,
                saveState = saveState,
                launchSingleTop = launchSingleTop
            ),
        )
    }

    override suspend fun navigateBack() {
        channel.send(InternalRoute.NavigateBack)
    }

    override suspend fun navigateAndClearBackStack(route: NavKey) {
        channel.send(
            InternalRoute.NavigateAndClearBackStack(route = route)
        )
    }
}
