package com.sjaindl.travelcompanion

import kotlinx.coroutines.*
import platform.UIKit.UIDevice
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue
import kotlin.coroutines.CoroutineContext

actual fun platformName(): String {
    return UIDevice.currentDevice.systemName() + UIDevice.currentDevice.systemVersion
}

private class CoroutineMainDispatcher : CoroutineDispatcher() {

    override fun dispatch(context: CoroutineContext, block: Runnable) {
        dispatch_async(dispatch_get_main_queue()) {
            block.run()
        }
    }

	// usage: CoroutineMainDispatcher.CoroutineMainScope().launch vs. MainScope().launch
    internal class CoroutineMainScope : CoroutineScope {
        private val dispatcher = CoroutineMainDispatcher()
        private val job = Job()

        override val coroutineContext: CoroutineContext
            get() = dispatcher + job
    }

}
