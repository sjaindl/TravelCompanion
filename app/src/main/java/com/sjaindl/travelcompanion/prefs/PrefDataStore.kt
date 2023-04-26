package com.sjaindl.travelcompanion.prefs

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

// At the top level of your kotlin file:
val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
