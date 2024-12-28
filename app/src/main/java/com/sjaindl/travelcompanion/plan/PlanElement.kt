package com.sjaindl.travelcompanion.plan

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sjaindl.travelcompanion.theme.TravelCompanionTheme

@Composable
fun PlanElement(
    modifier: Modifier,
    name: String,
    dateString: String,
    bitmap: Bitmap?,
    imagePath: Uri?,
    onClick: () -> Unit,
) {
    TravelCompanionTheme {
        Row(
            modifier = modifier
                .background(colorScheme.background)
                .padding(vertical = 4.dp)
                .clickable {
                    onClick()
                },
            verticalAlignment = Alignment.CenterVertically,
        ) {
            PlanImageElement(
                bitmap = bitmap,
                imagePath = imagePath,
                modifier = Modifier
                    .weight(2f),
                onClick = onClick,
            )

            Column(
                modifier = Modifier
                    .weight(3f)
                    .padding(horizontal = 4.dp),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = name,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.primary,
                    modifier = Modifier
                        .background(colorScheme.background)
                        .fillMaxWidth()
                        .padding(top = 16.dp, start = 4.dp, end = 4.dp),
                    textAlign = TextAlign.Start,
                    fontSize = 18.sp,
                )

                Text(
                    text = dateString,
                    color = colorScheme.primary,
                    modifier = Modifier
                        .background(colorScheme.background)
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
        dateString = "23.06.2023 - 27.06.2023",
        bitmap = null,
        imagePath = Uri.parse("https://ball-orientiert.de/wp-content/uploads/2023/02/Artikelbild-Sturm-Graz_Ilzer.png"),
        onClick = { },
    )
}
