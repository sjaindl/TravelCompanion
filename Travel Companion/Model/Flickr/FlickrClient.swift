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
                    
                    while photoNumber < Constants.CoreData.PHOTO_LIMIT && photoNumber < photos.count {
                        let photoDictionary = photos[photoNumber] as [String: AnyObject]
                        flickrPhotos.append(photoDictionary)
                        photoNumber = photoNumber + 1
                    }
                    
                    completionHandler(nil, false, flickrPhotos)
                }
            }
        }
    }
    
    func fetchPhotosOfLocation(with queryItems: [String: String], withPageNumber pageNumber: Int? = nil, completionHandler: @escaping (_ errorString: String?, _ randomPage: Int, _ photos: [[String: AnyObject]]?) -> Void) {
        
        let url = WebClient.sharedInstance.createUrl(forScheme: FlickrConstants.UrlComponents.PROTOCOL, forHost: FlickrConstants.UrlComponents.DOMAIN, forMethod: FlickrConstants.UrlComponents.PATH, withQueryItems: queryItems)
        
        let request = buildRequest(withUrl: url, withHttpMethod: WebConstants.ParameterKeys.HTTP_GET)
        
        WebClient.sharedInstance.taskForWebRequest(request, errorDomain: "fetchPhotosOfLocation") { (results, error) in
            
            /* Send the desired value(s) to completion handler */
            if let error = error {
                completionHandler(error.localizedDescription, 0, nil)
            } else {
                /* GUARD: Is "photos" key in our result? */
                
                if let photosDictionary = results?[FlickrConstants.FlickrResponseKeys.Photos] as? [String:AnyObject], let photosArray = photosDictionary[FlickrConstants.FlickrResponseKeys.Photo] as? [[String: AnyObject]] {

                    completionHandler(nil, 0, photosArray) //page 0
                } else {
                    print("Could not find \(FlickrConstants.ParameterKeys.RESULTS) in \(results!)")
                    completionHandler("Fetching of Photos failed (no results).", 0, nil)
                }
            }
        }
    }
    
    func downloadImage( imagePath:String, completionHandler: @escaping (_ imageData: Data?, _ errorString: String?) -> Void){
        let session = URLSession.shared
        let imgURL = NSURL(string: imagePath)
        let request: NSURLRequest = NSURLRequest(url: imgURL! as URL)
        
        let task = session.dataTask(with: request as URLRequest) {data, response, downloadError in
            
            if downloadError != nil {
                completionHandler(nil, "Could not download image \(imagePath)")
            } else {
                completionHandler(data, nil)
            }
        }
        
        task.resume()
    }
    
    func buildQueryItems() -> [String: String] {
        return [
            FlickrConstants.FlickrParameterKeys.Method: FlickrConstants.FlickrParameterValues.SearchMethod,
            FlickrConstants.FlickrParameterKeys.APIKey: SecretConstants.FLICKR_KEY,
            FlickrConstants.FlickrParameterKeys.SafeSearch: FlickrConstants.FlickrParameterValues.UseSafeSearch,
            FlickrConstants.FlickrParameterKeys.Extras: FlickrConstants.FlickrParameterValues.ImageSize,
            FlickrConstants.FlickrParameterKeys.Format: FlickrConstants.FlickrParameterValues.ResponseFormat,
            FlickrConstants.FlickrParameterKeys.NoJSONCallback: FlickrConstants.FlickrParameterValues.DisableJSONCallback,
            FlickrConstants.FlickrParameterKeys.SortOrder: FlickrConstants.FlickrParameterValues.SortOrder
        ]
    }
    
    private func buildRequest(withUrl url: URL, withHttpMethod httpMethod: String) -> URLRequest {
        return WebClient.sharedInstance.buildRequest(withUrl: url, withHttpMethod: httpMethod)
    }
    
    func bboxString(latitude: Double, longitude: Double) -> String {
        // ensure bbox is bounded by minimum and maximums
        let minimumLon = max(longitude - FlickrConstants.Flickr.SearchBBoxHalfWidth, FlickrConstants.Flickr.SearchLonRange.0)
        let minimumLat = max(latitude - FlickrConstants.Flickr.SearchBBoxHalfHeight, FlickrConstants.Flickr.SearchLatRange.0)
        let maximumLon = min(longitude + FlickrConstants.Flickr.SearchBBoxHalfWidth, FlickrConstants.Flickr.SearchLonRange.1)
        let maximumLat = min(latitude + FlickrConstants.Flickr.SearchBBoxHalfHeight, FlickrConstants.Flickr.SearchLatRange.1)
        return "\(minimumLon),\(minimumLat),\(maximumLon),\(maximumLat)"
    }
    
}
