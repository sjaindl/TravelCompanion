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

                val name = document.getString(FireStoreConstants.Ids.Plan.name) ?: return@addOnCompleteListener
                val pinName = document.getString(FireStoreConstants.Ids.Plan.pinName) ?: return@addOnCompleteListener
                val startDate = document.getTimestamp(FireStoreConstants.Ids.Plan.startDate)?.toDate() ?: return@addOnCompleteListener
                val endDate = document.getTimestamp(FireStoreConstants.Ids.Plan.endDate)?.toDate() ?: return@addOnCompleteListener

                val imageRef = if (withImageRef) document.getString(FireStoreConstants.Ids.Plan.imageReference) else null
                val storageImageRef = imageRef?.let { FirebaseStorage.getInstance().getReferenceFromUrl(it) }
                val downloadFromUrlTask = storageImageRef?.downloadUrl

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
        plan.imageData?.let { data ->
            // Has an image been chosen?
            byteArrayToBitmap(data)?.let {
                onLoaded(plan, it)
            }
            persistPhoto(plan = plan, photoData = data, onInfo = onInfo, onError = onError)
        } ?: run {
            // Is an image available in storage?
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
    }

    private fun persistPhoto(
        plan: Plan,
        photoData: ByteArray,
        onInfo: (info: Int) -> Unit,
        onError: (Exception) -> Unit,
    ) {
        val path = FireStoreClient.storageByPath(
            path = FireStoreConstants.Collections.plans,
            fileName = plan.pinName
        )
        FireStoreClient.storePhoto(storageRef = storageRef, path = path, photoData = photoData) { metadata, exception ->
            if (exception != null) {
                onError(exception)
            } else {
                val storagePath = metadata?.path
                if (storagePath == null) {
                    onInfo(R.string.imageNotSaved)
                } else {
                    plan.imagePath = storageRef.child(storagePath).downloadUrl.result
                    updatePlan(plan = plan, onError = onError)
                }
            }
        }
    }

    private fun updatePlan(
        plan: Plan,
        onError: (Exception) -> Unit,
    ) {
        val data = hashMapOf(
            FireStoreConstants.Ids.Plan.name to plan.name,
            FireStoreConstants.Ids.Plan.pinName to plan.pinName,
            FireStoreConstants.Ids.Plan.startDate to plan.startDate,
            FireStoreConstants.Ids.Plan.endDate to plan.endDate,
            FireStoreConstants.Ids.Plan.imageReference to plan.imagePath,
        )

        FireStoreClient.addData(
            collectionReference = fireStoreDbReferencePlans,
            documentName = plan.name,
            data = data,
        ) { exception: Exception? ->
            if (exception != null) {
                onError(exception)
            }
        }
    }

    private fun byteArrayToBitmap(data: ByteArray): Bitmap? {
        return BitmapFactory.decodeByteArray(data, 0, data.size)
    }
}
