package com.sjaindl.travelcompanion.sqldelight.typeadapter

import com.sjaindl.travelcompanion.model.RegionalBlock
import com.squareup.sqldelight.ColumnAdapter
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class RegionalBlockListToStringAdapter : ColumnAdapter<List<RegionalBlock>, String> {
    override fun decode(databaseValue: String): List<RegionalBlock> {
        return Json.decodeFromString(databaseValue)
    }

    override fun encode(value: List<RegionalBlock>): String {
        return Json.encodeToString(value)
    }
}
