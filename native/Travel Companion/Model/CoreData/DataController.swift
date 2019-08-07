//
//  DataController.swift
//  VirtualTourist
//
//  Created by Stefan Jaindl on 17.06.18.
//  Copyright © 2018 Stefan Jaindl. All rights reserved.
//

import CoreData
import Foundation

class DataController {
    let persistentContainer: NSPersistentContainer
    var backgroundContext: NSManagedObjectContext!
    var viewContext: NSManagedObjectContext {
        return persistentContainer.viewContext
    }
    
    init(persistentContainer: NSPersistentContainer) {
        self.persistentContainer = persistentContainer
    }
    
    func load(completion: (() -> Void)? = nil) {
        persistentContainer.loadPersistentStores() { (description, error) in
            guard error == nil else {
                debugPrint("\(error!.localizedDescription)")
                return
            }
            
            self.backgroundContext = self.persistentContainer.newBackgroundContext()
            
            self.configureContexts()
            completion?()
        }
    }
    
    func save() throws {
        if viewContext.hasChanges {
            try viewContext.save()
        }
    }
    
    func configureContexts() {
        persistentContainer.viewContext.automaticallyMergesChangesFromParent = true
        backgroundContext.automaticallyMergesChangesFromParent = true
        
        persistentContainer.viewContext.mergePolicy = NSMergePolicy.mergeByPropertyStoreTrump
        backgroundContext.mergePolicy = NSMergePolicy.mergeByPropertyObjectTrump
    }
}
