package com.pixelro.nenoonkiosk.core.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

sealed interface Route : NavKey {
    @Serializable
    data object Login : Route

    @Serializable
    data object SignUp : Route

    @Serializable
    data class BookDetail(val isbn: String? = null, val bookId: Long? = null) : Route

    @Serializable
    data class Celebration(val nickname: String) : Route

    @Serializable
    data object Setting : Route
}

sealed interface MyGlimsRoute : Route {

    @Serializable
    data object Liked : MyGlimsRoute

    @Serializable
    data object Upload : MyGlimsRoute
}

sealed interface UpdateInfoRoute : Route {
    @Serializable
    data object Personal : UpdateInfoRoute

    @Serializable
    data object Password : UpdateInfoRoute
}

sealed interface BottomTabRoute : NavKey {
    @Serializable
    data object Home : BottomTabRoute

    @Serializable
    data object Search : BottomTabRoute

    @Serializable
    data class Post(val bookId: Long = -1) : BottomTabRoute

    @Serializable
    data class Shorts(val quoteId: Long = -1) : BottomTabRoute

    @Serializable
    data object Profile : BottomTabRoute
}
