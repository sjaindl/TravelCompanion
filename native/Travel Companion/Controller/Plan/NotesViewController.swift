//
//  NotesViewController.swift
//  Travel Companion
//
//  Created by Stefan Jaindl on 13.09.18.
//  Copyright © 2018 Stefan Jaindl. All rights reserved.
//

import CodableFirebase
import Firebase
import UIKit

class NotesViewController: UIViewController {
    
    var plannable: Plannable!
    var plannableCollectionReference: CollectionReference!
    
    var keyboardHeight: CGFloat = 0.0
    
    @IBOutlet weak var notes: UITextField!
    @IBOutlet weak var bottomConstraint: NSLayoutConstraint!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        self.navigationItem.title = "addNote".localized()
        
        notes.text = plannable.getNotes()
        notes.delegate = self
        
        NotificationCenter.default.addObserver(
            self,
            selector: #selector(keyboardWillShow),
            name: UIResponder.keyboardWillShowNotification,
            object: nil
        )
        
        NotificationCenter.default.addObserver(
            self,
            selector: #selector(keyboardDidHide),
            name: UIResponder.keyboardDidHideNotification,
            object: nil
        )
    }
    
    @objc func keyboardWillShow(_ notification: Notification) {
        if let keyboardFrame: NSValue = notification.userInfo?[UIResponder.keyboardFrameEndUserInfoKey] as? NSValue {
            let keyboardRectangle = keyboardFrame.cgRectValue
            keyboardHeight = keyboardRectangle.height
            setTextFieldConstraints()
        }
    }
    
    @objc func keyboardDidHide(_ notification: Notification) {
        keyboardHeight = 0
        setTextFieldConstraints()
    }
    
    func setTextFieldConstraints() {
        bottomConstraint.constant = keyboardHeight + 8
    }
    
    @IBAction func addNote(_ sender: Any) {
        persistNotes()
        
        dismiss(animated: true, completion: nil)
    }
    
    @IBAction func cancel(_ sender: Any) {
        dismiss(animated: true, completion: nil)
    }
    
    func persistNotes() {
        guard let text = notes.text, text != plannable.getNotes() else {
            return
        }
        
        plannable.setNotes(notes: text)
        
        let docData = plannable.encode()
        
        FirestoreClient.addData(collectionReference: plannableCollectionReference, documentName: plannable.getId(), data: docData) { (error) in
            if let error = error {
                UiUtils.showError(error.localizedDescription, controller: self)
            } else {
                debugPrint("Notes document added")
            }
        }
    }
}

extension NotesViewController: UITextFieldDelegate {
    func textFieldShouldReturn(_ textField: UITextField) -> Bool {
        textField.resignFirstResponder()
        return true
    }
}
