package com.sjaindl.travelcompanion.plan.detail.expandable

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sjaindl.travelcompanion.theme.TravelCompanionTheme

@Composable
fun CardTitle(modifier: Modifier, title: String) {
    TravelCompanionTheme {
        Text(
            text = title,
            modifier = modifier
                .padding(16.dp),
            textAlign = TextAlign.Center,
        )
    }
}

@Preview
@Composable
fun CardTitlePreview() {
    CardTitle(
        modifier = Modifier,
        title = "Card"
    )
}
