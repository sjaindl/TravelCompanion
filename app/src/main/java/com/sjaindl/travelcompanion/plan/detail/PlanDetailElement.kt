package com.sjaindl.travelcompanion.plan.detail

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import com.sjaindl.travelcompanion.theme.TravelCompanionTheme

@Composable
fun PlanDetailElement(imagePath: Uri?, title: String, details: String, attribution: String) {
    TravelCompanionTheme {
        Row(
            modifier = Modifier.background(colorScheme.background),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val data = imagePath ?: Icons.Default.Place
            val model = ImageRequest.Builder(LocalContext.current)
                .data(data)
                .size(Size.ORIGINAL)
                .placeholder(android.R.drawable.gallery_thumb)
                .crossfade(enable = true)
                .build()

            val painter = rememberAsyncImagePainter(model)

            Image(
                modifier = Modifier
                    .weight(1f),
                painter = painter,
                contentDescription = null,
                alignment = Alignment.Center,
                contentScale = ContentScale.FillWidth,
            )

            Column(
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .weight(2f)
                    .padding(horizontal = 4.dp)
            ) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.primary,
                    modifier = Modifier
                        .background(colorScheme.background)
                        .fillMaxWidth()
                        .padding(top = 4.dp, start = 4.dp, end = 4.dp),
                    textAlign = TextAlign.Start,
                    fontSize = 18.sp,
                )

                Text(
                    text = details,
                    color = colorScheme.primary,
                    modifier = Modifier
                        .background(colorScheme.background)
                        .fillMaxWidth()
                        .padding(top = 4.dp, start = 4.dp, end = 4.dp),
                    textAlign = TextAlign.Start,
                    fontSize = 16.sp,
                )

                Text(
                    text = attribution,
                    color = colorScheme.primary,
                    modifier = Modifier
                        .background(colorScheme.background)
                        .fillMaxWidth()
                        .padding(top = 4.dp, start = 4.dp, end = 4.dp),
                    textAlign = TextAlign.Start,
                    fontSize = 16.sp,
                )
            }
        }
    }
}

@Composable
@Preview
fun PlanDetailElementPreview() {
    PlanDetailElement(
        imagePath = Uri.parse("https://ball-orientiert.de/wp-content/uploads/2023/02/Artikelbild-Sturm-Graz_Ilzer.png"),
        title = "Hard Rock Café Bali",
        details = "Jalan Pantai Kuta",
        attribution = "Hard Rock Hotel Bali"
    )
}
