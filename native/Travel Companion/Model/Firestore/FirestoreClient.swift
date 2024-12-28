//
//  FirestoreClient.swift
//  Travel Companion
//
//  Created by Stefan Jaindl on 14.08.18.
//  Copyright © 2018 Stefan Jaindl. All rights reserved.
//

import CodableFirebase
import Firebase
import FirebaseAuth
import FirebaseStorage
import Foundation

class FirestoreClient {
    
    static let sharedInstance = FirestoreClient()
    
    private init() {}
    
    static func newDatabaseInstance() -> Firestore {
        let firestoreDb = Firestore.firestore()
        let settings = firestoreDb.settings
        firestoreDb.settings = settings
        return firestoreDb
    }
    
    static func userReference() -> DocumentReference {
        let uid = Auth.auth().currentUser?.uid ?? "anonymous"
        return newDatabaseInstance().collection(FirestoreConstants.Collections.users).document(uid)
    }
    
    static func addData(collectionReference: CollectionReference, documentName: String, data: [String: Any], completion: @escaping (Error?) -> ()) {
        collectionReference.document(documentName).setData(data) { err in
            completion(err)
        }
    }
    
    static func addData(collectionReference: CollectionReference, data: [String: Any], completion: @escaping (Error?, String?) -> ()) {
        let document = collectionReference.document()
        document.setData(data) { err in
            completion(err, document.documentID)
        }
    }
    
    static func storageByPath(path: String) -> String {
        let uid = Auth.auth().currentUser?.uid ?? "anonymous"
        let path = uid + "/" + path
        return path
    }
    
    static func storageByPath(path: String, fileName: String) -> String {
        let uid = Auth.auth().currentUser?.uid ?? "anonymous"
        let path = uid + "/" + path + "/" + fileName + ".jpg"
        return path
    }
    
    static func storePhoto(storageRef: StorageReference, path: String, photoData: Data, completionHandler: @escaping (_ metadata: StorageMetadata?, _ error: Error?) -> Void) {
        let metadata = StorageMetadata()
        metadata.contentType = "image/jpeg"
        let userInfo = [NSLocalizedDescriptionKey : "Could not store photo"]
        let error = NSError(domain: "storePhoto", code: 1, userInfo: userInfo)
        
        guard let image = UIImage(data: photoData) else {
            completionHandler(nil, error)
            return
        }
        
        let resizedImage = resizeImage(image: image, targetSize: CGSize(width: FirestoreRemoteConfig.sharedInstance.photoResizingWidth, height: FirestoreRemoteConfig.sharedInstance.photoResizingHeight))
        
        guard let resizedImageData = resizedImage.pngData() else {
            completionHandler(nil, error)
            return
        }
        
        storageRef.child(path).putData(resizedImageData, metadata: metadata) { (metadata: StorageMetadata?, error: Error?) in
            completionHandler(metadata, error)
        }
    }
    
    static func resizeImage(image: UIImage, targetSize: CGSize) -> UIImage {
        let size = image.size
        
        let widthRatio = targetSize.width  / size.width
        let heigthRatio = targetSize.height / size.height
        
        //Determine aspect ratio (by orientation):
        var newSize: CGSize
        if(widthRatio > heigthRatio) {
            newSize = CGSize(width: size.width * heigthRatio, height: size.height * heigthRatio)
        } else {
            newSize = CGSize(width: size.width * widthRatio,  height: size.height * widthRatio)
        }
        
        let rect = CGRect(x: 0, y: 0, width: newSize.width, height: newSize.height)
        
        //Resize image with UIGraphics
        UIGraphicsBeginImageContextWithOptions(newSize, false, 1.0)
        image.draw(in: rect)
        let newImage = UIGraphicsGetImageFromCurrentImageContext()
        UIGraphicsEndImageContext()
        
        return newImage!
    }
}

extension Timestamp: TimestampType {} //make Timestamp codable
