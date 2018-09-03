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
    
    static func storageByPath(path: String, fileName: String) -> String {
        let uid = Auth.auth().currentUser?.uid ?? "anonymous"
        let path = path + "/" + uid + "/" + fileName + ".jpg" //String(Double(Date.timeIntervalSinceReferenceDate * 1000))
        return path
    }
    
    static func storePhoto(storageRef: StorageReference, path: String, photoData: Data, completionHandler: @escaping (_ metadata: StorageMetadata?, _ error: Error?) -> Void) {
        let metadata = StorageMetadata()
        metadata.contentType = "image/jpeg"
        
        storageRef.child(path).putData(photoData, metadata: metadata) { (metadata: StorageMetadata?, error: Error?) in
            completionHandler(metadata, error)
        }
    }
    
}
