package com.sjaindl.travelcompanion.explore.details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Photo
import androidx.compose.material.icons.rounded.Place
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.pagerTabIndicatorOffset
import com.google.accompanist.pager.rememberPagerState
import com.sjaindl.travelcompanion.R
import com.sjaindl.travelcompanion.theme.TravelCompanionTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalPagerApi::class)
@Composable
fun DetailsTabBarLayout(tabRowItems: List<TabItem>) {
    val pagerState = rememberPagerState()
    val coroutineScope = rememberCoroutineScope()

    TravelCompanionTheme {
        Column {
            TabRow(
                selectedTabIndex = pagerState.currentPage,
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        modifier = Modifier.pagerTabIndicatorOffset(pagerState, tabPositions),
                        color = colors.secondary
                    )
                },
            ) {
                tabRowItems.forEachIndexed { index, item ->
                    Tab(
                        selected = pagerState.currentPage == index,
                        onClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        },
                        icon = {
                            Icon(imageVector = item.icon, contentDescription = "")
                        },
                        text = {
                            Text(
                                text = item.title,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                    )
                }
            }

            HorizontalPager(
                count = tabRowItems.size,
                state = pagerState,
                modifier = Modifier
                    .fillMaxSize()
                    .background(colors.background)
            ) {
                tabRowItems[pagerState.currentPage].screen()
            }
        }
    }
}

@Preview
@Composable
fun DetailsTabBarLayoutPreview() {
    DetailsTabBarLayout(
        tabRowItems = listOf(
            TabItem(
                title = "Tab 1",
                screen = { TabScreen(text = stringResource(id = R.string.detail)) },
                icon = Icons.Rounded.Place,
            ),
            TabItem(
                title = "Tab 2",
                screen = { TabScreen(text = stringResource(id = R.string.photos)) },
                icon = Icons.Rounded.Photo,
            ),
            TabItem(
                title = "Tab 3",
                screen = { TabScreen(text = stringResource(id = R.string.info)) },
                icon = Icons.Rounded.Info,
            )
        )
    )
}
