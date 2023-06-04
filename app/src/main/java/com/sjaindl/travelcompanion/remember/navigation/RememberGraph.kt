package com.sjaindl.travelcompanion.remember.navigation

import androidx.navigation.NamedNavArgument
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.sjaindl.travelcompanion.navigation.DestinationItem
import com.sjaindl.travelcompanion.remember.RememberDetailScreen
import com.sjaindl.travelcompanion.remember.RememberScreen

private const val planArg = "plan"

private val rememberDetailArgs = listOf(navArgument(planArg) {
    type = NavType.StringType
    // defaultValue = 0
})

private const val rememberRoute = "remember"
private const val rememberDetailRoute = "rememberDetail"

private val rememberHome by lazy {
    RememberHome()
}

private val rememberDetail by lazy {
    RememberDetail()
}

const val rememberNavigation = "rememberNavigation"

data class RememberHome(
    override var route: String = rememberRoute,
    override var arguments: List<NamedNavArgument> = emptyList(),
    override var routeWithArgs: String = route,
) : DestinationItem {
    override fun routeWithSetArguments(vararg arguments: Any): String {
        return route
    }
}

data class RememberDetail(
    override var route: String = rememberDetailRoute,
    override var arguments: List<NamedNavArgument> = rememberDetailArgs,
    override var routeWithArgs: String = "$route/{$planArg}",
) : DestinationItem {
    override fun routeWithSetArguments(vararg arguments: Any): String {
        val planName = arguments.firstOrNull() as? String ?: return route

        return "$route/$planName"
    }
}

fun NavGraphBuilder.rememberGraph(navController: NavController) {
    navigation(startDestination = rememberHome.route, route = rememberNavigation) {
        composable(
            route = rememberHome.route,
            arguments = rememberHome.arguments,
        ) {
            RememberScreen(
                canNavigateBack = navController.previousBackStackEntry != null,
                navigateUp = { navController.navigateUp() },
            )
        }

        composable(
            route = rememberDetail.routeWithArgs,
            arguments = rememberDetail.arguments,
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
