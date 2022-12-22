package com.sjaindl.travelcompanion.persistence

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.CollectionReference
import dev.gitlive.firebase.firestore.DocumentReference
import dev.gitlive.firebase.firestore.FirebaseFirestore
import dev.gitlive.firebase.firestore.firestore

class FirestoreClient {
    companion object {
        fun newDatabaseInstance(): FirebaseFirestore {
            return Firebase.firestore
        }

        fun userReference(): DocumentReference {
            val uid = Firebase.auth.currentUser?.uid ?: "anonymous"
            return newDatabaseInstance().collection(FirestoreConstants.Collections.users).document(uid)
        }

        suspend fun addData(
            collectionReference: CollectionReference,
            documentName: String,
            data: Map<String, Any>,
            completion: (Error?) -> (Unit)
        ) {
            collectionReference.document(documentName).set(data)
            completion(null)
            /*
            setData(data) { err ->
                completion(err)
            }
             */
        }

        suspend fun addData(collectionReference: CollectionReference, data: Map<String, Any>, completion: (Error?, String?) -> (Unit)) {
            val document = collectionReference.document
            document.set(data)
            completion(null, document.id)
            /*
            document.setData(data) { err ->
                completion(err, document.id)
            }
             */
        }

        fun storageByPath(path: String): String {
            val uid = Firebase.auth.currentUser?.uid ?: "anonymous"
            return "$uid/$path"
        }

        fun storageByPath(path: String, fileName: String): String {
            val uid = Firebase.auth.currentUser?.uid ?: "anonymous"
            return "$uid/$path/$fileName.jpg"
        }
    }
}
//make Timestamp codable
