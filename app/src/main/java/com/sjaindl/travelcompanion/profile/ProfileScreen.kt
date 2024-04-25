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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
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
import com.sjaindl.travelcompanion.baseui.TCAppBar
import com.sjaindl.travelcompanion.theme.TravelCompanionTheme
import com.sjaindl.travelcompanion.shared.R as SharedR

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onClose: () -> Unit = { },
    goToPersonalInfo: () -> Unit = { },
    goToDataAccessRationaleInfo: () -> Unit = { },
    viewModel: ProfileViewModel = viewModel(),
    canNavigateBack: Boolean,
    navigateUp: () -> Unit = {},
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    TravelCompanionTheme {
        Scaffold(
            topBar = {
                TCAppBar(
                    title = stringResource(SharedR.string.profile),
                    canNavigateBack = canNavigateBack,
                    navigateUp = navigateUp,
                )
            },
        ) { paddingValues ->
            Column(
                Modifier
                    .padding(paddingValues)
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
                        title = stringResource(id = SharedR.string.personalInformation),
                        subTitle = stringResource(id = SharedR.string.nameAddressBirthday),
                        icon = R.drawable.ic_user,
                    ) {
                        goToPersonalInfo()
                    }

                    JumpItem(
                        title = stringResource(id = SharedR.string.dataUsageInfo),
                        icon = R.drawable.ic_user,
                    ) {
                        goToDataAccessRationaleInfo()
                    }

                    DisplayItem(
                        title = stringResource(id = SharedR.string.requestAccountDeletion),
                        icon = android.R.drawable.ic_delete
                    ) {
                        showDeleteDialog = true
                    }

                    DisplayItem(
                        title = stringResource(id = SharedR.string.signOut),
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
                                text = stringResource(id = SharedR.string.yesDelete),
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
                                text = stringResource(id = SharedR.string.cancel),
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
                            text = stringResource(id = SharedR.string.deleteAccount),
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier,
                            fontSize = 24.sp,
                        )
                    },
                    text = {
                        Text(
                            text = stringResource(id = SharedR.string.doYouReallyWantToDeleteYourAccount),
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
}
