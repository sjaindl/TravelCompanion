//
//  StretchingTableViewFooterProvider.swift
//  Travel Companion
//
//  Created by Stefan Jaindl on 26.07.22.
//  Copyright © 2022 Stefan Jaindl. All rights reserved.
//

import Foundation
import UIKit

public protocol StretchingTableViewFooterProvider {
    var footerView: UIView { get }
    var tableViewForFooter: UITableView { get }
}

public extension StretchingTableViewFooterProvider {
    func updateFooterViewHeight() {
        let tableViewHeightContentWithoutFooter = tableViewForFooter.contentSize.height - footerView.frame.height

        let sizeFillingBottomSize = (tableViewForFooter.frame.height - tableViewHeightContentWithoutFooter) - tableViewForFooter
            .safeAreaInsets.top - tableViewForFooter.safeAreaInsets.bottom

        let footerAutoSize = footerView.systemLayoutSizeFitting(
            .zero,
            withHorizontalFittingPriority: .defaultHigh,
            verticalFittingPriority: .fittingSizeLevel
        )

        let footerViewHeight = (sizeFillingBottomSize > footerAutoSize.height) ? sizeFillingBottomSize : footerAutoSize.height

        let size = CGSize(width: tableViewForFooter.frame.width, height: footerViewHeight)

        guard footerView.frame.size.height != size.height else {
            return
        }

        footerView.frame = CGRect(origin: .zero, size: size)
        tableViewForFooter.tableFooterView = footerView
    }
}
