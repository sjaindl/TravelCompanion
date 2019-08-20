package com.sjaindl.travelcompanion

import platform.UIKit.UIDevice
import platform.Foundation.*

actual fun platformName(): String {

  return "" +
  UIDevice.currentDevice.systemName() +
          " xxyy " +
          UIDevice.currentDevice.systemVersion
}

//https://stackoverflow.com/a/24505884
