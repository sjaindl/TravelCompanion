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
            const val placeId = "place_id"
            const val id = "id"
            const val name = "name"
            const val icon = "icon"
            const val rating = "rating"
            const val reference = "reference"
            const val scope = "scope"
            const val userRatingsTotal = "user_ratings_total"
            const val vicinity = "vicinity"

            const val types = "types"

            const val geometry = "geometry"
            const val location = "location"
            const val latitude = "latitude"
            const val longitude = "longitude"

            const val photos = "photos"
            const val height = "height"
            const val width = "width"
            const val photoReference = "photo_reference"
            const val htmlAttributions = "html_attributions"

            const val plusCode = "plus_code"
            const val compound_code = "compound_code"
            const val global_code = "global_code"
        }

        object Plan {
            const val name = "name"
            const val pinName = "pinName"
            const val startDate = "startDate"
            const val endDate = "endDate"
            const val imageReference = "image"

            const val path = "path"
        }

        object Plannable {
            const val notes = "notes"
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
