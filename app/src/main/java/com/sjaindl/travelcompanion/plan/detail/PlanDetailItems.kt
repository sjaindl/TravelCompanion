package com.sjaindl.travelcompanion.plan.detail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sjaindl.travelcompanion.R
import com.sjaindl.travelcompanion.plan.Plan
import com.sjaindl.travelcompanion.plan.detail.bottomsheet.PlanItemActionBottomSheet
import com.sjaindl.travelcompanion.plan.detail.expandable.CardsViewModel
import com.sjaindl.travelcompanion.plan.detail.expandable.ExpandableCard
import com.sjaindl.travelcompanion.theme.TravelCompanionTheme
import kotlinx.coroutines.ExperimentalCoroutinesApi
import timber.log.Timber
import java.util.Date

// https://developer.android.com/jetpack/compose/touch-input/pointer-input/scroll
@ExperimentalCoroutinesApi
@Composable
fun PlanDetailItems(
    plan: Plan,
    onAddPlace: (PlanDetailItemType) -> Unit,
    onAddNote: (plannableId: String, planName: String, planDetailItemType: PlanDetailItemType) -> Unit,
    viewModel: CardsViewModel = viewModel(
        factory = CardsViewModel.CardsViewModelFactory(
            plan = plan,
        ),
    )
) {
    val tag = "PlanDetailItems"

    val cards = viewModel.cards.collectAsState()
    val expandedCardIds = viewModel.expandedCardIdsList.collectAsState()

    val hotels by viewModel.hotels.collectAsState()
    val restaurants by viewModel.restaurants.collectAsState()
    val attractions by viewModel.attractions.collectAsState()

    val showDialogState by viewModel.showDialog.collectAsState()
    val bottomSheetData by viewModel.bottomSheetData.collectAsState()

    LaunchedEffect(key1 = Unit) {
        viewModel.loadPlannables()
    }

    TravelCompanionTheme {
        PlanItemActionBottomSheet(
            show = showDialogState,
            title = stringResource(id = R.string.chooseAction),
            onAddNote = {
                viewModel.onDismiss()
                val data = bottomSheetData ?: return@PlanItemActionBottomSheet
                onAddNote(data.plannableId, data.planName, data.planDetailItemType)
            },
            onDelete = viewModel::onDelete,
            onCancel = viewModel::onDismiss,
        ) {
            Scaffold(
                backgroundColor = colors.background,
            ) { paddingValues ->
                Column(
                    modifier = Modifier
                        .padding(paddingValues = paddingValues)
                        .verticalScroll(rememberScrollState())
                ) {
                    //items(cards.value) { card ->
                    cards.value.forEach { card ->
                        val items = when (card.type) {
                            PlanDetailItemType.HOTEL -> hotels
                            PlanDetailItemType.RESTAURANT -> restaurants
                            PlanDetailItemType.ATTRACTION -> attractions
                        }

                        ExpandableCard(
                            card = card,
                            planDetailItems = items,
                            onCardArrowClick = { viewModel.onCardArrowClicked(card.id) },
                            onAdd = {
                                Timber.d(tag, "Add ${card.type} to plan")
                                onAddPlace(card.type)
                            },
                            expanded = expandedCardIds.value.contains(card.id),
                            onClick = { plannableId ->
                                viewModel.clickedOnItem(plannableId, plan.name, card.type)
                            }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
@Composable
@Preview
fun PlanDetailItemsPreview() {
    PlanDetailItems(
        plan = Plan(
            "Singapore",
            "Singapore",
            startDate = Date(),
            endDate = Date(),
            imagePath = null,
        ),
        onAddPlace = { },
        onAddNote = { _, _, _ -> }
    )
}