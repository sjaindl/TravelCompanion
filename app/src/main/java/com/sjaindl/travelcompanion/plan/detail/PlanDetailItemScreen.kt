package com.sjaindl.travelcompanion.plan.detail

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
import com.sjaindl.travelcompanion.baseui.TCLink
import com.sjaindl.travelcompanion.plan.PlanImageElement
import com.sjaindl.travelcompanion.theme.TravelCompanionTheme

data class PlanDetailItem(
    val id: String,
    val title: String,
    val details: String,
    val attributionWithText: AttributionWithText?,
    val imagePath: Uri?,
)

@Composable
fun PlanDetailItemScreen(
    modifier: Modifier,
    planDetailItem: PlanDetailItem,
    onClick: (plannableId: String) -> Unit,
) {
    TravelCompanionTheme {
        Row(
            modifier = modifier
                .background(colorScheme.background)
                .padding(vertical = 4.dp)
                .clickable {
                    onClick(planDetailItem.id)
                },
            verticalAlignment = Alignment.CenterVertically,
        ) {
            PlanImageElement(
                bitmap = null,
                imagePath = planDetailItem.imagePath,
                modifier = Modifier
                    .weight(2f)
                    .padding(start = 4.dp)
            )

            Column(
                modifier = Modifier
                    .weight(3f)
                    .padding(horizontal = 4.dp),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = planDetailItem.title,
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
                    text = planDetailItem.details,
                    color = colorScheme.primary,
                    modifier = Modifier
                        .background(colorScheme.background)
                        .fillMaxWidth()
                        .padding(top = 8.dp, bottom = 16.dp, start = 4.dp, end = 4.dp),
                    textAlign = TextAlign.Start,
                    fontSize = 12.sp,
                )

                planDetailItem.attributionWithText?.let {
                    TCLink(
                        url = it.link,
                        title = it.name,
                        modifier = Modifier
                            .background(colorScheme.background)
                            .fillMaxWidth()
                            .padding(top = 8.dp, bottom = 16.dp, start = 4.dp, end = 4.dp),
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun PlanDetailItemPreview() {
    PlanDetailItemScreen(
        modifier = Modifier,
        planDetailItem = PlanDetailItem(
            id = "1234567890",
            title = "Marina Bay Sands",
            details = "Marina Bay Singapore",
            attributionWithText = AttributionWithText(link = "https://www.singapore.com", name = "Singapore"),
            imagePath = Uri.parse("https://ball-orientiert.de/wp-content/uploads/2023/02/Artikelbild-Sturm-Graz_Ilzer.png"),
        ),
        onClick = { },
    )
}
