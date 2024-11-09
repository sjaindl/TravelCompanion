package com.sjaindl.travelcompanion.plan.detail.bottomsheet

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
fun PlanItemActionBottomSheet(
    title: String,
    onAddNote: () -> Unit,
    onDelete: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TCBottomSheet(
        onCancel = onCancel,
        content = {
            PlanItemActionContent(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                shape = RectangleShape,
                title = title,
                onAddNote = onAddNote,
                onDelete = onDelete,
                onCancel = onCancel,
            )
        },
    )
}

@Preview
@Composable
fun PlanItemActionBottomSheetPreview() {
    PlanItemActionBottomSheet(
        title = "Plan item actions",
        onAddNote = { },
        onDelete = { },
        onCancel = { },
    )
}
