package com.sjaindl.travelcompanion.plan.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.sjaindl.travelcompanion.navigation.DestinationItem
import com.sjaindl.travelcompanion.plan.PlanHomeScreen
import com.sjaindl.travelcompanion.plan.add.AddPlanScreen
import com.sjaindl.travelcompanion.plan.detail.PlanDetailHomeScreen
import com.sjaindl.travelcompanion.util.navigateSingleTopTo

private const val DESTINATION_ARG = "destination"

const val PLAN_NAVIGATION = "planNavigation"

private object PlanHome : DestinationItem {
    override var route = "plan"
}

// https://developer.android.com/jetpack/compose/navigation#optional-args
private object AddPlan : DestinationItem {
    override var route = "addPlan"
    override var arguments = listOf(navArgument(DESTINATION_ARG) {
        type = NavType.StringType
        nullable = true
    })
    override var routeWithArgs = "$route?destination={$DESTINATION_ARG}"
}

fun NavController.navigateToAddPlan(destination: String, navOptions: NavOptions? = null) {
    this.navigate(route = "${AddPlan.route}?destination=$destination", navOptions = navOptions)
}

private object PlanDetailContainer : DestinationItem {
    override var route = "planDetailsContainerRoute"
    override var arguments = planArgs
    override var routeWithArgs: String = "$route/{$planArg}"
}

private fun NavController.navigateToPlanDetailContainer(plan: String, navOptions: NavOptions? = null) {
    this.navigate(route = "${PlanDetailContainer.route}/$plan", navOptions = navOptions)
}

fun NavGraphBuilder.planGraph(
    navController: NavHostController,
    onShowDetails: (Long) -> Unit = { },
    onChoosePlanImage: (pinId: Long) -> Unit,
) {
    navigation(startDestination = PlanHome.route, route = PLAN_NAVIGATION) {
        composable(
            route = PlanHome.route,
            arguments = emptyList(),
        ) {
            PlanHomeScreen(
                onShowDetails = onShowDetails,
                onShowPlan = { plan ->
                    navController.navigateToPlanDetailContainer(plan = plan)
                },
                onAddPlan = {
                    navController.navigate(AddPlan.route)
                },
                canNavigateBack = navController.previousBackStackEntry != null,
                navigateUp = { navController.navigateUp() },
            )
        }

        // TODO: convert to dialog?
        composable(
            route = AddPlan.route,
            arguments = emptyList()
        ) { navBackStackEntry ->
            val destination = navBackStackEntry.arguments?.getString(DESTINATION_ARG)
            AddPlanScreen(
                preselectedDestination = destination,
                canNavigateBack = navController.previousBackStackEntry != null,
                navigateUp = {
                    navController.navigateUp()
                },
                planAdded = {
                    navController.navigateUp()
                    navController.navigateSingleTopTo(route = PlanHome.route)
                }
            )
        }

        // TODO: convert to dialog?
        composable(
            route = AddPlan.routeWithArgs,
            arguments = AddPlan.arguments,
        ) { navBackStackEntry ->
            val destination = navBackStackEntry.arguments?.getString(DESTINATION_ARG)
            AddPlanScreen(
                preselectedDestination = destination,
                canNavigateBack = navController.previousBackStackEntry != null,
                navigateUp = { navController.navigateUp() },
                planAdded = {
                    navController.navigateUp()
                    navController.navigateSingleTopTo(PlanHome.route)
                }
            )
        }

        composable(
            route = PlanDetailContainer.routeWithArgs,
            arguments = PlanDetailContainer.arguments,
        ) { navBackStackEntry ->
            val plan = navBackStackEntry.arguments?.getString(planArg) ?: throw IllegalStateException("No plan given")
            PlanDetailHomeScreen(
                planName = plan,
                onChoosePlanImage = onChoosePlanImage,
                canNavigateBack = navController.previousBackStackEntry != null,
                navigateUp = { navController.navigateUp() },
            )
        }
    }
}
