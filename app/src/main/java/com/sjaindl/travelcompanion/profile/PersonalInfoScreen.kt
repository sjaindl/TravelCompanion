package com.sjaindl.travelcompanion.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sjaindl.travelcompanion.baseui.DisplayItem
import com.sjaindl.travelcompanion.theme.TravelCompanionTheme

@Composable
fun PersonalInfoScreen(
    viewModel: PersonalInfoViewModel = viewModel()
) {
    TravelCompanionTheme {
        Column(
            Modifier
                .fillMaxSize()
                .background(colors.background)
                .padding(start = 16.dp, end = 16.dp, top = 32.dp),
            verticalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                DisplayItem(
                    subTitle = viewModel.email,
                    imageVector = Icons.Default.Mail
                )

                DisplayItem(
                    subTitle = viewModel.userName,
                    imageVector = Icons.Default.VerifiedUser,
                )
            }
        }
    }
}

@Preview
@Composable
fun PersonalInfoScreenPreview() {
    PersonalInfoScreen()
}