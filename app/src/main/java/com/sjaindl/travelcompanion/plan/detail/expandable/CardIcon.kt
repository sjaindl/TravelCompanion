package com.sjaindl.travelcompanion.plan.detail.expandable

import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import com.sjaindl.travelcompanion.theme.TravelCompanionTheme

@Composable
fun CardIcon(
    modifier: Modifier,
    image: ImageVector,
    onClick: () -> Unit
) {
    TravelCompanionTheme {
        IconButton(
            modifier = modifier,
            onClick = onClick,
            content = {
                Icon(
                    imageVector = image,
                    contentDescription = null,
                )
            }
        )
    }
}

@Preview
@Composable
fun CardIconPreview() {
    CardIcon(
        modifier = Modifier,
        image = Icons.Default.Add,
        onClick = { },
    )
}
