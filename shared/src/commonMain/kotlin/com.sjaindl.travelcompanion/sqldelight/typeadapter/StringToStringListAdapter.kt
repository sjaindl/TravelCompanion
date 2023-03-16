package com.sjaindl.travelcompanion.sqldelight.typeadapter

import com.squareup.sqldelight.ColumnAdapter

class StringToStringListAdapter : ColumnAdapter<List<String>, String> {
    override fun decode(databaseValue: String): List<String> =
        if (databaseValue.isEmpty()) {
            listOf()
        } else {
            databaseValue.split(",")
        }

    override fun encode(value: List<String>): String = value.joinToString(separator = ",")
}
