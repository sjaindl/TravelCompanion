package com.sjaindl.travelcompanion.plan

import android.content.Context
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.storage.FirebaseStorage
import com.sjaindl.travelcompanion.Constants
import com.sjaindl.travelcompanion.api.firestore.FireStoreClient
import com.sjaindl.travelcompanion.api.firestore.FireStoreConstants
import com.sjaindl.travelcompanion.api.google.Plannable
import com.sjaindl.travelcompanion.plan.detail.PlanDetailItemType
import com.sjaindl.travelcompanion.plan.detail.PlanDetailItemType.ATTRACTION
import com.sjaindl.travelcompanion.plan.detail.PlanDetailItemType.HOTEL
import com.sjaindl.travelcompanion.plan.detail.PlanDetailItemType.RESTAURANT
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import timber.log.Timber
import javax.inject.Inject

class PlannableUtils @Inject constructor(private val planName: String) {

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface PlannableUtilsEntryPoint {
        fun fireStoreClient(): FireStoreClient
    }

    @Inject
    lateinit var fireStoreClient: FireStoreClient


    var hotels: MutableList<Plannable> = mutableListOf()
    var restaurants: MutableList<Plannable> = mutableListOf()
    var attractions: MutableList<Plannable> = mutableListOf()

    private val planCollectionReference by lazy {
        fireStoreClient.userReference().collection(FireStoreConstants.Collections.plans)
    }

    var fireStoreHotelDbReference: CollectionReference? = null
    var fireStoreRestaurantDbReference: CollectionReference? = null
    var fireStoreAttractionDbReference: CollectionReference? = null

    private var fireStoreRememberPhotosDbReference: CollectionReference? = null

    private val tag = "Plan"

    fun initialize(context: Context) {
        val hiltEntryPoint = EntryPointAccessors.fromApplication(context, PlannableUtilsEntryPoint::class.java)
        fireStoreClient = hiltEntryPoint.fireStoreClient()

        configureDatabase()
    }

    fun loadPlannables(completion: (exception: Exception?) -> Unit) {
        // https://firebase.google.com/docs/firestore/manage-data/enable-offline?hl=en#kotlin+ktx_2
        hotels.ifEmpty { loadPlannables(HOTEL, fireStoreHotelDbReference, Constants.Plannables.hotel, completion) }
        restaurants.ifEmpty { loadPlannables(RESTAURANT, fireStoreRestaurantDbReference, Constants.Plannables.restaurant, completion) }
        attractions.ifEmpty { loadPlannables(ATTRACTION, fireStoreAttractionDbReference, Constants.Plannables.attraction, completion) }
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

    fun deleteSubDocuments(pinName: String, completion: (exception: Exception?) -> Unit) {
        val fireStorePlanBaseDbReference = planCollectionReference.document(pinName)

        val hotelsRef = fireStorePlanBaseDbReference.collection(FireStoreConstants.Collections.hotels)
        val restaurantsRef = fireStorePlanBaseDbReference.collection(FireStoreConstants.Collections.restaurants)
        val attractionsRef = fireStorePlanBaseDbReference.collection(FireStoreConstants.Collections.attractions)

        deleteSubDocument(
            hotelsRef,
            hotels,
            completion,
        )

        deleteSubDocument(
            restaurantsRef,
            restaurants,
            completion,
        )

        deleteSubDocument(
            attractionsRef,
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
        val planReference = fireStoreClient.userReference().collection(FireStoreConstants.Collections.plans).document(planName)

        fireStoreHotelDbReference = planReference.collection(FireStoreConstants.Collections.hotels)
        fireStoreRestaurantDbReference = planReference.collection(FireStoreConstants.Collections.restaurants)
        fireStoreAttractionDbReference = planReference.collection(FireStoreConstants.Collections.attractions)

        fireStoreRememberPhotosDbReference = planReference.collection(FireStoreConstants.Collections.photos)
    }
}
