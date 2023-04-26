package com.sjaindl.travelcompanion.prefs

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.sjaindl.travelcompanion.model.MapLocationData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class MapLocationDataPrefs(private val context: Context) {
    companion object {
        private val LOCATION_KEY = stringPreferencesKey("lastLocation")
    }

    val lastLocationFlow: Flow<MapLocationData> = context.settingsDataStore.data.map { preferences ->
        preferences[LOCATION_KEY]?.let { encoded ->
            Json.decodeFromString(encoded)
        } ?: MapLocationData(latitude = 37.38605f, longitude = -122.083855f, radius = 50.0f)
    }

    suspend fun updateLastLocation(latitude: Float, longitude: Float, radius: Float) {
        val mapLocationData = MapLocationData(latitude = latitude, longitude = longitude, radius = radius)
        val encoded = Json.encodeToString(mapLocationData)
        context.settingsDataStore.edit {
            it[LOCATION_KEY] = encoded
        }
    }
}
