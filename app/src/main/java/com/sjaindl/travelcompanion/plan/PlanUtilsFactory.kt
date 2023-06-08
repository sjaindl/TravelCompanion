package com.sjaindl.travelcompanion.plan

object PlanUtilsFactory {
    private val planUtilsMap = mutableMapOf<String, PlanUtils>()

    fun getOrCreate(planName: String): PlanUtils {
        val utils = planUtilsMap.getOrDefault(planName, PlanUtils(planName = planName))
        planUtilsMap[planName] = utils
        return utils
    }
}
