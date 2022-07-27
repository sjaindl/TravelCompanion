//
// Copyright (c) topmind GmbH and contributors. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for details.
//

#if os(iOS) || os(tvOS)
	import UIKit

	public protocol XibLoadable {
		static var xibName: String { get }
	}

	public extension XibLoadable {
		static func loadXib(owner: AnyObject? = nil, bundle: Bundle = Bundle.main, builder: ((Self) -> Void)? = nil) -> Self {
			guard let nib = bundle.loadNibNamed(xibName, owner: owner, options: nil)?.first as? Self else {
				fatalError("Unexpected Logic Error. \(xibName) not found.")
			}
			builder?(nib)
			return nib
		}

		static func nib(bundle: Bundle? = nil) -> UINib {
			UINib(nibName: xibName, bundle: bundle)
		}
	}

	public protocol PrototypeCell: AnyObject {
		static var cellIdentifier: String { get }
	}

	public extension UITableView {
		func register<T: PrototypeCell>(cell: T.Type)
			where T: UITableViewCell {
			register(cell, forCellReuseIdentifier: cell.cellIdentifier)
		}

		func dequeueCell<T: PrototypeCell>(for indexPath: IndexPath) -> T?
			where T: UITableViewCell {
			dequeueReusableCell(withIdentifier: T.cellIdentifier, for: indexPath) as? T
		}

		func register<T: XibLoadable>(xibLoadable: T.Type, bundle: Bundle? = nil)
			where T: UITableViewCell {
			register(xibLoadable.nib(bundle: bundle), forCellReuseIdentifier: xibLoadable.xibName)
		}

		func dequeueCell<T: XibLoadable>(for indexPath: IndexPath) -> T?
			where T: UITableViewCell {
			dequeueReusableCell(withIdentifier: T.xibName, for: indexPath) as? T
		}
	}

	public extension UICollectionView {
		func register<T: PrototypeCell>(cell: T.Type)
			where T: UICollectionViewCell {
			register(cell, forCellWithReuseIdentifier: cell.cellIdentifier)
		}

		func dequeueCell<T: PrototypeCell>(for indexPath: IndexPath) -> T?
			where T: UICollectionViewCell {
			dequeueReusableCell(withReuseIdentifier: T.cellIdentifier, for: indexPath) as? T
		}

		func registerCell<T: XibLoadable>(xibLoadable: T.Type, bundle: Bundle? = nil)
			where T: UICollectionViewCell {
			register(xibLoadable.nib(bundle: bundle), forCellWithReuseIdentifier: xibLoadable.xibName)
		}

		func dequeueCell<T: XibLoadable>(for indexPath: IndexPath) -> T?
			where T: UICollectionViewCell {
			dequeueReusableCell(withReuseIdentifier: T.xibName, for: indexPath) as? T
		}

		func registerSupplementaryView<T: XibLoadable>(xibLoadable: T.Type, kind: String, bundle: Bundle? = nil)
			where T: UICollectionReusableView {
			register(xibLoadable.nib(bundle: bundle), forSupplementaryViewOfKind: kind, withReuseIdentifier: xibLoadable.xibName)
		}

		func dequeueSupplementaryView<T: XibLoadable>(ofKind kind: String, for indexPath: IndexPath) -> T?
			where T: UICollectionReusableView {
			dequeueReusableSupplementaryView(ofKind: kind, withReuseIdentifier: T.xibName, for: indexPath) as? T
		}
	}

#endif
