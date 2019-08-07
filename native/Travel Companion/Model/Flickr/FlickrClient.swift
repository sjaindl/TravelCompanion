//
//  ParseClient.swift
//  VirtualTourist
//
//  Created by Stefan Jaindl on 23.05.18.
//  Copyright © 2018 Stefan Jaindl. All rights reserved.
//

import CoreLocation
import Foundation

class FlickrClient {
    
    static let sharedInstance = FlickrClient()
    
    private init() {}
    
    func fetchPhotos(with queryItems: [String: String], completionHandler: @escaping (_ errorString: String?, _ isEmtpy: Bool, _ photos: [[String: AnyObject]]?) -> Void) {
        
        self.fetchPhotosOfLocation(with: queryItems, withPageNumber: 1) { (error, _, photos) in
            if error != nil {
                completionHandler(error, true, nil)
            } else {
                guard let photos = photos else {
                    completionHandler("Could not fetch photos", true, nil)
                    return
                }
                if photos.count == 0 {
                    completionHandler(nil, true, nil)
                } else {
                    var photoNumber: Int = 0
                    
                    var flickrPhotos: [[String: AnyObject]] = []
                    
                    for photo in photos.reversed() {
                        flickrPhotos.append(photo)
                        photoNumber = photoNumber + 1
                        if photoNumber >= FirestoreRemoteConfig.sharedInstance.numberOfPhotosToDownload {
                            break
                        }
                    }
                    
                    completionHandler(nil, false, flickrPhotos)
                }
            }
        }
    }
    
    private func fetchPhotosOfLocation(with queryItems: [String: String], withPageNumber pageNumber: Int? = nil, completionHandler: @escaping (_ errorString: String?, _ randomPage: Int, _ photos: [[String: AnyObject]]?) -> Void) {
        
        let url = WebClient.sharedInstance.createUrl(forScheme: FlickrConstants.UrlComponents.urlProtocol, forHost: FlickrConstants.UrlComponents.domain, forMethod: FlickrConstants.UrlComponents.path, withQueryItems: queryItems)
        
        let request = buildRequest(withUrl: url, withHttpMethod: WebConstants.ParameterKeys.httpGet)
        
        WebClient.sharedInstance.taskForWebRequest(request, errorDomain: "fetchPhotosOfLocation") { (results, error) in
            
            /* Send the desired value(s) to completion handler */
            if let error = error {
                completionHandler(error.localizedDescription, 0, nil)
            } else {
                /* GUARD: Is "photos" key in our result? */
                
                if let photosDictionary = results?[FlickrConstants.ResponseKeys.photos] as? [String:AnyObject], let photosArray = photosDictionary[FlickrConstants.ResponseKeys.photo] as? [[String: AnyObject]] {

                    completionHandler(nil, 0, photosArray) //page 0
                } else {
                    print("Could not find \(FlickrConstants.ResponseKeys.photo) in \(results!)")
                    completionHandler("Fetching of Photos failed (no results).", 0, nil)
                }
            }
        }
    }
    
    func buildQueryItems() -> [String: String] {
        return [
            FlickrConstants.ParameterKeys.method: FlickrConstants.ParameterValues.searchMethod,
            FlickrConstants.ParameterKeys.apiKey: SecretConstants.apiKeyFlickr,
            FlickrConstants.ParameterKeys.safeSearch: FlickrConstants.ParameterValues.useSafeSearch,
            FlickrConstants.ParameterKeys.extras: FlickrConstants.ParameterValues.imageSize,
            FlickrConstants.ParameterKeys.format: FlickrConstants.ParameterValues.responseFormat,
            FlickrConstants.ParameterKeys.noJsonCallback: FlickrConstants.ParameterValues.disableJsonCallback,
            FlickrConstants.ParameterKeys.sortOrder: FlickrConstants.ParameterValues.sortOrder
            //"tags": "1025fav" //wow
        ]
    }
    
    private func buildRequest(withUrl url: URL, withHttpMethod httpMethod: String) -> URLRequest {
        return WebClient.sharedInstance.buildRequest(withUrl: url, withHttpMethod: httpMethod)
    }
    
    func bboxString(latitude: Double, longitude: Double) -> String {
        // ensure bbox is bounded by minimum and maximums
        let minimumLon = max(longitude - FlickrConstants.Location.searchBBoxHalfWidth, FlickrConstants.Location.searchLongitudeRange.0)
        let minimumLat = max(latitude - FlickrConstants.Location.searchBBoxHalfHeight, FlickrConstants.Location.searchLatitudeRange.0)
        let maximumLon = min(longitude + FlickrConstants.Location.searchBBoxHalfWidth, FlickrConstants.Location.searchLongitudeRange.1)
        let maximumLat = min(latitude + FlickrConstants.Location.searchBBoxHalfHeight, FlickrConstants.Location.searchLatitudeRange.1)
        return "\(minimumLon),\(minimumLat),\(maximumLon),\(maximumLat)"
    }
    
}
