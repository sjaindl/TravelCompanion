//
// Copyright (c) topmind GmbH and contributors. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for details.
//

#if os(iOS)
	import UIKit

	public final class SeperatorView: UIView {
		public enum Position: Int {
			case top = 0
			case left = 1
			case bottom = 2
			case right = 3
		}
        
        override public var intrinsicContentSize: CGSize {
            CGSize(width: super.intrinsicContentSize.width, height: 1)
        }

		@IBInspectable public var position: Int = Position.bottom.rawValue {
			didSet {
				setNeedsDisplay()
			}
		}

		override public func didMoveToWindow() {
			super.didMoveToWindow()
			layer.allowsEdgeAntialiasing = true
		}

		private static func staticSeperator(position: Position) -> SeperatorView {
			let separatorView = SeperatorView()
			separatorView.tintColor = UIColor.lightGray
			separatorView.backgroundColor = UIColor.clear
			separatorView.translatesAutoresizingMaskIntoConstraints = false
			separatorView.position = position.rawValue
			return separatorView
		}

		public static func defaultSeperator(position: Position = .bottom) -> SeperatorView {
			SeperatorView.staticSeperator(position: position)
		}

		override public func draw(_ rect: CGRect) {
			guard let context = UIGraphicsGetCurrentContext() else {
				super.draw(rect)
				return
			}

			let scale = UIScreen.main.scale
			let lineWidth = 1.0 / scale
			var lineRect = CGRect.zero

			switch position {
			case Position.top.rawValue:
				lineRect = CGRect(x: 0, y: 0, width: bounds.width, height: lineWidth)
			case Position.left.rawValue:
				lineRect = CGRect(x: 0, y: 0, width: lineWidth, height: bounds.height)
			case Position.bottom.rawValue:
				lineRect = CGRect(x: 0, y: bounds.height - lineWidth, width: bounds.width, height: lineWidth)
			case Position.right.rawValue:
				lineRect = CGRect(x: bounds.width - lineWidth, y: 0, width: lineWidth, height: bounds.height)
			default:
				break
			}

			context.setFillColor(tintColor.cgColor)
			context.fill(lineRect)
		}

		override public func point(inside _: CGPoint, with _: UIEvent?) -> Bool {
			// never participate in the responder chain
			false
		}
	}
#endif
