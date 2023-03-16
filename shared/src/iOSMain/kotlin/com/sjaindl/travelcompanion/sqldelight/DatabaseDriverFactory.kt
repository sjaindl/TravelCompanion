package com.sjaindl.travelcompanion.sqldelight

import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.drivers.native.NativeSqliteDriver

actual class DatabaseDriverFactory {
    actual fun createDriver(): SqlDriver {
        return NativeSqliteDriver(TravelCompanionDatabase.Schema, "content.db")
    }
}
