package com.sjaindl.travelcompanion.plan.detail.expandable

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.tooling.preview.Preview
import com.sjaindl.travelcompanion.theme.TravelCompanionTheme

@Composable
fun CardArrow(
    modifier: Modifier,
    degrees: Float,
    onClick: () -> Unit
) {
    TravelCompanionTheme {
        IconButton(
            modifier = modifier,
            onClick = onClick,
            content = {
                Icon(
                    imageVector = Icons.Rounded.KeyboardArrowDown,
                    contentDescription = null,
                    modifier = Modifier.rotate(degrees),
                )
            }
        )
    }
}

@Preview
@Composable
fun CardArrowPreview() {
    CardArrow(
        modifier = Modifier,
        degrees = 0f,
        onClick = { },
    )
}
