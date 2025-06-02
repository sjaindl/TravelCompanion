package com.sjaindl.travelcompanion.plan.detail

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation3.runtime.rememberNavBackStack
import com.sjaindl.travelcompanion.plan.navigation.PlanDetail
import com.sjaindl.travelcompanion.plan.navigation.PlanDetailNavDisplay
import com.sjaindl.travelcompanion.theme.TravelCompanionTheme

@Composable
fun PlanDetailHomeScreen(
    planName: String,
    canNavigateBack: () -> Boolean,
    navigateUp: () -> Unit = {},
    onChoosePlanImage: (pinId: Long) -> Unit,
) {
    val backStack = rememberNavBackStack(PlanDetail(planName = planName))

    TravelCompanionTheme {
        PlanDetailNavDisplay(
            backStack = backStack,
            modifier = Modifier
                .fillMaxSize(),
            plan = planName,
            onChoosePlanImage = onChoosePlanImage,
            canNavigateBack = canNavigateBack,
            navigateUp = navigateUp,
        )
    }
}

@Preview
@Composable
fun PlanDetailContainerPreview() {
    PlanDetailHomeScreen(
        planName = "Graz",
        canNavigateBack = { true },
        navigateUp = { },
        onChoosePlanImage = { },
    )
}
