package com.sjaindl.travelcompanion.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import com.sjaindl.travelcompanion.R
import com.sjaindl.travelcompanion.api.firestore.FireStoreClient
import com.sjaindl.travelcompanion.api.firestore.FireStoreConstants
import com.sjaindl.travelcompanion.plan.Plan
import com.sjaindl.travelcompanion.plan.PlannableUtilsFactory
import com.sjaindl.travelcompanion.remember.detail.RememberPhoto
import timber.log.Timber
import java.util.Date
import javax.inject.Inject
import kotlin.random.Random

// https://firebase.google.com/docs/firestore/manage-data/enable-offline?hl=en#java
class FireStoreUtils @Inject constructor(
    private val fireStoreClient: FireStoreClient,
) {
    private companion object {
        const val tag = "FireStoreUtils"
    }

    private val plans = mutableListOf<Plan>()
    private val planBitmaps = mutableMapOf<String, Bitmap>()

    private val fireStoreDbReferencePlans by lazy {
        fireStoreClient.userReference().collection(FireStoreConstants.Collections.plans)
    }

    private val storageRef by lazy {
        FirebaseStorage.getInstance().reference
    }

    fun loadPlans(
        onLoaded: (plan: Plan?) -> Unit,
        onInfo: (info: Int) -> Unit,
        onError: (Exception) -> Unit,
    ) {
        if (plans.isNotEmpty()) {
            plans.forEach { cachedPlan ->
                onLoaded(cachedPlan)
            }

            return
        }

        fireStoreDbReferencePlans.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {

                val snapshot = task.result

                if (snapshot.isEmpty) {
                    onLoaded(null)
                } else {
                    snapshot.forEach { document ->
                        Timber.d(tag, "${document.id} => {$document.data}")

                        val name = document.getString(FireStoreConstants.Ids.Plan.name)
                        val pinName = document.getString(FireStoreConstants.Ids.Plan.pinName)
                        val startDate = document.getTimestamp(FireStoreConstants.Ids.Plan.startDate)?.toDate()
                        val endDate = document.getTimestamp(FireStoreConstants.Ids.Plan.endDate)?.toDate()

                        val imageRef = document.getString(FireStoreConstants.Ids.Plan.imageReference)
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
                                plans.add(plan)
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
                                    plans.add(plan)
                                    onLoaded(plan)
                                }.addOnCanceledListener {
                                    onInfo(R.string.cancelled)
                                }.addOnFailureListener {
                                    onError(it)
                                }
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
    ) {
        val cachedPlan = plans.find {
            it.name == planName
        }
        if (cachedPlan != null) {
            val bitmap = planBitmaps[planName]
            onLoaded(cachedPlan, bitmap)
            return
        }

        fireStoreDbReferencePlans.document(planName).get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val document = task.result

                val name = document.getString(FireStoreConstants.Ids.Plan.name)
                val pinName = document.getString(FireStoreConstants.Ids.Plan.pinName)
                val startDate = document.getTimestamp(FireStoreConstants.Ids.Plan.startDate)?.toDate()
                val endDate = document.getTimestamp(FireStoreConstants.Ids.Plan.endDate)?.toDate()

                val imageRef = document.getString(FireStoreConstants.Ids.Plan.imageReference)
                val storageImageRef = imageRef?.let { FirebaseStorage.getInstance().getReferenceFromUrl(it) }
                val downloadFromUrlTask = storageImageRef?.downloadUrl

                if (name != null && pinName != null && startDate != null && endDate != null) {
                    if (downloadFromUrlTask == null) {
                        Timber.tag(tag).d("Add plan without image: $name")
                        val plan = Plan(name = name, pinName = pinName, startDate = startDate, endDate = endDate, imagePath = null)
                        plans.add(plan)
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

    fun loadPhotoPaths(
        planName: String,
        onLoaded: (photos: List<RememberPhoto>) -> Unit,
        onInfo: (info: Int) -> Unit,
        onError: (Exception) -> Unit,
    ) {
        val fireStoreDbReferencePhotos = fireStoreDbReferencePlans.document(planName).collection(FireStoreConstants.Collections.photos)

        fireStoreDbReferencePhotos.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val photos = mutableListOf<RememberPhoto>()

                task.result.forEach { document ->
                    Timber.d(tag, "${document.id} => {$document.data}")
                    val path = document.getString(FireStoreConstants.Ids.Plan.path)

                    if (path != null) {
                        Timber.tag(tag).d("Add path: $path")
                        photos.add(RememberPhoto(url = path, documentId = document.id))
                    }
                }

                onLoaded(photos)
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
        }.addOnFailureListener {
            onError(it)
        }
    }

    fun bitmapForPlan(planName: String): Bitmap? {
        return planBitmaps[planName]
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

        fireStoreClient.updateDocumentFields(
            documentReference = planReference,
            data = data,
        ) { exception: Exception? ->
            if (exception != null) {
                onError(exception)
            } else {
                plans.firstOrNull {
                    it.name == planName
                }.apply {
                    this?.startDate = startDate
                    this?.endDate = endDate
                }

                onSuccess()
            }
        }
    }

    fun loadImageIfAvailable(
        imagePath: String,
        onLoaded: (bitmap: Bitmap) -> Unit,
        onInfo: (info: Int) -> Unit,
        onError: (Exception) -> Unit,
    ) {
        val storageImageRef = FirebaseStorage.getInstance().getReferenceFromUrl(imagePath)
        storageImageRef.getBytes(2 * 1024 * 1024)
            .addOnSuccessListener { data ->
                val bitmap = byteArrayToBitmap(data)
                if (bitmap != null) {
                    onLoaded(bitmap)
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

    fun persistPlanPhoto(
        plan: Plan,
        image: Bitmap,
        onSuccess: () -> Unit,
        onInfo: (info: Int) -> Unit,
        onError: (Exception) -> Unit,
    ) {
        val path = fireStoreClient.storageByPath(
            path = FireStoreConstants.Collections.plans,
            fileName = plan.pinName
        )

        storePhoto(
            path = path,
            image = image,
            onError = onError,
            onInfo = onInfo,
            onSuccess = {
                plan.imagePath = it
                planBitmaps[plan.name] = image

                updatePlan(
                    planName = plan.name,
                    imagePath = it,
                    onSuccess = onSuccess,
                    onError = onError,
                )
            },
        )
    }

    fun persistRememberPhoto(
        planName: String,
        image: Bitmap,
        onSuccess: (RememberPhoto) -> Unit,
        onInfo: (info: Int) -> Unit,
        onError: (Exception) -> Unit,
    ) {
        val fileName = "$planName${Random.nextInt()}"
        val path = fireStoreClient.storageByPath(
            path = "${FireStoreConstants.Collections.plans}/$planName/${FireStoreConstants.Collections.photos}",
            fileName = fileName,
        )

        storePhoto(
            path = path,
            image = image,
            onError = onError,
            onInfo = onInfo,
            onSuccess = {
                updatePhotos(
                    planName = planName,
                    path = it.toString(),
                    onSuccess = onSuccess,
                    onError = onError,
                )
            },
        )
    }

    fun deletePhoto(
        planName: String,
        url: String,
        documentId: String,
        onSuccess: () -> Unit,
        onInfo: (info: Int) -> Unit,
        onError: (Exception) -> Unit,
    ) {
        val storageImageRef = FirebaseStorage.getInstance().getReferenceFromUrl(url)
        val fireStoreDbReferencePhotos = fireStoreDbReferencePlans.document(planName).collection(FireStoreConstants.Collections.photos)

        storageImageRef.delete().addOnSuccessListener {
            fireStoreDbReferencePhotos.document(documentId).delete().addOnSuccessListener {
                onSuccess()
            }.addOnFailureListener {
                onError(it)
            }.addOnCanceledListener {
                onInfo(R.string.errorDeleteImage)
            }
        }.addOnFailureListener {
            onError(it)
        }.addOnCanceledListener {
            onInfo(R.string.errorDeleteImage)
        }
    }

    fun planExists(planName: String): Boolean {
        return plans.firstOrNull {
            it.name == planName
        } != null
    }

    fun addPlan(
        name: String,
        pinName: String,
        startDate: Date,
        endDate: Date,
        onError: (Exception) -> Unit,
        completion: () -> Unit,
    ) {
        val plan = Plan(
            name = name,
            pinName = pinName,
            startDate = startDate,
            endDate = endDate,
            imagePath = null,
        )

        persistPlan(plan = plan, onError = onError, completion = completion)
    }

    fun deletePlan(
        plan: Plan,
        onError: (Exception?) -> Unit,
        onSuccess: () -> Unit,
    ) {
        plan.imagePath?.let {
            // delete plan photo in Firebase storage
            val storageImageRef = FirebaseStorage.getInstance().getReferenceFromUrl(it.toString())
            storageImageRef.delete().addOnCompleteListener { imageTask ->
                if (!imageTask.isSuccessful) {
                    onError(imageTask.exception)
                }
            }
        }

        val plannableUtils = PlannableUtilsFactory.getOrCreate(planName = plan.name)
        // delete subdocuments from Firestore
        // no auto deletion of subdocs: https://firebase.google.com/docs/firestore/manage-data/delete-data?hl=en
        plannableUtils.loadPlannables { exception ->
            if (exception != null) {
                onError(exception)
            } else {
                plannableUtils.deleteSubDocuments(pinName = plan.pinName) { subDocException ->
                    if (subDocException != null) {
                        onError(subDocException)
                    } else {
                        onSuccess()
                    }
                }
            }
        }

        val documentRef = fireStoreDbReferencePlans.document(plan.pinName)
        documentRef.delete().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Timber.d(tag, "Document successfully removed!")
                plans.removeIf {
                    it.name == plan.name
                }
                onSuccess()
            } else {
                onError(task.exception)
            }
        }
    }

    private fun persistPlan(
        plan: Plan,
        onError: (Exception) -> Unit,
        completion: () -> Unit,
    ) {
        val data = mapOf(
            FireStoreConstants.Ids.Plan.name to plan.name,
            FireStoreConstants.Ids.Plan.pinName to plan.pinName,
            FireStoreConstants.Ids.Plan.startDate to plan.startDate,
            FireStoreConstants.Ids.Plan.endDate to plan.endDate
        )

        fireStoreClient.addData(collectionReference = fireStoreDbReferencePlans, documentName = plan.pinName, data = data) { exception ->
            if (exception != null) {
                onError(exception)
            } else {
                plans.add(plan)
                completion()
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
            loadImageIfAvailable(
                imagePath = it,
                onLoaded = { bitmap ->
                    if (!plans.contains(plan)) {
                        plans.add(plan)
                    }
                    planBitmaps[plan.name] = bitmap

                    onLoaded(plan, bitmap)
                },
                onInfo = onInfo,
                onError = onError,
            )
        }
    }

    private fun storePhoto(
        path: String,
        image: Bitmap,
        onError: (Exception) -> Unit,
        onInfo: (info: Int) -> Unit,
        onSuccess: (Uri) -> Unit,
    ) {
        fireStoreClient.storePhoto(storageRef = storageRef, path = path, image = image) { metadata, exception ->
            if (exception != null) {
                onError(exception)
            } else {
                val storagePath = metadata?.path
                if (storagePath == null) {
                    onInfo(R.string.imageNotSaved)
                } else {
                    storageRef.child(storagePath).downloadUrl.addOnSuccessListener {
                        onSuccess(it)
                    }.addOnFailureListener {
                        onError(it)
                    }.addOnCanceledListener {
                        R.string.cancelled
                    }
                }
            }
        }
    }

    private fun updatePhotos(
        planName: String,
        path: String,
        onSuccess: (RememberPhoto) -> Unit,
        onError: (Exception) -> Unit,
    ) {
        val data: Map<String, Any> = hashMapOf(
            FireStoreConstants.Ids.Plan.path to path,
        )

        val fireStoreDbReferencePhotos = fireStoreDbReferencePlans.document(planName).collection(FireStoreConstants.Collections.photos)

        fireStoreClient.addData(
            collectionReference = fireStoreDbReferencePhotos,
            data = data,
        ) { exception, documentId ->
            if (exception != null) {
                onError(exception)
            } else {
                onSuccess(RememberPhoto(url = path, documentId = documentId))
            }
        }
    }

    private fun updatePlan(
        planName: String,
        imagePath: Uri,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit,
    ) {
        val data = hashMapOf(
            FireStoreConstants.Ids.Plan.imageReference to imagePath.toString(),
        )

        fireStoreClient.updateDocumentFields(
            documentReference = fireStoreDbReferencePlans.document(planName),
            data = data,
        ) { exception: Exception? ->
            if (exception != null) {
                onError(exception)
            } else {
                plans.firstOrNull {
                    it.name == planName
                }.apply {
                    this?.imagePath = imagePath
                }
                onSuccess()
            }
        }
    }

    private fun byteArrayToBitmap(data: ByteArray): Bitmap? {
        return BitmapFactory.decodeByteArray(data, 0, data.size)
    }
}
