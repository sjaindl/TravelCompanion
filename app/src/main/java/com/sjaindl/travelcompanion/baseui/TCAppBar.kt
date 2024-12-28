package com.sjaindl.travelcompanion.baseui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sjaindl.travelcompanion.R
import com.sjaindl.travelcompanion.TCAnimationScreenWithoutRecompositions
import com.sjaindl.travelcompanion.profile.usericon.UserIconContainer
import com.sjaindl.travelcompanion.theme.TravelCompanionTheme

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun TCAppBar(
    title: String,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit = { },
    showProfile: Boolean = false,
    onClickProfile: () -> Unit = { },
    customActionIcon: ImageVector? = null,
    onCustomAction: () -> Unit = { },
) {
    var showAnimationScreen by remember {
        mutableStateOf(value = false)
    }

    TopAppBar(
        title = {
            Text(
                text = title,
                modifier = Modifier.combinedClickable(
                    enabled = true,
                    onDoubleClick = {
                        showAnimationScreen = true
                    },
                    onClick = { }
                )
            )
        },
        modifier = Modifier,
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = { navigateUp() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(id = R.string.back),
                    )
                }
            }
        },
        actions = {
            if (showProfile) {
                UserIconContainer(
                    onClickedProfile = onClickProfile,
                )
            } else if (customActionIcon != null) {
                Image(
                    imageVector = customActionIcon,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .clickable {
                            onCustomAction()
                        },
                )
            }
        },
        colors = topAppBarColors(
            containerColor = colorScheme.primary,
        ),
    )

    if (showAnimationScreen) {
        TCAnimationScreenWithoutRecompositions(
            onDismiss = {
                showAnimationScreen = false
            }
        )
    }
}

@Preview
@Composable
fun TCAppBarPreview() {
    TravelCompanionTheme {
        TCAppBar(
            title = stringResource(R.string.appName),
            canNavigateBack = true,
            navigateUp = { },
            showProfile = true,
            onClickProfile = { },
        )
    }
}
