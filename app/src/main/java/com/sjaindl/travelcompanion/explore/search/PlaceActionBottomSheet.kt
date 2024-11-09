package com.sjaindl.travelcompanion.explore.search

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sjaindl.travelcompanion.baseui.TCBottomSheet

// https://proandroiddev.com/bottom-sheet-in-jetpack-compose-d7e106422606
@Composable
fun PlaceActionBottomSheet(
    modifier: Modifier = Modifier,
    title: String,
    onShowDetails: () -> Unit,
    onPlanTrip: () -> Unit,
    onDelete: () -> Unit,
    onCancel: () -> Unit,
) {
    TCBottomSheet(
        onCancel = onCancel,
        content = {
            PlaceActionContent(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                shape = RectangleShape,
                title = title,
                onShowDetails = onShowDetails,
                onPlanTrip = onPlanTrip,
                onDelete = onDelete,
                onCancel = onCancel,
            )
        },
    )
}

@Preview
@Composable
fun PlaceActionBottomSheetPreview() {
    PlaceActionBottomSheet(
        title = "Test Location",
        onShowDetails = { },
        onPlanTrip = { },
        onDelete = { },
        onCancel = { },
    )
}
