package com.sjaindl.travelcompanion.baseui

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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.sjaindl.travelcompanion.R
import com.sjaindl.travelcompanion.profile.UserIconView
import com.sjaindl.travelcompanion.theme.TravelCompanionTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TCAppBar(
    title: String,
    showProfile: Boolean = false,
    onClickProfile: () -> Unit = { },
) {
    val context = LocalContext.current
    val navController = rememberNavController()

    val currentBackStackEntry by navController.currentBackStackEntryAsState()

    TravelCompanionTheme {
        TopAppBar(
            title = {
                Text(title)
            },
            modifier = Modifier,
            navigationIcon = {
                // TODO: fix this
                if (true || navController.previousBackStackEntry != null) {
                    IconButton(onClick = { navController.navigateUp() }) {
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
        showProfile = true,
        onClickProfile = { },
    )
}
