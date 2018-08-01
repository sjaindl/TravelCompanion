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
    
    func fetchPhotos(_ latitude: Double, longitude: Double, completionHandler: @escaping (_ errorString: String?, _ isEmtpy: Bool, _ photos: [[String: AnyObject]]?) -> Void) {
        
        fetchPhotosOfLocation(latitude, longitude: longitude) { (error, randomPage, _) in
            if error != nil {
                completionHandler(error, true, nil)
            } else {
                self.fetchPhotosOfLocation(latitude, longitude: longitude, withPageNumber: randomPage) { (error, _, photos) in
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
                            var takenPhotos: Set<Int> = []
                            
                            var flickrPhotos: [[String: AnyObject]] = []
                            
                            while photoNumber < Constants.CoreData.PHOTO_LIMIT && photoNumber < photos.count {
                                var randomPhotoIndex: Int
                                repeat {
                                    randomPhotoIndex = Int(arc4random_uniform(UInt32(photos.count)))
                                } while takenPhotos.contains(randomPhotoIndex)
                                
                                takenPhotos.insert(randomPhotoIndex)
                                photoNumber = photoNumber + 1
                                
                                let photoDictionary = photos[randomPhotoIndex] as [String: AnyObject]
                                flickrPhotos.append(photoDictionary)
                            }
                            
                            completionHandler(nil, false, flickrPhotos)
                        }
                    }
                }
            }
        }
    }
    
    func fetchPhotosOfLocation(_ latitude: Double, longitude: Double, withPageNumber pageNumber: Int? = nil, completionHandler: @escaping (_ errorString: String?, _ randomPage: Int, _ photos: [[String: AnyObject]]?) -> Void) {
        var queryItems = [
            FlickrConstants.FlickrParameterKeys.Method: FlickrConstants.FlickrParameterValues.SearchMethod,
            FlickrConstants.FlickrParameterKeys.APIKey: SecretConstants.FLICKR_KEY,
            FlickrConstants.FlickrParameterKeys.BoundingBox: bboxString(latitude: latitude, longitude: longitude),
            FlickrConstants.FlickrParameterKeys.SafeSearch: FlickrConstants.FlickrParameterValues.UseSafeSearch,
            FlickrConstants.FlickrParameterKeys.Extras: FlickrConstants.FlickrParameterValues.ImageSize,
            FlickrConstants.FlickrParameterKeys.Format: FlickrConstants.FlickrParameterValues.ResponseFormat,
            FlickrConstants.FlickrParameterKeys.NoJSONCallback: FlickrConstants.FlickrParameterValues.DisableJSONCallback
        ]
        
        if let pageNumber = pageNumber {
            queryItems[FlickrConstants.FlickrParameterKeys.Page] = String(pageNumber)
        }
        
        let url = WebClient.sharedInstance.createUrl(forScheme: FlickrConstants.UrlComponents.PROTOCOL, forHost: FlickrConstants.UrlComponents.DOMAIN, forMethod: FlickrConstants.UrlComponents.PATH, withQueryItems: queryItems)
        
        let request = buildRequest(withUrl: url, withHttpMethod: WebConstants.ParameterKeys.HTTP_GET)
        
        WebClient.sharedInstance.taskForWebRequest(request, errorDomain: "fetchPhotosOfLocation") { (results, error) in
            
            /* Send the desired value(s) to completion handler */
            if let error = error {
                completionHandler(error.localizedDescription, 0, nil)
            } else {
                /* GUARD: Is "photos" key in our result? */
                
                if let photosDictionary = results?[FlickrConstants.FlickrResponseKeys.Photos] as? [String:AnyObject], let photosArray = photosDictionary[FlickrConstants.FlickrResponseKeys.Photo] as? [[String: AnyObject]], let totalPages = photosDictionary[FlickrConstants.FlickrResponseKeys.Pages] as? Int, let perPage = photosDictionary[FlickrConstants.FlickrResponseKeys.PerPage] as? Int {
                    
                    // pick a random page!
                    let pageLimit = min(totalPages, FlickrConstants.MAX_NUMBER_PHOTOS / perPage)
                    let randomPage = Int(arc4random_uniform(UInt32(pageLimit))) + 1
                    
                    completionHandler(nil, randomPage, photosArray)
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
    
    private func buildRequest(withUrl url: URL, withHttpMethod httpMethod: String) -> URLRequest {
        return WebClient.sharedInstance.buildRequest(withUrl: url, withHttpMethod: httpMethod)
    }
    
    private func bboxString(latitude: Double, longitude: Double) -> String {
        // ensure bbox is bounded by minimum and maximums
        let minimumLon = max(longitude - FlickrConstants.Flickr.SearchBBoxHalfWidth, FlickrConstants.Flickr.SearchLonRange.0)
        let minimumLat = max(latitude - FlickrConstants.Flickr.SearchBBoxHalfHeight, FlickrConstants.Flickr.SearchLatRange.0)
        let maximumLon = min(longitude + FlickrConstants.Flickr.SearchBBoxHalfWidth, FlickrConstants.Flickr.SearchLonRange.1)
        let maximumLat = min(latitude + FlickrConstants.Flickr.SearchBBoxHalfHeight, FlickrConstants.Flickr.SearchLatRange.1)
        return "\(minimumLon),\(minimumLat),\(maximumLon),\(maximumLat)"
    }
}
