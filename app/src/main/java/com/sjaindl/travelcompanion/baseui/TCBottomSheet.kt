package com.sjaindl.travelcompanion.baseui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Text
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sjaindl.travelcompanion.theme.TravelCompanionTheme
import kotlinx.coroutines.launch

// https://proandroiddev.com/bottom-sheet-in-jetpack-compose-d7e106422606
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun TCBottomSheet(
    show: Boolean,
    onCancel: () -> Unit,
    sheetContent: @Composable () -> Unit,
    content: @Composable () -> Unit = { },
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
                    modalSheetState.animateTo(ModalBottomSheetValue.Expanded)
                }
            }
        }

        ModalBottomSheetLayout(
            sheetState = modalSheetState,
            sheetShape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp),
            sheetContent = {
                sheetContent()
            }
        ) {
            content()
        }

        BackHandler(modalSheetState.isVisible) {
            coroutineScope.launch { modalSheetState.hide() }
        }
    }
}

@Preview
@Composable
fun TCBottomSheetPreview() {
    TCBottomSheet(
        show = true,
        onCancel = { },
        sheetContent = {
            Text(
                modifier = Modifier.padding(vertical = 16.dp),
                text = "Just some test content"
            )
        }
    )
}
