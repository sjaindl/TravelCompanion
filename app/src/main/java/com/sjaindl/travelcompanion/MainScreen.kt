package com.sjaindl.travelcompanion

import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidViewBinding
import com.sjaindl.travelcompanion.baseui.TCAppBar
import com.sjaindl.travelcompanion.databinding.FragmentMainBinding
import com.sjaindl.travelcompanion.theme.TravelCompanionTheme

@Composable
internal fun MainScreen(
    onNavigateToExplore: () -> Unit,
    onNavigateToPlan: () -> Unit,
    onNavigateToRemember: () -> Unit,
    onNavigateToProfile: () -> Unit,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit = {},
) {
    val context = LocalContext.current
    val title = stringResource(R.string.appName)

    TravelCompanionTheme {
        Scaffold(
            topBar = {
                TCAppBar(
                    title = title,
                    canNavigateBack = canNavigateBack,
                    navigateUp = navigateUp,
                    showProfile = true,
                    onClickProfile = onNavigateToProfile,
                )
            },
        ) { paddingValues ->
            AndroidViewBinding(
                modifier = Modifier.padding(paddingValues),
                factory = FragmentMainBinding::inflate,
            ) {
                this.exploreItem = MainMenuItem(
                    context.getString(R.string.explore),
                    context.getString(R.string.exploreDetail),
                    AppCompatResources.getDrawable(context, R.drawable.explore)
                )

                this.planItem = MainMenuItem(
                    context.getString(R.string.plan),
                    context.getString(R.string.planDetail),
                    AppCompatResources.getDrawable(context, R.drawable.plan)
                )

                this.rememberItem = MainMenuItem(
                    context.getString(R.string.remember),
                    context.getString(R.string.rememberDetail),
                    AppCompatResources.getDrawable(context, R.drawable.remember)
                )

                // set listeners:
                val exploreViews = listOf(
                    this.explore.mainTitle,
                    this.explore.mainSubtitle,
                    this.explore.imageView
                )
                val planViews =
                    listOf(this.plan.mainTitle, this.plan.mainSubtitle, this.plan.imageView)
                val rememberViews = listOf(
                    this.remember.mainTitle,
                    this.remember.mainSubtitle,
                    this.remember.imageView
                )

                exploreViews.forEach {
                    it.setOnClickListener {
                        onNavigateToExplore()
                    }
                }

                planViews.forEach {
                    it.setOnClickListener {
                        onNavigateToPlan()
                    }
                }

                rememberViews.forEach {
                    it.setOnClickListener {
                        onNavigateToRemember()
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun MainScreenPreview() {
    MainScreen(
        onNavigateToExplore = { },
        onNavigateToPlan = { },
        onNavigateToRemember = { },
        onNavigateToProfile = { },
        canNavigateBack = false,
    )
}
