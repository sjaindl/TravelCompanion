package com.sjaindl.travelcompanion.explore

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.model.Marker
import com.sjaindl.travelcompanion.repository.DataRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class ExploreViewModel(private val dataRepository: DataRepository) : ViewModel() {
    private val _showDialog = MutableStateFlow(false)
    val showDialog = _showDialog.asStateFlow()

    private val _dialogTitle = MutableStateFlow("")
    var dialogTitle = _dialogTitle.asStateFlow()

    private val _onShowDetails = MutableStateFlow(0L)
    var onShowDetails = _onShowDetails.asStateFlow()

    val markers: MutableMap<Long, Marker> = mutableMapOf()

    fun onShowDetails() {
        _showDialog.value = false

        val pin = dataRepository.singlePin(name = dialogTitle.value) ?: return

        _onShowDetails.value = pin.id
    }

    fun onDelete() {
        _showDialog.value = false

        val pin = dataRepository.singlePin(name = dialogTitle.value) ?: return

        markers[pin.id]?.isVisible = false
        markers[pin.id]?.remove()
        markers.remove(pin.id)

        dataRepository.deletePin(pin.id)
    }

    fun onPlanTrip() {
        _showDialog.value = false
    }

    fun onDismiss() {
        _showDialog.value = false
    }

    fun clickedOnPlace(name: String?) {
        if (name == null) return

        _dialogTitle.value = name
        _showDialog.value = true
    }

    fun clickedOnDetails() {
        _onShowDetails.value = 0L
    }
}

class ExploreViewModelFactory(private val dataRepository: DataRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ExploreViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ExploreViewModel(dataRepository) as T
        }
        throw IllegalArgumentException("UNKNOWN VIEW MODEL CLASS")
    }
}
