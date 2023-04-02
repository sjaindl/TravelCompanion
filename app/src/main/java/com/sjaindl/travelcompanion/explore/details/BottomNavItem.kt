package com.sjaindl.travelcompanion.explore.details

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Photo
import androidx.compose.material.icons.rounded.Place
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.sjaindl.travelcompanion.R

// https://medium.com/geekculture/bottom-navigation-in-jetpack-compose-android-9cd232a8b16

interface DestinationItem {
    var titleRes: Int
    var icon: ImageVector
    var route: String
    var arguments: List<NamedNavArgument>
    val routeWithArgs: String

    fun routeWithSetArguments(vararg arguments: Any): String
}

sealed class BottomNavItem : DestinationItem {
    companion object {
        const val pinArg = "pin"

        val pinArgs = listOf(navArgument(pinArg) {
            type = NavType.LongType
            // defaultValue = 0
        })

        private const val exploreDetailsHomeRoute = "exploreDetailsHome"
        private const val exploreDetailsPhotosRoute = "exploreDetailsPhoto"
        private const val exploreDetailsInfoRoute = "exploreDetailsInfo"
    }

    data class ExploreDetailHome(
        override var titleRes: Int = R.string.detail,
        override var icon: ImageVector = Icons.Rounded.Place,
        override var route: String = exploreDetailsHomeRoute,
        override var arguments: List<NamedNavArgument> = pinArgs,
        override var routeWithArgs: String = "$route?$pinArg={$pinArg}",
    ) : BottomNavItem() {
        override fun routeWithSetArguments(vararg arguments: Any): String {
            val pinId = arguments.firstOrNull() as? Long ?: return route

            return "$route?${pinArg}=$pinId"
        }
    }

    data class ExploreDetailPhotos(
        override var titleRes: Int = R.string.photos,
        override var icon: ImageVector = Icons.Rounded.Photo,
        override var route: String = exploreDetailsPhotosRoute,
        override var arguments: List<NamedNavArgument> = pinArgs,
        override var routeWithArgs: String = "$route?$pinArg={$pinArg}",
    ) : BottomNavItem() {
        override fun routeWithSetArguments(vararg arguments: Any): String {
            val pinId = arguments.firstOrNull() as? Long ?: return route

            return "$route?$pinArg=$pinId"
        }
    }

    data class ExploreDetailInfo(
        override var titleRes: Int = R.string.info,
        override var icon: ImageVector = Icons.Rounded.Info,
        override var route: String = exploreDetailsInfoRoute,
        override var arguments: List<NamedNavArgument> = pinArgs,
        override var routeWithArgs: String = "$route?$pinArg={$pinArg}",
    ) : BottomNavItem() {
        override fun routeWithSetArguments(vararg arguments: Any): String {
            val pinId = arguments.firstOrNull() as? Long ?: return route

            return "$route?${pinArg}=$pinId"
        }
    }
}
