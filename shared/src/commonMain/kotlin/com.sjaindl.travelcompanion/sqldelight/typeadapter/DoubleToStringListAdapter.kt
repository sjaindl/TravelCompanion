package com.sjaindl.travelcompanion.sqldelight.typeadapter

import com.squareup.sqldelight.ColumnAdapter

class DoubleToStringListAdapter : ColumnAdapter<List<Double>, String> {
    override fun decode(databaseValue: String): List<Double> {
        return databaseValue.split(",").map { it.toDouble() }
    }

    override fun encode(value: List<Double>): String = value.joinToString(separator = ",")
}
