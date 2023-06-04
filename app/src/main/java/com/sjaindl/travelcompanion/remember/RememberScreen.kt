package com.sjaindl.travelcompanion.remember

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sjaindl.travelcompanion.R
import com.sjaindl.travelcompanion.baseui.TCAppBar
import com.sjaindl.travelcompanion.plan.PlanElement
import com.sjaindl.travelcompanion.theme.TravelCompanionTheme
import com.sjaindl.travelcompanion.util.LoadingAnimation

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RememberScreen(
    modifier: Modifier = Modifier,
    viewModel: RememberViewModel = viewModel(),
    canNavigateBack: Boolean,
    navigateUp: () -> Unit = { },
) {
    TravelCompanionTheme {
        Scaffold(
            containerColor = MaterialTheme.colors.background,
            topBar = {
                TCAppBar(
                    title = stringResource(R.string.remember),
                    canNavigateBack = canNavigateBack,
                    navigateUp = navigateUp,
                )
            },
        ) { paddingValues ->
            val rememberTrips by viewModel.rememberTripsFlow.collectAsState()
            val state by viewModel.state.collectAsState()

            LaunchedEffect(Unit) {
                viewModel.fetchPlans()
            }

            when (state) {
                RememberViewModel.State.Loading -> {
                    Column(
                        modifier = Modifier
                            .padding(paddingValues)
                            .fillMaxSize()
                            .background(MaterialTheme.colors.background)
                            .padding(all = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        LoadingAnimation()
                    }
                }

                is RememberViewModel.State.Error -> {
                    val exception = (state as RememberViewModel.State.Error).exception

                    val errorMessage =
                        exception.localizedMessage ?: exception.message ?: stringResource(id = R.string.couldNotRetrieveData)

                    Column(
                        modifier = Modifier
                            .padding(paddingValues)
                            .fillMaxSize()
                            .background(MaterialTheme.colors.background)
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

                is RememberViewModel.State.Info -> {
                    val info = state as RememberViewModel.State.Info

                    Column(
                        modifier = Modifier
                            .padding(paddingValues)
                            .fillMaxSize()
                            .background(MaterialTheme.colors.background)
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

                RememberViewModel.State.Finished -> {
                    LazyColumn(modifier = modifier.padding(paddingValues)) {

                        items(
                            items = rememberTrips,
                            key = { plan -> plan.toString() }
                        ) {
                            PlanElement(
                                modifier = Modifier,
                                name = it.name,
                                dateString = it.formattedDate,
                                imagePath = it.imagePath,
                                onClick = {
                                    // navigate to detail screen
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun RememberScreenPreview() {
    RememberScreen(
        canNavigateBack = false,
    )
}
