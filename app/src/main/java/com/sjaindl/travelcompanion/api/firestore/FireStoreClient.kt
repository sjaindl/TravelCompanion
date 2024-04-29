package com.sjaindl.travelcompanion.api.firestore

import android.graphics.Bitmap
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.storage.StorageMetadata
import com.google.firebase.storage.StorageReference

interface FireStoreClientObserver {
    fun didAddData(documentName: String)
}

interface FireStoreClient {
    fun addObserver(observer: FireStoreClientObserver)

    fun removeObserver(observer: FireStoreClientObserver)

    fun userReference(): DocumentReference

    fun addData(
        collectionReference: CollectionReference,
        documentName: String,
        data: Map<String, Any?>,
        completion: (Exception?) -> Unit,
    )

    fun addData(collectionReference: CollectionReference, data: Map<String, Any>, completion: (Exception?, String?) -> Unit)

    fun updateDocumentFields(documentReference: DocumentReference, data: Map<String, Any>, completion: (Exception?) -> Unit)

    fun storageByPath(path: String): String

    fun storageByPath(path: String, fileName: String): String

    fun storePhoto(
        storageRef: StorageReference,
        path: String,
        image: Bitmap,
        completionHandler: (metadata: StorageMetadata?, exception: Exception?) -> Unit,
    )
}

