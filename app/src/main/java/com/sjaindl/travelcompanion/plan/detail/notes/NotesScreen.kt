package com.sjaindl.travelcompanion.plan.detail.notes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sjaindl.travelcompanion.R
import com.sjaindl.travelcompanion.baseui.TCAppBar
import com.sjaindl.travelcompanion.plan.detail.PlanDetailItemType
import com.sjaindl.travelcompanion.theme.TravelCompanionTheme
import com.sjaindl.travelcompanion.util.LoadingAnimation

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesScreen(
    planName: String,
    plannableId: String,
    planDetailItemType: PlanDetailItemType,
    modifier: Modifier = Modifier,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit = { },
    viewModel: NotesViewModel = viewModel(
        factory = NotesViewModel.NotesViewModelFactory(
            planName = planName,
            plannableId = plannableId,
            planDetailItemType = planDetailItemType,
        )
    ),
) {
    val state by viewModel.state.collectAsState()

    var notes: String? by remember { mutableStateOf(null) }

    val focusManager = LocalFocusManager.current

    val snackBarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.load()
    }

    TravelCompanionTheme {
        TravelCompanionTheme {
            Scaffold(
                snackbarHost = { SnackbarHost(snackBarHostState) },
                topBar = {
                    TCAppBar(
                        title = stringResource(R.string.addNote),
                        canNavigateBack = canNavigateBack,
                        navigateUp = navigateUp,
                    )
                },
            ) { paddingValues ->
                when (state) {
                    NotesViewModel.State.Initial -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(colors.background)
                                .wrapContentSize(Alignment.Center)
                        ) {
                            LoadingAnimation()
                        }
                    }

                    is NotesViewModel.State.Loaded -> {
                        val loaded = (state as NotesViewModel.State.Loaded)
                        if (notes == null) notes = loaded.plannable.getNotes()

                        Column(
                            modifier = modifier
                                .padding(paddingValues)
                                .fillMaxSize()
                                .padding(all = 8.dp),
                        ) {
                            OutlinedTextField(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .weight(9f),
                                colors = TextFieldDefaults.outlinedTextFieldColors(
                                    unfocusedBorderColor = colors.primary,
                                    focusedBorderColor = colors.primary,
                                    containerColor = Color.White,
                                ),
                                value = notes.orEmpty(),
                                onValueChange = {
                                    notes = it
                                },
                                placeholder = {
                                    Text(text = stringResource(id = R.string.add_notes))
                                }
                            )

                            Divider()

                            val buttonColors = ButtonDefaults.buttonColors(
                                containerColor = colorResource(id = R.color.colorMain),
                                contentColor = colorResource(id = R.color.textLight),
                            )

                            // TODO: Replace with MotionLayout & FAB?
                            Row(
                                modifier = Modifier.weight(1f),
                            ) {
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
                                    onClick = {
                                        focusManager.clearFocus(force = true)

                                        viewModel.persistNotes(
                                            plannable = loaded.plannable,
                                            notes = notes.orEmpty(),
                                        )
                                    },
                                    colors = buttonColors,
                                ) {
                                    Text(
                                        text = stringResource(id = R.string.addNote)
                                    )
                                }
                            }
                        }
                    }

                    is NotesViewModel.State.Error -> {
                        val exception = (state as NotesViewModel.State.Error).exception

                        val errorMessage =
                            exception?.localizedMessage ?: exception?.message ?: stringResource(id = R.string.couldNotRetrieveData)

                        Column(
                            modifier = modifier
                                .padding(paddingValues)
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

                    NotesViewModel.State.Finished -> {
                        val message = stringResource(id = R.string.notes_added)
                        LaunchedEffect(Unit) {
                            snackBarHostState.showSnackbar(
                                message = message
                            )
                        }

                        navigateUp()
                    }

                    is NotesViewModel.State.Info -> {
                        val info = (state as NotesViewModel.State.Info)

                        Column(
                            modifier = modifier
                                .padding(paddingValues)
                                .fillMaxSize()
                                .background(colors.background)
                                .padding(all = 16.dp),
                            verticalArrangement = Arrangement.Center,
                        ) {
                            Text(
                                text = stringResource(id = info.res),
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.align(Alignment.CenterHorizontally),
                                fontSize = 20.sp,
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
fun NotesScreenPreview() {
    NotesScreen(
        planName = "Bled",
        plannableId = "ChIJBXUvjSyRekcR3NxF5Mbb054",
        planDetailItemType = PlanDetailItemType.HOTEL,
        canNavigateBack = true,
    )
}
