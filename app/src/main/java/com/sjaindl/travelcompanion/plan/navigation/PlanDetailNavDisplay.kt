package com.sjaindl.travelcompanion.plan.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entry
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.sjaindl.travelcompanion.model.MapLocationData
import com.sjaindl.travelcompanion.plan.ChangeDateScreen
import com.sjaindl.travelcompanion.plan.detail.PlanDetailItemType
import com.sjaindl.travelcompanion.plan.detail.PlanDetailScreen
import com.sjaindl.travelcompanion.plan.detail.addplace.AddPlaceMapScreen
import com.sjaindl.travelcompanion.plan.detail.notes.NotesScreen
import kotlinx.serialization.Serializable

@Serializable
data class PlanDetail(val planName: String) : NavKey

@Serializable
data class AddPlace(
    val planName: String,
    val planDetailItemType: PlanDetailItemType,
    val mapLocationData: MapLocationData,
) : NavKey

@Serializable
data class ChangeDate(val planName: String) : NavKey

@Serializable
data class AddNote(
    val plannableId: String,
    val planName: String,
    val planDetailItemType: PlanDetailItemType,
) : NavKey

@Composable
fun PlanDetailNavDisplay(
    backStack: SnapshotStateList<Any>,
    modifier: Modifier = Modifier,
    plan: String,
    onChoosePlanImage: (pinId: Long) -> Unit,
    canNavigateBack: () -> Boolean,
    navigateUp: () -> Unit = {},
) {
    NavDisplay(
        backStack = backStack,
        modifier = modifier,
        entryProvider = entryProvider {
            entry<PlanDetail> {
                PlanDetailScreen(
                    planName = plan,
                    canNavigateBack = canNavigateBack,
                    navigateUp = navigateUp,
                    onAddPlace = { planDetailItemType, planName, mapLocationData ->
                        backStack.add(
                            AddPlace(
                                planName = planName,
                                planDetailItemType = planDetailItemType,
                                mapLocationData = mapLocationData,
                            )
                        )
                    },
                    onChangeDate = { planName ->
                        backStack.add(
                            ChangeDate(planName = planName)
                        )
                    },
                    onAddNote = { plannableId, planName, planDetailItemType ->
                        backStack.add(
                            AddNote(
                                plannableId = plannableId,
                                planName = planName,
                                planDetailItemType = planDetailItemType,
                            )
                        )
                    },
                    onChoosePlanImage = onChoosePlanImage,
                )
            }

            entry<AddPlace> {
                AddPlaceMapScreen(
                    modifier = Modifier.fillMaxSize(),
                    planDetailItemType = it.planDetailItemType,
                    initialLocation = it.mapLocationData,
                    planName = it.planName,
                    canNavigateBack = canNavigateBack,
                    navigateUp = navigateUp,
                )
            }

            entry<ChangeDate> {
                ChangeDateScreen(
                    planName = it.planName,
                    canNavigateBack = canNavigateBack,
                    navigateUp = navigateUp,
                )
            }

            entry<AddNote> {
                // dialog destinations not yet supported in navigation 3
                NotesScreen(
                    planName = it.planName,
                    plannableId = it.plannableId,
                    planDetailItemType = it.planDetailItemType,
                    navigateUp = navigateUp,
                )
            }
        }
    )
}
