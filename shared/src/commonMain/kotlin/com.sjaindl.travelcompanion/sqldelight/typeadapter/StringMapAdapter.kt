package com.sjaindl.travelcompanion.sqldelight.typeadapter

import com.squareup.sqldelight.ColumnAdapter

class StringMapAdapter : ColumnAdapter<Map<String, String>, String> {
    override fun decode(databaseValue: String): Map<String, String> {
        val map = mutableMapOf<String, String>()

        databaseValue.split(",").forEach {
            val entry = it.split("_")
            map[entry[0]] = entry[1]
        }

        return map
    }

    override fun encode(value: Map<String, String>): String {
        return value.entries.joinToString(separator = ",") {
            "${it.key}_${it.value}"
        }
    }
}
