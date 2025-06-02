package com.sjaindl.travelcompanion.explore.details.bottomnav

import androidx.navigation3.runtime.NavKey
import com.sjaindl.travelcompanion.R
import com.sjaindl.travelcompanion.explore.navigation.ExploreDestination
import com.sjaindl.travelcompanion.explore.navigation.NamedDestinationItem
import kotlinx.serialization.Serializable

@Serializable
data class ExploreDetailHome(
    val pinId: Long,
    val encodedPlaces: String? = null,
    override val titleRes: Int = R.string.detail,
    override var destination: ExploreDestination = ExploreDestination.ExploreHome,
) : NavKey, NamedDestinationItem

@Serializable
data class ExploreDetailPhotos(
    val pinId: Long,
    val pickerMode: Boolean,
    override val titleRes: Int = R.string.photos,
    override var destination: ExploreDestination = ExploreDestination.ExplorePhotos,
) : NavKey, NamedDestinationItem

@Serializable
data class ExploreDetailInfo(
    val pinId: Long,
    override val titleRes: Int = R.string.info,
    override var destination: ExploreDestination = ExploreDestination.ExploreInfo,
) : NavKey, NamedDestinationItem
