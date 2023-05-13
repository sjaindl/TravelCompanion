package com.sjaindl.travelcompanion

object Constants {
    const val photoLimit = 40

    object Plannables {
        const val hotel = "hotel"
        const val restaurant = "restaurant"
        const val attraction = "attraction"
    }

    object RemoteConfig {
        object Keys {
            const val photoResizingHeight = "firestorePhotoResizingHeight"
            const val photoResizingWidth = "firestorePhotoResizingWidth"
            const val transportSearchAutocomplete = "transportSearchAutocomplete"
        }

        object LocalDefaultValues {
            const val photoResizingHeight = 800
            const val photoResizingWidth = 800
        }
    }
}