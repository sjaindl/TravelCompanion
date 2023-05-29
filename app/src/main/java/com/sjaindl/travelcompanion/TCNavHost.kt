package com.sjaindl.travelcompanion

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.google.firebase.auth.FirebaseAuth
import com.sjaindl.travelcompanion.explore.navigation.exploreDetailContainer
import com.sjaindl.travelcompanion.explore.navigation.exploreGraph
import com.sjaindl.travelcompanion.explore.navigation.exploreNavigation
import com.sjaindl.travelcompanion.navigation.DestinationItem
import com.sjaindl.travelcompanion.plan.navigation.AddPlan
import com.sjaindl.travelcompanion.plan.navigation.planGraph
import com.sjaindl.travelcompanion.plan.navigation.planNavigation
import com.sjaindl.travelcompanion.profile.navigation.profileGraph
import com.sjaindl.travelcompanion.profile.navigation.profileNavigation
import com.sjaindl.travelcompanion.util.navigateSingleTopTo

private val tcHome by lazy {
    TCHome()
}

// https://developer.android.com/jetpack/compose/navigation#nested-nav
// https://medium.com/google-developer-experts/modular-navigation-with-jetpack-compose-fda9f6b2bef7
@Composable
fun TCNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    startDestination: String? = null,
    onClose: () -> Unit = { },
    onShowDetails: (Long) -> Unit = { pin ->
        navController.navigate(exploreDetailContainer.routeWithSetArguments(pin)) {
            launchSingleTop = true
        }
    },
    onClickedProfile: () -> Unit = { },
    openProfile: Boolean = false,
    profileOpened: () -> Unit = { },
    onAuthenticateAndOpenPlan: () -> Unit = { },
    onAuthenticateAndOpenAddPlan: (String) -> Unit = { },
    openPlan: Boolean = false,
    openAddPlan: String? = null,
    openedPlan: () -> Unit = { },
    openedAddPlan: () -> Unit = { },
) {
    if (openProfile) {
        navController.navigateSingleTopTo(profileNavigation)
        profileOpened()
    }

    if (openPlan) {
        navController.navigateSingleTopTo(planNavigation)
        openedPlan()
    }

    openAddPlan?.let { destination ->
        navController.navigate(AddPlan().routeWithSetArguments(destination))
        openedAddPlan()
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
                    navController.navigateSingleTopTo(exploreNavigation)
                },
                onNavigateToPlan = {
                    if (FirebaseAuth.getInstance().currentUser != null) {
                        navController.navigateSingleTopTo(planNavigation)
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
                canNavigateBack = false,
                navigateUp = {
                    navController.navigateUp()
                },
            )
        }

        profileGraph(navController = navController, onClose = onClose)

        exploreGraph(
            navController = navController,
            onPlanTrip = { destination ->
                if (FirebaseAuth.getInstance().currentUser != null) {
                    navController.navigate(AddPlan().routeWithSetArguments(destination))
                } else {
                    onAuthenticateAndOpenAddPlan(destination)
                }
            }
        )

        planGraph(
            navController = navController,
            onShowDetails = onShowDetails,
        )
    }
}

// Compile-time safe navigation annotation processing lib (had some issues with kotlin versioning, though):
// https://proandroiddev.com/safe-compose-arguments-an-improved-way-to-navigate-in-jetpack-compose-95c84722eec2

private const val tcHomeRoute = "tcHome"

data class TCHome(
    override var route: String = tcHomeRoute,
    override var arguments: List<NamedNavArgument> = emptyList(),
    override var routeWithArgs: String = route,
) : DestinationItem {
    override fun routeWithSetArguments(vararg arguments: Any): String {
        return route
    }
}
