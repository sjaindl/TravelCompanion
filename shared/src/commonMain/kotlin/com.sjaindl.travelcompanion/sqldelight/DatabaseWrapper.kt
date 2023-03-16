package com.sjaindl.travelcompanion.sqldelight

import com.sjaindl.travelcompanion.Country
import com.sjaindl.travelcompanion.Pin
import com.sjaindl.travelcompanion.sqldelight.typeadapter.*

class DatabaseWrapper(databaseDriverFactory: DatabaseDriverFactory) {
    private val database =
        TravelCompanionDatabase(
            driver = databaseDriverFactory.createDriver(),
            CountryAdapter = Country.Adapter(
                topLevelDomainAdapter = StringToStringListAdapter(),
                callingCodesAdapter = StringToStringListAdapter(),
                altSpellingsAdapter = StringToStringListAdapter(),
                latlngAdapter = DoubleToStringListAdapter(),
                timezonesAdapter = StringToStringListAdapter(),
                bordersAdapter = StringToStringListAdapter(),
                translationsAdapter = StringMapAdapter(),
                flagsAdapter = FlagToStringAdapter(),
                currencyAdapter = CurrencyListToStringAdapter(),
                languageAdapter = LanguageListToStringAdapter(),
                regionalBlockAdapter = RegionalBlockListToStringAdapter(),
            ),
            PinAdapter = Pin.Adapter(InstantToLongAdapter()),
        )

    val dbQueries = database.travelCompanionDatabaseQueries
}
