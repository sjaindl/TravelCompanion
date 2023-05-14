package com.sjaindl.travelcompanion

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.google.firebase.auth.FirebaseAuth
import com.sjaindl.travelcompanion.explore.ExploreScreen
import com.sjaindl.travelcompanion.explore.details.ExploreDetailContainer
import com.sjaindl.travelcompanion.explore.search.SearchPlaceScreen
import com.sjaindl.travelcompanion.navigation.DestinationItem
import com.sjaindl.travelcompanion.plan.PlanHomeScreen
import com.sjaindl.travelcompanion.profile.PersonalInfoScreen
import com.sjaindl.travelcompanion.profile.ProfileScreen

private val tcHome by lazy {
    TCHome()
}

private val profileHome by lazy {
    ProfileHome()
}

private val personalInfo by lazy {
    PersonalInfo()
}

private val exploreHome by lazy {
    ExploreHome()
}

private val searchPlace by lazy {
    SearchPlace()
}

private val exploreDetailContainer by lazy {
    ExploreDetailContainer()
}

private val planHome by lazy {
    PlanHome()
}

private const val pinArg = "pin"
private const val planArg = "plan"
private const val placesArg = "places"

private val planArgs = listOf(navArgument(planArg) {
    type = NavType.StringType
    nullable = true
})

private val placesArgs = listOf(navArgument(placesArg) {
    type = NavType.StringType
    nullable = true
})

private val pinArgs = listOf(navArgument(pinArg) {
    type = NavType.LongType
    // defaultValue = 0
})

@Composable
fun TCNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    startDestination: String? = null,
    startDestinationPinId: Long = 0,
    onClose: () -> Unit = { },
    onShowDetails: (Long) -> Unit = { },
    onClickedProfile: () -> Unit = { },
    openProfile: Boolean = false,
    onAuthenticateAndOpenPlan: () -> Unit = { },
    openPlan: Boolean = false,
) {
    if (openProfile) {
        navController.navigateSingleTopTo(profileHome.route)
    }

    if (openPlan) {
        navController.navigateSingleTopTo(planHome.route)
    }

    NavHost(
        navController = navController,
        startDestination = startDestination ?: tcHome.route,
        modifier = modifier
    ) {
        composable(
            route = tcHome.route
        ) {
            MainScreen(
                onNavigateToExplore = {
                    navController.navigateSingleTopTo(exploreHome.route)
                },
                onNavigateToPlan = {
                    if (FirebaseAuth.getInstance().currentUser != null) {
                        navController.navigateSingleTopTo(planHome.route)
                    } else {
                        onAuthenticateAndOpenPlan()
                    }
                },
                onNavigateToRemember = {
                    // TODO
                },
                onNavigateToProfile = {
                    onClickedProfile()
                },
            )
        }

        composable(
            route = profileHome.route,
            arguments = emptyList(),
        ) {
            ProfileScreen(
                onClose = onClose,
                goToPersonalInfo = {
                    navController.navigate(personalInfo.route)
                })
        }

        composable(
            route = personalInfo.route,
            arguments = emptyList(),
        ) {
            PersonalInfoScreen()
        }

        composable(
            route = exploreHome.routeWithArgs,
            arguments = exploreHome.arguments,
        ) { navBackStackEntry ->
            val encodedPlaces = navBackStackEntry.arguments?.getString(placesArg)
            ExploreScreen(
                encodedPlaces = encodedPlaces,
                onSearch = {
                    navController.navigate(searchPlace.route)
                },
                onNavigateToExploreDetails = { pinId ->
                    navController.navigate(exploreDetailContainer.routeWithSetArguments(pinId))
                }
            )
        }

        composable(
            route = searchPlace.route,
        ) {
            SearchPlaceScreen(
                onPickedPlace = { place ->
                    navController.popBackStack()
                    navController.navigate(route = exploreHome.routeWithSetArguments(place)) {
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(
            route = exploreDetailContainer.routeWithArgs,
            arguments = exploreDetailContainer.arguments,
        ) { navBackStackEntry ->
            val pinId = navBackStackEntry.arguments?.getLong(pinArg) ?: throw IllegalStateException("No pinId given")
            ExploreDetailContainer(pinId = pinId)
        }

        composable(
            route = planHome.route,
            arguments = emptyList(),
        ) {
            PlanHomeScreen(onShowDetails = onShowDetails)
        }
    }
}

fun NavHostController.navigateSingleTopTo(route: String, popToRoute: String? = graph.startDestinationRoute) =
    this.navigate(route) {
        popToRoute?.let {
            popUpTo(it) {
                saveState = true
            }
        }
        launchSingleTop = true
        restoreState = true
    }

// Compile-time safe navigation annotation processing lib (had some issues with kotlin versioning, though):
// https://proandroiddev.com/safe-compose-arguments-an-improved-way-to-navigate-in-jetpack-compose-95c84722eec2

private const val tcHomeRoute = "tcHome"
private const val profileRoute = "profile"
private const val personalInfoRoute = "personalInfo"

private const val exploreRoute = "explore"
private const val searchPlaceRoute = "searchPlaces"
private const val exploreDetailsContainerRoute = "exploreDetailsContainer"

private const val planRoute = "plan"

data class TCHome(
    override var route: String = tcHomeRoute,
    override var arguments: List<NamedNavArgument> = emptyList(),
    override var routeWithArgs: String = route,
) : DestinationItem {
    override fun routeWithSetArguments(vararg arguments: Any): String {
        return route
    }
}

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

data class ExploreHome(
    override var route: String = exploreRoute,
    override var arguments: List<NamedNavArgument> = placesArgs,
    override var routeWithArgs: String = "$route?$placesArg={$placesArg}",
) : DestinationItem {
    override fun routeWithSetArguments(vararg arguments: Any): String {
        val encodedPlaces = arguments.firstOrNull() as? String ?: return route

        return "$route?$placesArg=$encodedPlaces"
    }
}

data class ExploreDetailContainer(
    override var route: String = exploreDetailsContainerRoute,
    override var arguments: List<NamedNavArgument> = pinArgs,
    override var routeWithArgs: String = "$route/{$pinArg}",
) : DestinationItem {
    override fun routeWithSetArguments(vararg arguments: Any): String {
        val pinId = arguments.firstOrNull() as? Long ?: return route

        return "$route/$pinId"
    }
}

data class SearchPlace(
    override var route: String = searchPlaceRoute,
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
