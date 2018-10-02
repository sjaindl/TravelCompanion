//
//  WikiViewController.swift
//  Travel Companion
//
//  Created by Stefan Jaindl on 26.08.18.
//  Copyright © 2018 Stefan Jaindl. All rights reserved.
//

import WebKit
import UIKit

class WikiViewController: UIViewController, WKNavigationDelegate {

    //@IBOutlet weak var webView: WKWebView!
    
    @IBOutlet weak var webView: WKWebView!
    var pin: Pin!
    var domain: String!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        guard let name = pin.name else {
            UiUtils.showToast(message: "No Wiki page found", view: self.view)
            return
        }
        
        self.tabBarController?.navigationItem.title = name
        
        WikiClient.sharedInstance.fetchWikiLink(country: name, domain: domain) { (error, wikiLink) in
            if let error = error {
                debugPrint(error.debugDescription)
            } else {
                let url = URL(string: wikiLink!)
                let request = URLRequest(url:url!)
                DispatchQueue.main.async {
                    self.webView!.load(request)
                }
            }
        }
        
    }
    
    func webView(_ webView: WKWebView, didFinish navigation: WKNavigation!) {
        debugPrint("finish navigation to \(String(describing: webView.url))")
    }
    
    func webView(_ webView: WKWebView, didFail navigation: WKNavigation!, withError error: Error) {
        UiUtils.showToast(message: error.localizedDescription, view: self.view)
    }
    
}
