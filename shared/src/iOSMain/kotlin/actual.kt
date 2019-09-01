package com.sjaindl.travelcompanion

import kotlinx.coroutines.*
import platform.UIKit.UIDevice
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue
import kotlin.coroutines.CoroutineContext

actual fun platformName(): String {

  return UIDevice.currentDevice.systemName() + UIDevice.currentDevice.systemVersion
}

private class CoroutineMainDispatcher: CoroutineDispatcher() {

  override fun dispatch(context: CoroutineContext, block: Runnable) {
    dispatch_async(dispatch_get_main_queue()) {
      block.run()
    }
  }

  internal class CoroutineMainScope: CoroutineScope {
    private val dispatcher = CoroutineMainDispatcher()
    private val job = Job()

    override val coroutineContext: CoroutineContext
      get() = dispatcher + job
  }

}

fun showHelloCoroutine() {
  MainScope().launch {
    helloCoroutine()
  }
}

fun fetchCodeCoroutine(completion: (String) -> Unit) {
  CoroutineMainDispatcher.CoroutineMainScope().launch {
    val code = fetchCode(0.0, 0.0)
    completion(code)
  }
}

fun Api.request(completion: (String) -> Unit) {
  CoroutineMainDispatcher.CoroutineMainScope().launch {
    val result = request("https://tools.ietf.org/rfc/rfc8216.txt")
    completion(result)
  }
}
