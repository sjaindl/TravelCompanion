package com.sjaindl.travelcompanion.plan.detail

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sjaindl.travelcompanion.R
import com.sjaindl.travelcompanion.baseui.TCAppBar
import com.sjaindl.travelcompanion.plan.PlanImageElement
import com.sjaindl.travelcompanion.theme.TravelCompanionTheme
import com.sjaindl.travelcompanion.util.LoadingAnimation
import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(ExperimentalCoroutinesApi::class)
@Composable
fun PlanDetailScreen(
    planName: String,
    modifier: Modifier = Modifier,
    viewModel: PlanDetailViewModel = viewModel(
        factory = PlanDetailViewModel.PlanDetailViewModelFactory(
            plan = planName,
        )
    ),
    canNavigateBack: Boolean,
    navigateUp: () -> Unit = {},
) {
    TravelCompanionTheme {
        Scaffold(
            topBar = {
                TCAppBar(
                    title = planName,
                    canNavigateBack = canNavigateBack,
                    navigateUp = navigateUp,
                )
            },
        ) { paddingValues ->

            val state by viewModel.state.collectAsState()

            LaunchedEffect(key1 = Unit) {
                viewModel.loadPlan()
            }

            when (state) {
                is PlanDetailViewModel.State.Error -> {
                    val exception = (state as PlanDetailViewModel.State.Error).exception

                    val errorMessage = exception?.localizedMessage ?: exception?.message ?: stringResource(R.string.couldNotRetrieveData)

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
                                .fillMaxWidth()
                                .padding(horizontal = 64.dp)
                                .clip(CircleShape)
                                .border(
                                    width = 4.dp,
                                    color = colors.onBackground,
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center,
                        ) {
                            PlanImageElement(
                                bitmap = bitmap,
                                imagePath = plan.imagePath,
                                modifier = Modifier,
                            )
                        }

                        Text(
                            text = plan.formattedDate,
                            fontWeight = FontWeight.Bold,
                            color = colors.primary,
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally),
                            textAlign = TextAlign.Center,
                            fontSize = 16.sp,
                        )

                        PlanDetailItems(
                            plan = plan
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
        planName = "",
        canNavigateBack = true,
    )
}
