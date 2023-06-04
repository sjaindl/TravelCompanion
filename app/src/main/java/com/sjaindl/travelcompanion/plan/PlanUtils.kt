package com.sjaindl.travelcompanion.plan

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.google.firebase.storage.FirebaseStorage
import com.sjaindl.travelcompanion.R
import com.sjaindl.travelcompanion.api.firestore.FireStoreClient
import com.sjaindl.travelcompanion.api.firestore.FireStoreConstants
import timber.log.Timber
import java.util.Date

// TODO: Caching of plans in a hashmap
// https://firebase.google.com/docs/firestore/manage-data/enable-offline?hl=en#java
object PlanUtils {
    private const val tag = "PlanLoader"

    private val fireStoreDbReferencePlans by lazy {
        FireStoreClient.userReference().collection(FireStoreConstants.Collections.plans)
    }

    private val storageRef by lazy {
        FirebaseStorage.getInstance().reference
    }

    fun loadPlans(
        onLoaded: (plan: Plan) -> Unit,
        onInfo: (info: Int) -> Unit,
        onError: (Exception) -> Unit,
        withImageRef: Boolean = true,
    ) {
        fireStoreDbReferencePlans.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                task.result.forEach { document ->
                    Timber.d(tag, "${document.id} => {$document.data}")

                    val name = document.getString(FireStoreConstants.Ids.Plan.name)
                    val pinName = document.getString(FireStoreConstants.Ids.Plan.pinName)
                    val startDate = document.getTimestamp(FireStoreConstants.Ids.Plan.startDate)?.toDate()
                    val endDate = document.getTimestamp(FireStoreConstants.Ids.Plan.endDate)?.toDate()

                    val imageRef = if (withImageRef) document.getString(FireStoreConstants.Ids.Plan.imageReference) else null
                    val storageImageRef = imageRef?.let { FirebaseStorage.getInstance().getReferenceFromUrl(it) }
                    val downloadFromUrlTask = storageImageRef?.downloadUrl

                    if (name != null && pinName != null && startDate != null && endDate != null) {
                        if (downloadFromUrlTask == null) {
                            Timber.tag(tag).d("Add plan without image: $name")
                            val plan = Plan(
                                name = name,
                                pinName = pinName,
                                startDate = startDate,
                                endDate = endDate,
                                imagePath = null,
                            )
                            onLoaded(plan)
                        } else {
                            downloadFromUrlTask.addOnSuccessListener { imagePath ->
                                Timber.tag(tag).d("fetched imagePath: $imagePath for $name")
                                val plan = Plan(
                                    name = name,
                                    pinName = pinName,
                                    startDate = startDate,
                                    endDate = endDate,
                                    imagePath = imagePath,
                                )
                                onLoaded(plan)
                            }.addOnCanceledListener {
                                onInfo(R.string.cancelled)
                            }.addOnFailureListener {
                                onError(it)
                            }
                        }
                    }
                }
            } else {
                val exception = task.exception
                if (exception != null) {
                    onError(exception)
                } else {
                    onInfo(R.string.cancelled)
                }
            }
        }
    }

    fun loadPlan(
        planName: String,
        onLoaded: (plan: Plan, bitmap: Bitmap?) -> Unit,
        onInfo: (info: Int) -> Unit,
        onError: (Exception) -> Unit,
        withImageRef: Boolean = true,
    ) {
        fireStoreDbReferencePlans.document(planName).get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val document = task.result

                val name = document.getString(FireStoreConstants.Ids.Plan.name)
                val pinName = document.getString(FireStoreConstants.Ids.Plan.pinName)
                val startDate = document.getTimestamp(FireStoreConstants.Ids.Plan.startDate)?.toDate()
                val endDate = document.getTimestamp(FireStoreConstants.Ids.Plan.endDate)?.toDate()

                val imageRef = if (withImageRef) document.getString(FireStoreConstants.Ids.Plan.imageReference) else null
                val storageImageRef = imageRef?.let { FirebaseStorage.getInstance().getReferenceFromUrl(it) }
                val downloadFromUrlTask = storageImageRef?.downloadUrl

                if (name != null && pinName != null && startDate != null && endDate != null) {
                    if (downloadFromUrlTask == null) {
                        Timber.tag(tag).d("Add plan without image: $name")
                        val plan = Plan(name = name, pinName = pinName, startDate = startDate, endDate = endDate, imagePath = null)
                        onLoaded(plan, null)
                    } else {
                        downloadFromUrlTask.addOnSuccessListener { imagePath ->
                            Timber.tag(tag).d("fetched imagePath: $imagePath")
                            val plan = Plan(name = name, pinName = pinName, startDate = startDate, endDate = endDate, imagePath = imagePath)
                            onLoaded(plan, null)
                            loadImageIfAvailable(
                                plan = plan,
                                onLoaded = onLoaded,
                                onInfo = onInfo,
                                onError = onError,
                            )
                        }.addOnCanceledListener {
                            onInfo(R.string.cancelled)
                        }.addOnFailureListener { exception ->
                            onError(exception)
                        }
                    }
                }

            } else {
                val exception = task.exception
                if (exception != null) {
                    onError(exception)
                } else {
                    onInfo(R.string.cancelled)
                }
            }
        }.addOnCanceledListener {
            onInfo(R.string.cancelled)
        }.addOnFailureListener { exception ->
            onError(exception)
        }
    }

    fun updatePlanDates(
        planName: String,
        startDate: Date,
        endDate: Date,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit,
    ) {
        val planReference = fireStoreDbReferencePlans.document(planName)

        val data = hashMapOf(
            FireStoreConstants.Ids.Plan.startDate to startDate,
            FireStoreConstants.Ids.Plan.endDate to endDate,
        )

        FireStoreClient.updateDocumentFields(
            documentReference = planReference,
            data = data,
        ) { exception: Exception? ->
            if (exception != null) {
                onError(exception)
            } else {
                onSuccess()
            }
        }
    }

    private fun loadImageIfAvailable(
        plan: Plan,
        onLoaded: (plan: Plan, bitmap: Bitmap?) -> Unit,
        onInfo: (info: Int) -> Unit,
        onError: (Exception) -> Unit,
    ) {
        plan.imagePath?.toString()?.let {
            val storageImageRef = FirebaseStorage.getInstance().getReferenceFromUrl(it)
            storageImageRef.getBytes(2 * 1024 * 1024)
                .addOnSuccessListener { data ->
                    val bitmap = byteArrayToBitmap(data)
                    if (bitmap != null) {
                        onLoaded(plan, bitmap)
                    } else {
                        onInfo(R.string.noImageData)
                    }
                }
                .addOnCanceledListener {
                    onInfo(R.string.cancelled)
                }
                .addOnFailureListener { exception ->
                    onError(exception)
                }
        }
    }

    fun persistPhoto(
        plan: Plan,
        image: Bitmap,
        onSuccess: () -> Unit,
        onInfo: (info: Int) -> Unit,
        onError: (Exception) -> Unit,
    ) {
        val path = FireStoreClient.storageByPath(
            path = FireStoreConstants.Collections.plans,
            fileName = plan.pinName
        )
        FireStoreClient.storePhoto(storageRef = storageRef, path = path, image = image) { metadata, exception ->
            if (exception != null) {
                onError(exception)
            } else {
                val storagePath = metadata?.path
                if (storagePath == null) {
                    onInfo(R.string.imageNotSaved)
                } else {
                    storageRef.child(storagePath).downloadUrl.addOnSuccessListener {
                        plan.imagePath = it
                        updatePlan(
                            planName = plan.name,
                            imagePath = it.toString(),
                            onSuccess = onSuccess,
                            onError = onError,
                        )
                    }.addOnFailureListener {
                        onError(it)
                    }.addOnCanceledListener {
                        R.string.cancelled
                    }
                }
            }
        }
    }

    private fun updatePlan(
        planName: String,
        imagePath: String,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit,
    ) {
        val data = hashMapOf(
            FireStoreConstants.Ids.Plan.imageReference to imagePath,
        )

        FireStoreClient.updateDocumentFields(
            documentReference = fireStoreDbReferencePlans.document(planName),
            data = data,
        ) { exception: Exception? ->
            if (exception != null) {
                onError(exception)
            } else {
                onSuccess()
            }
        }
    }

    private fun byteArrayToBitmap(data: ByteArray): Bitmap? {
        return BitmapFactory.decodeByteArray(data, 0, data.size)
    }
}
