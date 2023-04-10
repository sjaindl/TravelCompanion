package com.sjaindl.travelcompanion.di

import com.sjaindl.travelcompanion.repository.DataRepository
import com.sjaindl.travelcompanion.sqldelight.DatabaseDriverFactory
import org.kodein.di.DI
import org.kodein.di.direct
import org.kodein.di.instance

class PersistenceInjector(driverFactory: DatabaseDriverFactory) {
    private val container = DI.lazy {
        import(PersistenceModule(driverFactory).container)
    }

    val dataRepository = container.direct.instance<DataRepository>()
}
