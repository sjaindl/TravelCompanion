package com.sjaindl.travelcompanion.prefs

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.sjaindl.travelcompanion.model.MapLocationData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class MapLocationDataPreferences(private val context: Context) {
    companion object {
        private val LOCATION_KEY = stringPreferencesKey(name = "lastLocation")
    }

    val lastLocationFlow: Flow<MapLocationData> = context.settingsDataStore.data.map { preferences ->
        preferences[LOCATION_KEY]?.let { encoded ->
            Json.decodeFromString(encoded)
        } ?: MapLocationData.default
    }

    suspend fun updateLastLocation(latitude: Float, longitude: Float, radius: Float) {
        val mapLocationData = MapLocationData(latitude = latitude, longitude = longitude, radius = radius)
        val encoded = Json.encodeToString(value = mapLocationData)
        context.settingsDataStore.edit {
            it[LOCATION_KEY] = encoded
        }
    }
}
