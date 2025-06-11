package com.sjaindl.travelcompanion.explore.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.entry
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSavedStateNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.navigation3.ui.rememberSceneSetupNavEntryDecorator
import com.sjaindl.travelcompanion.explore.details.bottomnav.ExploreDetailHome
import com.sjaindl.travelcompanion.explore.details.bottomnav.ExploreDetailInfo
import com.sjaindl.travelcompanion.explore.details.bottomnav.ExploreDetailPhotos
import com.sjaindl.travelcompanion.explore.details.home.ExploreDetailHomeScreen
import com.sjaindl.travelcompanion.explore.details.info.ExploreDetailInfoMainScreen
import com.sjaindl.travelcompanion.explore.details.photos.ExploreDetailPhotosMainScreen

@Composable
fun ExploreDetailNavDisplay(
    backStack: NavBackStack,
    rootBackStack: NavBackStack,
    modifier: Modifier = Modifier,
    pinId: Long,
) {

    NavDisplay(
        entryDecorators = listOf(
            // Default decorators for managing scenes and saving state
            rememberSceneSetupNavEntryDecorator(),
            rememberSavedStateNavEntryDecorator(),
            // View model store decorator
            rememberViewModelStoreNavEntryDecorator()
        ),
        backStack = backStack,
        modifier = modifier,
        entryProvider = entryProvider {
            entry<ExploreDetailHome> {
                ExploreDetailHomeScreen(pinId = pinId)
            }

            entry<ExploreDetailPhotos> {
                ExploreDetailPhotosMainScreen(
                    pinId = it.pinId,
                    isPickerMode = it.pickerMode,
                    onPhotoChosen = {
                        if (it.pickerMode) rootBackStack.removeAt(rootBackStack.size - 1)
                        else backStack.removeAt(backStack.size - 1)
                    },
                    isChoosePlanImageMode = it.pickerMode,
                )
            }

            entry<ExploreDetailInfo> {
                ExploreDetailInfoMainScreen(pinId = it.pinId)
            }
        }
    )
}
