package com.sjaindl.travelcompanion.plan.detail

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.EditCalendar
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sjaindl.travelcompanion.baseui.TCAppBar
import com.sjaindl.travelcompanion.model.MapLocationData
import com.sjaindl.travelcompanion.plan.PlanImageElement
import com.sjaindl.travelcompanion.theme.TravelCompanionTheme
import com.sjaindl.travelcompanion.util.LoadingAnimation
import kotlinx.coroutines.ExperimentalCoroutinesApi
import com.sjaindl.travelcompanion.R

@OptIn(ExperimentalCoroutinesApi::class)
@Composable
fun PlanDetailScreen(
    planName: String,
    modifier: Modifier = Modifier,
    viewModel: PlanDetailViewModel = hiltViewModel(
        creationCallback = { factory: PlanDetailViewModel.PlanDetailViewModelFactory ->
            factory.create(planName = planName)
        },
    ),
    onAddPlace: (PlanDetailItemType, String, MapLocationData) -> Unit,
    onChangeDate: (String) -> Unit,
    onAddNote: (plannableId: String, planName: String, planDetailItemType: PlanDetailItemType) -> Unit,
    onChoosePlanImage: (pinId: Long) -> Unit,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit = { },
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(key1 = Unit) {
        viewModel.loadPlan()
    }

    TravelCompanionTheme {
        Scaffold(
            topBar = {
                TCAppBar(
                    title = planName,
                    canNavigateBack = canNavigateBack,
                    navigateUp = navigateUp,
                    customActionIcon = if (state is PlanDetailViewModel.State.Loaded) Icons.Rounded.EditCalendar else null,
                    onCustomAction = {
                        val plan = (state as PlanDetailViewModel.State.Loaded).plan
                        onChangeDate(plan.name)
                    }
                )
            },
        ) { paddingValues ->
            when (state) {
                is PlanDetailViewModel.State.Error -> {
                    val exception = (state as PlanDetailViewModel.State.Error).exception

                    val errorMessage =
                        exception?.localizedMessage ?: exception?.message ?: stringResource(R.string.couldNotRetrieveData)

                    Box(
                        modifier = modifier
                            .fillMaxWidth()
                            .padding(paddingValues),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = errorMessage,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.align(Alignment.Center),
                            textAlign = TextAlign.Center,
                            fontSize = 20.sp,
                        )
                    }
                }

                is PlanDetailViewModel.State.Info -> {
                    val info = (state as PlanDetailViewModel.State.Info)
                    Box(
                        modifier = modifier
                            .fillMaxWidth()
                            .padding(paddingValues),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = stringResource(id = info.stringRes),
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.align(Alignment.Center),
                            textAlign = TextAlign.Center,
                            fontSize = 20.sp,
                        )
                    }
                }

                is PlanDetailViewModel.State.Loaded -> {
                    val result = (state as PlanDetailViewModel.State.Loaded)
                    val plan = result.plan
                    val bitmap = result.bitmap

                    Column(
                        modifier = modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Box(
                            modifier = Modifier
                                .padding(paddingValues)
                                .fillMaxWidth()
                                .padding(horizontal = 64.dp)
                                .clip(CircleShape)
                                .border(
                                    width = 4.dp,
                                    color = colorScheme.onBackground,
                                    shape = CircleShape
                                )
                                .clickable {
                                    val pinId = viewModel.pin?.id ?: return@clickable
                                    onChoosePlanImage(pinId)
                                },
                            contentAlignment = Alignment.Center,
                        ) {
                            PlanImageElement(
                                bitmap = bitmap,
                                imagePath = plan.imagePath,
                                modifier = Modifier,
                                onClick = {
                                    val pinId = viewModel.pin?.id ?: return@PlanImageElement
                                    onChoosePlanImage(pinId)
                                }
                            )
                        }

                        Text(
                            text = plan.formattedDate,
                            fontWeight = FontWeight.Bold,
                            color = colorScheme.primary,
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally),
                            textAlign = TextAlign.Center,
                            fontSize = 16.sp,
                        )

                        PlanDetailItems(
                            planName = plan.name,
                            onAddPlace = {
                                onAddPlace(it, plan.name, viewModel.locationData())
                            },
                            onAddNote = onAddNote
                        )
                    }
                }

                PlanDetailViewModel.State.Loading -> {
                    Box(
                        modifier = modifier
                            .fillMaxWidth()
                            .padding(paddingValues),
                        contentAlignment = Alignment.Center,
                    ) {
                        LoadingAnimation()
                    }
                }
            }
        }
    }
}

@Composable
@Preview
fun PlanDetailScreenPreview() {
    PlanDetailScreen(
        planName = "Bled",
        onAddPlace = { _, _, _ -> },
        canNavigateBack = true,
        onChangeDate = { },
        onAddNote = { _, _, _ -> },
        onChoosePlanImage = { },
    )
}
