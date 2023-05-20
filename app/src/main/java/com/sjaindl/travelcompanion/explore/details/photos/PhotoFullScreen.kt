package com.sjaindl.travelcompanion.explore.details.photos

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import com.sjaindl.travelcompanion.theme.TravelCompanionTheme

@Composable
fun PhotoFullScreen(
    bitmap: ImageBitmap?,
    url: String?,
    title: String,
    onBack: () -> Unit
) {
    TravelCompanionTheme {
        BackHandler {
            onBack()
        }

        if (bitmap != null) {
            Image(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 12.dp),
                bitmap = bitmap,
                contentDescription = title,
            )
        } else if (url != null) {
            val model = ImageRequest.Builder(LocalContext.current)
                .data(url)
                .size(Size.ORIGINAL)
                .placeholder(android.R.drawable.gallery_thumb)
                .crossfade(enable = true)
                .build()

            val painter = rememberAsyncImagePainter(model)

            Image(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 12.dp),
                painter = painter,
                contentDescription = title,
                alignment = Alignment.Center,
                contentScale = ContentScale.FillWidth,
            )
        }
    }
}
