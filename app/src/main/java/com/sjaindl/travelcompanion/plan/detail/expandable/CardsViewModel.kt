package com.sjaindl.travelcompanion.plan.detail.expandable

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sjaindl.travelcompanion.plan.Plan
import com.sjaindl.travelcompanion.plan.detail.PlanDetailItem
import com.sjaindl.travelcompanion.plan.detail.PlanDetailItemType
import com.sjaindl.travelcompanion.plan.detail.planDetailItems
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import timber.log.Timber

class CardsViewModel(private val plan: Plan) : ViewModel() {
    private val items = listOf(
        ExpandableCardModel(1, PlanDetailItemType.HOTEL),
        ExpandableCardModel(2, PlanDetailItemType.RESTAURANT),
        ExpandableCardModel(3, PlanDetailItemType.ATTRACTION),
    )

    val cards = MutableStateFlow(items)
    val expandedCardIdsList: MutableStateFlow<List<Int>> = MutableStateFlow(emptyList())

    val hotels: MutableStateFlow<List<PlanDetailItem>> = MutableStateFlow(emptyList())
    val restaurants: MutableStateFlow<List<PlanDetailItem>> = MutableStateFlow(emptyList())
    val attractions: MutableStateFlow<List<PlanDetailItem>> = MutableStateFlow(emptyList())

    fun loadPlannables() {
        // load subdocuments of plan
        plan.loadPlannables { exception ->
            Timber.e(exception)
            hotels.update {
                plan.planDetailItems(PlanDetailItemType.HOTEL)
            }
            restaurants.update {
                plan.planDetailItems(PlanDetailItemType.RESTAURANT)
            }
            attractions.update {
                plan.planDetailItems(PlanDetailItemType.ATTRACTION)
            }
        }
    }

    fun onCardArrowClicked(id: Int) {
        val list = expandedCardIdsList.value.toMutableList()
        if (list.contains(id)) list.remove(id) else list.add(id)
        expandedCardIdsList.value = list
    }

    class CardsViewModelFactory(private val plan: Plan) : ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return CardsViewModel(plan) as T
        }
    }
}
