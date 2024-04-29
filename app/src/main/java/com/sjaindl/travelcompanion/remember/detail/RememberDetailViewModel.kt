package com.sjaindl.travelcompanion.remember.detail

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.lifecycle.ViewModel
import com.sjaindl.travelcompanion.util.FireStoreUtils
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

@HiltViewModel(assistedFactory = RememberDetailViewModelFactory::class)
class RememberDetailViewModel @AssistedInject constructor(
    @Assisted private val planName: String,
    private val fireStoreUtils: FireStoreUtils,
) : ViewModel() {

    companion object {
        const val tag = "RememberDetailViewModel"
    }

    sealed class State {
        data object Loading : State()

        data class Error(val exception: Exception) : State()

        data class Info(val stringRes: Int) : State()

        data class LoadedPhotos(val photos: List<RememberPhoto>) : State()
    }

    private var _state: MutableStateFlow<State> = MutableStateFlow(State.Loading)
    var state = _state.asStateFlow()

    fun loadPhotos() {
        fireStoreUtils.loadPhotoPaths(
            planName = planName,
            onLoaded = {
                _state.value = State.LoadedPhotos(photos = it)
            },
            onInfo = {
                _state.value = State.Info(it)
            },
            onError = { exception ->
                _state.value = State.Error(exception)
            },
        )
    }

    fun removePhoto(documentId: String) {
        val loaded = state.value as? State.LoadedPhotos ?: return
        val photos = loaded.photos.toMutableList()
        photos.removeIf {
            it.documentId == documentId
        }

        _state.value = State.LoadedPhotos(photos = photos)
    }

    fun bitmapForPlan(planName: String) = fireStoreUtils.bitmapForPlan(planName = planName)

    fun setError(exception: Exception) {
        _state.value = State.Error(exception)
    }

    fun setInfo(stringRes: Int) {
        _state.value = State.Info(stringRes)
    }

    fun setLoading() {
        _state.value = State.Loading
    }

    fun addPhoto(photo: RememberPhoto) {
        val curPhotos = ((state.value as? State.LoadedPhotos)?.photos ?: emptyList()).toMutableList()
        curPhotos.add(photo)
        _state.value = State.LoadedPhotos(curPhotos)
    }

    fun addPhotos(photos: List<RememberPhoto>) {
        val curPhotos = ((state.value as? State.LoadedPhotos)?.photos ?: emptyList()).toMutableList()
        curPhotos.addAll(photos)
        _state.value = State.LoadedPhotos(curPhotos)
    }

    suspend fun persistPhotos(context: Context, uris: List<Uri>): AddMultiplePhotosState {
        return suspendCancellableCoroutine { continuation ->
            val photos = mutableListOf<RememberPhoto>()
            uris.map { uri ->
                BitmapFactory.decodeStream(context.contentResolver.openInputStream(uri))
            }.forEach { bitmap ->
                fireStoreUtils.persistRememberPhoto(
                    planName = planName,
                    image = bitmap,
                    onSuccess = {
                        photos.add(it)
                        if (photos.size == uris.size) {
                            continuation.resume(AddMultiplePhotosState.AddedPhotos(photos = photos))
                        }
                    },
                    onInfo = {
                        continuation.resume(AddMultiplePhotosState.Info(stringRes = it))
                    },
                    onError = {
                        continuation.resume(AddMultiplePhotosState.Error(exception = it))
                    },
                )
            }
        }
    }

    fun persistRememberPhoto(bitmap: Bitmap) {
        fireStoreUtils.persistRememberPhoto(
            planName = planName,
            image = bitmap,
            onSuccess = {
                addPhoto(photo = it)
            },
            onInfo = {
                setInfo(stringRes = it)
            },
            onError = {
                setError(exception = it)
            },
        )
    }
}

@AssistedFactory
interface RememberDetailViewModelFactory {
    fun create(
        planName: String,
    ): RememberDetailViewModel
}

