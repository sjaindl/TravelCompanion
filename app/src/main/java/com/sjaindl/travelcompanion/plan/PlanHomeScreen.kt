package com.sjaindl.travelcompanion.plan

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sjaindl.travelcompanion.R
import com.sjaindl.travelcompanion.com.sjaindl.travelcompanion.di.AndroidPersistenceInjector
import com.sjaindl.travelcompanion.theme.TravelCompanionTheme
import com.sjaindl.travelcompanion.util.LoadingAnimation

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PlanHomeScreen(
    modifier: Modifier = Modifier,
    viewModel: PlanViewModel = viewModel(
        factory = PlanViewModelFactory(
            dataRepository = AndroidPersistenceInjector(LocalContext.current).shared.dataRepository,
        )
    ),
    onShowDetails: (Long) -> Unit
) {
    var showDialogForPlan: Plan? by remember { mutableStateOf(null) }

    TravelCompanionTheme {
        val upcomingTrips by viewModel.upcomingTripsFlow.collectAsState()
        val pastTrips by viewModel.pastTripsFlow.collectAsState()
        val state by viewModel.state.collectAsState()

        LaunchedEffect(Unit) {
            viewModel.fetchPlans()
        }

        when (state) {
            PlanViewModel.State.Loading -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(colors.background)
                        .padding(all = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    LoadingAnimation()
                }
            }

            is PlanViewModel.State.Error -> {
                val exception = (state as PlanViewModel.State.Error).exception

                val errorMessage = exception?.localizedMessage ?: exception?.message ?: stringResource(id = R.string.couldNotRetrieveData)

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(colors.background)
                        .padding(all = 16.dp),
                    verticalArrangement = Arrangement.Center,
                ) {
                    Text(
                        text = errorMessage,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        fontSize = 20.sp,
                    )
                }
            }

            PlanViewModel.State.Finished -> {
                LazyColumn(modifier = modifier) {
                    stickyHeader {
                        Text(
                            text = stringResource(id = R.string.upcomingTrips) + " (${upcomingTrips.size})",
                            fontWeight = FontWeight.Bold,
                            color = colors.background,
                            modifier = Modifier
                                .background(colors.onBackground)
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            textAlign = TextAlign.Center,
                            fontSize = 20.sp
                        )
                    }

                    items(
                        items = upcomingTrips,
                        key = { plan -> plan.name }
                    ) {
                        PlanElement(
                            modifier = Modifier,
                            name = it.name,
                            startDate = it.startDate,
                            endDate = it.endDate,
                            imagePath = it.imagePath,
                            onClick = {
                                showDialogForPlan = it
                            }
                        )
                    }

                    stickyHeader {
                        Text(
                            text = stringResource(id = R.string.pastTrips) + " (${pastTrips.size})",
                            fontWeight = FontWeight.Bold,
                            color = colors.background,
                            modifier = Modifier
                                .background(colors.onBackground)
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            textAlign = TextAlign.Center,
                            fontSize = 20.sp
                        )
                    }

                    items(
                        items = pastTrips,
                        key = { plan -> plan.name }
                    ) {
                        PlanElement(
                            modifier = Modifier,
                            name = it.name,
                            startDate = it.startDate,
                            endDate = it.endDate,
                            imagePath = it.imagePath,
                            onClick = {
                                showDialogForPlan = it
                            }
                        )
                    }

                }
            }
        }
    }

    PlanActionBottomSheet(
        show = showDialogForPlan != null,
        title = stringResource(id = R.string.chooseAction),
        onShow = {
            viewModel.onShow()
            showDialogForPlan = null
        },
        onShowDetails = {
            showDialogForPlan?.let {
                viewModel.getPinId(it.pinName)?.let { pinId ->
                    onShowDetails(pinId)
                }
            }
            showDialogForPlan = null
        },
        onDelete = {
            showDialogForPlan?.let {
                viewModel.onDelete(plan = it)
            }
            showDialogForPlan = null
        },
        onCancel = {
            showDialogForPlan = null
        }
    )
}

@Composable
@Preview
fun PlanHomeScreenPreview() {
    PlanHomeScreen(
        onShowDetails = { }
    )
}
