package com.sjaindl.travelcompanion.plan

import com.sjaindl.travelcompanion.TCApplication

object PlannableUtilsFactory {
    private val plannableUtilsMap = mutableMapOf<String, PlannableUtils>()

    fun getOrCreate(planName: String): PlannableUtils {
        val utils = plannableUtilsMap.getOrDefault(planName, PlannableUtils(planName = planName).apply {
            initialize(context = TCApplication.appContext)
        })
        plannableUtilsMap[planName] = utils
        return utils
    }
}
