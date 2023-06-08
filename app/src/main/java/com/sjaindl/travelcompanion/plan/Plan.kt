package com.sjaindl.travelcompanion.plan

import android.net.Uri
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date

data class Plan(
    val name: String,
    val pinName: String,
    var startDate: Date,
    var endDate: Date,
    var imagePath: Uri?,
) {
    val formattedDate: String
        get() {
            val formatter = DateTimeFormatter.ISO_LOCAL_DATE.withZone(ZoneId.systemDefault())
            val formattedStartDate = formatter.format(startDate.toInstant())
            val formattedEndDate = formatter.format(endDate.toInstant())
            return "$formattedStartDate - $formattedEndDate"
        }
}
