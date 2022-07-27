//  Travel Companion
//
//  Created by Stefan Jaindl on 27.07.22.
//  Copyright © 2022 Stefan Jaindl. All rights reserved.
//

import Foundation

public extension Form {
    struct LoadingButton: FormRow {
        // MARK: Lifecycle

        public init(
            name: String,
            loading: Bool,
            shouldHighlight: Bool,
            style: LoadingButton.Style,
            required: Bool = false) {
            self.name = name
            self.loading = loading
            self.shouldHighlight = shouldHighlight
            self.style = style
            self.required = required
        }

        // MARK: Public

        public enum Style {
            case normal, destructive, disabled
        }

        public let name: String
        public let loading: Bool
        public let shouldHighlight: Bool
        public var style: LoadingButton.Style
        public let required: Bool
        public let validator: ((String?) -> Bool)? = nil
        public var errorsEnabled = false
    }
}
