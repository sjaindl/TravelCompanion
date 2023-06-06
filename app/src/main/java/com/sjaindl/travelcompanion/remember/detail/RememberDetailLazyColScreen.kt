package com.sjaindl.travelcompanion.remember.detail

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.res.ResourcesCompat
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import com.sjaindl.travelcompanion.R
import com.sjaindl.travelcompanion.explore.details.photos.PhotoFullScreen
import com.sjaindl.travelcompanion.theme.TravelCompanionTheme

@Composable
fun RememberDetailLazyColScreen(
    modifier: Modifier = Modifier,
    bitmaps: List<Bitmap>,
) {
    val state = rememberLazyListState()

    var fullScreenImage: Bitmap? by remember {
        mutableStateOf(null)
    }

    TravelCompanionTheme {
        if (fullScreenImage != null) {
            Box(modifier = Modifier.fillMaxSize()) {
                PhotoFullScreen(bitmap = fullScreenImage?.asImageBitmap(), url = null, title = "") {
                    fullScreenImage = null
                }
            }
        } else {
            if (bitmaps.isEmpty()) {
                Text(
                    text = stringResource(id = R.string.noImageData),
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    fontSize = 20.sp
                )
            } else {
                LazyColumn(
                    modifier = modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colors.background),
                    state = state,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top,
                ) {

                    items(bitmaps) { photo ->
                        val model = ImageRequest.Builder(LocalContext.current)
                            .data(photo)
                            .size(Size.ORIGINAL)
                            .placeholder(android.R.drawable.gallery_thumb)
                            .crossfade(enable = true)
                            .build()

                        val painter = rememberAsyncImagePainter(model)

                        Image(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp)
                                .clickable {
                                    fullScreenImage = photo
                                },
                            painter = painter,
                            contentDescription = null,
                            alignment = Alignment.Center,
                            contentScale = ContentScale.FillWidth,
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun RememberDetailLazyColScreenPreview() {
    val rememberDrawable = ResourcesCompat.getDrawable(LocalContext.current.resources, R.drawable.remember, null)

    TravelCompanionTheme {
        RememberDetailLazyColScreen(
            modifier = Modifier,
            bitmaps = listOf(
                BitmapFactory.decodeResource(LocalContext.current.resources, R.drawable.plan),
                (rememberDrawable as BitmapDrawable).bitmap,
                BitmapFactory.decodeResource(LocalContext.current.resources, R.drawable.plan),
                rememberDrawable.bitmap,
                BitmapFactory.decodeResource(LocalContext.current.resources, R.drawable.plan),
                rememberDrawable.bitmap,
            )
        )
    }
}
