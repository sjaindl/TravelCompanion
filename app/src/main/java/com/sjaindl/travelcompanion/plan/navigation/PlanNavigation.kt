package com.sjaindl.travelcompanion.plan.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.navigation3.runtime.EntryProviderBuilder
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entry
import com.sjaindl.travelcompanion.plan.PlanHomeScreen
import com.sjaindl.travelcompanion.plan.add.AddPlanScreen
import com.sjaindl.travelcompanion.plan.detail.PlanDetailHomeScreen
import kotlinx.serialization.Serializable

@Serializable
data object PlanHome : NavKey

@Serializable
data class AddPlan(val destination: String?) : NavKey

@Serializable
data class PlanDetailContainer(val plan: String) : NavKey

@Composable
fun EntryProviderBuilder<Any>.PlanNavigation(
    backStack: SnapshotStateList<Any>,
    onShowDetails: (Long) -> Unit = { },
    onChoosePlanImage: (pinId: Long) -> Unit,
) {
    entry<PlanHome> {
        PlanHomeScreen(
            onShowDetails = onShowDetails,
            onShowPlan = { plan ->
                backStack.add(PlanDetailContainer(plan = plan))
            },
            onAddPlan = {
                backStack.add(AddPlan(destination = null))
            },
            canNavigateBack = backStack.size > 1,
            navigateUp = {
                backStack.removeAt(backStack.size - 1)
            },
        )
    }

    entry<AddPlan> {
        AddPlanScreen(
            preselectedDestination = it.destination,
            canNavigateBack = backStack.size > 1,
            navigateUp = {
                backStack.removeAt(backStack.size - 1)
            },
            planAdded = {
                backStack.removeAt(backStack.size - 1)
                backStack.add(PlanHome)
            }
        )
    }

    entry<PlanDetailContainer> {
        PlanDetailHomeScreen(
            planName = it.plan,
            canNavigateBack = {
                backStack.size > 1
            },
            navigateUp = {
                backStack.removeAt(backStack.size - 1)
            },
            onChoosePlanImage = onChoosePlanImage,

            )
    }
}
