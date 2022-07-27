//  Travel Companion
//
//  Created by Stefan Jaindl on 27.07.22.
//  Copyright © 2022 Stefan Jaindl. All rights reserved.
//

import Foundation

public protocol Validatable {
    associatedtype Object
    func isValid(_ object: Object) -> Bool
}

public struct KeyPathValidation<Object, Value>: Validatable {
    // MARK: Lifecycle

    public init(field: KeyPath<Object, Value>, validate: @escaping (Value) -> Bool) {
        self.field = field
        self.validate = validate
    }

    // MARK: Public

    public var asAny: AnyValidatable<Object> {
        AnyValidatable(self, identifierForDebugging: "\(field)")
    }

    public func isValid(_ object: Object) -> Bool {
        validate(object[keyPath: field])
    }

    // MARK: Internal

    let field: KeyPath<Object, Value>
    let validate: (Value) -> Bool
}

public struct AnyValidatable<I>: Validatable {
    // MARK: Lifecycle

    public init<T: Validatable>(_ validatable: T, identifierForDebugging: String = "") where T.Object == I {
        validationClosure = validatable.isValid
        self.identifierForDebugging = identifierForDebugging
    }

    // MARK: Public

    public func isValid(_ object: I) -> Bool {
        validationClosure(object)
    }

    // MARK: Internal

    let validationClosure: (I) -> Bool

    // MARK: Private

    private let identifierForDebugging: String
}

public enum FormValidation {
    public static func isValidUserName(_ string: String?) -> Bool {
        string?.isValidUserName ?? false
    }

    public static func isValidName(_ string: String?) -> Bool {
        string?.isValidName ?? false
    }

    public static func isValidNameIncludingNumbers(_ string: String?) -> Bool {
        guard let s = string else { return false }
        let stringWithoutNumbers = s // Poor mans solution if you cant read RegEx... it's late...
            .replacingOccurrences(of: "0", with: "a")
            .replacingOccurrences(of: "1", with: "a")
            .replacingOccurrences(of: "2", with: "a")
            .replacingOccurrences(of: "3", with: "a")
            .replacingOccurrences(of: "4", with: "a")
            .replacingOccurrences(of: "5", with: "a")
            .replacingOccurrences(of: "6", with: "a")
            .replacingOccurrences(of: "7", with: "a")
            .replacingOccurrences(of: "8", with: "a")
            .replacingOccurrences(of: "9", with: "a")
        return stringWithoutNumbers.isValidNameIncludingNumbers
    }

    public static func isValidEmail(_ string: String?) -> Bool {
        string?.isValidEmail ?? false
    }

    public static func isValidPassword(_ string: String?) -> Bool {
        string?.isValidPassword ?? false
    }

    public static func isValidPostalCode(_ string: String?) -> Bool {
        isNotBlank(string) && (string?.isValidPostCode ?? false)
    }

    public static func isNotEmpty(_ string: String?) -> Bool {
        isNotBlank(string) && !(string?.isEmpty ?? true)
    }
    
    public static func isNotBlank(_ string: String?) -> Bool {
        !(string?.isEmpty ?? true)
    }

    public static func isNotNil(_ any: Any?) -> Bool {
        any != nil
    }
}

public extension String {
    static var emailRegex: String {
        "[_\\.0-9A-Za-z\\.\\!\\#\\$\\%\\&\\'\\*\\+\\-\\/\\=\\?\\^\\`\\{\\|\\}\\~]+@([0-9A-Za-z-]+\\.)*[0-9A-Za-z-]{2,}\\.[A-Za-z]{2,6}"
    }

    var rangeOfEmail: NSRange {
        guard let regex = try? NSRegularExpression(pattern: String.emailRegex, options: [.caseInsensitive]) else {
            return NSRange(location: 0, length: 0)
        }

        return regex.rangeOfFirstMatch(in: self, options: [], range: fullRange)
    }
}

public extension String {
    var fullRange: NSRange {
        NSRange(location: 0, length: count)
    }

    var isValidEmail: Bool {
        guard count < 125 else {
            return false
        }
        return (try? isFullMatch(pattern: String.emailRegex)) ?? false
    }

    var isValidName: Bool {
        guard (1 ..< 50).contains(count) else {
            return false
        }
        let pattern = #"([^+!|#%";?$=*<;>;^&°\\€;~{}[\\]_]*)"#
        return (try? isFullMatch(pattern: pattern)) ?? false
    }

    var isValidNameIncludingNumbers: Bool {
        guard (1 ..< 50).contains(count) else {
            return false
        }
        let pattern = #"([^+!|#%";?$=*<;>;^&°;\\€;~{}[\\]_]*)"#
        return (try? isFullMatch(pattern: pattern)) ?? false
    }

    var isValidUserName: Bool {
        guard (3 ..< 50).contains(count) else {
            return false
        }
        let pattern = "[!$\\.0-9A-Z_a-z-]*"
        return (try? isFullMatch(pattern: pattern)) ?? false
    }

    var isValidPassword: Bool {
        !isEmpty // password rules are evaluated server-side
    }

    var isValidPostCode: Bool {
        guard (4 ..< 11).contains(count) else {
            return false
        }
        let pattern = "([a-zA-Z0-9- ]*)"
        return (try? isFullMatch(pattern: pattern)) ?? false
    }

    var isValidCountryCode: Bool {
        Locale.isoRegionCodes.contains(self)
    }

    internal var isValidPinDigit: Bool {
        guard let digit = Int(self) else {
            return false
        }
        return (0 ... 9).contains(digit)
    }

    internal var isValidPin: Bool {
        reduce(true) { $0 && String($1).isValidPinDigit }
    }

    private func isFullMatch(pattern: String, options: NSRegularExpression.Options = []) throws -> Bool {
        guard !isEmpty else {
            return false
        }

        let range = NSRange(location: 0, length: count)
        let regEx = try NSRegularExpression(pattern: pattern, options: options)
        return regEx.rangeOfFirstMatch(in: self, options: [], range: range) == fullRange
    }
}
