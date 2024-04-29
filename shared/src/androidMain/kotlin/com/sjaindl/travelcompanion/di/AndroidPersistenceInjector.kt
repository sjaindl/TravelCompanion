package com.sjaindl.travelcompanion.com.sjaindl.travelcompanion.di

import android.content.Context
import com.sjaindl.travelcompanion.di.PersistenceInjector
import com.sjaindl.travelcompanion.sqldelight.DatabaseDriverFactory

class AndroidPersistenceInjector(private val context: Context) {
    private val driverFactory by lazy {
        DatabaseDriverFactory(context = context)
    }

    val shared = PersistenceInjector(driverFactory = driverFactory)
}
