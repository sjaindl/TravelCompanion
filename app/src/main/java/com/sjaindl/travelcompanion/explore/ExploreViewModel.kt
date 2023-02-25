package com.sjaindl.travelcompanion.explore

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ExploreViewModel : ViewModel() {

    private val _showDialog = MutableStateFlow(false)
    val showDialog: StateFlow<Boolean> = _showDialog.asStateFlow()

    private val _dialogTitle = MutableStateFlow("")
    var dialogTitle: StateFlow<String> = _dialogTitle.asStateFlow()

    fun onShowDetails() {
        _showDialog.value = false
    }

    fun onDelete() {
        _showDialog.value = false
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
}
