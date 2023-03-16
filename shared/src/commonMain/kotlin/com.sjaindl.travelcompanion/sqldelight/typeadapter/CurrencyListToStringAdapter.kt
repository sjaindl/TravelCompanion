package com.sjaindl.travelcompanion.sqldelight.typeadapter

import com.sjaindl.travelcompanion.model.Currency
import com.squareup.sqldelight.ColumnAdapter
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class CurrencyListToStringAdapter : ColumnAdapter<List<Currency>, String> {
    override fun decode(databaseValue: String): List<Currency> {
        return Json.decodeFromString(databaseValue)
    }

    override fun encode(value: List<Currency>): String {
        return Json.encodeToString(value)
    }
}
