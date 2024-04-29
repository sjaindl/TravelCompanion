package com.sjaindl.travelcompanion.explore.details.tabnav

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.TabRowDefaults
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Photo
import androidx.compose.material.icons.rounded.Place
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.pagerTabIndicatorOffset
import com.sjaindl.travelcompanion.theme.TravelCompanionTheme
import kotlinx.coroutines.launch
import com.sjaindl.travelcompanion.R

const val initialPhotoTabPage = 1

@OptIn(ExperimentalFoundationApi::class, ExperimentalPagerApi::class)
@Composable
fun DetailsTabBarLayout(tabRowItems: List<TabItem>, userScrollEnabled: Boolean) {
    val pagerState = rememberPagerState(pageCount = {
        tabRowItems.size
    })

    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(key1 = Unit) {
        pagerState.animateScrollToPage(page = initialPhotoTabPage)
    }

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
                            item.icon?.let {
                                Icon(imageVector = item.icon, contentDescription = "")
                            }
                        },
                        text = {
                            Text(
                                fontSize = 12.sp,
                                text = item.title,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                softWrap = true,
                            )
                        }
                    )
                }
            }

            HorizontalPager(
                modifier = Modifier
                    .fillMaxSize()
                    .background(colors.background),
                state = pagerState,
                userScrollEnabled = userScrollEnabled,
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
        ),
        userScrollEnabled = true,
    )
}
