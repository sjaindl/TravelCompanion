package com.sjaindl.travelcompanion

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.sjaindl.travelcompanion.explore.details.bottomnav.BottomNavItem
import com.sjaindl.travelcompanion.explore.details.bottomnav.BottomNavItem.Companion.pinArg
import com.sjaindl.travelcompanion.explore.details.home.ExploreDetailHomeScreen
import com.sjaindl.travelcompanion.explore.details.info.ExploreDetailInfoMainScreen
import com.sjaindl.travelcompanion.explore.details.photos.ExploreDetailPhotosMainScreen
import com.sjaindl.travelcompanion.navigation.DestinationItem
import com.sjaindl.travelcompanion.plan.PlanHomeScreen
import com.sjaindl.travelcompanion.profile.PersonalInfoScreen
import com.sjaindl.travelcompanion.profile.ProfileScreen

val exploreDetailHome = BottomNavItem.ExploreDetailHome()
val exploreDetailPhotos = BottomNavItem.ExploreDetailPhotos()
val exploreDetailInfo = BottomNavItem.ExploreDetailInfo()

val profileHome = ProfileHome()
val personalInfo = PersonalInfo()

val planHome = PlanHome()

@Composable
fun TCNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    startDestination: String? = null,
    startDestinationPinId: Long = 0,
    onClose: () -> Unit = { },
    onShowDetails: (Long) -> Unit = { },
) {
    NavHost(
        navController = navController,
        startDestination = startDestination ?: exploreDetailHome.route,
        modifier = modifier
    ) {
        composable(
            route = exploreDetailHome.route,
        ) {
            ExploreDetailHomeScreen(pinId = startDestinationPinId)
        }

        composable(
            route = exploreDetailHome.routeWithArgs,
            arguments = exploreDetailHome.arguments,
        ) { navBackStackEntry ->
            val pinId = navBackStackEntry.arguments?.getLong(pinArg) ?: throw java.lang.IllegalStateException("No pinId given")
            ExploreDetailHomeScreen(pinId = pinId)
        }

        composable(
            route = exploreDetailPhotos.routeWithArgs,
            arguments = exploreDetailPhotos.arguments,
        ) { navBackStackEntry ->
            val pinId = navBackStackEntry.arguments?.getLong(pinArg) ?: throw java.lang.IllegalStateException("No pinId given")
            ExploreDetailPhotosMainScreen(pinId = pinId)
        }

        composable(
            route = exploreDetailInfo.routeWithArgs,
            arguments = exploreDetailInfo.arguments,
        ) { navBackStackEntry ->
            val pinId = navBackStackEntry.arguments?.getLong(pinArg) ?: throw java.lang.IllegalStateException("No pinId given")
            ExploreDetailInfoMainScreen(pinId = pinId)
        }

        composable(
            route = profileHome.route,
            arguments = emptyList(),
        ) {
            ProfileScreen(
                onClose = onClose,
                goToPersonalInfo = {
                    navController.navigateSingleTopTo(personalInfoRoute)
                })
        }

        composable(
            route = personalInfo.route,
            arguments = emptyList(),
        ) {
            PersonalInfoScreen()
        }

        composable(
            route = planHome.route,
            arguments = emptyList(),
        ) {
            PlanHomeScreen(onShowDetails = onShowDetails)
        }
    }
}

fun NavHostController.navigateSingleTopTo(route: String) =
    this.navigate(route) {
        graph.startDestinationRoute?.let {
            popUpTo(it) {
                saveState = true
            }
        }
        launchSingleTop = true
        restoreState = true
    }

// Compile-time safe navigation annotation processing lib (had some issues with kotlin versioning, though):
// https://proandroiddev.com/safe-compose-arguments-an-improved-way-to-navigate-in-jetpack-compose-95c84722eec2


private const val profileRoute = "profile"
private const val personalInfoRoute = "personalInfo"

private const val planRoute = "plan"

data class ProfileHome(
    override var route: String = profileRoute,
    override var arguments: List<NamedNavArgument> = emptyList(),
    override var routeWithArgs: String = route,
) : DestinationItem {
    override fun routeWithSetArguments(vararg arguments: Any): String {
        return route
    }
}

data class PersonalInfo(
    override var route: String = personalInfoRoute,
    override var arguments: List<NamedNavArgument> = emptyList(),
    override var routeWithArgs: String = route,
) : DestinationItem {
    override fun routeWithSetArguments(vararg arguments: Any): String {
        return route
    }
}

data class PlanHome(
    override var route: String = planRoute,
    override var arguments: List<NamedNavArgument> = emptyList(),
    override var routeWithArgs: String = route,
) : DestinationItem {
    override fun routeWithSetArguments(vararg arguments: Any): String {
        return route
    }
}
