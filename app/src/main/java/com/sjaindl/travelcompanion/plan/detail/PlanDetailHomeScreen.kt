package com.sjaindl.travelcompanion.plan.detail

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.sjaindl.travelcompanion.plan.navigation.PlanDetailNavHost
import com.sjaindl.travelcompanion.theme.TravelCompanionTheme

@Composable
fun PlanDetailHomeScreen(
    planName: String,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit = {},
) {
    val navController = rememberNavController()

    TravelCompanionTheme {
        PlanDetailNavHost(
            navController = navController,
            modifier = Modifier
                .fillMaxSize(),
            plan = planName,
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
        canNavigateBack = false,
    )
}
