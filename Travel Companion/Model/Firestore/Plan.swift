//
//  Plan.swift
//  Travel Companion
//
//  Created by Stefan Jaindl on 26.08.18.
//  Copyright © 2018 Stefan Jaindl. All rights reserved.
//

import Firebase
import Foundation

class Plan {
    public var name: String
    public var pinName: String
    public var startDate: Timestamp
    public var endDate: Timestamp
    public var imageRef: String
    public var imageData: Data?
    
    public var fligths: [Plannable] = []
    public var publicTransport: [Plannable] = []
    public var hotels: [Plannable] = []
    public var restaurants: [Plannable] = []
    public var attractions: [Plannable] = []
    
    var firestoreFligthDbReference: CollectionReference!
    var firestorePublicTransportDbReference: CollectionReference!
    var firestoreHotelDbReference: CollectionReference!
    var firestoreRestaurantDbReference: CollectionReference!
    var firestoreAttractionDbReference: CollectionReference!
    
    var firestoreRememberPhotosDbReference: CollectionReference!
    
    init(name: String, originalName: String, startDate: Timestamp, endDate: Timestamp) {
        self.name = name
        self.pinName = originalName
        self.startDate = startDate
        self.endDate = endDate
        self.imageRef = ""
        
        configureDatabase()
    }
    
    init(name: String, originalName: String, startDate: Timestamp, endDate: Timestamp, imageRef: String) {
        self.name = name
        self.pinName = originalName
        self.startDate = startDate
        self.endDate = endDate
        self.imageRef = imageRef
        
        configureDatabase()
    }
    
    func deleteSubDocuments(completion: @escaping (_ error: Error?) -> Void) {
        let firestorePlanBaseDbReference = FirestoreClient.userReference().collection(FirestoreConstants.Collections.PLANS).document(pinName)
        
        deleteSubDocument(firestoreCollectionReference: firestorePlanBaseDbReference.collection(FirestoreConstants.Collections.FLIGTHS), plannables: fligths, completion: completion)
        deleteSubDocument(firestoreCollectionReference: firestorePlanBaseDbReference.collection(FirestoreConstants.Collections.PUBLIC_TRANSPORT), plannables: publicTransport, completion: completion)
        deleteSubDocument(firestoreCollectionReference: firestorePlanBaseDbReference.collection(FirestoreConstants.Collections.HOTELS), plannables: hotels, completion: completion)
        deleteSubDocument(firestoreCollectionReference: firestorePlanBaseDbReference.collection(FirestoreConstants.Collections.RESTAURANTS), plannables: restaurants, completion: completion)
        deleteSubDocument(firestoreCollectionReference: firestorePlanBaseDbReference.collection(FirestoreConstants.Collections.ATTRACTIONS), plannables: attractions, completion: completion)
        
        deletePhotos(completion: completion)
    }
    
    func deletePhotos(completion: @escaping (_ error: Error?) -> Void) {
        firestoreRememberPhotosDbReference.getDocuments() { (querySnapshot, error) in
            if let error = error {
                completion(error)
            } else {
                for document in querySnapshot!.documents {
                    
                    //delete remember photo in firebase storage
                    if let photo = document.data()[FirestoreConstants.Ids.Plan.PATH] as? String {
                        let storageImageRef = Storage.storage().reference(forURL: photo)
                        
                        storageImageRef.delete(completion: { (error) in
                            if error != nil {
                                completion(error)
                                return
                            } else {
                                debugPrint("Successfully deleted remember photo")
                            }
                        })
                    }
                    
                    //Delete database reference
                    self.deleteDocument(with: document.documentID, firestoreCollectionReference: self.firestoreRememberPhotosDbReference, completion: completion)
                }
            }
        }
    }
    
    func deleteSubDocument(firestoreCollectionReference: CollectionReference, plannables: [Plannable], completion: @escaping (_ error: Error?) -> Void) {
        for plannable in plannables {
            deleteDocument(with: plannable.getId(), firestoreCollectionReference: firestoreCollectionReference, completion: completion)
        }
    }
    
    func deleteDocument(with id: String, firestoreCollectionReference: CollectionReference, completion: @escaping (_ error: Error?) -> Void) {
        firestoreCollectionReference.document(id).delete() { error in
            if error != nil {
                completion(error)
                return
            } else {
                debugPrint("Document successfully removed!")
            }
        }
    }
    
    func configureDatabase() {
        let planReference = FirestoreClient.userReference().collection(FirestoreConstants.Collections.PLANS).document(name)
        
        firestoreFligthDbReference = planReference.collection(FirestoreConstants.Collections.FLIGTHS)
        firestorePublicTransportDbReference = planReference.collection(FirestoreConstants.Collections.PUBLIC_TRANSPORT)
        firestoreHotelDbReference = planReference.collection(FirestoreConstants.Collections.HOTELS)
        firestoreRestaurantDbReference = planReference.collection(FirestoreConstants.Collections.RESTAURANTS)
        firestoreAttractionDbReference = planReference.collection(FirestoreConstants.Collections.ATTRACTIONS)
        
        firestoreRememberPhotosDbReference = planReference.collection(FirestoreConstants.Collections.PHOTOS)
    }
    
    func loadPlannables(completion: @escaping (_ error: Error?) -> Void) {
        reset()
        
        loadPlannables(\Plan.fligths, collectionReference: firestoreFligthDbReference, plannableType: Constants.PLANNABLES.FLIGHT, completion: completion)
        
        loadPlannables(\Plan.publicTransport, collectionReference: firestorePublicTransportDbReference, plannableType: Constants.PLANNABLES.PUBLIC_TRANSPORT, completion: completion)
        
        loadPlannables(\Plan.hotels, collectionReference: firestoreHotelDbReference, plannableType: Constants.PLANNABLES.HOTEL, completion: completion)
        
        loadPlannables(\Plan.restaurants, collectionReference: firestoreRestaurantDbReference, plannableType: Constants.PLANNABLES.RESTAURANT, completion: completion)
        
        loadPlannables(\Plan.attractions, collectionReference: firestoreAttractionDbReference, plannableType: Constants.PLANNABLES.ATTRACTION, completion: completion)
    }
    
    func reset() {
        fligths.removeAll()
        publicTransport.removeAll()
        hotels.removeAll()
        restaurants.removeAll()
        attractions.removeAll()
    }
    
    func resetReferences() {
        firestoreFligthDbReference = nil
        firestorePublicTransportDbReference = nil
        firestoreHotelDbReference = nil
        firestoreRestaurantDbReference = nil
        firestoreAttractionDbReference = nil
        
        firestoreRememberPhotosDbReference = nil
    }
    
    func loadPlannables(_ plannables: WritableKeyPath<Plan, [Plannable]>, collectionReference: CollectionReference, plannableType: String, completion: @escaping (_ error: Error?) -> Void) {
        
        collectionReference.getDocuments() { (querySnapshot, error) in
            if error != nil {
                completion(error)
            } else {
                for document in querySnapshot!.documents {
                    debugPrint("\(document.documentID) => \(document.data())")
                    
                    let plannable = try? PlannableFactory.createPlannable(of: plannableType, data: document.data())
                    
                    if let plannable = plannable {
                        DispatchQueue.main.async {
                            // Use weak to avoid retain cycle
                            [weak self] in
                            self?[keyPath: plannables].append(plannable)
                            
                            completion(nil)
                        }
                    }
                }
            }
        }
    }
}
