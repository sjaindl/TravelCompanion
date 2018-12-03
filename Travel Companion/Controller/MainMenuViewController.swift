//
//  MainMenuViewController.swift
//  
//
//  Created by Stefan Jaindl on 02.08.18.
//

import UIKit
import Firebase
import FirebaseUI
import GooglePlaces
import GooglePlacePicker

class MainMenuViewController: UIViewController {
    
    fileprivate var _authHandle: AuthStateDidChangeListenerHandle!
    var user: User?
    var displayName = "anonymous".localized()
    var isSignedIn = false
    
    @IBOutlet weak var exploreImage: UIImageView!
    @IBOutlet weak var planImage: UIImageView!
    @IBOutlet weak var rememberImage: UIImageView!
    
    @IBOutlet weak var exploreLabel: UILabel!
    @IBOutlet weak var planLabel: UILabel!
    @IBOutlet weak var rememberLabel: UILabel!
    
    @IBOutlet weak var signOutButton: UIBarButtonItem!
    var dataController: DataController!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        self.navigationItem.title = "mainMenuTitle".localized()
        
        configureAuth()
        configureGestureRecognizers()
    }
    
    func configureGestureRecognizers() {
        addGestureRecognizer(selector: #selector(explore), view: exploreImage)
        addGestureRecognizer(selector: #selector(explore), view: exploreLabel)
        addGestureRecognizer(selector: #selector(plan), view: planImage)
        addGestureRecognizer(selector: #selector(plan), view: planLabel)
        addGestureRecognizer(selector: #selector(remember), view: rememberImage)
        addGestureRecognizer(selector: #selector(remember), view: rememberLabel)
    }
    
    func addGestureRecognizer(selector: Selector?, view: UIView) {
        let gestureRecognizer = UITapGestureRecognizer(target: self, action: selector)
        view.isUserInteractionEnabled = true
        view.addGestureRecognizer(gestureRecognizer)
    }
    
    @objc
    func explore() {
        performSegue(withIdentifier: Constants.Segues.explore, sender: nil)
    }
    
    @objc
    func plan() {
        if !isSignedIn {
            UiUtils.showHint("accountRequired".localized(), title: "loginRequired".localized(), controller: self)
        } else {
            performSegue(withIdentifier: Constants.Segues.plan, sender: nil)
        }
    }
    
    @objc
    func remember() {
        if !isSignedIn {
            UiUtils.showHint("accountRequired".localized(), title: "loginRequired".localized(), controller: self)
        } else {
            performSegue(withIdentifier: Constants.Segues.remember, sender: nil)
        }
    }
    
    deinit {
        Auth.auth().removeStateDidChangeListener(_authHandle)
    }
    
    @IBAction func signOut(_ sender: Any) {
        if isSignedIn {
            user = nil
            
            do {
                try Auth.auth().signOut()
                signedInStatus(isSignedIn: false)
            } catch {
                UiUtils.showError("signOutError".localized(), controller: self)
            }
        } else {
            loginSession()
        }
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if segue.identifier == Constants.Segues.explore {
            let controller = segue.destination as! ExploreViewController
            controller.dataController = dataController
        } else if segue.identifier == Constants.Segues.plan {
            let controller = segue.destination as! PlanViewController
            controller.dataController = dataController
        }
    }
    
    func configureAuth() {
        FUIAuth.defaultAuthUI()?.providers = [FUIGoogleAuth(), FUIFacebookAuth()]
        
        // listen for changes in the authorization state
        _authHandle = Auth.auth().addStateDidChangeListener { (auth: Auth, user: User?) in
            
            // check if there is a current user
            if let activeUser = user {
                // check if the current app user is the current FIRUser
                if self.user != activeUser {
                    self.user = activeUser
                    self.signedInStatus(isSignedIn: true)
                    let name = user!.email!.components(separatedBy: "@")[0]
                    self.displayName = name
                    
                    let firestoreDbReference = FirestoreClient.userReference()
                    let data: [String: String] = [FirestoreConstants.Ids.User.userId: activeUser.uid,
                                                  FirestoreConstants.Ids.User.email: activeUser.email ?? "",
                                                  FirestoreConstants.Ids.User.displayName: activeUser.displayName ?? "",
                                                  FirestoreConstants.Ids.User.providerId: activeUser.providerID,
                                                  FirestoreConstants.Ids.User.photoUrl: activeUser.photoURL?.absoluteString ?? "",
                                                  FirestoreConstants.Ids.User.phoneNumber: activeUser.phoneNumber ?? ""]
                    
                    firestoreDbReference.setData(data)
                }
            } else {
                // user must sign in
                self.signedInStatus(isSignedIn: false)
                self.loginSession()
            }
        }
    }
    
    func signedInStatus(isSignedIn: Bool) {
        self.isSignedIn = isSignedIn
        if isSignedIn {
            self.signOutButton.title = "signOut".localized()
        } else {
            self.signOutButton.title = "signIn".localized()
        }
    }
    
    func loginSession() {
        let authViewController = FUIAuth.defaultAuthUI()!.authViewController()
        present(authViewController, animated: true, completion: nil)
    }
}
