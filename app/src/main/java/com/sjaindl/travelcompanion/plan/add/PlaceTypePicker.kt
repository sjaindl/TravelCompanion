package com.sjaindl.travelcompanion.plan.add

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.sjaindl.travelcompanion.api.google.GooglePlaceType
import com.sjaindl.travelcompanion.api.google.description
import com.sjaindl.travelcompanion.theme.TravelCompanionTheme

@Composable
fun PlaceTypePicker(
    modifier: Modifier = Modifier,
    show: Boolean,
    title: String,
    onPickedPlace: (GooglePlaceType) -> Unit,
    onCancel: () -> Unit,
) {
    TravelCompanionTheme {
        if (!show) return@TravelCompanionTheme

        Dialog(
            onDismissRequest = {
                onCancel()
            },
            content = {
                Surface(
                    modifier = modifier
                        .fillMaxSize()
                        .padding(top = 16.dp),
                    color = colorScheme.onBackground,
                    shape = RoundedCornerShape(8.dp),
                ) {
                    LazyColumn(
                        modifier = Modifier.padding(all = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        item {
                            Text(
                                text = title,
                                color = colorScheme.primary,
                                fontSize = 20.sp,
                            )
                        }

                        items(GooglePlaceType.values()) {
                            Text(
                                text = stringResource(id = it.description.resourceId),
                                fontSize = 16.sp,
                                modifier = Modifier
                                    .padding(top = 8.dp)
                                    .fillMaxWidth()
                                    .clickable {
                                        onPickedPlace(it)
                                    },
                            )
                        }
                    }
                }
            }
        )
    }
}

@Preview
@Composable
fun PlaceTypePickerPreview() {
    PlaceTypePicker(
        modifier = Modifier,
        show = true,
        title = "Choose a type",
        onPickedPlace = { _ -> },
        onCancel = { },
    )
}
