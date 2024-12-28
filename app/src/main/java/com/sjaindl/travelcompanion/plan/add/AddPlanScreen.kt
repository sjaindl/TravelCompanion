package com.sjaindl.travelcompanion.plan.add

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults.TrailingIcon
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.platform.LocalFocusManager
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
import com.sjaindl.travelcompanion.theme.TravelCompanionTheme
import com.sjaindl.travelcompanion.util.LoadingAnimation
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPlanScreen(
    modifier: Modifier = Modifier,
    preselectedDestination: String? = null,
    viewModel: AddPlanViewModel = hiltViewModel(),
    canNavigateBack: Boolean,
    navigateUp: () -> Unit = { },
    planAdded: () -> Unit = { },
) {
    val focusManager = LocalFocusManager.current

    var placeSelectionIsExpanded by remember { mutableStateOf(false) }
    var selectedDestination: String? by remember { mutableStateOf(preselectedDestination) }
    var displayName: String? by remember { mutableStateOf(preselectedDestination) }
    var startDate: Date? by remember { mutableStateOf(null) }
    var endDate: Date? by remember { mutableStateOf(null) }

    TravelCompanionTheme {
        Scaffold(
            topBar = {
                TCAppBar(
                    title = stringResource(R.string.addPlan),
                    canNavigateBack = canNavigateBack,
                    navigateUp = navigateUp,
                )
            },
        ) { paddingValues ->

            LaunchedEffect(Unit) {
                viewModel.fetchPlans()
            }

            val state by viewModel.state.collectAsState()

            when (state) {
                AddPlanViewModel.State.Loading -> {
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

                is AddPlanViewModel.State.Error -> {
                    val exception = (state as AddPlanViewModel.State.Error).exception

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

                is AddPlanViewModel.State.Info -> {
                    val info = state as AddPlanViewModel.State.Info

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

                is AddPlanViewModel.State.LoadedPlaces -> {
                    val places = (state as AddPlanViewModel.State.LoadedPlaces).places

                    if (selectedDestination.isNullOrEmpty() && places.isNotEmpty()) {
                        selectedDestination = places.first()
                        displayName = places.first()
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
                            text = stringResource(id = R.string.destination),
                            fontWeight = FontWeight.Bold,
                            color = colorScheme.background,
                            modifier = Modifier
                                .background(colorScheme.onBackground)
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            textAlign = TextAlign.Center,
                            fontSize = 20.sp
                        )

                        ExposedDropdownMenuBox(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(all = 8.dp),
                            expanded = placeSelectionIsExpanded,
                            onExpandedChange = {
                                placeSelectionIsExpanded = !placeSelectionIsExpanded
                            }
                        ) {
                            TextField(
                                value = selectedDestination ?: "",
                                onValueChange = {},
                                readOnly = true,
                                trailingIcon = { TrailingIcon(expanded = placeSelectionIsExpanded) },
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth()
                            )

                            ExposedDropdownMenu(
                                expanded = placeSelectionIsExpanded,
                                onDismissRequest = { placeSelectionIsExpanded = false },
                                modifier = Modifier
                                    .fillMaxWidth()
                            ) {
                                places.forEach { item ->
                                    DropdownMenuItem(
                                        text = {
                                            Text(text = item)
                                        },
                                        onClick = {
                                            selectedDestination = item
                                            displayName = item
                                            placeSelectionIsExpanded = false
                                        },
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }
                        }

                        if (selectedDestination != null) {
                            Text(
                                text = stringResource(id = R.string.displayName),
                                fontWeight = FontWeight.Bold,
                                color = colorScheme.background,
                                modifier = Modifier
                                    .background(colorScheme.onBackground)
                                    .fillMaxWidth()
                                    .padding(vertical = 16.dp),
                                textAlign = TextAlign.Center,
                                fontSize = 20.sp
                            )

                            OutlinedTextField(
                                colors = TextFieldDefaults.colors(),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(all = 8.dp),
                                enabled = places.isNotEmpty(),
                                value = displayName.orEmpty(),
                                onValueChange = {
                                    displayName = it
                                },
                            )
                        }

                        if (!displayName.isNullOrEmpty()) {
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
                                onDateSelected = {
                                    startDate = it
                                    focusManager.clearFocus(force = true)
                                }
                            )
                        }

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
                                minDate = startDate,
                                onDateSelected = {
                                    endDate = it
                                    focusManager.clearFocus(force = true)
                                }
                            )
                        }

                        HorizontalDivider()

                        val buttonColors = ButtonDefaults.buttonColors(
                            containerColor = colorResource(id = R.color.colorMain),
                            contentColor = colorResource(id = R.color.textLight),
                            disabledContentColor = colorResource(id = R.color.textLight).copy(alpha = 0.4f),
                            disabledContainerColor = colorResource(id = R.color.colorMain).copy(alpha = 0.4f),
                        )

                        Row {
                            Button(
                                modifier = Modifier
                                    .padding(all = 8.dp)
                                    .weight(1f),
                                onClick = {
                                    focusManager.clearFocus(force = true)
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
                                enabled = selectedDestination != null && displayName != null && startDate != null && endDate != null,
                                onClick = {
                                    focusManager.clearFocus(force = true)

                                    val destination = selectedDestination ?: return@Button
                                    val name = displayName ?: return@Button
                                    val start = startDate ?: return@Button
                                    val end = endDate ?: return@Button

                                    viewModel.addPlan(
                                        name = destination,
                                        pinName = name,
                                        startDate = start,
                                        endDate = end,
                                    ) {
                                        focusManager.clearFocus(force = true)
                                        planAdded()
                                    }
                                },
                                colors = buttonColors,
                            ) {
                                Text(
                                    text = stringResource(id = R.string.addPlan)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
@Preview
fun AddPlanScreenPreview() {
    AddPlanScreen(
        canNavigateBack = true,
    )
}
