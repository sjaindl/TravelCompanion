package com.sjaindl.travelcompanion.plan.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.sjaindl.travelcompanion.navigation.DestinationItem
import com.sjaindl.travelcompanion.plan.detail.PlanDetailScreen

internal const val planArg = "plan"

internal val planArgs = listOf(navArgument(planArg) {
    type = NavType.StringType
    // nullable = true
})

internal val planDetail by lazy {
    PlanDetail()
}

internal const val planDetailRoute = "planDetail"

data class PlanDetail(
    override var route: String = planDetailRoute,
    override var arguments: List<NamedNavArgument> = planArgs,
    override var routeWithArgs: String = "$route/{$planArg}",
) : DestinationItem {
    override fun routeWithSetArguments(vararg arguments: Any): String {
        val plan = arguments.firstOrNull() as? String ?: return route
        return "$route/{$plan}"
    }
}

@Composable
fun PlanDetailNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    plan: String,
) {
    NavHost(
        navController = navController,
        startDestination = planDetail.route,
        modifier = modifier,
    ) {

        composable(
            route = planDetail.route,
            arguments = emptyList(),
        ) {
            PlanDetailScreen(planName = plan)
        }

        composable(
            route = planDetail.routeWithArgs,
            arguments = planArgs
        ) { navBackStackEntry ->
            val planArg = navBackStackEntry.arguments?.getString(planArg) ?: throw IllegalStateException("No plan given")
            PlanDetailScreen(planName = planArg)
        }
    }
}
