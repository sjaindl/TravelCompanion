package com.sjaindl.travelcompanion.explore.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Photo
import androidx.compose.material.icons.rounded.Place

enum class ExploreDestination {
    ExploreHome,
    ExplorePhotos,
    ExploreInfo;

    fun icon() = when (this) {
        ExploreHome -> Icons.Rounded.Place
        ExplorePhotos -> Icons.Rounded.Photo
        ExploreInfo -> Icons.Rounded.Info
    }
}

interface NamedDestinationItem {
    val titleRes: Int
    var destination: ExploreDestination
}
