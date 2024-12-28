package com.sjaindl.travelcompanion.explore.details.info

import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import com.sjaindl.travelcompanion.R
import com.sjaindl.travelcompanion.exception.OfflineException
import com.sjaindl.travelcompanion.theme.TravelCompanionTheme
import com.sjaindl.travelcompanion.util.LoadingAnimation

@Composable
fun ExploreDetailInfoScreen(
    infoType: InfoType,
    pinId: Long,
    viewModel: ExploreInfoViewModel = hiltViewModel(
        key = infoType.toString(),
        creationCallback = { factory: ExploreInfoViewModelFactory ->
            factory.create(pinId = pinId, infoType = infoType)
        },
    )
) {
    TravelCompanionTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(colorScheme.background)
                .wrapContentSize(Alignment.Center)
        ) {
            val state by viewModel.state.collectAsState()

            when (state) {
                ExploreInfoViewModel.State.Loading -> {
                    LoadingAnimation()
                }

                is ExploreInfoViewModel.State.Done -> {
                    val url = (state as ExploreInfoViewModel.State.Done).url

                    if (url != null) {
                        AndroidView(
                            factory = { context ->
                                WebView(context).apply {
                                    layoutParams = ViewGroup.LayoutParams(
                                        ViewGroup.LayoutParams.MATCH_PARENT,
                                        ViewGroup.LayoutParams.MATCH_PARENT
                                    )
                                    with(settings) {
                                        javaScriptEnabled = true
                                        domStorageEnabled = true
                                    }
                                    clearHistory()
                                    webViewClient = WebViewClient()
                                }
                            }, update = {
                                it.loadUrl(url)
                            }
                        )
                    } else {
                        Text(
                            text = stringResource(id = R.string.couldNotRetrieveData),
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                            textAlign = TextAlign.Center,
                            fontSize = 20.sp
                        )
                    }
                }

                is ExploreInfoViewModel.State.Error -> {
                    val error = state as ExploreInfoViewModel.State.Error

                    val errorMessage =
                        if (error.throwable is OfflineException) stringResource(id = R.string.offline)
                        else (error.throwable.localizedMessage ?: error.throwable.toString())

                    Text(
                        text = errorMessage,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        fontSize = 20.sp
                    )
                }

                is ExploreInfoViewModel.State.NoData -> {
                    Text(
                        text = stringResource(id = R.string.couldNotRetrieveData),
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        fontSize = 20.sp
                    )
                }
            }
        }
    }
}
