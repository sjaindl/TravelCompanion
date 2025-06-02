package com.sjaindl.travelcompanion.remember.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.navigation3.runtime.EntryProviderBuilder
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entry
import com.sjaindl.travelcompanion.remember.RememberScreen
import com.sjaindl.travelcompanion.remember.detail.RememberDetailScreen
import kotlinx.serialization.Serializable

private const val planArg = "plan"

const val rememberNavigation = "rememberNavigation"

@Serializable
data object RememberHome : NavKey

@Serializable
data class RememberDetail(val planName: String) : NavKey

@Composable
fun EntryProviderBuilder<Any>.RememberNavigation(
    backStack: SnapshotStateList<Any>,
) {
    entry<RememberHome> {
        RememberScreen(
            canNavigateBack = backStack.size > 1,
            navigateUp = {
                backStack.removeAt(backStack.size - 1)
            },
            onNavigateToRememberDetails = { planName ->
                backStack.add(RememberDetail(planName = planName))
            }
        )
    }

    entry<RememberDetail> {
        RememberDetailScreen(
            planName = it.planName,
            canNavigateBack = backStack.size > 1,
            navigateUp = {
                backStack.removeAt(backStack.size - 1)
            },
        )
    }
}
