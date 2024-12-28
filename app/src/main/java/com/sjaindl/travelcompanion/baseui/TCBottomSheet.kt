package com.sjaindl.travelcompanion.baseui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sjaindl.travelcompanion.theme.TravelCompanionTheme

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun TCBottomSheet(
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = BottomSheetDefaults.ContainerColor,
    contentColor: Color = contentColorFor(containerColor),
    scrimColor: Color = BottomSheetDefaults.ScrimColor,
    content: @Composable () -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onCancel,
        modifier = modifier
            .padding(bottom = 8.dp),
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = containerColor,
        scrimColor = scrimColor,
        contentColor = contentColor,
        windowInsets = WindowInsets.systemBars.only(WindowInsetsSides.Bottom),
    ) {
        TravelCompanionTheme {
            Column(
                modifier = Modifier
                    .padding(bottom = 8.dp),
            ) {
                content()
            }
        }
    }
}

@Preview
@Composable
fun TCBottomSheetPreview() {
    TCBottomSheet(
        onCancel = { },
        content = {
            Text(
                modifier = Modifier.padding(vertical = 16.dp),
                text = "Just some test content"
            )
        },
    )
}
