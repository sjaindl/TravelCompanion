package com.sjaindl.travelcompanion.api.google

import com.sjaindl.travelcompanion.api.firestore.FireStoreConstants

fun GooglePlace.asMap(): Map<String, Any> {
    val data: MutableMap<String, Any> = mutableMapOf(
        FireStoreConstants.Ids.Place.placeId to placeId,
        FireStoreConstants.Ids.Place.name to name,
        FireStoreConstants.Ids.Place.reference to reference,
        FireStoreConstants.Ids.Place.scope to scope,
        FireStoreConstants.Ids.Place.vicinity to vicinity.orEmpty(),
    )

    id?.let {
        data[FireStoreConstants.Ids.Place.id] = it
    }

    icon?.let {
        data[FireStoreConstants.Ids.Place.icon] = it
    }

    rating?.let {
        data[FireStoreConstants.Ids.Place.rating] = it
    }

    userRatingsTotal?.let {
        data[FireStoreConstants.Ids.Place.userRatingsTotal] = it
    }

    types?.let {
        data[FireStoreConstants.Ids.Place.types] = it
    }

    htmlAttributions?.let {
        data[FireStoreConstants.Ids.Place.htmlAttributions] = it
    }

    val latitude = geometry?.location?.lat
    val longitude = geometry?.location?.lng

    if (latitude != null && longitude != null) {
        val location: MutableMap<String, Any> = mutableMapOf(
            FireStoreConstants.Ids.Place.location to mutableMapOf(
                FireStoreConstants.Ids.Place.latitude to latitude,
                FireStoreConstants.Ids.Place.longitude to longitude,
            )
        )

        data[FireStoreConstants.Ids.Place.geometry] = location
    }

    val photoList: MutableList<Any> = mutableListOf()
    photos?.forEach { photo ->
        val photoMap: MutableMap<String, Any> = mutableMapOf()
        photo.height?.let {
            photoMap[FireStoreConstants.Ids.Place.height] = it
        }

        photo.width?.let {
            photoMap[FireStoreConstants.Ids.Place.width] = it
        }

        photo.photoReference?.let {
            photoMap[FireStoreConstants.Ids.Place.photoReference] = it
        }

        photo.htmlAttributions?.let {
            photoMap[FireStoreConstants.Ids.Place.htmlAttributions] = it
        }

        photoList.add(photoMap)
    }

    data[FireStoreConstants.Ids.Place.photos] = photoList

    val plusCodeMap = mutableMapOf<String, String>()
    plusCode?.let { plusCode ->
        plusCode.compoundCode?.let {
            plusCodeMap[FireStoreConstants.Ids.Place.compound_code] = it
        }
        plusCode.globalCode?.let {
            plusCodeMap[FireStoreConstants.Ids.Place.global_code] = it
        }
        data[FireStoreConstants.Ids.Place.plusCode] = plusCodeMap
    }

    return data
}
