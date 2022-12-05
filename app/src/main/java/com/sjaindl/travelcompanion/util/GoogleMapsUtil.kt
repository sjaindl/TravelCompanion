package com.sjaindl.travelcompanion.util

import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.SphericalUtil

/**
 * Utility class for sharing common map based calculations.
 */
object GoogleMapsUtil {

    /**
     * Prepares [CameraUpdate] for [GoogleMap] component.
     * The current Google Maps API V2 is not able to directly set a center with a visible radius.
     * As workaround we can calculate the southwest and northwest bounds which should be shown.
     *
     * @param latitude: The map center latitude.
     * @param longitude: The map center longitude.
     * @param radius: The radius in kilometers.
     *
     * @see https://stackoverflow.com/a/31029389
     */
    fun getCameraUpdate(latitude: Double, longitude: Double, radius: Double): CameraUpdate {
        val mapBounds = getLatLngBounds(latitude, longitude, radius)

        return CameraUpdateFactory.newLatLngBounds(mapBounds, 0)
    }

    /**
     * Calculates bounds of given location.
     *
     * @param latitude: The map center latitude.
     * @param longitude: The map center longitude.
     * @param radius: The radius in kilometers.
     */
    private fun getLatLngBounds(latitude: Double, longitude: Double, radius: Double): LatLngBounds {
        val mapCenter = LatLng(latitude, longitude)
        val radiusMeters = radius * 1000

        return LatLngBounds.Builder()
            .include(SphericalUtil.computeOffset(mapCenter, radiusMeters, 0.0))
            .include(SphericalUtil.computeOffset(mapCenter, radiusMeters, 90.0))
            .include(SphericalUtil.computeOffset(mapCenter, radiusMeters, 180.0))
            .include(SphericalUtil.computeOffset(mapCenter, radiusMeters, 270.0))
            .build()
    }
}
