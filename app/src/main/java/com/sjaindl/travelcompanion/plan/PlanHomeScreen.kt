package com.sjaindl.travelcompanion.plan

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sjaindl.travelcompanion.R
import com.sjaindl.travelcompanion.baseui.TCAppBar
import com.sjaindl.travelcompanion.theme.TravelCompanionTheme
import com.sjaindl.travelcompanion.util.LoadingAnimation

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PlanHomeScreen(
    modifier: Modifier = Modifier,
    viewModel: PlanViewModel = hiltViewModel(),
    onShowDetails: (Long) -> Unit,
    onShowPlan: (String) -> Unit,
    onAddPlan: () -> Unit,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit = { },
) {
    var showDialogForPlan: Plan? by remember { mutableStateOf(null) }

    val upcomingTrips by viewModel.upcomingTripsFlow.collectAsState()
    val pastTrips by viewModel.pastTripsFlow.collectAsState()
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchPlans()
    }

    TravelCompanionTheme {
        Scaffold(
            containerColor = colorScheme.background,
            topBar = {
                TCAppBar(
                    title = stringResource(R.string.plan),
                    canNavigateBack = canNavigateBack,
                    navigateUp = navigateUp,
                )
            },
            floatingActionButton = {
                TravelCompanionTheme {
                    FloatingActionButton(
                        onClick = {
                            onAddPlan()
                        },
                        containerColor = colorScheme.primary,
                    ) {
                        Row(
                            modifier = Modifier
                                .background(colorScheme.primary)
                                .padding(8.dp),
                        ) {
                            Image(
                                imageVector = Icons.Rounded.Add,
                                contentDescription = stringResource(id = R.string.addPlan),
                            )
                        }
                    }
                }
            }
        ) { paddingValues ->

            when (state) {
                PlanViewModel.State.Loading -> {
                    Column(
                        modifier = Modifier
                            .padding(paddingValues)
                            .fillMaxSize()
                            .background(colorScheme.background)
                            .padding(all = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        LoadingAnimation()
                    }
                }

                is PlanViewModel.State.Error -> {
                    val exception = (state as PlanViewModel.State.Error).exception

                    val errorMessage =
                        exception?.localizedMessage ?: exception?.message ?: stringResource(id = R.string.couldNotRetrieveData)

                    Column(
                        modifier = Modifier
                            .padding(paddingValues)
                            .fillMaxSize()
                            .background(colorScheme.background)
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

                is PlanViewModel.State.Info -> {
                    val info = state as PlanViewModel.State.Info

                    Column(
                        modifier = Modifier
                            .padding(paddingValues)
                            .fillMaxSize()
                            .background(colorScheme.background)
                            .padding(all = 16.dp),
                        verticalArrangement = Arrangement.Center,
                    ) {
                        Text(
                            text = stringResource(id = info.stringRes),
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                            fontSize = 20.sp,
                        )
                    }
                }

                PlanViewModel.State.Finished -> {
                    LazyColumn(modifier = modifier.padding(paddingValues)) {
                        stickyHeader {
                            Text(
                                text = stringResource(id = R.string.upcomingTrips) + " (${upcomingTrips.size})",
                                fontWeight = FontWeight.Bold,
                                color = colorScheme.background,
                                modifier = Modifier
                                    .background(colorScheme.onBackground)
                                    .fillMaxWidth()
                                    .padding(vertical = 16.dp),
                                textAlign = TextAlign.Center,
                                fontSize = 20.sp
                            )
                        }

                        items(
                            items = upcomingTrips,
                            key = { plan -> plan.toString() }
                        ) {
                            PlanElement(
                                modifier = Modifier,
                                name = it.name,
                                dateString = it.formattedDate,
                                bitmap = viewModel.bitmapForPlan(planName = it.name),
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
                                color = colorScheme.background,
                                modifier = Modifier
                                    .background(colorScheme.onBackground)
                                    .fillMaxWidth()
                                    .padding(vertical = 16.dp),
                                textAlign = TextAlign.Center,
                                fontSize = 20.sp
                            )
                        }

                        items(
                            items = pastTrips,
                            key = { plan -> plan.toString() }
                        ) {
                            PlanElement(
                                modifier = Modifier,
                                name = it.name,
                                dateString = it.formattedDate,
                                bitmap = viewModel.bitmapForPlan(planName = it.name),
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
    }

    if (showDialogForPlan != null) {
        PlanActionBottomSheet(
            title = stringResource(id = R.string.chooseAction),
            onShow = {
                showDialogForPlan?.let {
                    onShowPlan(it.name)
                }
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
}

@Composable
@Preview
fun PlanHomeScreenPreview() {
    PlanHomeScreen(
        onShowDetails = { },
        onShowPlan = { },
        onAddPlan = { },
        canNavigateBack = true,
    )
}
