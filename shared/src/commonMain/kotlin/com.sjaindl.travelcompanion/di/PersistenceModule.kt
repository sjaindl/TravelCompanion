package com.sjaindl.travelcompanion.di

import com.sjaindl.travelcompanion.repository.DataRepository
import com.sjaindl.travelcompanion.repository.DataRepositoryImpl
import com.sjaindl.travelcompanion.sqldelight.DatabaseDriverFactory
import com.sjaindl.travelcompanion.sqldelight.DatabaseWrapper
import org.kodein.di.DI
import org.kodein.di.bindProvider
import org.kodein.di.bindSingleton
import org.kodein.di.instance

class PersistenceModule(driverFactory: DatabaseDriverFactory) {
    val container = DI.Module(name = "persistence") {
        bindProvider {
            DatabaseWrapper(driverFactory).dbQueries
        }

        bindSingleton { DatabaseWrapper(driverFactory) }

        bindSingleton<DataRepository> { DataRepositoryImpl(instance()) }
    }
}
