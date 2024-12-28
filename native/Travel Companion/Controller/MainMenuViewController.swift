//
//  MainMenuViewController.swift
//  
//
//  Created by Stefan Jaindl on 02.08.18.
//

import UIKit
import FirebaseAuth
import FirebaseAuthUI
import FirebaseEmailAuthUI
import FirebaseFacebookAuthUI
import FirebaseGoogleAuthUI
import FirebaseOAuthUI
import shared

class MainMenuViewController: UIViewController {
    fileprivate var _authHandle: AuthStateDidChangeListenerHandle!
    var user: User?
    var displayName: String?
    var isSignedIn = false
    
    @IBOutlet weak var exploreImage: UIImageView!
    @IBOutlet weak var planImage: UIImageView!
    @IBOutlet weak var rememberImage: UIImageView!
    
    @IBOutlet weak var exploreLabel: UILabel!
    @IBOutlet weak var planLabel: UILabel!
    @IBOutlet weak var rememberLabel: UILabel!
    
    @IBOutlet weak var exporeDetailLabel: UILabel!
    @IBOutlet weak var planDetailLabel: UILabel!
    @IBOutlet weak var rememberDetailLabel: UILabel!
    
    @IBOutlet weak var signOutButton: UIBarButtonItem!
    
    var dataController: DataController!
    
    var forwardToPlanScreen = false
    var forwardToRememberScreen = false
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        setupNavigation()
        
        configureAuth()
        configureGestureRecognizers()
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        
        isSignedIn = Auth.auth().currentUser != nil
        handleAuthStateChanges()
    }
    
    override func viewWillDisappear(_ animated: Bool) {
        super.viewWillDisappear(animated)
        
        Auth.auth().removeStateDidChangeListener(_authHandle)
    }
    
    func setupNavigation() {
        navigationItem.title = "mainMenuTitle".localized()
        navigationController?.navigationBar.backgroundColor = UIColor.gray
        
        //self.navigationItem.title = CommonKt.createApplicationScreenMessage() //just for testing kotlin
        
        addUserProfileNavigationItem()
    }
    
    func configureGestureRecognizers() {
        addGestureRecognizer(selector: #selector(explore), view: exploreImage)
        addGestureRecognizer(selector: #selector(explore), view: exploreLabel)
        addGestureRecognizer(selector: #selector(explore), view: exporeDetailLabel)
        
        addGestureRecognizer(selector: #selector(plan), view: planImage)
        addGestureRecognizer(selector: #selector(plan), view: planLabel)
        addGestureRecognizer(selector: #selector(plan), view: planDetailLabel)
        
        addGestureRecognizer(selector: #selector(remember), view: rememberImage)
        addGestureRecognizer(selector: #selector(remember), view: rememberLabel)
        addGestureRecognizer(selector: #selector(remember), view: rememberDetailLabel)
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
            forwardToPlanScreen = true
            forwardToRememberScreen = false
            loginSession()
        } else {
            performSegue(withIdentifier: Constants.Segues.plan, sender: nil)
        }
    }
    
    @objc
    func remember() {
        if !isSignedIn {
            forwardToRememberScreen = true
            forwardToPlanScreen = false
            loginSession()
        } else {
            performSegue(withIdentifier: Constants.Segues.remember, sender: nil)
        }
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
        let authUI = FUIAuth.defaultAuthUI()!
        
        authUI.providers.append(FUIFacebookAuth(authUI: authUI))
        authUI.providers.append(FUIEmailAuth.init())
        authUI.providers.append(FUIGoogleAuth.init(authUI: authUI))
        authUI.providers.append(FUIOAuth.appleAuthProvider())
    }
    
    func handleAuthStateChanges() {
        // listen for changes in the authorization state
        _authHandle = Auth.auth().addStateDidChangeListener { (auth: Auth, user: User?) in
            
            // check if there is a current user
            if let activeUser = user {
                self.user = activeUser
                
                if let displayName = activeUser.displayName {
                    self.displayName = displayName
                } else if let email = user?.email {
                    self.displayName = email.components(separatedBy: "@")[0]
                }
                
                self.signedInStatus(isSignedIn: true)
                
                let firestoreDbReference = FirestoreClient.userReference()
                let data: [String: String] = [FirestoreConstants.Ids.User.userId: activeUser.uid,
                                              FirestoreConstants.Ids.User.email: activeUser.email ?? "",
                                              FirestoreConstants.Ids.User.displayName: activeUser.displayName ?? "",
                                              FirestoreConstants.Ids.User.providerId: activeUser.providerID,
                                              FirestoreConstants.Ids.User.photoUrl: activeUser.photoURL?.absoluteString ?? "",
                                              FirestoreConstants.Ids.User.phoneNumber: activeUser.phoneNumber ?? ""]
                
                firestoreDbReference.setData(data)
                
                if self.forwardToPlanScreen {
                    self.plan()
                } else if self.forwardToRememberScreen {
                    self.remember()
                }
            } else {
                self.signedInStatus(isSignedIn: false)
            }
            
            self.forwardToPlanScreen = false
            self.forwardToRememberScreen = false
        }
    }
    
    func signedInStatus(isSignedIn: Bool) {
        self.isSignedIn = isSignedIn
        if isSignedIn {
            self.signOutButton.title = "signOut".localized()
        } else {
            self.signOutButton.title = "signIn".localized()
            displayName = nil
        }
        
        addUserProfileNavigationItem()
    }
    
    func loginSession() {
        let authViewController = FUIAuth.defaultAuthUI()!.authViewController()
        present(authViewController, animated: true, completion: nil)
    }
}
