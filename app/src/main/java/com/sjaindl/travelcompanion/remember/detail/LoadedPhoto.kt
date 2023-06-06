package com.sjaindl.travelcompanion.remember.detail

import android.graphics.Bitmap

data class LoadedPhoto(
    var url: String,
    var documentId: String?,
    var bitmap: Bitmap?,
)
