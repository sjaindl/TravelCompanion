//
//  ListDataSource.swift
//  VirtualTourist
//
//  Created by Stefan Jaindl on 03.07.18.
//  Copyright © 2018 Stefan Jaindl. All rights reserved.
//

import CoreData
import Foundation
import UIKit

class GenericListDataSource<ObjectType: NSManagedObject, CellType: UICollectionViewCell>: NSObject, UICollectionViewDataSource, NSFetchedResultsControllerDelegate {
    
    var fetchedResultsController: NSFetchedResultsController<ObjectType>?
    var collectionView: UICollectionView!
    var cellReuseId: String
    
    init(collectionView: UICollectionView, managedObjectContext: NSManagedObjectContext, fetchRequest: NSFetchRequest<ObjectType>, cellReuseId: String, cacheName: String?) {
        self.collectionView = collectionView
        self.cellReuseId = cellReuseId
        
        self.fetchedResultsController = NSFetchedResultsController(fetchRequest: fetchRequest, managedObjectContext: managedObjectContext, sectionNameKeyPath: nil, cacheName: cacheName)
        
        super.init()
        
        fetchedResultsController?.delegate = self
    }
    
    func numberOfSections(in collectionView: UICollectionView) -> Int {
        return fetchedResultsController?.sections?.count ?? 1
    }
    
    func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
        let cell = collectionView.dequeueReusableCell(withReuseIdentifier: cellReuseId, for: indexPath) as! CellType
        
        return cell
    }
    
    func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        return fetchedResultsController?.sections?[section].numberOfObjects ?? 0
    }
    
    func fetchedObjects() -> [ObjectType]? {
        return fetchedResultsController?.fetchedObjects
    }
    
    func performFetch() throws {
        try fetchedResultsController?.performFetch()
    }
    
    func object(at: IndexPath) -> ObjectType? {
        return fetchedResultsController?.object(at: at)
    }
    
    func count() -> Int {
        return fetchedResultsController?.fetchedObjects?.count ?? 0
    }

    func controller(_ controller: NSFetchedResultsController<NSFetchRequestResult>, didChange anObject: Any, at indexPath: IndexPath?, for type: NSFetchedResultsChangeType, newIndexPath: IndexPath?) {
        
        switch type {
        case .insert:
            if let newIndexPath = newIndexPath {
                collectionView.insertItems(at: [newIndexPath])
            }
        
        case .delete:
            if let indexPath = indexPath {
                collectionView.deleteItems(at: [indexPath])
            }
        
        case .move:
            if let indexPath = indexPath, let newIndexPath = newIndexPath {
                collectionView.moveItem(at: indexPath, to: newIndexPath)
            }
        
        case .update:
            if let indexPath = indexPath {
                collectionView.reloadItems(at: [indexPath])
            }
        @unknown default:
            debugPrint("Unsupported section action type")
        }
        
    }
    
    func controller(_ controller: NSFetchedResultsController<NSFetchRequestResult>, didChange sectionInfo: NSFetchedResultsSectionInfo, atSectionIndex sectionIndex: Int, for type: NSFetchedResultsChangeType) {
        let indexSet = IndexSet(integer: sectionIndex)
        
        switch type {
        case .insert:
            collectionView.insertSections(indexSet)
        case .delete:
            collectionView.deleteSections(indexSet)
        case .update, .move:
            debugPrint("Unsupported section action type")
        @unknown default:
            debugPrint("Unsupported section action type")
        }
    }
}
