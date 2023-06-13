package com.sjaindl.travelcompanion.plan.detail.expandable

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sjaindl.travelcompanion.plan.PlannableUtilsFactory
import com.sjaindl.travelcompanion.plan.detail.PlanDetailItem
import com.sjaindl.travelcompanion.plan.detail.PlanDetailItemType
import com.sjaindl.travelcompanion.plan.detail.notes.NoteData
import com.sjaindl.travelcompanion.plan.detail.planDetailItems
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import timber.log.Timber

class CardsViewModel(private val planName: String) : ViewModel() {
    companion object {
        const val hotelCardId = 1
        const val restaurantCardId = 2
        const val attractionCardId = 3
    }

    private val items = listOf(
        ExpandableCardModel(id = hotelCardId, type = PlanDetailItemType.HOTEL),
        ExpandableCardModel(id = restaurantCardId, type = PlanDetailItemType.RESTAURANT),
        ExpandableCardModel(id = attractionCardId, type = PlanDetailItemType.ATTRACTION),
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

    private val plannableUtils by lazy {
        PlannableUtilsFactory.getOrCreate(planName = planName)
    }

    fun loadPlannables() {
        // load subdocuments of plan
        plannableUtils.loadPlannables { exception ->
            Timber.e(exception)
            hotels.update {
                plannableUtils.planDetailItems(PlanDetailItemType.HOTEL)
            }
            restaurants.update {
                plannableUtils.planDetailItems(PlanDetailItemType.RESTAURANT)
            }
            attractions.update {
                plannableUtils.planDetailItems(PlanDetailItemType.ATTRACTION)
            }
        }
    }

    fun onCardArrowClicked(id: Int) {
        toggleCardExpandedState(id = id)
    }

    fun onDelete(plannableId: String, planDetailItemType: PlanDetailItemType) {
        val docRef = when (planDetailItemType) {
            PlanDetailItemType.HOTEL -> {
                val newList = hotels.value.toMutableList()
                newList.removeIf { it.id == plannableId }
                hotels.value = newList

                plannableUtils.fireStoreHotelDbReference?.document(plannableId)
            }

            PlanDetailItemType.RESTAURANT -> {
                val newList = restaurants.value.toMutableList()
                newList.removeIf { it.id == plannableId }
                hotels.value = newList

                plannableUtils.fireStoreRestaurantDbReference?.document(plannableId)
            }

            PlanDetailItemType.ATTRACTION -> {
                val newList = attractions.value.toMutableList()
                newList.removeIf { it.id == plannableId }
                hotels.value = newList

                plannableUtils.fireStoreAttractionDbReference?.document(plannableId)
            }
        }

        docRef?.delete()
            ?.addOnSuccessListener {
                Timber.d("Successfully removed plannable")
            }
            ?.addOnFailureListener {
                _exception.value = it
            }
    }

    fun onDismiss() {
        _showDialog.value = false
    }

    fun clickedOnItem(plannableId: String, name: String, type: PlanDetailItemType) {
        _showDialog.value = true
        _bottomSheetData.value = NoteData(plannableId = plannableId, planName = name, planDetailItemType = type)
    }

    private fun toggleCardExpandedState(id: Int) {
        val list = expandedCardIdsList.value.toMutableList()
        if (list.contains(id)) list.remove(id) else list.add(id)
        expandedCardIdsList.value = list
    }

    class CardsViewModelFactory(private val planName: String) :
        ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return CardsViewModel(planName = planName) as T
        }
    }
}
