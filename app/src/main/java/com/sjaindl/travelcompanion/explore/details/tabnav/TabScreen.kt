package com.sjaindl.travelcompanion.explore.details.tabnav

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.sjaindl.travelcompanion.theme.TravelCompanionTheme

@Composable
fun TabScreen(
    text: String,
) {
    TravelCompanionTheme {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge,
            )
        }
    }
}

@Preview
@Composable
fun TabScreenPreview() {
    TabScreen(text = "Test")
}
