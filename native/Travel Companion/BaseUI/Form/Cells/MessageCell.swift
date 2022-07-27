//  Travel Companion
//
//  Created by Stefan Jaindl on 27.07.22.
//  Copyright © 2022 Stefan Jaindl. All rights reserved.
//

import UIKit

public final class MessageCell: FormCell, XibLoadable {
    public static let xibName = "MessageCell"

    @IBOutlet private var messageLabel: UILabel!
    @IBOutlet private var messageLabelBottomConstraint: NSLayoutConstraint!
    @IBOutlet private var messageLabelTopConstraint: NSLayoutConstraint!

    public var message: String? {
        get { messageLabel.text }
        set { messageLabel.text = newValue }
    }

    public var messageColor: UIColor? {
        get { messageLabel.textColor }
        set { messageLabel.textColor = newValue }
    }

    override public func isValid() -> Bool {
        false
    }

    override public func awakeFromNib() {
        super.awakeFromNib()

        selectionStyle = .none
    }

    public func heightToFitMessage() -> CGFloat {
        let height = messageLabel.sizeThatFits(CGSize(
            width: frame.size.width,
            height: 50
        )).height
        return height + messageLabelVerticalMargin
    }

    // MARK: Private

    private var messageLabelVerticalMargin: CGFloat {
        messageLabelTopConstraint.constant +
            messageLabelBottomConstraint.constant
    }
}
