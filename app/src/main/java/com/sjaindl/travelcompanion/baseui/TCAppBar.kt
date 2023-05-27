package com.sjaindl.travelcompanion.baseui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.sjaindl.travelcompanion.R
import com.sjaindl.travelcompanion.profile.UserIconView
import com.sjaindl.travelcompanion.theme.TravelCompanionTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TCAppBar(
    title: String,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit = {},
    showProfile: Boolean = false,
    onClickProfile: () -> Unit = { },
    customActionIcon: ImageVector? = null,
    onCustomAction: () -> Unit = { },
) {
    val context = LocalContext.current

    TravelCompanionTheme {
        TopAppBar(
            title = {
                Text(title)
            },
            modifier = Modifier,
            navigationIcon = {
                if (canNavigateBack) {
                    IconButton(onClick = { navigateUp() }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.back),
                        )
                    }
                }
            },
            actions = {
                if (showProfile) {
                    AndroidView(
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .clickable {
                                onClickProfile()
                            },
                        factory = {
                            UserIconView(context)
                        }
                    )
                } else if (customActionIcon != null) {
                    Image(
                        imageVector = customActionIcon,
                        contentDescription = null,
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .clickable {
                                onCustomAction()
                            }
                    )
                }
            },
            colors = TopAppBarDefaults.smallTopAppBarColors(
                containerColor = colors.primary
            )
        )
    }
}

@Preview
@Composable
fun TCAppBarPreview() {
    TCAppBar(
        title = stringResource(R.string.app_name),
        canNavigateBack = true,
        navigateUp = {},
        showProfile = true,
        onClickProfile = { },
    )
}
