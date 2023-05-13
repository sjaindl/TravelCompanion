package com.sjaindl.travelcompanion.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sjaindl.travelcompanion.R
import com.sjaindl.travelcompanion.baseui.DisplayItem
import com.sjaindl.travelcompanion.baseui.JumpItem
import com.sjaindl.travelcompanion.theme.TravelCompanionTheme

@Composable
fun ProfileScreen(
    onClose: () -> Unit = { },
    goToPersonalInfo: () -> Unit = { },
    viewModel: ProfileViewModel = viewModel(),
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    TravelCompanionTheme {
        Column(
            Modifier
                .fillMaxSize()
                .background(colors.background)
                .padding(start = 16.dp, end = 16.dp, top = 32.dp),
            verticalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(color = colors.onBackground),
            ) {
                Text(
                    text = viewModel.initials,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    color = colors.primary,
                    modifier = Modifier
                        .align(Alignment.Center),
                    fontSize = 32.sp,
                )
            }

            Text(
                text = viewModel.userName,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                color = colors.primary,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally),
                fontSize = 24.sp,
            )

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                JumpItem(
                    title = stringResource(id = R.string.personalInformation),
                    subTitle = stringResource(id = R.string.nameAddressBirthday),
                    icon = R.drawable.ic_user,
                ) {
                    goToPersonalInfo()
                }

                DisplayItem(
                    title = stringResource(id = R.string.requestAccountDeletion),
                    icon = android.R.drawable.ic_delete
                ) {
                    showDeleteDialog = true
                }

                DisplayItem(
                    title = stringResource(id = R.string.signOut),
                    icon = android.R.drawable.ic_lock_idle_lock
                ) {
                    viewModel.logout()
                    onClose()
                }
            }
        }

        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = {
                    showDeleteDialog = false
                },
                confirmButton = {
                    TextButton(onClick = {
                        showDeleteDialog = false
                        viewModel.deleteAccount()
                    }) {
                        Text(
                            text = stringResource(id = R.string.yesDelete),
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold,
                            color = colors.primary,
                            modifier = Modifier,
                            fontSize = 16.sp,
                        )
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        showDeleteDialog = false
                    }) {
                        Text(
                            text = stringResource(id = R.string.cancel),
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold,
                            color = colors.primary,
                            modifier = Modifier,
                            fontSize = 16.sp,
                        )
                    }
                },
                title = {
                    Text(
                        text = stringResource(id = R.string.deleteAccount),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier,
                        fontSize = 24.sp,
                    )
                },
                text = {
                    Text(
                        text = stringResource(id = R.string.doYouReallyWantToDeleteYourAccount),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier,
                        fontSize = 18.sp,
                    )
                }
            )
        }
    }
}