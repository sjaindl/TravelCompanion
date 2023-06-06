package com.sjaindl.travelcompanion.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import com.sjaindl.travelcompanion.R
import com.sjaindl.travelcompanion.api.firestore.FireStoreClient
import com.sjaindl.travelcompanion.api.firestore.FireStoreConstants
import com.sjaindl.travelcompanion.plan.Plan
import com.sjaindl.travelcompanion.remember.detail.RememberPhoto
import timber.log.Timber
import java.util.Date
import kotlin.random.Random

// TODO: Caching of plans in a hashmap
// https://firebase.google.com/docs/firestore/manage-data/enable-offline?hl=en#java
object FireStoreUtils {
    private const val tag = "FireStoreUtils"

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
        val path = FireStoreClient.storageByPath(
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
                updatePlan(
                    planName = plan.name,
                    imagePath = it.toString(),
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
        val path = FireStoreClient.storageByPath(
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
        FireStoreClient.storePhoto(storageRef = storageRef, path = path, image = image) { metadata, exception ->
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

        FireStoreClient.addData(
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
