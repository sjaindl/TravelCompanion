package com.sjaindl.travelcompanion.explore.details.bottomnav

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Photo
import androidx.compose.material.icons.rounded.Place
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.sjaindl.travelcompanion.navigation.NamedDestinationItem
import com.sjaindl.travelcompanion.R

// https://medium.com/geekculture/bottom-navigation-in-jetpack-compose-android-9cd232a8b16

sealed class BottomNavItem : NamedDestinationItem {
    companion object {
        const val PIN_ARG = "pin"
        const val PICKER_MODE = "pickerMode"

        private val pinArgs = listOf(navArgument(PIN_ARG) {
            type = NavType.LongType
        })
    }

    data object ExploreDetailHome : BottomNavItem() {
        override var titleRes = R.string.detail
        override var icon = Icons.Rounded.Place
        override var route = "exploreDetailsHome"
        override var arguments = pinArgs
        override var routeWithArgs = "$route/{$PIN_ARG}"
        override fun routeWithSetArguments(vararg arguments: Any): String {
            val pinId = arguments.firstOrNull() as? Long ?: return route

            return "$route/$pinId"
        }
    }

    data object ExploreDetailPhotos : BottomNavItem() {
        override var titleRes = R.string.photos
        override var icon = Icons.Rounded.Photo
        override var route = "exploreDetailsPhoto"
        override var arguments = listOf(
            navArgument(PIN_ARG) {
                type = NavType.LongType
            },
            navArgument(PICKER_MODE) {
                type = NavType.BoolType
                defaultValue = false
            },
        )
        override var routeWithArgs = "$route/{$PIN_ARG}/{$PICKER_MODE}"
        override fun routeWithSetArguments(vararg arguments: Any): String {
            if (arguments.size < 2) return route
            val pinId = arguments.firstOrNull() as? Long ?: return route
            val pickerMode = arguments[1] as? Boolean ?: return route

            return "$route/$pinId/$pickerMode"
        }
    }

    data object ExploreDetailInfo : BottomNavItem() {
        override var titleRes = R.string.info
        override var icon = Icons.Rounded.Info
        override var route = "exploreDetailsInfo"
        override var arguments = pinArgs
        override var routeWithArgs = "$route/{$PIN_ARG}"
        override fun routeWithSetArguments(vararg arguments: Any): String {
            val pinId = arguments.firstOrNull() as? Long ?: return route

            return "$route/$pinId"
        }
    }
}
