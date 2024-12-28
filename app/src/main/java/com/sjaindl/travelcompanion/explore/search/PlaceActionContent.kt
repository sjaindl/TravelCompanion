package com.sjaindl.travelcompanion.explore.search

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sjaindl.travelcompanion.R

@Composable
fun PlaceActionContent(
    modifier: Modifier = Modifier,
    shape: Shape,
    title: String,
    onShowDetails: () -> Unit,
    onPlanTrip: () -> Unit,
    onDelete: () -> Unit,
    onCancel: () -> Unit,
) {
    val buttonColors = ButtonDefaults.buttonColors(
        containerColor = colorResource(id = R.color.colorMain),
        contentColor = colorResource(id = R.color.textLight),
    )

    Surface(
        shape = shape,
    ) {
        Column(
            modifier = modifier
                .padding(8.dp)
                .width(IntrinsicSize.Max)
                .widthIn(min = 250.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = title,
                color = colorResource(id = R.color.colorMain)
            )
            Button(
                modifier = modifier.fillMaxWidth(),
                colors = buttonColors,
                onClick = {
                    onShowDetails()
                }) {
                Text(stringResource(id = R.string.showDetails))
            }
            Button(
                modifier = modifier.fillMaxWidth(),
                colors = buttonColors,
                onClick = {
                    onPlanTrip()
                }) {
                Text(stringResource(id = R.string.planTrip))
            }
            Button(
                modifier = modifier.fillMaxWidth(),
                colors = buttonColors,
                onClick = {
                    onDelete()
                }) {
                Text(stringResource(id = R.string.delete))
            }
            Button(
                modifier = modifier.fillMaxWidth(),
                colors = buttonColors,
                onClick = {
                    onCancel()
                }) {
                Text(stringResource(id = R.string.cancel))
            }
        }
    }
}

@Preview
@Composable
fun PlaceActionContentPreview() {
    PlaceActionContent(
        title = "Test Location",
        shape = RoundedCornerShape(percent = 8),
        onShowDetails = { },
        onPlanTrip = { },
        onDelete = { },
        onCancel = { },
    )
}
