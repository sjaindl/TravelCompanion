package com.sjaindl.travelcompanion.plan.detail

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.storage.FirebaseStorage
import com.sjaindl.travelcompanion.R
import com.sjaindl.travelcompanion.api.firestore.FireStoreClient
import com.sjaindl.travelcompanion.api.firestore.FireStoreConstants
import com.sjaindl.travelcompanion.plan.Plan
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import timber.log.Timber

class PlanDetailViewModel(private val planName: String) : ViewModel() {
    sealed class State {
        object Loading : State()

        data class Info(@StringRes val stringRes: Int) : State()

        data class Error(val exception: Exception?) : State()

        data class Loaded(val plan: Plan, val bitmap: Bitmap?) : State()
    }

    val tag = "PlanDetailViewModel"

    private val _state: MutableStateFlow<State> = MutableStateFlow(State.Loading)
    var state = _state.asStateFlow()

    private val storageRef by lazy {
        FirebaseStorage.getInstance().reference
    }

    private val fireStoreDbReference by lazy {
        FireStoreClient.userReference().collection(FireStoreConstants.Collections.plans)
    }

    fun loadPlan() {
        fireStoreDbReference.document(planName).get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val document = task.result

                val name = document.getString(FireStoreConstants.Ids.Plan.name) ?: return@addOnCompleteListener
                val pinName = document.getString(FireStoreConstants.Ids.Plan.pinName) ?: return@addOnCompleteListener
                val startDate = document.getTimestamp(FireStoreConstants.Ids.Plan.startDate)?.toDate() ?: return@addOnCompleteListener
                val endDate = document.getTimestamp(FireStoreConstants.Ids.Plan.endDate)?.toDate() ?: return@addOnCompleteListener

                val imageRef = document.getString(FireStoreConstants.Ids.Plan.imageReference)
                val storageImageRef = imageRef?.let { FirebaseStorage.getInstance().getReferenceFromUrl(it) }
                val downloadFromUrlTask = storageImageRef?.downloadUrl

                if (downloadFromUrlTask == null) {
                    Timber.tag(tag).d("Add plan without image: $name")
                    val plan = Plan(name = name, pinName = pinName, startDate = startDate, endDate = endDate, imagePath = null)
                    _state.value = State.Loaded(plan = plan, bitmap = null)
                } else {
                    downloadFromUrlTask.addOnSuccessListener { imagePath ->
                        Timber.tag(tag).d("fetched imagePath: $imagePath")
                        val plan = Plan(name = name, pinName = pinName, startDate = startDate, endDate = endDate, imagePath = imagePath)
                        _state.value = State.Loaded(plan = plan, bitmap = null)
                        loadImageIfAvailable(plan = plan)
                    }.addOnCanceledListener {
                        _state.value = State.Info(R.string.cancelled)
                    }.addOnFailureListener { exception ->
                        _state.value = State.Error(exception)
                    }
                }
            }
        }.addOnCanceledListener {
            _state.value = State.Info(R.string.cancelled)
        }.addOnFailureListener { exception ->
            _state.value = State.Error(exception)
        }
    }

    private fun loadImageIfAvailable(plan: Plan) {
        plan.imageData?.let { data ->
            // Has an image been chosen?
            byteArrayToBitmap(data)?.let {
                _state.value = State.Loaded(plan = plan, bitmap = it)
            }
            persistPhoto(plan = plan, photoData = data)
        } ?: run {
            // Is an image available in storage?
            plan.imagePath?.toString()?.let {
                val storageImageRef = FirebaseStorage.getInstance().getReferenceFromUrl(it)
                storageImageRef.getBytes(2 * 1024 * 1024)
                    .addOnSuccessListener { data ->
                        val bitmap = byteArrayToBitmap(data)
                        if (bitmap != null) {
                            _state.value = State.Loaded(plan = plan, bitmap = bitmap)
                        } else {
                            _state.value = State.Info(R.string.noImageData)
                        }
                    }
                    .addOnCanceledListener {
                        _state.value = State.Info(R.string.cancelled)
                    }
                    .addOnFailureListener { exception ->
                        _state.value = State.Error(exception)
                    }
            }
        }
    }

    private fun persistPhoto(plan: Plan, photoData: ByteArray) {
        val path = FireStoreClient.storageByPath(
            path = FireStoreConstants.Collections.plans,
            fileName = plan.pinName
        )
        FireStoreClient.storePhoto(storageRef = storageRef, path = path, photoData = photoData) { metadata, exception ->
            if (exception != null) {
                _state.value = State.Error(exception)
            } else {
                val storagePath = metadata?.path
                if (storagePath == null) {
                    _state.value = State.Info(R.string.imageNotSaved)
                } else {
                    plan.imagePath = storageRef.child(storagePath).downloadUrl.result
                    updatePlan(plan = plan)
                }
            }
        }
    }

    private fun updatePlan(plan: Plan) {
        val data = hashMapOf(
            FireStoreConstants.Ids.Plan.name to plan.name,
            FireStoreConstants.Ids.Plan.pinName to plan.pinName,
            FireStoreConstants.Ids.Plan.startDate to plan.startDate,
            FireStoreConstants.Ids.Plan.endDate to plan.endDate,
            FireStoreConstants.Ids.Plan.imageReference to plan.imagePath,
        )

        FireStoreClient.addData(
            collectionReference = fireStoreDbReference,
            documentName = plan.name,
            data = data,
        ) { exception: Exception? ->
            if (exception != null) {
                _state.value = State.Error(exception)
            }
        }
    }

    private fun byteArrayToBitmap(data: ByteArray): Bitmap? {
        return BitmapFactory.decodeByteArray(data, 0, data.size)
    }

    class PlanDetailViewModelFactory(private val plan: String) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(PlanDetailViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return PlanDetailViewModel(plan) as T
            }
            throw IllegalArgumentException("UNKNOWN VIEW MODEL CLASS")
        }
    }
}
