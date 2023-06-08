package com.sjaindl.travelcompanion.plan

object PlannableUtilsFactory {
    private val plannableUtilsMap = mutableMapOf<String, PlannableUtils>()

    fun getOrCreate(planName: String): PlannableUtils {
        val utils = plannableUtilsMap.getOrDefault(planName, PlannableUtils(planName = planName))
        plannableUtilsMap[planName] = utils
        return utils
    }
}
