package com.sjaindl.travelcompanion.sqldelight.typeadapter

import com.sjaindl.travelcompanion.model.Language
import com.squareup.sqldelight.ColumnAdapter
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class LanguageListToStringAdapter : ColumnAdapter<List<Language>, String> {
    override fun decode(databaseValue: String): List<Language> {
        return Json.decodeFromString(databaseValue)
    }

    override fun encode(value: List<Language>): String {
        return Json.encodeToString(value)
    }
}
