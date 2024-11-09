package com.sjaindl.travelcompanion.plan

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
fun PlanActionBottomSheet(
    modifier: Modifier = Modifier,
    title: String,
    onShow: () -> Unit,
    onShowDetails: () -> Unit,
    onDelete: () -> Unit,
    onCancel: () -> Unit,
) {
    TCBottomSheet(
        onCancel = onCancel,
        content = {
            PlanActionContent(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                shape = RectangleShape,
                title = title,
                onShow = onShow,
                onShowDetails = onShowDetails,
                onDelete = onDelete,
                onCancel = onCancel,
            )
        },
    )
}

@Preview
@Composable
fun PlanActionBottomSheetPreview() {
    PlanActionBottomSheet(
        title = "Test Location",
        onShow = { },
        onShowDetails = { },
        onDelete = { },
        onCancel = { },
    )
}
