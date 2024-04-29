package com.sjaindl.travelcompanion.profile

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.sjaindl.travelcompanion.api.firestore.FireStoreClient
import com.sjaindl.travelcompanion.api.firestore.FireStoreConstants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val fireStoreClient: FireStoreClient,
) : ViewModel() {
    sealed class State {
        data object Loading : State()

        data class Error(val exception: Exception?) : State()

        data object Deleted : State()

        data object Finished : State()
    }

    private val _state: MutableStateFlow<State> = MutableStateFlow(State.Loading)
    var state = _state.asStateFlow()

    private val currentUser = FirebaseAuth.getInstance().currentUser

    var userName: String = currentUser?.displayName ?: "Anonymous"

    val initials: String
        get() {
            val characters = (currentUser?.displayName ?: "TC").split(" ").mapNotNull { it.firstOrNull() }
            return if (characters.size > 1) {
                val first = characters.first()
                val last = characters.last()
                "$first$last".uppercase()
            } else {
                characters.joinToString("").uppercase()
            }
        }

    fun logout() {
        FirebaseAuth.getInstance().signOut()
    }

    fun deleteAccount() {
        val user = currentUser ?: return
        _state.value = State.Loading

        val userRef = fireStoreClient.userReference()

        deletePhotos()

        userRef.delete().addOnCompleteListener {
            user.delete().addOnCompleteListener {
                _state.value = State.Deleted
            }.addOnFailureListener {
                _state.value = State.Error(it)
            }
        }.addOnFailureListener {
            _state.value = State.Error(it)
        }
    }

    private fun deletePhotos() {
        val userRef = fireStoreClient.userReference()
        val plans = userRef.collection(FireStoreConstants.Collections.plans)

        plans.addSnapshotListener { query, error ->
            query?.documents?.forEach { planDocument ->
                val photoCollection = planDocument.reference.collection(FireStoreConstants.Collections.photos)
                photoCollection.addSnapshotListener { photoQuery, error ->
                    photoQuery?.documents?.forEach { photoDoc ->
                        (photoDoc.get("path") as? String)?.let { fileName ->
                            val storageImageRef = FirebaseStorage.getInstance().getReference(fileName)

                            storageImageRef.delete()
                                .addOnCompleteListener {
                                    if (it.isSuccessful) {
                                        _state.value = State.Finished
                                    } else {
                                        _state.value = State.Error(it.exception)
                                    }
                                }
                                .addOnFailureListener {
                                    _state.value = State.Error(it)
                                }
                        }
                    }
                }
            }
        }
    }
}
