package com.sjaindl.travelcompanion.util

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import com.sjaindl.travelcompanion.Constants
import com.sjaindl.travelcompanion.R
import java.io.File

class TCFileProvider : FileProvider(
    R.xml.file_paths
) {
    companion object {
        fun getImageUri(context: Context): Uri {
            val directory = File(context.cacheDir, "images")
            directory.mkdirs()
            val file = File.createTempFile(
                Constants.Files.tempFilePath,
                Constants.Files.jpg,
                directory,
            )
            val authority = context.packageName + ".fileprovider"
            return getUriForFile(
                context,
                authority,
                file,
            )
        }
    }
}
