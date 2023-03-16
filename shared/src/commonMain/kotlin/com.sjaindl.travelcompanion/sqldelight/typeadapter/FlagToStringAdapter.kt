package com.sjaindl.travelcompanion.sqldelight.typeadapter

import com.sjaindl.travelcompanion.model.Flag
import com.squareup.sqldelight.ColumnAdapter
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class FlagToStringAdapter : ColumnAdapter<Flag, String> {
    override fun decode(databaseValue: String): Flag {
        return Json.decodeFromString(databaseValue)
    }

    override fun encode(value: Flag): String {
        return Json.encodeToString(value)
    }
}
