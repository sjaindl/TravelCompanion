package com.sjaindl.travelcompanion.sqldelight.typeadapter

import com.squareup.sqldelight.ColumnAdapter
import kotlinx.datetime.Instant

class InstantToLongAdapter : ColumnAdapter<Instant, Long> {
    override fun decode(databaseValue: Long): Instant {
        return Instant.fromEpochMilliseconds(databaseValue)
    }

    override fun encode(value: Instant): Long {
        return value.toEpochMilliseconds()
    }
}
