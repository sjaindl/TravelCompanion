package com.sjaindl.travelcompanion.remember.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.sjaindl.travelcompanion.navigation.DestinationItem
import com.sjaindl.travelcompanion.remember.RememberScreen
import com.sjaindl.travelcompanion.remember.detail.RememberDetailScreen

private const val planArg = "plan"

const val rememberNavigation = "rememberNavigation"

private object RememberHome : DestinationItem {
    override var route = "remember"
}

private object RememberDetail : DestinationItem {
    override var route = "rememberDetail"
    override var arguments = listOf(navArgument(planArg) {
        type = NavType.StringType
    })
    override var routeWithArgs: String = "$route/{$planArg}"
}

private fun NavController.navigateToRememberDetail(plan: String, navOptions: NavOptions? = null) {
    this.navigate(route = "${RememberDetail.route}/$plan", navOptions = navOptions)
}

fun NavGraphBuilder.rememberGraph(navController: NavController) {
    navigation(startDestination = RememberHome.route, route = rememberNavigation) {
        composable(
            route = RememberHome.route,
            arguments = RememberHome.arguments,
        ) {
            RememberScreen(
                canNavigateBack = navController.previousBackStackEntry != null,
                navigateUp = { navController.navigateUp() },
                onNavigateToRememberDetails = { planName ->
                    navController.navigateToRememberDetail(plan = planName)
                }
            )
        }

        composable(
            route = RememberDetail.routeWithArgs,
            arguments = RememberDetail.arguments,
        ) { navBackStackEntry ->
            val planName = navBackStackEntry.arguments?.getString(planArg) ?: throw IllegalStateException("No plan given")
            RememberDetailScreen(
                planName = planName,
                canNavigateBack = navController.previousBackStackEntry != null,
                navigateUp = { navController.navigateUp() },
            )
        }
    }
}
