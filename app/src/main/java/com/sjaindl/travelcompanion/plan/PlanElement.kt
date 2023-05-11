package com.sjaindl.travelcompanion.plan

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
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
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

@Composable
fun PlanElement(
    modifier: Modifier,
    name: String,
    startDate: Date,
    endDate: Date,
    imagePath: Uri?,
    onClick: () -> Unit,
) {
    TravelCompanionTheme {
        Row(
            modifier = modifier
                .background(colors.background)
                .padding(vertical = 4.dp)
                .clickable {
                    onClick()
                },
            verticalAlignment = Alignment.CenterVertically,
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
                    .weight(2f),
                painter = painter,
                contentDescription = name,
                alignment = Alignment.Center,
                contentScale = ContentScale.FillWidth,
            )

            Column(
                modifier = Modifier
                    .weight(3f),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = name,
                    fontWeight = FontWeight.Bold,
                    color = colors.primary,
                    modifier = Modifier
                        .background(colors.background)
                        .fillMaxWidth()
                        .padding(top = 16.dp, start = 4.dp, end = 4.dp),
                    textAlign = TextAlign.Start,
                    fontSize = 18.sp,
                )

                val formatter = DateTimeFormatter.ISO_LOCAL_DATE.withZone(ZoneId.systemDefault())

                val formattedStartDate = formatter.format(startDate.toInstant())
                val formattedEndDate = formatter.format(endDate.toInstant())

                Text(
                    text = "$formattedStartDate - $formattedEndDate",
                    color = colors.primary,
                    modifier = Modifier
                        .background(colors.background)
                        .fillMaxWidth()
                        .padding(top = 8.dp, bottom = 16.dp, start = 4.dp, end = 4.dp),
                    textAlign = TextAlign.Start,
                    fontSize = 12.sp,
                )
            }
        }
    }
}

@Preview
@Composable
fun PlanElementPreview() {
    PlanElement(
        modifier = Modifier,
        name = "Graz",
        startDate = Date(),
        endDate = Date(),
        imagePath = Uri.parse("https://ball-orientiert.de/wp-content/uploads/2023/02/Artikelbild-Sturm-Graz_Ilzer.png"),
        onClick = {  }
    )
}
