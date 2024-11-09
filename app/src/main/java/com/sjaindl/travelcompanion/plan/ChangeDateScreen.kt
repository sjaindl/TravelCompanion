package com.sjaindl.travelcompanion.plan

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sjaindl.travelcompanion.R
import com.sjaindl.travelcompanion.baseui.TCAppBar
import com.sjaindl.travelcompanion.plan.add.PickDateElement
import com.sjaindl.travelcompanion.theme.TravelCompanionTheme
import com.sjaindl.travelcompanion.util.LoadingAnimation
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangeDateScreen(
    planName: String,
    modifier: Modifier = Modifier,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit = { },
    viewModel: ChangeDateViewModel = hiltViewModel(
        creationCallback = { factory: ChangeDateViewModel.ChangeDateViewModelFactory ->
            factory.create(planName = planName)
        },
    ),
) {
    var startDate: Date? by remember {
        mutableStateOf(null)
    }

    var endDate: Date? by remember {
        mutableStateOf(null)
    }

    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadPlan()
    }

    TravelCompanionTheme {
        Scaffold(
            topBar = {
                TCAppBar(
                    title = stringResource(R.string.changeDate),
                    canNavigateBack = canNavigateBack,
                    navigateUp = navigateUp,
                )
            },
        ) { paddingValues ->
            when (state) {
                ChangeDateViewModel.State.Loading -> {
                    Column(
                        modifier = modifier
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

                is ChangeDateViewModel.State.Error -> {
                    val exception = (state as ChangeDateViewModel.State.Error).exception

                    val errorMessage =
                        exception?.localizedMessage ?: exception?.message ?: stringResource(id = R.string.couldNotRetrieveData)

                    Column(
                        modifier = modifier
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

                is ChangeDateViewModel.State.Info -> {
                    val info = (state as ChangeDateViewModel.State.Info)

                    Column(
                        modifier = modifier
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

                is ChangeDateViewModel.State.Loaded -> {
                    val plan = (state as ChangeDateViewModel.State.Loaded).plan

                    LaunchedEffect(Unit) {
                        startDate = plan.startDate
                        endDate = plan.endDate
                    }

                    val scrollState = rememberScrollState()

                    Column(
                        modifier = modifier
                            .fillMaxWidth()
                            .padding(paddingValues)
                            .verticalScroll(state = scrollState),
                    )
                    {
                        Text(
                            text = stringResource(id = R.string.startDate),
                            fontWeight = FontWeight.Bold,
                            color = colorScheme.background,
                            modifier = Modifier
                                .background(colorScheme.onBackground)
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            textAlign = TextAlign.Center,
                            fontSize = 20.sp
                        )

                        PickDateElement(
                            prefilled = startDate,
                            onDateSelected = {
                                startDate = it
                                endDate = null
                            }
                        )

                        if (startDate != null) {
                            Text(
                                text = stringResource(id = R.string.endDate),
                                fontWeight = FontWeight.Bold,
                                color = colorScheme.background,
                                modifier = Modifier
                                    .background(colorScheme.onBackground)
                                    .fillMaxWidth()
                                    .padding(vertical = 16.dp),
                                textAlign = TextAlign.Center,
                                fontSize = 20.sp
                            )

                            PickDateElement(
                                prefilled = endDate,
                                minDate = startDate,
                                onDateSelected = {
                                    endDate = it
                                }
                            )
                        }

                        HorizontalDivider()

                        val buttonColors = ButtonDefaults.buttonColors(
                            containerColor = colorResource(id = R.color.colorMain),
                            contentColor = colorResource(id = R.color.textLight),
                        )

                        Row {
                            Button(
                                modifier = Modifier
                                    .padding(all = 8.dp)
                                    .weight(1f),
                                onClick = {
                                    navigateUp()
                                },
                                colors = buttonColors,
                            ) {
                                Text(
                                    text = stringResource(id = R.string.cancel)
                                )
                            }

                            Button(
                                modifier = Modifier
                                    .padding(all = 8.dp)
                                    .weight(1f),
                                enabled = startDate != null && endDate != null,
                                onClick = {
                                    val start = startDate ?: return@Button
                                    val end = endDate ?: return@Button

                                    viewModel.changeDate(
                                        planName = plan.name,
                                        startDate = start,
                                        endDate = end,
                                    )
                                },
                                colors = buttonColors,
                            ) {
                                Text(
                                    text = stringResource(id = R.string.changeDate)
                                )
                            }
                        }
                    }
                }

                ChangeDateViewModel.State.Done -> {
                    navigateUp()
                }
            }
        }
    }
}

@Composable
@Preview
fun ChangeDateScreenPreview() {
    ChangeDateScreen(
        planName = "Bled",
        canNavigateBack = true,
    )
}
