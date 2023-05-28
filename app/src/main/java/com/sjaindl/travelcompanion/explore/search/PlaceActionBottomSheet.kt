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
    show: Boolean,
    title: String,
    onShowDetails: () -> Unit,
    onPlanTrip: () -> Unit,
    onDelete: () -> Unit,
    onCancel: () -> Unit,
    content: @Composable () -> Unit = { },
) {
    TCBottomSheet(
        show = show,
        onCancel = onCancel,
        sheetContent = {
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
        content = content,
    )
}

@Preview
@Composable
fun PlaceActionBottomSheetPreview() {
    PlaceActionBottomSheet(
        show = true,
        title = "Test Location",
        onShowDetails = { },
        onPlanTrip = { },
        onDelete = { },
        onCancel = { },
    )
}
