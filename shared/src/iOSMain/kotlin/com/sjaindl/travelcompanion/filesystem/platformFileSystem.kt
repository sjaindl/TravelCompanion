package com.sjaindl.travelcompanion.filesystem

import okio.FileSystem

actual fun platformFileSystem(): FileSystem {
    return FileSystem.SYSTEM
}
