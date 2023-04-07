package com.sjaindl.travelcompanion.explore.details.photos

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.EmojiFlags
import androidx.compose.material.icons.rounded.LocationCity
import androidx.compose.material.icons.rounded.Place
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.sjaindl.travelcompanion.R
import com.sjaindl.travelcompanion.explore.details.photos.model.PhotoType
import com.sjaindl.travelcompanion.explore.details.tabnav.DetailsTabBarLayout
import com.sjaindl.travelcompanion.explore.details.tabnav.TabItem
import com.sjaindl.travelcompanion.theme.TravelCompanionTheme

@Composable
fun ExploreDetailPhotosMainScreen(pinId: Long) {
    val tabRowItems = listOf(
        TabItem(
            title = stringResource(id = R.string.country),
            screen = { ExploreDetailFlickrPhotosScreen(pinId = pinId, photoType = PhotoType.COUNTRY) },
            icon = Icons.Rounded.EmojiFlags,
        ),
        TabItem(
            title = stringResource(id = R.string.place),
            screen = { ExploreDetailPlacesPhotosScreen(pinId = pinId) },
            icon = Icons.Rounded.Place,
        ),
        TabItem(
            title = stringResource(id = R.string.location),
            screen = { ExploreDetailFlickrPhotosScreen(pinId = pinId, photoType = PhotoType.LOCATION) },
            icon = Icons.Rounded.LocationCity,
        )
    )

    TravelCompanionTheme {
        DetailsTabBarLayout(tabRowItems = tabRowItems, userScrollEnabled = true)
    }
}
