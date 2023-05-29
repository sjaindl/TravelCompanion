package com.sjaindl.travelcompanion.plan.navigation

import androidx.navigation.NamedNavArgument
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.sjaindl.travelcompanion.navigation.DestinationItem
import com.sjaindl.travelcompanion.plan.PlanHomeScreen
import com.sjaindl.travelcompanion.plan.add.AddPlanScreen
import com.sjaindl.travelcompanion.plan.detail.PlanDetailHomeScreen

private const val planRoute = "plan"
private const val planDetailsContainerRoute = "planDetailsContainerRoute"
private const val addPlanRoute = "addPlan"

const val destinationArg = "destination"

internal val addPlanArgs = listOf(navArgument(destinationArg) {
    type = NavType.StringType
    nullable = true
})

private val planHome by lazy {
    PlanHome()
}

val addPlan by lazy {
    AddPlan()
}

private val planDetailsContainer by lazy {
    PlanDetailContainer()
}

const val planNavigation = "planNavigation"

data class PlanHome(
    override var route: String = planRoute,
    override var arguments: List<NamedNavArgument> = emptyList(),
    override var routeWithArgs: String = route,
) : DestinationItem {
    override fun routeWithSetArguments(vararg arguments: Any): String {
        return route
    }
}

// https://developer.android.com/jetpack/compose/navigation#optional-args
data class AddPlan(
    override var route: String = addPlanRoute,
    override var arguments: List<NamedNavArgument> = addPlanArgs,
    override var routeWithArgs: String = "$route?destination={$destinationArg}",
) : DestinationItem {
    override fun routeWithSetArguments(vararg arguments: Any): String {
        val destination = arguments.firstOrNull() as? String ?: return route
        return "$route?destination=$destination"
    }
}

data class PlanDetailContainer(
    override var route: String = planDetailsContainerRoute,
    override var arguments: List<NamedNavArgument> = planArgs,
    override var routeWithArgs: String = "$route/{$planArg}",
) : DestinationItem {
    override fun routeWithSetArguments(vararg arguments: Any): String {
        val plan = arguments.firstOrNull() as? String ?: return route

        return "$route/$plan"
    }
}

fun NavGraphBuilder.planGraph(
    navController: NavController,
    onShowDetails: (Long) -> Unit = { },
) {
    navigation(startDestination = planHome.route, route = planNavigation) {
        composable(
            route = planHome.route,
            arguments = emptyList(),
        ) {
            PlanHomeScreen(
                onShowDetails = onShowDetails,
                onShowPlan = { plan ->
                    navController.navigate(planDetailsContainer.routeWithSetArguments(plan))
                },
                onAddPlan = {
                    navController.navigate(addPlan.route)
                },
                canNavigateBack = navController.previousBackStackEntry != null,
                navigateUp = { navController.navigateUp() },
            )
        }

        composable(
            route = addPlan.route,
            arguments = emptyList()
        ) { navBackStackEntry ->
            val destination = navBackStackEntry.arguments?.getString(destinationArg)
            AddPlanScreen(
                preselectedDestination = destination,
                canNavigateBack = navController.previousBackStackEntry != null,
                navigateUp = {
                    navController.navigateUp()

                },
            )
        }

        composable(
            route = addPlan.routeWithArgs,
            arguments = addPlan.arguments,
        ) { navBackStackEntry ->
            val destination = navBackStackEntry.arguments?.getString(destinationArg)
            AddPlanScreen(
                preselectedDestination = destination,
                canNavigateBack = navController.previousBackStackEntry != null,
                navigateUp = { navController.navigateUp() },
            )
        }

        composable(
            route = planDetailsContainer.routeWithArgs,
            arguments = planDetailsContainer.arguments,
        ) { navBackStackEntry ->
            val plan = navBackStackEntry.arguments?.getString(planArg) ?: throw IllegalStateException("No plan given")
            PlanDetailHomeScreen(
                planName = plan,
                canNavigateBack = navController.previousBackStackEntry != null,
                navigateUp = { navController.navigateUp() },
            )
        }
    }
}
