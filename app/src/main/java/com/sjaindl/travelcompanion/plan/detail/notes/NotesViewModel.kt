package com.sjaindl.travelcompanion.plan.detail.notes

import androidx.lifecycle.ViewModel
import com.sjaindl.travelcompanion.api.firestore.FireStoreClient
import com.sjaindl.travelcompanion.api.firestore.FireStoreConstants
import com.sjaindl.travelcompanion.api.google.Plannable
import com.sjaindl.travelcompanion.plan.Plan
import com.sjaindl.travelcompanion.plan.PlannableUtilsFactory
import com.sjaindl.travelcompanion.plan.detail.PlanDetailItemType
import com.sjaindl.travelcompanion.plan.detail.PlanDetailItemType.ATTRACTION
import com.sjaindl.travelcompanion.plan.detail.PlanDetailItemType.HOTEL
import com.sjaindl.travelcompanion.plan.detail.PlanDetailItemType.RESTAURANT
import com.sjaindl.travelcompanion.util.FireStoreUtils
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

@HiltViewModel(assistedFactory = NotesViewModel.NotesViewModelFactory::class)
class NotesViewModel @AssistedInject constructor(
    private val fireStoreClient: FireStoreClient,
    private val fireStoreUtils: FireStoreUtils,
    @Assisted("planName") private val planName: String,
    @Assisted("plannableId") private val plannableId: String, // = place id
    @Assisted private val planDetailItemType: PlanDetailItemType,
) : ViewModel() {
    sealed class State {
        data object Initial : State()

        data class Loaded(val plan: Plan, val plannable: Plannable) : State()

        data class Info(val res: Int) : State()

        data class Error(val exception: Exception?) : State()

        data object Finished : State()
    }

    private val _state: MutableStateFlow<State> = MutableStateFlow(State.Initial)
    var state = _state.asStateFlow()

    private val plannableUtils by lazy {
        PlannableUtilsFactory.getOrCreate(planName = planName)
    }

    fun load() {
        fireStoreUtils.loadPlan(
            planName = planName,
            onLoaded = { plan, _ ->
                plannableUtils.loadPlannables { exception ->
                    if (exception != null) {
                        _state.value = State.Error(exception)
                    } else {
                        try {
                            val plannable = when (planDetailItemType) {
                                HOTEL -> {
                                    plannableUtils.hotels.first {
                                        it.getId() == plannableId
                                    }
                                }

                                RESTAURANT -> {
                                    plannableUtils.restaurants.first {
                                        it.getId() == plannableId
                                    }
                                }

                                ATTRACTION -> {
                                    plannableUtils.attractions.first {
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
        )
    }

    fun persistNotes(
        plannable: Plannable,
        notes: String,
    ) {
        if (notes == plannable.getNotes()) {
            _state.value = State.Finished
            return
        }

        val plannableCollectionReference = when (planDetailItemType) {
            HOTEL -> plannableUtils.fireStoreHotelDbReference
            RESTAURANT -> plannableUtils.fireStoreRestaurantDbReference
            ATTRACTION -> plannableUtils.fireStoreAttractionDbReference
        } ?: return

        plannable.setNotes(notes = notes)

        val docRef = plannableCollectionReference.document(plannableId)

        val data = hashMapOf(
            FireStoreConstants.Ids.Plannable.notes to notes,
        )

        fireStoreClient.updateDocumentFields(
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

    @AssistedFactory
    interface NotesViewModelFactory {
        fun create(
            @Assisted("planName") planName: String,
            @Assisted("plannableId") plannableId: String,
            planDetailItemType: PlanDetailItemType,
        ): NotesViewModel
    }
}
