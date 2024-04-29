package com.sjaindl.travelcompanion.explore.details.info

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.sjaindl.travelcompanion.explore.details.tabnav.DetailsTabBarLayout
import com.sjaindl.travelcompanion.explore.details.tabnav.TabItem
import com.sjaindl.travelcompanion.theme.TravelCompanionTheme
import com.sjaindl.travelcompanion.R

@Composable
fun ExploreDetailInfoMainScreen(pinId: Long) {
    val tabRowItems = listOf(
        TabItem(
            title = stringResource(id = R.string.wikipedia),
            screen = { ExploreDetailInfoScreen(infoType = InfoType.WIKIPEDIA, pinId = pinId) },
        ),
        TabItem(
            title = stringResource(id = R.string.wikivoyage),
            screen = { ExploreDetailInfoScreen(infoType = InfoType.WIKIVOYAGE, pinId = pinId) },
        ),
        TabItem(
            title = stringResource(id = R.string.google),
            screen = { ExploreDetailInfoScreen(infoType = InfoType.GOOGLE, pinId = pinId) },
        ),
        TabItem(
            title = stringResource(id = R.string.lonelyplanet),
            screen = { ExploreDetailInfoScreen(infoType = InfoType.LONELYPLANET, pinId = pinId) },
        ),
    )

    TravelCompanionTheme {
        DetailsTabBarLayout(tabRowItems = tabRowItems, userScrollEnabled = false)
    }
}

@Preview
@Composable
fun ExploreDetailInfoMainScreenPreview() {
    ExploreDetailInfoMainScreen(pinId = 1)
}
