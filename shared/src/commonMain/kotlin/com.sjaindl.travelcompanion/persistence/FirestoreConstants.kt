package com.sjaindl.travelcompanion.persistence

object FirestoreConstants {

    object Collections {
        internal val users = "users"
        internal val places = "places"
        internal val plans = "plans"
        internal val fligths = "fligths"
        internal val publicTransport = "publicTransport"
        internal val hotels = "hotels"
        internal val restaurants = "restaurants"
        internal val attractions = "attractions"
        internal val photos = "photos"
    }

    object Ids {
        object Place {
            internal val placeId = "placeId"
            internal val latitude = "latitude"
            internal val longitude = "longitude"
            internal val name = "name"
        }

        object Plan {
            internal val name = "name"
            internal val pinName = "pinName"
            internal val startDate = "startDate"
            internal val endDate = "endDate"
            internal val imageReference = "image"
            internal val path = "path"
        }

        object User {
            internal val userId = "uid"
            internal val email = "email"
            internal val displayName = "displayName"
            internal val providerId = "providerID"
            internal val photoUrl = "photoURL"
            internal val phoneNumber = "phoneNumber"
        }
    }
}
