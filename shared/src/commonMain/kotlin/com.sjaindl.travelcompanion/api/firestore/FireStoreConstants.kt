package com.sjaindl.travelcompanion.api.firestore

class FireStoreConstants {
    object Collections {
        const val users = "users"
        const val places = "places"
        const val plans = "plans"

        const val hotels = "hotels"
        const val restaurants = "restaurants"
        const val attractions = "attractions"

        const val photos = "photos"
    }

    object Ids {
        object Place {
            const val placeId = "placeId"
            const val latitude = "latitude"
            const val longitude = "longitude"
            const val name = "name"
        }

        object Plan {
            const val name = "name"
            const val pinName = "pinName"
            const val startDate = "startDate"
            const val endDate = "endDate"
            const val imageReference = "image"

            const val path = "path"
        }

        object User {
            const val userId = "uid"
            const val email = "email"
            const val displayName = "displayName"
            const val providerId = "providerID"
            const val photoUrl = "photoURL"
            const val phoneNumber = "phoneNumber"
        }
    }
}
