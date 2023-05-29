package com.sjaindl.travelcompanion.plan.detail.expandable

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sjaindl.travelcompanion.plan.Plan
import com.sjaindl.travelcompanion.plan.detail.PlanDetailItem
import com.sjaindl.travelcompanion.plan.detail.PlanDetailItemType
import com.sjaindl.travelcompanion.plan.detail.notes.NoteData
import com.sjaindl.travelcompanion.plan.detail.planDetailItems
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
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

    private val _showDialog = MutableStateFlow(false)
    val showDialog = _showDialog.asStateFlow()

    private val _bottomSheetData: MutableStateFlow<NoteData?> = MutableStateFlow(null)
    var bottomSheetData = _bottomSheetData.asStateFlow()

    private val _exception: MutableStateFlow<Exception?> = MutableStateFlow(null)
    var exception = _exception.asStateFlow()

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

    fun onDelete() {
        _showDialog.value = false
        // TODO
    }

    fun onDismiss() {
        _showDialog.value = false
    }

    fun clickedOnItem(plannableId: String, name: String, type: PlanDetailItemType) {
        _showDialog.value = true
        _bottomSheetData.value = NoteData(plannableId = plannableId, planName = name, planDetailItemType = type)
    }

    class CardsViewModelFactory(private val plan: Plan) :
        ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return CardsViewModel(plan = plan) as T
        }
    }
}