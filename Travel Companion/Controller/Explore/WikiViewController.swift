//
//  WikiViewController.swift
//  Travel Companion
//
//  Created by Stefan Jaindl on 26.08.18.
//  Copyright © 2018 Stefan Jaindl. All rights reserved.
//

import WebKit
import UIKit

class WikiViewController: UIViewController, WKUIDelegate {
    
    var webView: WKWebView!
    var pin: Pin!
    var domain: String!
    
    override func loadView() {
        super.loadView()
        
        let webConfiguration = WKWebViewConfiguration()
        webView = WKWebView(frame: .zero, configuration: webConfiguration)
        webView.uiDelegate = self
        webView.navigationDelegate = self
        webView.translatesAutoresizingMaskIntoConstraints = false
        self.view.addSubview(webView)
        
        if #available(iOS 11, *) {
            let guide = view.safeAreaLayoutGuide
            NSLayoutConstraint.activate([
                webView.leadingAnchor.constraint(equalTo: guide.leadingAnchor),
                webView.trailingAnchor.constraint(equalTo: guide.trailingAnchor),
                webView.topAnchor.constraint(equalTo: guide.topAnchor),
                webView.bottomAnchor.constraint(equalTo: guide.bottomAnchor)
                ])
            
        } else {
            let standardSpacing: CGFloat = 8.0
            let margins = view.layoutMarginsGuide
            NSLayoutConstraint.activate([
                webView.leadingAnchor.constraint(equalTo: margins.leadingAnchor),
                webView.trailingAnchor.constraint(equalTo: margins.trailingAnchor),
                webView.topAnchor.constraint(equalTo: topLayoutGuide.bottomAnchor, constant: standardSpacing),
                bottomLayoutGuide.bottomAnchor.constraint(equalTo: webView.bottomAnchor, constant: standardSpacing),
                webView.centerXAnchor.constraint(equalTo: self.view.centerXAnchor),
                webView.centerYAnchor.constraint(equalTo: self.view.centerYAnchor)
                ])
        }
    }
    
    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
        
        navigationController?.hidesBarsOnSwipe = true
    }
    
    override func viewDidDisappear(_ animated: Bool) {
        super.viewDidDisappear(animated)
        
        navigationController?.hidesBarsOnSwipe = false
        navigationController?.isNavigationBarHidden = false
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        guard let name = pin.name else {
            UiUtils.showError("noWikiPage".localized(), controller: self)
            return
        }
        
        self.tabBarController?.navigationItem.title = name
        
        WikiClient.sharedInstance.fetchWikiLink(country: name, domain: domain) { (error, wikiLink) in
            if let error = error {
                UiUtils.showError(error, controller: self)
            } else {
                let url = URL(string: wikiLink!)
                let request = URLRequest(url:url!)
                DispatchQueue.main.async {
                    self.webView!.load(request)
                }
            }
        }
    }
}

extension WikiViewController: WKNavigationDelegate {
    func webView(_ webView: WKWebView, didFinish navigation: WKNavigation!) {
        debugPrint("finish navigation to \(String(describing: webView.url))")
    }
    
    func webView(_ webView: WKWebView, didFail navigation: WKNavigation!, withError error: Error) {
        UiUtils.showToast(message: error.localizedDescription, view: self.view)
    }
}
