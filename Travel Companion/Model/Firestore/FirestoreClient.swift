//
//  FirestoreClient.swift
//  Travel Companion
//
//  Created by Stefan Jaindl on 14.08.18.
//  Copyright © 2018 Stefan Jaindl. All rights reserved.
//

import Firebase
import Foundation

class FirestoreClient {
    
    static let sharedInstance = FirestoreClient()
    
    private init() {}
    
    static func newDatabaseInstance() -> Firestore {
        let firestoreDb = Firestore.firestore()
        let settings = firestoreDb.settings
        settings.areTimestampsInSnapshotsEnabled = true
        firestoreDb.settings = settings
        return firestoreDb
    }
    
    static func userReference() -> DocumentReference {
        let uid = Auth.auth().currentUser?.uid ?? "anonymous"
        return newDatabaseInstance().collection(FirestoreConstants.Collections.USERS).document(uid)
    }
    
    static func addData(collectionReference: CollectionReference, documentName: String, data: [String: Any], completion: @escaping (Error?) -> ()) {
        collectionReference.document(documentName).setData(data) { err in
            completion(err)
        }
    }
    
}
