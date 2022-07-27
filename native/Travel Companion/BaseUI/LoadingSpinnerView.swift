//  Travel Companion
//
//  Created by Stefan Jaindl on 27.07.22.
//  Copyright © 2022 Stefan Jaindl. All rights reserved.
//

import UIKit

public final class LoadingSpinnerView: UIView {
    // MARK: Lifecycle

    public required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
        backgroundColor = UIColor.clear
    }

    public init(frame: CGRect, topColor: UIColor, backColor: UIColor) {
        self.topColor = topColor
        self.backColor = backColor
        super.init(frame: frame)
    }

    // MARK: Public

    @IBInspectable
    public var topColor: UIColor = .red

    @IBInspectable
    public var backColor: UIColor = .lightGray

    @IBInspectable
    public var lineWidth: CGFloat = 4.0

    override public var intrinsicContentSize: CGSize {
        CGSize(width: bounds.width, height: bounds.height)
    }

    override public var bounds: CGRect {
        didSet {
            // setup the animation again for the new bounds
            if oldValue != bounds, isAnimating {
                setUpAnimation()
            }
        }
    }

    public final func startAnimating() {
        imageView?.removeFromSuperview()
        isAnimating = true
        layer.speed = 1
        setUpAnimation()
    }

    public final func stopAnimating(imageName: String? = nil) {
        isAnimating = false
        layer.sublayers?.removeAll()

        if let image = (imageName.flatMap { UIImage(named: $0, in: .main, compatibleWith: nil) }) {
            let imageView = UIImageView(image: image)
            addSubview(imageView)

            imageView.translatesAutoresizingMaskIntoConstraints = false
            imageView.centerXAnchor.constraint(equalTo: centerXAnchor).isActive = true
            imageView.centerYAnchor.constraint(equalTo: centerYAnchor).isActive = true

            self.imageView = imageView
            bringSubviewToFront(imageView)
        }
    }

    // MARK: Internal

    public private(set) var isAnimating: Bool = false

    // MARK: Private

    private weak var imageView: UIImageView?

    private final func setUpAnimation() {
        let minEdge = min(frame.width, frame.height)
        layer.sublayers = nil
        setUpAnimation(
            in: layer,
            size: CGSize(width: minEdge, height: minEdge),
            topColor: topColor,
            backColor: backColor,
            lineWidth: lineWidth
        )
    }

    private func setUpAnimation(in layer: CALayer, size: CGSize, topColor: UIColor, backColor: UIColor, lineWidth: CGFloat) {
        let frame = CGRect(
            x: (layer.bounds.width - size.width) / 2,
            y: (layer.bounds.height - size.height) / 2,
            width: size.width,
            height: size.height
        )

        let backgroundCircle = layerWith(size: size, color: backColor, lineWidth: lineWidth - 1)
        backgroundCircle.frame = frame
        layer.addSublayer(backgroundCircle)

        let topCircle = layerWith(size: size, color: topColor, lineWidth: lineWidth)
        topCircle.frame = frame
        topCircle.add(CAAnimationGroup.circleRotation(), forKey: "animation")
        layer.addSublayer(topCircle)
    }

    private func layerWith(size: CGSize, color: UIColor, lineWidth: CGFloat) -> CALayer {
        let layer = CAShapeLayer()
        let path = UIBezierPath()

        path.addArc(
            withCenter: CGPoint(x: size.width / 2, y: size.height / 2),
            radius: size.width / 2,
            startAngle: -(.pi / 2),
            endAngle: .pi + .pi / 2,
            clockwise: true
        )

        layer.fillColor = nil
        layer.strokeColor = color.cgColor
        layer.lineWidth = lineWidth

        layer.backgroundColor = nil
        layer.path = path.cgPath
        layer.frame = CGRect(x: 0, y: 0, width: size.width, height: size.height)

        return layer
    }
}

extension CAAnimationGroup {
    static func circleRotation(
        beginTime: Double = 0.5,
        strokeStartDuration: Double = 1.2,
        strokeEndDuration: Double = 0.7
    ) -> CAAnimationGroup {
        let rotationAnimation = CABasicAnimation(keyPath: "transform.rotation")
        rotationAnimation.byValue = Float.pi * 2
        rotationAnimation.timingFunction = CAMediaTimingFunction(name: CAMediaTimingFunctionName.linear)

        let strokeEndAnimation = CABasicAnimation(keyPath: "strokeEnd")
        strokeEndAnimation.duration = strokeEndDuration
        strokeEndAnimation.timingFunction = CAMediaTimingFunction(controlPoints: 0.4, 0.0, 0.2, 1.0)
        strokeEndAnimation.fromValue = 0
        strokeEndAnimation.toValue = 1

        let strokeStartAnimation = CABasicAnimation(keyPath: "strokeStart")
        strokeStartAnimation.duration = strokeStartDuration
        strokeStartAnimation.timingFunction = CAMediaTimingFunction(controlPoints: 0.4, 0.0, 0.2, 1.0)
        strokeStartAnimation.fromValue = 0
        strokeStartAnimation.toValue = 1
        strokeStartAnimation.beginTime = beginTime

        let groupAnimation = CAAnimationGroup()
        groupAnimation.animations = [rotationAnimation, strokeEndAnimation, strokeStartAnimation]
        groupAnimation.duration = strokeStartDuration + beginTime
        groupAnimation.repeatCount = .infinity
        groupAnimation.isRemovedOnCompletion = false
        groupAnimation.fillMode = CAMediaTimingFillMode.forwards

        return groupAnimation
    }
}
