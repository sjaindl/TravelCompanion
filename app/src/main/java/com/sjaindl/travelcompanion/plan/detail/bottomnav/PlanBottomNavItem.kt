package com.sjaindl.travelcompanion.plan.detail.bottomnav

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Attractions
import androidx.compose.material.icons.rounded.Hotel
import androidx.compose.material.icons.rounded.Restaurant
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.sjaindl.travelcompanion.R
import com.sjaindl.travelcompanion.navigation.NamedDestinationItem

// https://medium.com/geekculture/bottom-navigation-in-jetpack-compose-android-9cd232a8b16
sealed class PlanBottomNavItem : NamedDestinationItem {
    companion object {
        const val planArg = "plan"

        val planArgs = listOf(navArgument(planArg) {
            type = NavType.StringType
            // defaultValue = 0
        })

        private const val hotelsRoute = "hotels"
        private const val restaurantsRoute = "restaurants"
        private const val attractionsRoute = "attractions"
    }

    data class PlanDetailAddHotel(
        override var titleRes: Int = R.string.hotels,
        override var icon: ImageVector = Icons.Rounded.Hotel,
        override var route: String = hotelsRoute,
        override var arguments: List<NamedNavArgument> = planArgs,
        override var routeWithArgs: String = "$route?$planArg={$planArg}",
    ) : PlanBottomNavItem() {
        override fun routeWithSetArguments(vararg arguments: Any): String {
            val planName = arguments.firstOrNull() as? String ?: return route

            return "$route?$planArg=$planName"
        }
    }

    data class PlanDetailAddRestaurant(
        override var titleRes: Int = R.string.restaurants,
        override var icon: ImageVector = Icons.Rounded.Restaurant,
        override var route: String = restaurantsRoute,
        override var arguments: List<NamedNavArgument> = planArgs,
        override var routeWithArgs: String = "$route?$planArg={$planArg}",
    ) : PlanBottomNavItem() {
        override fun routeWithSetArguments(vararg arguments: Any): String {
            val planName = arguments.firstOrNull() as? String ?: return route

            return "$route?$planArg=$planName"
        }
    }

    data class PlanDetailAddAttraction(
        override var titleRes: Int = R.string.attractions,
        override var icon: ImageVector = Icons.Rounded.Attractions,
        override var route: String = attractionsRoute,
        override var arguments: List<NamedNavArgument> = planArgs,
        override var routeWithArgs: String = "$route?$planArg={$planArg}",
    ) : PlanBottomNavItem() {
        override fun routeWithSetArguments(vararg arguments: Any): String {
            val planName = arguments.firstOrNull() as? String ?: return route

            return "$route?$planArg=$planName"
        }
    }
}
