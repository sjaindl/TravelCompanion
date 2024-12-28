package com.sjaindl.travelcompanion.explore.details

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sjaindl.travelcompanion.theme.TravelCompanionTheme

@Composable
fun ExploreDetailEntry(modifier: Modifier = Modifier, title: String? = null, value: String? = null) {
    TravelCompanionTheme {
        Column(modifier = modifier) {
            title?.let {
                Text(
                    text = it,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.primary,
                    textAlign = TextAlign.Start,
                    fontSize = 20.sp
                )

                Spacer(modifier = Modifier.height(8.dp))
            }

            value?.let {
                Text(
                    text = it,
                    color = colorScheme.primary,
                    textAlign = TextAlign.Start,
                    fontSize = 16.sp
                )
            }
        }
    }
}

@Preview
@Composable
fun ExploreDetailEntryPreview() {
    ExploreDetailEntry(title = "Capital", value = "Roma")
}
