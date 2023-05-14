package com.sjaindl.travelcompanion.plan.detail

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.sjaindl.travelcompanion.plan.detail.bottomnav.PlanDetailsBottomNavigation
import com.sjaindl.travelcompanion.plan.navigation.PlanDetailNavHost
import com.sjaindl.travelcompanion.theme.TravelCompanionTheme

@Composable
fun PlanDetailHomeScreen(planName: String) {
    val navController = rememberNavController()

    TravelCompanionTheme {
        Scaffold(
            bottomBar = { PlanDetailsBottomNavigation(navController = navController, planName = planName) },
        ) { innerPadding ->
            PlanDetailNavHost(
                navController = navController,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                plan = planName,
            )
        }
    }
}

@Preview
@Composable
fun PlanDetailContainerPreview() {
    PlanDetailHomeScreen(planName = "Graz")
}
