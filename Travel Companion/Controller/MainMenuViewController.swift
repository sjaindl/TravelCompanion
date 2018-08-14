//
//  MainMenuViewController.swift
//  
//
//  Created by Stefan Jaindl on 02.08.18.
//

import UIKit
import Firebase
import FirebaseAuthUI
import FirebaseFacebookAuthUI
import FirebaseGoogleAuthUI
import GooglePlaces
import GooglePlacePicker

class MainMenuViewController: UIViewController {
    
    fileprivate var _authHandle: AuthStateDidChangeListenerHandle!
    var user: User?
    var displayName = "Anonymous"
    
    @IBOutlet weak var exploreImage: UIImageView!
    @IBOutlet weak var planImage: UIImageView!
    @IBOutlet weak var rememberImage: UIImageView!
    
    @IBOutlet weak var exploreLabel: UILabel!
    @IBOutlet weak var planLabel: UILabel!
    @IBOutlet weak var rememberLabel: UILabel!
    
    var dataController: DataController!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view.

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
        print("explore")
        performSegue(withIdentifier: Constants.EXPLORE_SEGUE_ID, sender: nil)
    }
    
    @objc
    func plan() {
        print("plan")
    }
    
    @objc
    func remember() {
        print("remember")
    }
    
    deinit {
        Auth.auth().removeStateDidChangeListener(_authHandle)
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    @IBAction func signOut(_ sender: Any) {
        user = nil
        try? Auth.auth().signOut()
        
        loginSession()
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if segue.identifier == Constants.EXPLORE_SEGUE_ID {
            let controller = segue.destination as! ExploreViewController
            controller.dataController = dataController
        }
    }
    
    func configureAuth() {
        FUIAuth.defaultAuthUI()?.providers = [FUIGoogleAuth(), FUIFacebookAuth()]
        
        // listen for changes in the authorization state
        _authHandle = Auth.auth().addStateDidChangeListener { (auth: Auth, user: User?) in
            // refresh table data
//            self.messages.removeAll(keepingCapacity: false)
//            self.messagesTable.reloadData()
            
            // check if there is a current user
            if let activeUser = user {
                // check if the current app user is the current FIRUser
                if self.user != activeUser {
                    self.user = activeUser
//                    self.signedInStatus(isSignedIn: true)
                    let name = user!.email!.components(separatedBy: "@")[0]
                    self.displayName = name
                    
                    let firestoreDbReference = FirestoreClient.userReference()
                    firestoreDbReference.setData([FirestoreConstants.Ids.User.UID : user?.uid ?? "",
                                                  FirestoreConstants.Ids.User.EMAIL: user?.email ?? "",
                                                  FirestoreConstants.Ids.User.NAME: user?.displayName ?? "",
                                                  FirestoreConstants.Ids.User.PROVIDER: user?.providerID ?? "",
                                                  FirestoreConstants.Ids.User.PHOTO_URL: user?.photoURL ?? "",
                                                  FirestoreConstants.Ids.User.PHONE: user?.phoneNumber ?? ""])
                }
            } else {
                // user must sign in
//                self.signedInStatus(isSignedIn: false)
                self.loginSession()
            }
        }
    }

    func loginSession() {
        let authViewController = FUIAuth.defaultAuthUI()!.authViewController()
        present(authViewController, animated: true, completion: nil)
    }
    
}
