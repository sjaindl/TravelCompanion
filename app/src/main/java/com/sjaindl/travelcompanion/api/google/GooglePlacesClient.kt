package com.sjaindl.travelcompanion.api.google

import android.content.Context
import android.graphics.Bitmap
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.PhotoMetadata
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPhotoRequest
import com.google.android.libraries.places.api.net.FetchPhotoResponse
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.sjaindl.travelcompanion.BuildConfig
import kotlinx.coroutines.suspendCancellableCoroutine
import timber.log.Timber
import kotlin.coroutines.resume

interface GooglePlacesClient {
    suspend fun fetchPhotoMetaData(placeId: String): List<PhotoMetadata>
    suspend fun fetchPhotoWithAttributions(metadata: PhotoMetadata): Pair<Bitmap, String>?
}

class GooglePlacesClientImpl(context: Context) : GooglePlacesClient {
    private val tag = "GooglePlacesClient"

    private var placesClient: PlacesClient

    init {
        Places.initialize(context, BuildConfig.apiKeyGooglePlaces)
        placesClient = Places.createClient(context)
    }

    override suspend fun fetchPhotoMetaData(placeId: String): List<PhotoMetadata> = suspendCancellableCoroutine { continuation ->
        val fields = listOf(Place.Field.PHOTO_METADATAS)

        val placeRequest = FetchPlaceRequest.newInstance(placeId, fields)

        placesClient.fetchPlace(placeRequest)
            .addOnSuccessListener { response ->
                val place = response.place
                val metadata = place.photoMetadatas
                if (metadata == null) {
                    continuation.resume(value = emptyList())
                } else {
                    continuation.resume(value = metadata)
                }
            }
            .addOnFailureListener { exception ->
                Timber.tag(tag).e(exception)
                continuation.resume(value = emptyList())
            }
    }

    override suspend fun fetchPhotoWithAttributions(metadata: PhotoMetadata): Pair<Bitmap, String>? =
        suspendCancellableCoroutine { continuation ->
            val attributions = metadata.attributions

            val photoRequest = FetchPhotoRequest.builder(metadata)
                // .setMaxWidth(500) // Optional.
                //.setMaxHeight(300) // Optional.
                .build()
            placesClient.fetchPhoto(photoRequest)
                .addOnSuccessListener { fetchPhotoResponse: FetchPhotoResponse ->
                    continuation.resume(value = fetchPhotoResponse.bitmap to attributions)
                }.addOnFailureListener { exception: Exception ->
                    Timber.tag(tag).e(exception)
                    continuation.resume(value = null)
                }
        }
}
