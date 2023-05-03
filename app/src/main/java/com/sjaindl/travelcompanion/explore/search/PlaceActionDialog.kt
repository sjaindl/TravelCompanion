package com.sjaindl.travelcompanion.explore.search


import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.Dialog
import com.sjaindl.travelcompanion.theme.TravelCompanionTheme

@Composable
fun PlaceActionDialog(
    modifier: Modifier = Modifier,
    show: Boolean,
    title: String,
    onShowDetails: () -> Unit,
    onPlanTrip: () -> Unit,
    onDelete: () -> Unit,
    onCancel: () -> Unit,
) {
    TravelCompanionTheme {
        if (!show) return@TravelCompanionTheme

        Dialog(
            onDismissRequest = {
                // Dismiss the dialog when the user clicks outside the dialog or on the back
                // button. If you want to disable that functionality, simply use an empty
                // onCloseRequest.
                onPlanTrip()
            },
            content = {
                PlaceActionContent(
                    modifier = modifier,
                    shape = RoundedCornerShape(percent = 8),
                    title = title,
                    onShowDetails = onShowDetails,
                    onPlanTrip = onPlanTrip,
                    onDelete = onDelete,
                    onCancel = onCancel,
                )
            }
        )
    }
}

@Preview
@Composable
fun PlaceActionDialogPreview() {
    PlaceActionDialog(
        show = true,
        title = "Test Location",
        onShowDetails = { },
        onPlanTrip = { },
        onDelete = { },
        onCancel = { },
    )
}
