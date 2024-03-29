package com.sjaindl.travelcompanion.plan

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sjaindl.travelcompanion.theme.TravelCompanionTheme
import kotlinx.coroutines.launch

// https://proandroiddev.com/bottom-sheet-in-jetpack-compose-d7e106422606
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PlanActionBottomSheet(
    modifier: Modifier = Modifier,
    show: Boolean,
    title: String,
    onShow: () -> Unit,
    onShowDetails: () -> Unit,
    onDelete: () -> Unit,
    onCancel: () -> Unit,
) {
    TravelCompanionTheme {
        val coroutineScope = rememberCoroutineScope()
        val modalSheetState = rememberModalBottomSheetState(
            initialValue = ModalBottomSheetValue.Hidden,
            confirmStateChange = {
                if (it == ModalBottomSheetValue.Hidden) {
                    onCancel()
                }
                true
            },
            skipHalfExpanded = false,
        )

        LaunchedEffect(key1 = !show) {
            coroutineScope.launch {
                if (!show) {
                    modalSheetState.hide()
                } else {
                    modalSheetState.show()
                }
            }
        }

        ModalBottomSheetLayout(
            sheetState = modalSheetState,
            sheetShape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp),
            sheetContent = {
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
            }
        ) {
            // no content
        }

        BackHandler(modalSheetState.isVisible) {
            coroutineScope.launch { modalSheetState.hide() }
        }
    }
}

@Preview
@Composable
fun PlanActionBottomSheetPreview() {
    PlanActionBottomSheet(
        show = true,
        title = "Test Location",
        onShow = { },
        onShowDetails = { },
        onDelete = { },
        onCancel = { },
    )
}
