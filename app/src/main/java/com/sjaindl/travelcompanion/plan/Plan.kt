package com.sjaindl.travelcompanion.plan

import android.net.Uri
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.sjaindl.travelcompanion.Constants
import com.sjaindl.travelcompanion.api.Plannable
import com.sjaindl.travelcompanion.api.firestore.FireStoreConstants
import timber.log.Timber
import java.util.*

class Plan(
    val name: String,
    val pinName: String,
    val startDate: Date,
    val endDate: Date,
    val imagePath: Uri?,
) {
    var hotels: MutableList<Plannable> = mutableListOf()
    var restaurants: MutableList<Plannable> = mutableListOf()
    var attractions: MutableList<Plannable> = mutableListOf()

    var fireStoreHotelDbReference: CollectionReference? = null
    var fireStoreRestaurantDbReference: CollectionReference? = null
    var fireStoreAttractionDbReference: CollectionReference? = null

    var fireStoreRememberPhotosDbReference: CollectionReference? = null

    private val tag = "Plan"

    init {
        configureDatabase()
    }

    private fun configureDatabase() {
        val planReference = FirebaseFirestore.getInstance()
            .collection(FireStoreConstants.Collections.plans)
            .document(name)

        fireStoreHotelDbReference = planReference.collection(FireStoreConstants.Collections.hotels)
        fireStoreRestaurantDbReference = planReference.collection(FireStoreConstants.Collections.restaurants)
        fireStoreAttractionDbReference = planReference.collection(FireStoreConstants.Collections.attractions)

        fireStoreRememberPhotosDbReference = planReference.collection(FireStoreConstants.Collections.photos)
    }

    fun loadPlannables(completion: (exception: Exception?) -> Unit) {
        reset()

        loadPlannables(hotels, fireStoreHotelDbReference, Constants.Plannables.hotel, completion)
        loadPlannables(restaurants, fireStoreRestaurantDbReference, Constants.Plannables.restaurant, completion)
        loadPlannables(attractions, fireStoreAttractionDbReference, Constants.Plannables.attraction, completion)
    }

    fun resetReferences() {
        fireStoreHotelDbReference = null
        fireStoreRestaurantDbReference = null
        fireStoreAttractionDbReference = null

        fireStoreRememberPhotosDbReference = null
    }

    fun loadPlannables(
        plannables: MutableList<Plannable>,
        collectionReference: CollectionReference?,
        plannableType: String,
        completion: (exception: Exception?) -> Unit
    ) {
        collectionReference?.get()?.addOnSuccessListener { querySnapshot ->
            querySnapshot.forEach { document ->
                println("${document.id} => ${document.data}")
                plannables.add(PlannableFactory.makePlannable(plannableType, document.data))
            }

            completion(null)
        }?.addOnFailureListener { exception ->
            completion(exception)
        }
    }

    fun deleteSubDocuments(completion: (exception: Exception?) -> Unit) {
        val fireStorePlanBaseDbReference = FirebaseFirestore.getInstance()
            .collection(FireStoreConstants.Collections.plans)
            .document(pinName)

        deleteSubDocument(
            fireStorePlanBaseDbReference.collection(FireStoreConstants.Collections.hotels),
            hotels,
            completion,
        )

        deleteSubDocument(
            fireStorePlanBaseDbReference.collection(FireStoreConstants.Collections.restaurants),
            restaurants,
            completion,
        )

        deleteSubDocument(
            fireStorePlanBaseDbReference.collection(FireStoreConstants.Collections.attractions),
            attractions,
            completion,
        )

        deletePhotos(completion = completion)
    }

    private fun deletePhotos(completion: (exception: Exception?) -> Unit) {
        fireStoreRememberPhotosDbReference?.get()?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val querySnapshot = task.result
                for (document in querySnapshot.documents) {
                    //Delete remember photo in firebase storage
                    val photo = document.getString(FireStoreConstants.Ids.Plan.path)
                    if (photo != null) {
                        val storageImageRef = FirebaseStorage.getInstance().getReferenceFromUrl(photo)
                        storageImageRef.delete().addOnCompleteListener {
                            if (it.isSuccessful) {
                                Timber.tag(tag).d("Successfully deleted remember photo")
                            } else {
                                completion(it.exception)
                            }
                        }
                    }

                    //Delete database reference
                    fireStoreRememberPhotosDbReference?.document(document.id)?.delete()?.addOnCompleteListener {
                        if (it.isSuccessful) {
                            Timber.tag(tag).d("Successfully deleted document")
                        } else {
                            completion(it.exception)
                        }
                    }
                }
            } else {
                completion(task.exception)
            }
        }
    }

    private fun deleteSubDocument(
        firestoreCollectionReference: CollectionReference,
        plannables: List<Plannable>,
        completion: (exception: Exception?) -> Unit
    ) {
        plannables.forEach {
            deleteDocument(it.getId(), firestoreCollectionReference, completion)
        }
    }

    private fun deleteDocument(id: String, firestoreCollectionReference: CollectionReference, completion: (exception: Exception?) -> Unit) {
        firestoreCollectionReference.document(id).delete()
            .addOnFailureListener {
                completion(it)
            }
            .addOnSuccessListener {
                println("Document successfully removed!")
            }
    }

    private fun reset() {
        hotels.clear()
        restaurants.clear()
        attractions.clear()
    }
}
