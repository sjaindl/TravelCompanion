# TravelCompanion
A travel app that offers all in one place: Discovering, planning and remembering travels. It allows to drop pins on a map and retrieve basic information and photos of potential travel destinations, planning trips including flights, public transport, hotels, restaurants and attractions, as well as persist your travel memories.

## Installation

No special prerequisites required. You just need an IDE (preferably Xcode) and clone the source code:
`git clone https://github.com/sjaindl/TravelCompanion.git`

Then install dependencies via command line:
`pod install`

In the file SecretConstants.swift are only placeholder-values, that need to be exchanged with valid API keys/user names.
A Google configuration file, associated with the above Google API key, must be added to under: Supporting Files/GoogleService-Info.plist

Android/Kotlin needs gradle to work. You can install it with the command:
gradle wrapper --gradle-version 3.4

## Licence

This project is under exclusive copyright, see https://github.com/sjaindl/TravelCompanion/blob/master/LICENSE for license notes.

## Intented User Experience

TravelCompanion's goal is to let explore, plan and remember travels all in one place.

1. Explore:
The explore feature offers a Google Map that allows to search for and drop pins on desired destinations. Basic information about the destination and country as well as photos about the country, place and geographical location can be retrieved. Articles on Wikipedia and Wikivoyage about the destination are offered, too.

2. Plan:
The plan feature allows to add travel plans for destinations that previously have been explored. The travel plans include flights, public transport (including ferries), hotels, restaurants and attractions. 

Flight and public transport search allow to enter an origin and destination with autocompletion support and a travel date. Available transport opportunities with detailled information are displayed in a TableView and can be added to the trip.

Hotels, restaurants and attractions can be searched by destination: The user can drop a pin on the map, which triggers a search for the chosen place type. The search can further be restricted by text search. Attractions are subdivided in various groups offered by Google Places, that can be chosen by the user in a picker.

The plan overview displays all flights, public transports, hotels, restaurants and attractions in a grouped TableView. By tapping an item it is possible to delete it or to add a note. Furthermore, an image from the linked explore photos can be chosen by tapping the placeholder image.

Finally, plans are subdivided into upcoming and past trips, depending on the travel date.

3. Remember:
The remember feature displays past trips (or trips that have been started), where users can add photos from the gallery or camera.

It has an integrated sign-in mechanism with either Facebook, Google or email. This allows to synchronize data via Firebase over different devices (and potentially to Android, too, if there will be a future Android version).

The ideas behind the app and a prototype designed in Adobe XD can be found in the following blog post on my website:
https://jaindlstefan.wixsite.com/projects


## Web APIs

The app makes use of the following Web APIs:

- Google Maps (Map features)
- Google Place API (Search for places, place photos and nearby search)
- flickR API (Retrieve Photos for a given latitude/longitude and country)
- Firebase Authentication (Google- & Facebook-SignIn, E-Mail-Login)
- Cloud Firestore (Online noSQL Database)
- Firebase Storage (Online photo storage)
- Firebase Analytics (Collect App Analytics Data to better understand users)
- Firebase Crashlytics (Retrieve crash reports)
- Firebase Performance (Performance monitoring, especially network activities)
- Firebase Remote Config (Configuration without updates - for number of flickR photos, activation of Rome2Rio autocompletion, Firebase Storage image resizing size)
- Rome2Rio (Search for flights and public transports & search autocompletion)
- Restcountries (http://restcountries.eu, country data for explore feature)
- Geonames (Convert latitude/longitude to country codes)
- Wikipedia/Wikivoyage (Search Wiki sites for destinations)

## Persistance

Data persistance is built in with CoreData and Cloud Firestore. CoreData is used in the explore feature to persist these places and photos, as they can easily be re-retrieved on other devices (no need to store this data on a server). Cloud Firestore is used for all other persistance. Cloud Firestore allows to synchronize data over devices and offers offline capabilities, too. 

Furthermore, NSUserDefaults are used to remember the last map location (explore feature).
