//
//  ExploreInfoViewController.swift
//  Travel Companion
//
//  Created by Stefan Jaindl on 09.07.19.
//  Copyright © 2019 Stefan Jaindl. All rights reserved.
//

import UIKit
import shared
import WebKit

class ExploreInfoViewController: UIViewController, WKUIDelegate {

    @IBOutlet weak var wikipediaButton: UIButton!
    @IBOutlet weak var wikivoyageButton: UIButton!
    @IBOutlet weak var googleButton: UIButton!
    @IBOutlet weak var lonelyplanetButton: UIButton!
    @IBOutlet weak var infoStack: UIStackView!
    
    var webView: WKWebView!
    var pin: Pin!
    var placeName: String = ""
    
    override public func viewDidLoad() {
        super.viewDidLoad()
        
        guard let name = pin.name else {
            UiUtils.showError("noPage".localized(), controller: self)
            return
        }
        
        placeName = name
        
        self.tabBarController?.navigationItem.title = name
        
        openWikipedia(self)
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
                webView.topAnchor.constraint(equalTo: guide.topAnchor, constant: 5),
                webView.bottomAnchor.constraint(equalTo: infoStack.topAnchor)
                ])
            
        } else {
            let standardSpacing: CGFloat = 8.0
            let margins = view.layoutMarginsGuide
            NSLayoutConstraint.activate([
                webView.leadingAnchor.constraint(equalTo: margins.leadingAnchor),
                webView.trailingAnchor.constraint(equalTo: margins.trailingAnchor),
                webView.topAnchor.constraint(equalTo: topLayoutGuide.bottomAnchor, constant: standardSpacing),
                webView.bottomAnchor.constraint(equalTo: infoStack.topAnchor, constant: standardSpacing),
                webView.centerXAnchor.constraint(equalTo: self.view.centerXAnchor),
                webView.centerYAnchor.constraint(equalTo: self.view.centerYAnchor)
                ])
        }
    }
    
    @IBAction func openWikipedia(_ sender: Any) {
        openWikiLink(for: WikiConstants.UrlComponents.domainWikipedia)
    }
    
    @IBAction func openWikiVoyage(_ sender: Any) {
        openWikiLink(for: WikiConstants.UrlComponents.domainWikiVoyage)
    }
    
    func openWikiLink(for domain: String) {
        WikiClient.sharedInstance.fetchWikiLink(country: placeName, domain: domain) { (error, wikiLink) in
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
    
    @IBAction func openGoogle(_ sender: Any) {
        let urlComponents = GoogleConstants.UrlComponents()
        let domain = urlComponents.DOMAIN_SEARCH
        let queryItems: [String: String] = [GoogleConstants.ParameterKeys().SEARCH_QUERY: placeName]
        
        let url = WebClient.sharedInstance.createUrl(
            forScheme: urlComponents.URL_PROTOCOL,
            forHost: domain,
            forMethod: urlComponents.PATH_SEARCH,
            withQueryItems: queryItems
        )
        
        loadUrl(url, queryItems: queryItems)
    }
    
    @IBAction func openLonelyPlanet(_ sender: Any) {
        let domain = LonelyPlanetConstants.UrlComponents.domain
        
        let queryItems: [String: String] = [LonelyPlanetConstants.ParameterKeys.searchQuery: placeName]
        
        let url = WebClient.sharedInstance.createUrl(forScheme: LonelyPlanetConstants.UrlComponents.urlProtocol, forHost: domain, forMethod: LonelyPlanetConstants.UrlComponents.pathSearch, withQueryItems: queryItems)
        
        loadUrl(url, queryItems: queryItems)
    }
    
    func loadUrl(_ url: URL, queryItems: [String: String]) {
        let request = URLRequest(url:url)
        
        DispatchQueue.main.async {
            self.webView!.load(request)
        }
    }
}

extension ExploreInfoViewController: WKNavigationDelegate {
    func webView(_ webView: WKWebView, didFinish navigation: WKNavigation!) {
        debugPrint("finish navigation to \(String(describing: webView.url))")
    }
    
    func webView(_ webView: WKWebView, didFail navigation: WKNavigation!, withError error: Error) {
        UiUtils.showToast(message: error.localizedDescription, view: self.view)
    }
}
