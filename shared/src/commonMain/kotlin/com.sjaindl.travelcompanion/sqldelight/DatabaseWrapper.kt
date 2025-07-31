package com.sjaindl.travelcompanion.sqldelight

import com.sjaindl.travelcompanion.Country
import com.sjaindl.travelcompanion.Pin
import com.sjaindl.travelcompanion.sqldelight.typeadapter.CurrencyListToStringAdapter
import com.sjaindl.travelcompanion.sqldelight.typeadapter.DoubleToStringListAdapter
import com.sjaindl.travelcompanion.sqldelight.typeadapter.FlagToStringAdapter
import com.sjaindl.travelcompanion.sqldelight.typeadapter.InstantToLongAdapter
import com.sjaindl.travelcompanion.sqldelight.typeadapter.LanguageListToStringAdapter
import com.sjaindl.travelcompanion.sqldelight.typeadapter.RegionalBlockListToStringAdapter
import com.sjaindl.travelcompanion.sqldelight.typeadapter.StringMapAdapter
import com.sjaindl.travelcompanion.sqldelight.typeadapter.StringToStringListAdapter

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
