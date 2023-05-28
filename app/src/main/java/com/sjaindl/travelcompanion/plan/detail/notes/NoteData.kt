package com.sjaindl.travelcompanion.plan.detail.notes

import com.sjaindl.travelcompanion.plan.detail.PlanDetailItemType

data class NoteData(
    val plannableId: String,
    val planName: String,
    val planDetailItemType: PlanDetailItemType,
)
