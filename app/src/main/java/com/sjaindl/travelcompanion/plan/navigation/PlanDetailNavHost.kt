package com.sjaindl.travelcompanion.plan.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.sjaindl.travelcompanion.model.MapLocationData
import com.sjaindl.travelcompanion.navigation.DestinationItem
import com.sjaindl.travelcompanion.plan.ChangeDateScreen
import com.sjaindl.travelcompanion.plan.detail.PlanDetailItemType
import com.sjaindl.travelcompanion.plan.detail.PlanDetailScreen
import com.sjaindl.travelcompanion.plan.detail.addplace.AddPlaceMapScreen
import com.sjaindl.travelcompanion.plan.detail.notes.NotesScreen
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

internal const val planArg = "plan"
internal const val plannableIdArg = "plannableId"
internal const val planDetailTypeArg = "planDetailType"
internal const val locationArg = "location"

internal val addPlaceArgs = listOf(navArgument(planDetailTypeArg) {
    type = NavType.StringType
}, navArgument(planArg) {
    type = NavType.StringType
}, navArgument(locationArg) {
    type = NavType.StringType
})

internal val planArgs = listOf(navArgument(planArg) {
    type = NavType.StringType
    // nullable = true
})

internal val addNoteArgs = listOf(navArgument(plannableIdArg) {
    type = NavType.StringType
}, navArgument(planArg) {
    type = NavType.StringType
}, navArgument(planDetailTypeArg) {
    type = NavType.StringType
})

internal val planDetail by lazy {
    PlanDetail()
}

internal val addPlace by lazy {
    AddPlace()
}

internal val changeDate by lazy {
    ChangeDate()
}

internal val addNote by lazy {
    AddNote()
}

internal const val planDetailRoute = "planDetail"
internal const val addPlaceRoute = "addPlace"
internal const val changeDateRoute = "changeDate"
internal const val addNoteRoute = "addNote"

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

data class AddPlace(
    override var route: String = addPlaceRoute,
    override var arguments: List<NamedNavArgument> = addPlaceArgs,
    override var routeWithArgs: String = "$route/{$planDetailTypeArg}/{$planArg}/{$locationArg}",
) : DestinationItem {
    override fun routeWithSetArguments(vararg arguments: Any): String {
        if (arguments.size < 3) return route
        val planDetailItemType = arguments.first() as? PlanDetailItemType ?: return route
        val planName = arguments[1] as? String ?: return route
        val mapLocationData = arguments[2] as? MapLocationData ?: return route

        val encodedPlanDetailItemType = Json.encodeToString(planDetailItemType)
        val encodedMapLocationData = Json.encodeToString(mapLocationData)
        return "$route/$encodedPlanDetailItemType/$planName/$encodedMapLocationData"
    }
}

data class ChangeDate(
    override var route: String = changeDateRoute,
    override var arguments: List<NamedNavArgument> = planArgs,
    override var routeWithArgs: String = "$route/{$planArg}",
) : DestinationItem {
    override fun routeWithSetArguments(vararg arguments: Any): String {
        val plan = arguments.firstOrNull() as? String ?: return route
        return "$route/$plan"
    }
}

data class AddNote(
    override var route: String = addNoteRoute,
    override var arguments: List<NamedNavArgument> = addNoteArgs,
    override var routeWithArgs: String = "$route/{$plannableIdArg}/{$planArg}/{$planDetailTypeArg}",
) : DestinationItem {
    override fun routeWithSetArguments(vararg arguments: Any): String {
        if (arguments.size < 3) return route
        val plannableId = arguments.first() as? String ?: return route
        val planName = arguments[1] as? String ?: return route
        val planDetailItemType = arguments[2] as? PlanDetailItemType ?: return route

        val encodedPlanDetailItemType = Json.encodeToString(planDetailItemType)
        return "$route/$plannableId/$planName/$encodedPlanDetailItemType"
    }
}

@Composable
fun PlanDetailNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    plan: String,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit = {},
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
            PlanDetailScreen(
                planName = plan,
                canNavigateBack = canNavigateBack,
                navigateUp = { navigateUp() },
                onAddPlace = { planDetailItemType, planName, mapLocationData ->
                    navController.navigate(addPlace.routeWithSetArguments(planDetailItemType, planName, mapLocationData))
                },
                onChangeDate = { planName ->
                    navController.navigate(changeDate.routeWithSetArguments(planName))
                },
                onAddNote = { plannableId, planName, planDetailItemType ->
                    navController.navigate(addNote.routeWithSetArguments(plannableId, planName, planDetailItemType))
                },
            )
        }

        composable(
            route = planDetail.routeWithArgs,
            arguments = planArgs
        ) { navBackStackEntry ->
            val planArg = navBackStackEntry.arguments?.getString(planArg) ?: throw IllegalStateException("No plan given")
            PlanDetailScreen(
                planName = planArg,
                canNavigateBack = canNavigateBack,
                navigateUp = { navigateUp() },
                onAddPlace = { planDetailItemType, planName, mapLocationData ->
                    val encodedLocationData = Json.encodeToString(mapLocationData)
                    navController.navigate(addPlace.routeWithSetArguments(planDetailItemType, planName, encodedLocationData))
                },
                onChangeDate = { planName ->
                    navController.navigate(changeDate.routeWithSetArguments(planName))
                },
                onAddNote = { plannableId, planName, planDetailItemType ->
                    navController.navigate(addNote.routeWithSetArguments(plannableId, planName, planDetailItemType))
                },
            )
        }

        composable(
            route = addPlace.routeWithArgs,
            arguments = addPlaceArgs,
        ) { navBackStackEntry ->
            val args = navBackStackEntry.arguments
            val encodedPlanDetailItemType = args?.getString(planDetailTypeArg) ?: throw IllegalStateException("No placeType given")
            val planArgument = args?.getString(planArg) ?: throw IllegalStateException("No plan given")
            val encodedLocationData = args?.getString(locationArg) ?: throw IllegalStateException("No location given")

            val locationData: MapLocationData = Json.decodeFromString(encodedLocationData)
            val planDetailItemType: PlanDetailItemType = Json.decodeFromString(encodedPlanDetailItemType)

            AddPlaceMapScreen(
                modifier = Modifier.fillMaxSize(),
                planDetailItemType = planDetailItemType,
                initialLocation = locationData,
                planName = planArgument,
                canNavigateBack = navController.previousBackStackEntry != null,
                navigateUp = {
                    navController.navigateUp()
                },
            )
        }

        composable(
            route = addNote.routeWithArgs,
            arguments = addNoteArgs,
        ) { navBackStackEntry ->
            val args = navBackStackEntry.arguments
            val plannableIdArg = args?.getString(plannableIdArg) ?: throw IllegalStateException("No plannable given")
            val planArgument = args?.getString(planArg) ?: throw IllegalStateException("No plan given")
            val encodedPlanDetailItemType = args?.getString(planDetailTypeArg) ?: throw IllegalStateException("No placeType given")

            val planDetailItemType: PlanDetailItemType = Json.decodeFromString(encodedPlanDetailItemType)

            NotesScreen(
                planName = planArgument,
                plannableId = plannableIdArg,
                planDetailItemType = planDetailItemType,
                canNavigateBack = navController.previousBackStackEntry != null,
                navigateUp = {
                    navController.navigateUp()
                },
            )
        }

        composable(
            route = changeDate.routeWithArgs,
            arguments = planArgs,
        ) { navBackStackEntry ->
            val planArg = navBackStackEntry.arguments?.getString(planArg) ?: throw IllegalStateException("No plan given")
            ChangeDateScreen(
                planName = planArg,
                canNavigateBack = navController.previousBackStackEntry != null,
                navigateUp = {
                    navController.navigateUp()
                },
            )
        }
    }
}
