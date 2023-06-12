package com.sjaindl.travelcompanion.plan.detail

import com.sjaindl.travelcompanion.api.google.Plannable
import com.sjaindl.travelcompanion.plan.PlannableUtils

enum class PlanDetailItemType {
    HOTEL,
    RESTAURANT,
    ATTRACTION
}

fun PlannableUtils.planDetailItems(type: PlanDetailItemType): List<PlanDetailItem> {
    return when (type) {
        PlanDetailItemType.HOTEL -> {
            hotels.map {
                createPlanDetailItemType(plannable = it)
            }
        }

        PlanDetailItemType.RESTAURANT -> {
            restaurants.map {
                createPlanDetailItemType(plannable = it)
            }
        }

        PlanDetailItemType.ATTRACTION -> {
            attractions.map {
                createPlanDetailItemType(plannable = it)
            }
        }
    }
}

fun createPlanDetailItemType(plannable: Plannable): PlanDetailItem {
    val link = plannable.getLink()
    val linkText = plannable.getLinkText()?.toString()
    val attributionWithText = if (link != null && linkText != null) AttributionWithText(link = link, name = linkText) else null

    return PlanDetailItem(
        id = plannable.getId(),
        title = plannable.description(),
        details = plannable.details().toString(),
        attributionWithText = attributionWithText,
        imagePath = plannable.imageUrl(),
    )
}
