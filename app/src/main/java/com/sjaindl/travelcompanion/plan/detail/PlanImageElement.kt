package com.sjaindl.travelcompanion.plan.detail

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Place
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import com.sjaindl.travelcompanion.theme.TravelCompanionTheme

@Composable
fun PlanImageElement(bitmap: Bitmap?, imagePath: Uri?, modifier: Modifier) {
    TravelCompanionTheme {
        val data = bitmap ?: imagePath
        if (data != null) {
            val model = ImageRequest.Builder(LocalContext.current)
                .data(data)
                .size(Size.ORIGINAL)
                .placeholder(android.R.drawable.gallery_thumb)
                .crossfade(enable = true)
                .build()

            val painter = rememberAsyncImagePainter(model)

            Image(
                modifier = modifier,
                painter = painter,
                contentDescription = null,
                alignment = Alignment.Center,
                contentScale = ContentScale.FillWidth,
            )
        } else {
            Image(
                imageVector = Icons.Default.Image,
                contentDescription = null,
            )
        }
    }
}

@Composable
@Preview
fun PlanImageElementPreview() {
    PlanImageElement(
        bitmap = null,
        imagePath = Uri.parse("https://ball-orientiert.de/wp-content/uploads/2023/02/Artikelbild-Sturm-Graz_Ilzer.png"),
        modifier = Modifier,
    )
}
