package com.sjaindl.travelcompanion.plan

import android.net.Uri
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.sjaindl.travelcompanion.Constants
import com.sjaindl.travelcompanion.api.firestore.FireStoreClient
import com.sjaindl.travelcompanion.api.firestore.FireStoreConstants
import com.sjaindl.travelcompanion.api.google.Plannable
import com.sjaindl.travelcompanion.plan.detail.PlanDetailItemType
import com.sjaindl.travelcompanion.plan.detail.PlanDetailItemType.ATTRACTION
import com.sjaindl.travelcompanion.plan.detail.PlanDetailItemType.HOTEL
import com.sjaindl.travelcompanion.plan.detail.PlanDetailItemType.RESTAURANT
import timber.log.Timber
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date

data class Plan(
    val name: String,
    val pinName: String,
    val startDate: Date,
    val endDate: Date,
    var imagePath: Uri?,
) {
    var imageData: ByteArray? = null

    var hotels: MutableList<Plannable> = mutableListOf()
    var restaurants: MutableList<Plannable> = mutableListOf()
    var attractions: MutableList<Plannable> = mutableListOf()

    var fireStoreHotelDbReference: CollectionReference? = null
    var fireStoreRestaurantDbReference: CollectionReference? = null
    var fireStoreAttractionDbReference: CollectionReference? = null

    private var fireStoreRememberPhotosDbReference: CollectionReference? = null

    private val tag = "Plan"

    val formattedDate: String
        get() {
            val formatter = DateTimeFormatter.ISO_LOCAL_DATE.withZone(ZoneId.systemDefault())
            val formattedStartDate = formatter.format(startDate.toInstant())
            val formattedEndDate = formatter.format(endDate.toInstant())
            return "$formattedStartDate - $formattedEndDate"
        }

    init {
        configureDatabase()
    }

    fun loadPlannables(completion: (exception: Exception?) -> Unit) {
        reset()
        hotels.ifEmpty { loadPlannables(HOTEL, fireStoreHotelDbReference, Constants.Plannables.hotel, completion) }
        restaurants.ifEmpty { loadPlannables(RESTAURANT, fireStoreRestaurantDbReference, Constants.Plannables.restaurant, completion) }
        attractions.ifEmpty { loadPlannables(ATTRACTION, fireStoreAttractionDbReference, Constants.Plannables.attraction, completion) }
    }

    fun resetReferences() {
        fireStoreHotelDbReference = null
        fireStoreRestaurantDbReference = null
        fireStoreAttractionDbReference = null

        fireStoreRememberPhotosDbReference = null
    }

    fun loadPlannables(
        type: PlanDetailItemType,
        collectionReference: CollectionReference?,
        plannableType: String,
        completion: (exception: Exception?) -> Unit
    ) {
        collectionReference?.get()?.addOnSuccessListener { querySnapshot ->
            querySnapshot.forEach { document ->
                println("${document.id} => ${document.data}")
                val plannable = PlannableFactory.makePlannable(plannableType, document.data)
                when (type) {
                    HOTEL -> {
                        apply {
                            val list = hotels.toMutableList()
                            list.add(plannable)
                            hotels = list
                        }
                    }

                    RESTAURANT -> {
                        apply {
                            val list = restaurants.toMutableList()
                            list.add(plannable)
                            restaurants = list
                        }
                    }

                    ATTRACTION -> {
                        apply {
                            val list = attractions.toMutableList()
                            list.add(plannable)
                            attractions = list
                        }
                    }
                }
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

    private fun configureDatabase() {
        val planReference = FireStoreClient.userReference().collection(FireStoreConstants.Collections.plans).document(name)

        fireStoreHotelDbReference = planReference.collection(FireStoreConstants.Collections.hotels)
        fireStoreRestaurantDbReference = planReference.collection(FireStoreConstants.Collections.restaurants)
        fireStoreAttractionDbReference = planReference.collection(FireStoreConstants.Collections.attractions)

        fireStoreRememberPhotosDbReference = planReference.collection(FireStoreConstants.Collections.photos)
    }

    private fun reset() {
        hotels = mutableListOf()
        restaurants = mutableListOf()
        attractions = mutableListOf()
    }
}
