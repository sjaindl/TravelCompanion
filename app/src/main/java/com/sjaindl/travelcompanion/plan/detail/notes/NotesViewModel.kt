package com.sjaindl.travelcompanion.plan.detail.notes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sjaindl.travelcompanion.api.firestore.FireStoreClient
import com.sjaindl.travelcompanion.api.firestore.FireStoreConstants
import com.sjaindl.travelcompanion.api.google.Plannable
import com.sjaindl.travelcompanion.plan.Plan
import com.sjaindl.travelcompanion.plan.PlanUtils
import com.sjaindl.travelcompanion.plan.detail.PlanDetailItemType
import com.sjaindl.travelcompanion.plan.detail.PlanDetailItemType.ATTRACTION
import com.sjaindl.travelcompanion.plan.detail.PlanDetailItemType.HOTEL
import com.sjaindl.travelcompanion.plan.detail.PlanDetailItemType.RESTAURANT
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class NotesViewModel(
    private val planName: String,
    private val planDetailItemType: PlanDetailItemType,
    private val plannableId: String, // = place id
) : ViewModel() {
    sealed class State {
        object Initial : State()

        data class Loaded(val plan: Plan, val plannable: Plannable) : State()

        data class Info(val res: Int) : State()

        data class Error(val exception: Exception?) : State()

        object Finished : State()
    }

    private val _state: MutableStateFlow<State> = MutableStateFlow(State.Initial)
    var state = _state.asStateFlow()

    fun load() {
        PlanUtils.loadPlan(
            planName = planName,
            onLoaded = { plan, _ ->
                plan.loadPlannables { exception ->
                    if (exception != null) {
                        _state.value = State.Error(exception)
                    } else {
                        try {
                            val plannable = when (planDetailItemType) {
                                HOTEL -> {
                                    plan.hotels.first {
                                        it.getId() == plannableId
                                    }
                                }

                                RESTAURANT -> {
                                    plan.restaurants.first {
                                        it.getId() == plannableId
                                    }
                                }

                                ATTRACTION -> {
                                    plan.attractions.first {
                                        it.getId() == plannableId
                                    }
                                }
                            }

                            _state.value = State.Loaded(plan, plannable)
                        } catch (exception: NoSuchElementException) {
                            _state.value = State.Error(exception)
                        }
                    }
                }
            },
            onInfo = {
                _state.value = State.Info(it)
            },
            onError = { exception ->
                _state.value = State.Error(exception)
            },
            withImageRef = false,
        )
    }

    fun persistNotes(
        plan: Plan,
        plannable: Plannable,
        notes: String,
    ) {
        if (notes == plannable.getNotes()) {
            _state.value = State.Finished
            return
        }

        val plannableCollectionReference = when (planDetailItemType) {
            HOTEL -> plan.fireStoreHotelDbReference
            RESTAURANT -> plan.fireStoreRestaurantDbReference
            ATTRACTION -> plan.fireStoreAttractionDbReference
        } ?: return

        plannable.setNotes(notes = notes)

        val docRef = plannableCollectionReference.document(plannableId)

        val data = hashMapOf(
            FireStoreConstants.Ids.Plannable.notes to notes,
        )

        FireStoreClient.updateDocumentFields(
            documentReference = docRef,
            data = data,
        ) { exception: Exception? ->
            if (exception != null) {
                _state.value = State.Error(exception)
            } else {
                _state.value = State.Finished
            }
        }
    }

    class NotesViewModelFactory(
        private val planName: String,
        private val plannableId: String,
        private val planDetailItemType: PlanDetailItemType,
    ) :
        ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return NotesViewModel(planName = planName, plannableId = plannableId, planDetailItemType = planDetailItemType) as T
        }
    }
}
