package com.sjaindl.travelcompanion.explore.details.bottomnav

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Photo
import androidx.compose.material.icons.rounded.Place
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.sjaindl.travelcompanion.R
import com.sjaindl.travelcompanion.navigation.NamedDestinationItem

// https://medium.com/geekculture/bottom-navigation-in-jetpack-compose-android-9cd232a8b16

sealed class BottomNavItem : NamedDestinationItem {
    companion object {
        const val pinArg = "pin"
        const val pickerMode = "pickerMode"

        private val pinArgs = listOf(navArgument(pinArg) {
            type = NavType.LongType
            // defaultValue = 0
        })

        private val photoDetailArgs = listOf(
            navArgument(pinArg) {
                type = NavType.LongType
                // defaultValue = 0
            },
            navArgument(pickerMode) {
                type = NavType.BoolType
                defaultValue = false
            },
        )

        private const val exploreDetailsHomeRoute = "exploreDetailsHome"
        private const val exploreDetailsPhotosRoute = "exploreDetailsPhoto"
        private const val exploreDetailsInfoRoute = "exploreDetailsInfo"
    }

    data class ExploreDetailHome(
        override var titleRes: Int = R.string.detail,
        override var icon: ImageVector = Icons.Rounded.Place,
        override var route: String = exploreDetailsHomeRoute,
        override var arguments: List<NamedNavArgument> = pinArgs,
        override var routeWithArgs: String = "$route/{$pinArg}",
    ) : BottomNavItem() {
        override fun routeWithSetArguments(vararg arguments: Any): String {
            val pinId = arguments.firstOrNull() as? Long ?: return route

            return "$route/$pinId"
        }
    }

    data class ExploreDetailPhotos(
        override var titleRes: Int = R.string.photos,
        override var icon: ImageVector = Icons.Rounded.Photo,
        override var route: String = exploreDetailsPhotosRoute,
        override var arguments: List<NamedNavArgument> = photoDetailArgs,
        override var routeWithArgs: String = "$route/{$pinArg}/{$pickerMode}",
    ) : BottomNavItem() {
        override fun routeWithSetArguments(vararg arguments: Any): String {
            if (arguments.size < 2) return route
            val pinId = arguments.firstOrNull() as? Long ?: return route
            val pickerMode = arguments[1] as? Boolean ?: return route

            return "$route/$pinId/$pickerMode"
        }
    }

    data class ExploreDetailInfo(
        override var titleRes: Int = R.string.info,
        override var icon: ImageVector = Icons.Rounded.Info,
        override var route: String = exploreDetailsInfoRoute,
        override var arguments: List<NamedNavArgument> = pinArgs,
        override var routeWithArgs: String = "$route/{$pinArg}",
    ) : BottomNavItem() {
        override fun routeWithSetArguments(vararg arguments: Any): String {
            val pinId = arguments.firstOrNull() as? Long ?: return route

            return "$route/$pinId"
        }
    }
}
