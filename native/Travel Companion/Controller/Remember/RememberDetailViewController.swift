//
//  RememberViewController.swift
//  Travel Companion
//
//  Created by Stefan Jaindl on 20.09.18.
//  Copyright © 2018 Stefan Jaindl. All rights reserved.
//

import Firebase
import FirebaseStorage
import UIKit

class RememberDetailViewController: UIViewController, UIImagePickerControllerDelegate, UINavigationControllerDelegate {

    @IBOutlet weak var collectionView: UICollectionView!
    @IBOutlet weak var flowLayout: UICollectionViewFlowLayout!
    @IBOutlet weak var activityIndicator: UIActivityIndicatorView!
    @IBOutlet weak var noPhotoLabel: UILabel!
    @IBOutlet weak var gallery: UIBarButtonItem!
    @IBOutlet weak var camera: UIBarButtonItem!
    
    let imageCache = GlobalCache.imageCache
    var plan: Plan!
    
    var storageRef: StorageReference!
    var firestorePlanDbReference: CollectionReference!
    var firestorePhotoDbReference: CollectionReference!
    
    var photos = [RememberPhotos]()
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        self.navigationItem.title = plan.name
        
        if !UIImagePickerController.isSourceTypeAvailable(.camera) {
            camera.isEnabled = false
        }
        
        // Do any additional setup after loading the view.
        firestorePhotoDbReference = FirestoreClient.userReference().collection(FirestoreConstants.Collections.plans).document(plan.pinName).collection(FirestoreConstants.Collections.photos)
        
        configureStorage()
        loadPhotos()
    }
    
    deinit {
        firestorePlanDbReference = nil
        firestorePhotoDbReference = nil
    }

    override func viewDidLayoutSubviews() {
        super.viewDidLayoutSubviews()
        UiUtils.setupFlowLayout(for: self, view: view, flowLayout: flowLayout)
    }
    
    func configureStorage() {
        storageRef = Storage.storage().reference()
    }
    
    func loadPhotos() {
        firestorePhotoDbReference.getDocuments() { (querySnapshot, error) in
            if let error = error {
                UiUtils.showToast(message: error.localizedDescription, view: self.view)
                self.enableUi(true)
                self.noPhotoLabel.isHidden = false
            } else {
                for document in querySnapshot!.documents {
                    print("\(document.documentID) => \(document.data())")
                    if let photo = document.data()[FirestoreConstants.Ids.Plan.path] as? String {
                        self.photos.append(RememberPhotos(url: photo, documentId: document.documentID))
                    }
                }
                self.collectionView.reloadData()
                self.enableUi(true)
            }
        }
    }
    
    func enableUi(_ enable: Bool) {
        if enable {
            activityIndicator.stopAnimating()
        } else {
            activityIndicator.startAnimating()
        }
    }
    
    @IBAction func takePhotoFromCamera(_ sender: UIBarButtonItem) {
        showPicker(withType: .camera)
    }
    
    @IBAction func selectPhotoFromAlbum(_ sender: UIBarButtonItem) {
        showPicker(withType: .photoLibrary)
    }
    
    func showPicker(withType: UIImagePickerController.SourceType) {
        enableButtons(false)
        
        let picker = UIImagePickerController()
        picker.delegate = self
        picker.sourceType = withType
        present(picker, animated: true, completion: nil)
    }
    
    func persistPhoto(photoData: Data) {
        let fileName = plan.pinName + String(Int(Date.timeIntervalSinceReferenceDate * 10000000))
        let path = FirestoreClient.storageByPath(path: FirestoreConstants.Collections.plans + "/" + plan.pinName + "/" + FirestoreConstants.Collections.photos, fileName: fileName)
        
        FirestoreClient.storePhoto(storageRef: storageRef, path: path, photoData: photoData) { (metadata, error) in
            if let error = error {
                UiUtils.showToast(message: error.localizedDescription, view: self.view)
                return
            }
            
            guard let storagePath = metadata?.path else {
                UiUtils.showError("imageNotSaved".localized(), controller: self)
                return
            }
            
            let path = self.storageRef.child(storagePath).description
            self.updatePhotos(path)
        }
    }
    
    func updatePhotos(_ path: String) {
        FirestoreClient.addData(collectionReference: firestorePhotoDbReference, data: [
            FirestoreConstants.Ids.Plan.path: path
        ]) { (error, documentId) in
            if let error = error {
                UiUtils.showError(error.localizedDescription, controller: self)
                return
            }
            
            self.photos.append(RememberPhotos(url: path, documentId: documentId))
            self.collectionView.reloadData()
        }
    }
}

extension RememberDetailViewController : UICollectionViewDelegate, UICollectionViewDataSource {
    
    func numberOfSections(in collectionView: UICollectionView) -> Int {
        return 1
    }
    
    func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        return photos.count
    }
    
    func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
        let cell = collectionView.dequeueReusableCell(withReuseIdentifier: Constants.ReuseIds.albumCell, for: indexPath) as! AlbumCollectionViewCell
        
        guard let url = photos[indexPath.row].url else {
            return cell
        }
        
        if let cachedImage = imageCache.object(forKey: url as NSString) {
            cell.locationImage.image = cachedImage
            cell.setNeedsLayout()
        } else {
            cell.locationImage.image = UIImage(named: Constants.CoreData.placeholderImage)
            
            let storageImageRef = Storage.storage().reference(forURL: url)
            storageImageRef.getData(maxSize: 2 * 1024 * 1024) { (imageData, error) in
                if let error = error {
                    UiUtils.showToast(message: error.localizedDescription, view: self.view)
                    return
                }
                
                guard let imageData = imageData, let image = UIImage(data: imageData) else {
                    UiUtils.showToast(message: "noImageData".localized(), view: self.view)
                    return
                }
                
                self.imageCache.setObject(image, forKey: url as NSString)
                cell.locationImage.image = image
                cell.setNeedsLayout()
            }
        }
        
        return cell
    }
    
    func collectionView(_ collectionView: UICollectionView, didSelectItemAt indexPath: IndexPath) {
        let cell = collectionView.cellForItem(at: indexPath) as! AlbumCollectionViewCell
        
        if let image = cell.locationImage.image, let data = image.pngData() {
            
            let alert = UIAlertController(title: "chooseAction".localized(), message: nil, preferredStyle: .alert)
            
            alert.addAction(UIAlertAction(title: "showPhoto".localized(), style: .default, handler: { _ in
                self.performSegue(withIdentifier: Constants.Segues.photoDetail, sender: data)
            }))
            
            let photo = self.photos[indexPath.row]
            
            if let url = photo.url, let documentId = photo.documentId {
            
                alert.addAction(UIAlertAction(title: "delete".localized(), style: .default, handler: { _ in
                    
                    let storageImageRef = Storage.storage().reference(forURL: url)
                    storageImageRef.delete() { error in
                        if let error = error {
                            UiUtils.showError(error.localizedDescription, controller: self)
                        } else {
                            self.firestorePhotoDbReference.document(documentId).delete() { err in
                                if let error = error {
                                    UiUtils.showError(error.localizedDescription, controller: self)
                                } else {
                                    debugPrint("Document successfully removed!")
                                    self.photos.remove(at: indexPath.row)
                                    self.collectionView.reloadData()
                                }
                            }
                        }
                    }
                    
                }))
            }
            
            alert.addAction(UIAlertAction(title: "cancel".localized(), style: .default, handler: { _ in
                self.dismiss(animated: true, completion: nil)
            }))
            
            self.present(alert, animated: true, completion: nil)
        }
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if segue.identifier == Constants.Segues.photoDetail {
            let controller = segue.destination as! PhotosDetailViewController
            controller.data = sender as? Data
        }
    }
    
    func enableButtons(_ enable: Bool) {
        camera.isEnabled = enable
        gallery.isEnabled = enable
    }
}

extension RememberDetailViewController {
    
    func imagePickerController(_ picker: UIImagePickerController, didFinishPickingMediaWithInfo info: [UIImagePickerController.InfoKey : Any]) {
        let info = convertFromUIImagePickerControllerInfoKeyDictionary(info)

        picker.dismiss(animated: true, completion: nil)
        
        if let originalImage = info["UIImagePickerControllerOriginalImage"] as? UIImage, let data = originalImage.pngData() {
            persistPhoto(photoData: data)
        }
        
        enableButtons(true)
    }
    
    func imagePickerControllerDidCancel(_ picker: UIImagePickerController) {
        picker.dismiss(animated: true, completion: nil)
        enableButtons(true)
    }
}

fileprivate func convertFromUIImagePickerControllerInfoKeyDictionary(_ input: [UIImagePickerController.InfoKey: Any]) -> [String: Any] {
	return Dictionary(uniqueKeysWithValues: input.map {key, value in (key.rawValue, value)})
}
