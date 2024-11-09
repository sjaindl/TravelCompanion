package com.sjaindl.travelcompanion.remember.detail.bottomsheet

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
fun RememberItemActionBottomSheet(
    modifier: Modifier = Modifier,
    title: String,
    onFullScreen: () -> Unit,
    onDelete: () -> Unit,
    onCancel: () -> Unit,
) {
    TCBottomSheet(
        onCancel = onCancel,
        content = {
            RememberItemActionContent(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                shape = RectangleShape,
                title = title,
                onFullScreen = onFullScreen,
                onDelete = onDelete,
                onCancel = onCancel,
            )
        },
    )
}

@Preview
@Composable
fun RememberItemActionBottomSheetPreview() {
    RememberItemActionBottomSheet(
        title = "Choose action",
        onFullScreen = { },
        onDelete = { },
        onCancel = { },
    )
}
