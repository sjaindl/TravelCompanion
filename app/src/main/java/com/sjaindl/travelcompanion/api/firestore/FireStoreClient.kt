package com.sjaindl.travelcompanion.api.firestore

import android.graphics.Bitmap
import android.graphics.Matrix
import androidx.compose.ui.geometry.Size
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageMetadata
import com.google.firebase.storage.StorageReference
import java.io.ByteArrayOutputStream

interface FireStoreClientObserver {
    fun didAddData(documentName: String)
}

object FireStoreClient {
    private var observers = mutableListOf<FireStoreClientObserver>()

    fun addObserver(observer: FireStoreClientObserver) {
        observers += observer
    }

    fun removeObserver(observer: FireStoreClientObserver) {
        observers -= observer
    }

    private fun newDatabaseInstance(): FirebaseFirestore {
        val fireStoreDb = FirebaseFirestore.getInstance()
        val settings = fireStoreDb.firestoreSettings
        fireStoreDb.firestoreSettings = settings
        return fireStoreDb
    }

    fun userReference(): DocumentReference {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: "anonymous"
        return newDatabaseInstance().collection(FireStoreConstants.Collections.users).document(uid)
    }

    fun addData(
        collectionReference: CollectionReference,
        documentName: String,
        data: Map<String, Any?>,
        completion: (Exception?) -> Unit,
    ) {
        collectionReference.document(documentName).set(data).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                completion(null)
                observers.forEach { it.didAddData(documentName) }
            } else {
                completion(task.exception)
            }
        }
    }

    fun addData(collectionReference: CollectionReference, data: Map<String, Any>, completion: (Exception?, String?) -> Unit) {
        val document = collectionReference.document()
        document.set(data).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                completion(null, document.id)
            } else {
                completion(task.exception, null)
            }
        }
    }

    fun updateDocumentFields(documentReference: DocumentReference, data: Map<String, Any>, completion: (Exception?) -> Unit) {
        documentReference.update(data).addOnFailureListener {
            completion(it)
        }.addOnSuccessListener {
            completion(null)
        }.addOnCanceledListener {
            completion(null)
        }
    }

    fun storageByPath(path: String): String {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: "anonymous"
        return "$uid/$path"
    }

    fun storageByPath(path: String, fileName: String): String {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: "anonymous"
        return "$uid/$path/$fileName.jpg"
    }

    fun storePhoto(
        storageRef: StorageReference,
        path: String,
        image: Bitmap,
        completionHandler: (metadata: StorageMetadata?, exception: Exception?) -> Unit
    ) {
        val metadata = StorageMetadata.Builder()
            .setContentType("image/jpeg")
            .build()

        val resizedImage = resizeImage(
            image,
            FireStoreRemoteConfig.photoResizingWidth,
            FireStoreRemoteConfig.photoResizingHeight,
        )

        val outputStream = ByteArrayOutputStream()
        resizedImage.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        val resizedImageData = outputStream.toByteArray()

        storageRef.child(path).putBytes(resizedImageData, metadata).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                completionHandler(task.result.metadata, null)
            } else {
                completionHandler(null, task.exception ?: Exception("Could not store photo"))
            }
        }
    }

    private fun resizeImage(image: Bitmap, targetWidth: Int, targetHeight: Int): Bitmap {
        val size = Size(image.width.toFloat(), image.height.toFloat())

        val widthRatio = targetWidth.toFloat() / size.width
        val heightRatio = targetHeight.toFloat() / size.height

        // Determine aspect ratio (by orientation):
        val newSize = if (widthRatio > heightRatio) {
            Size(size.width * heightRatio, size.height * heightRatio)
        } else {
            Size(size.width * widthRatio, size.height * widthRatio)
        }

        val matrix = Matrix()
        matrix.postScale(newSize.width / size.width, newSize.height / size.height)

        return Bitmap.createBitmap(image, 0, 0, size.width.toInt(), size.height.toInt(), matrix, true)
    }
}
