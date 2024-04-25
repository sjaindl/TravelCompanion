package com.sjaindl.travelcompanion.plan.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.navArgument
import com.sjaindl.travelcompanion.explore.details.bottomnav.BottomNavItem
import com.sjaindl.travelcompanion.explore.details.bottomnav.BottomNavItem.ExploreDetailPhotos
import com.sjaindl.travelcompanion.explore.details.photos.ExploreDetailPhotosMainScreen
import com.sjaindl.travelcompanion.model.MapLocationData
import com.sjaindl.travelcompanion.navigation.DestinationItem
import com.sjaindl.travelcompanion.plan.ChangeDateScreen
import com.sjaindl.travelcompanion.plan.detail.PlanDetailItemType
import com.sjaindl.travelcompanion.plan.detail.PlanDetailScreen
import com.sjaindl.travelcompanion.plan.detail.addplace.AddPlaceMapScreen
import com.sjaindl.travelcompanion.plan.detail.notes.NotesScreen
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private const val PLAN_ARG = "plan"
private const val PLANNABLE_ID_ARG = "plannableId"
private const val PLAN_DETAIL_TYPE_ARG = "planDetailType"
private const val LOCATION_ARG = "location"

private val planArgs = listOf(navArgument(name = PLAN_ARG) {
    type = NavType.StringType
})

private object PlanDetail : DestinationItem {
    override var route = "planDetail"
    override var arguments = planArgs
    override var routeWithArgs = "$route/{$PLAN_ARG}"
}

private object AddPlace : DestinationItem {
    override var route = "addPlace"
    override var arguments = listOf(navArgument(name = PLAN_DETAIL_TYPE_ARG) {
        type = NavType.StringType
    }, navArgument(name = PLAN_ARG) {
        type = NavType.StringType
    }, navArgument(name = LOCATION_ARG) {
        type = NavType.StringType
    })
    override var routeWithArgs = "$route/{$PLAN_DETAIL_TYPE_ARG}/{$PLAN_ARG}/{$LOCATION_ARG}"
}

private fun NavController.navigateToAddPlace(
    planDetailItemType: PlanDetailItemType,
    planName: String,
    mapLocationData: MapLocationData,
    navOptions: NavOptions? = null,
) {
    val encodedPlanDetailItemType = Json.encodeToString(planDetailItemType)
    val encodedMapLocationData = Json.encodeToString(mapLocationData)

    this.navigate(route = "${AddPlace.route}/$encodedPlanDetailItemType/$planName/$encodedMapLocationData", navOptions = navOptions)
}

private object ChangeDate : DestinationItem {
    override var route = "changeDate"
    override var arguments = planArgs
    override var routeWithArgs = "$route/{$PLAN_ARG}"
}

private fun NavController.navigateToChangeDate(
    planName: String,
    navOptions: NavOptions? = null,
) {
    this.navigate(route = "${ChangeDate.route}/$planName", navOptions = navOptions)
}

private object AddNote : DestinationItem {
    override var route = "addNote"
    override var arguments = listOf(navArgument(name = PLANNABLE_ID_ARG) {
        type = NavType.StringType
    }, navArgument(name = PLAN_ARG) {
        type = NavType.StringType
    }, navArgument(name = PLAN_DETAIL_TYPE_ARG) {
        type = NavType.StringType
    })
    override var routeWithArgs = "$route/{$PLANNABLE_ID_ARG}/{$PLAN_ARG}/{$PLAN_DETAIL_TYPE_ARG}"
}

private fun NavController.navigateToAddNote(
    plannableId: String,
    planName: String,
    planDetailItemType: PlanDetailItemType,
    navOptions: NavOptions? = null,
) {
    val encodedPlanDetailItemType = Json.encodeToString(planDetailItemType)

    this.navigate(route = "${AddNote.route}/$plannableId/$planName/$encodedPlanDetailItemType", navOptions = navOptions)
}

@Composable
fun PlanDetailNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    plan: String,
    onChoosePlanImage: (pinId: Long) -> Unit,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit = {},
) {
    NavHost(
        navController = navController,
        startDestination = PlanDetail.route,
        modifier = modifier,
    ) {

        composable(
            route = PlanDetail.route,
            arguments = emptyList(),
        ) {
            PlanDetailScreen(
                planName = plan,
                canNavigateBack = canNavigateBack,
                navigateUp = { navigateUp() },
                onAddPlace = { planDetailItemType, planName, mapLocationData ->
                    navController.navigateToAddPlace(
                        planDetailItemType = planDetailItemType,
                        planName = planName,
                        mapLocationData = mapLocationData,
                    )
                },
                onChangeDate = { planName ->
                    navController.navigateToChangeDate(planName = planName)
                },
                onAddNote = { plannableId, planName, planDetailItemType ->
                    navController.navigateToAddNote(plannableId = plannableId, planName = planName, planDetailItemType = planDetailItemType)
                },
                onChoosePlanImage = { pinId ->
                    val route = ExploreDetailPhotos.routeWithSetArguments(pinId, true)
                    navController.navigate(route)
                }
            )
        }

        composable(
            route = PlanDetail.routeWithArgs,
            arguments = PlanDetail.arguments,
        ) { navBackStackEntry ->
            val planArg = navBackStackEntry.arguments?.getString(PLAN_ARG) ?: throw IllegalStateException("No plan given")
            PlanDetailScreen(
                planName = planArg,
                canNavigateBack = canNavigateBack,
                navigateUp = { navigateUp() },
                onAddPlace = { planDetailItemType, planName, mapLocationData ->
                    navController.navigateToAddPlace(
                        planDetailItemType = planDetailItemType,
                        planName = planName,
                        mapLocationData = mapLocationData,
                    )
                },
                onChangeDate = { planName ->
                    navController.navigateToChangeDate(planName = planName)
                },
                onAddNote = { plannableId, planName, planDetailItemType ->
                    navController.navigateToAddNote(plannableId = plannableId, planName = planName, planDetailItemType = planDetailItemType)
                },
                onChoosePlanImage = onChoosePlanImage,
            )
        }

        composable(
            route = AddPlace.routeWithArgs,
            arguments = AddPlace.arguments,
        ) { navBackStackEntry ->
            val args = navBackStackEntry.arguments
            val encodedPlanDetailItemType = args?.getString(PLAN_DETAIL_TYPE_ARG) ?: throw IllegalStateException("No placeType given")
            val planArgument = args.getString(PLAN_ARG) ?: throw IllegalStateException("No plan given")
            val encodedLocationData = args.getString(LOCATION_ARG) ?: throw IllegalStateException("No location given")

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

        dialog(
            route = AddNote.routeWithArgs,
            arguments = AddNote.arguments,
        ) { navBackStackEntry ->
            val args = navBackStackEntry.arguments
            val plannableIdArg = args?.getString(PLANNABLE_ID_ARG) ?: throw IllegalStateException("No plannable given")
            val planArgument = args.getString(PLAN_ARG) ?: throw IllegalStateException("No plan given")
            val encodedPlanDetailItemType = args.getString(PLAN_DETAIL_TYPE_ARG) ?: throw IllegalStateException("No placeType given")

            val planDetailItemType: PlanDetailItemType = Json.decodeFromString(encodedPlanDetailItemType)

            NotesScreen(
                planName = planArgument,
                plannableId = plannableIdArg,
                planDetailItemType = planDetailItemType,
                navigateUp = {
                    navController.navigateUp()
                },
            )
        }

        composable(
            route = ChangeDate.routeWithArgs,
            arguments = planArgs,
        ) { navBackStackEntry ->
            val planArg = navBackStackEntry.arguments?.getString(PLAN_ARG) ?: throw IllegalStateException("No plan given")
            ChangeDateScreen(
                planName = planArg,
                canNavigateBack = navController.previousBackStackEntry != null,
                navigateUp = {
                    navController.navigateUp()
                },
            )
        }

        composable(
            route = ExploreDetailPhotos.routeWithArgs,
            arguments = ExploreDetailPhotos.arguments,
        ) { navBackStackEntry ->
            val argPinId =
                navBackStackEntry.arguments?.getLong(BottomNavItem.PIN_ARG) ?: throw IllegalStateException("No pinId given")
            val isPickerMode =
                navBackStackEntry.arguments?.getBoolean(BottomNavItem.PICKER_MODE) ?: throw IllegalStateException("No pickerMode given")

            ExploreDetailPhotosMainScreen(
                pinId = argPinId,
                isPickerMode = isPickerMode,
                onPhotoChosen = {
                    navController.navigateUp()
                },
                isChoosePlanImageMode = true,
            )
        }
    }
}
