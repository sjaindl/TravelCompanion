package com.sjaindl.travelcompanion.explore.details

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Photo
import androidx.compose.material.icons.rounded.Place
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.sjaindl.travelcompanion.R
import com.sjaindl.travelcompanion.theme.TravelCompanionTheme

@Composable
private fun TabScreen(
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

val tabRowItems = listOf(
    TabItem(
        title = "Tab 1",
        screen = { TabScreen(text = stringResource(id = R.string.detail)) },
        icon = Icons.Rounded.Place,
    ),
    TabItem(
        title = "Tab 2",
        screen = { TabScreen(text = stringResource(id = R.string.photos)) },
        icon = Icons.Rounded.Photo,
    ),
    TabItem(
        title = "Tab 3",
        screen = { TabScreen(text = stringResource(id = R.string.info)) },
        icon = Icons.Rounded.Info,
    )
)

@Preview
@Composable
fun TabScreenPreview() {
    TabScreen(text = "Test")
}
