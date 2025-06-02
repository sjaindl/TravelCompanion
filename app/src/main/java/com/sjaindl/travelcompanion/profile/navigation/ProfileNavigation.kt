package com.sjaindl.travelcompanion.profile.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderBuilder
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entry
import com.sjaindl.travelcompanion.profile.PersonalInfoScreen
import com.sjaindl.travelcompanion.profile.ProfileScreen
import com.sjaindl.travelcompanion.profile.ProfileViewModel
import kotlinx.serialization.Serializable

@Serializable
data object Profile : NavKey

@Serializable
data object PersonalInfo : NavKey

@Composable
fun EntryProviderBuilder<Any>.ProfileNavigation(
    backStack: SnapshotStateList<Any>,
    onNavigateToDataAccessRationaleActivity: () -> Unit,
) {
    entry<Profile> {
        val viewModel = hiltViewModel<ProfileViewModel>()

        ProfileScreen(
            initials = viewModel.initials,
            userName = viewModel.userName,
            logout = viewModel::logout,
            deleteAccount = viewModel::deleteAccount,
            onClose = {
                backStack.remove(backStack.size - 1)
            },
            goToPersonalInfo = {
                backStack.add(PersonalInfo)
            },
            goToDataAccessRationaleInfo = onNavigateToDataAccessRationaleActivity,
            canNavigateBack = backStack.size > 1,
            navigateUp = {
                backStack.removeAt(backStack.size - 1)
            },
        )
    }

    entry<PersonalInfo> {
        PersonalInfoScreen(
            canNavigateBack = backStack.size > 1,
            navigateUp = {
                backStack.removeAt(backStack.size - 1)
            },
        )
    }
}
