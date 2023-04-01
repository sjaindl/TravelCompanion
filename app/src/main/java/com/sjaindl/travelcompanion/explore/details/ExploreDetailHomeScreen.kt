package com.sjaindl.travelcompanion.explore.details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.sjaindl.travelcompanion.R
import com.sjaindl.travelcompanion.explore.ExploreDetailViewModel
import com.sjaindl.travelcompanion.explore.ExploreDetailViewModelFactory
import com.sjaindl.travelcompanion.repository.DataRepositoryImpl
import com.sjaindl.travelcompanion.sqldelight.DatabaseDriverFactory
import com.sjaindl.travelcompanion.sqldelight.DatabaseWrapper
import com.sjaindl.travelcompanion.theme.TravelCompanionTheme
import java.text.NumberFormat

@Composable
fun ExploreDetailHomeScreen(
    pinId: Long,
    viewModel: ExploreDetailViewModel = viewModel(
        factory = ExploreDetailViewModelFactory(
            pinId = pinId,
            // TODO: Introduce Hilt for DI
            dataRepository = DataRepositoryImpl(DatabaseWrapper(DatabaseDriverFactory(LocalContext.current)).dbQueries)
        ),
    )
) {
    TravelCompanionTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(colorResource(id = R.color.darkGrey))
                .padding(all = 16.dp)
        ) {

            val state by viewModel.state.collectAsState()
            val spacerHeight = 12.dp

            when (state) {
                ExploreDetailViewModel.State.Loading -> {
                    Text(
                        text = "Loading ..",
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        textAlign = TextAlign.Center,
                        fontSize = 20.sp
                    )
                }

                is ExploreDetailViewModel.State.Done -> {
                    val countryUiData = (state as ExploreDetailViewModel.State.Done).countryUi

                    Column(
                        modifier = Modifier.verticalScroll(state = rememberScrollState())
                    ) {
                        countryUiData.placeName?.let {
                            ExploreDetailEntry(modifier = Modifier.padding(vertical = 8.dp), title = it, value = null)
                        }

                        ExploreDetailEntry(title = null, value = countryUiData.latitudeLongitude)

                        countryUiData.website?.let { link ->
                            val annotatedString = buildAnnotatedString {
                                append(link)
                                addStyle(
                                    style = SpanStyle(
                                        color = Color(0xff64B5F6),
                                        fontSize = 18.sp,
                                        textDecoration = TextDecoration.Underline
                                    ),
                                    start = 0,
                                    end = link.length
                                )

                                addStringAnnotation(
                                    tag = "URL",
                                    annotation = link,
                                    start = 0,
                                    end = link.length
                                )
                            }

                            val uriHandler = LocalUriHandler.current
                            ClickableText(text = annotatedString) {
                                annotatedString.getStringAnnotations(tag = "URL", start = it, end = it).firstOrNull()?.let { annotation ->
                                    uriHandler.openUri(annotation.item)
                                }
                            }

                            Spacer(modifier = Modifier.height(spacerHeight))
                        }

                        countryUiData.phoneNumber?.let {
                            ExploreDetailEntry(modifier = Modifier.padding(vertical = 8.dp), title = null, value = it)
                            Spacer(modifier = Modifier.height(spacerHeight))
                        }

                        Spacer(modifier = Modifier.height(spacerHeight))

                        countryUiData.countryName?.let {
                            ExploreDetailEntry(title = it, value = null)
                            Spacer(modifier = Modifier.height(spacerHeight))
                        }

                        countryUiData.flagLink?.let {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current).data(it).decoderFactory(SvgDecoder.Factory()).build(),
                                contentDescription = null,
                                modifier = Modifier
                                    .height(100.dp)
                                    .padding(bottom = 12.dp),
                            )

                            Spacer(modifier = Modifier.height(spacerHeight))
                        }

                        countryUiData.capital?.let {
                            ExploreDetailEntry(title = stringResource(id = R.string.capital), value = it)
                            Spacer(modifier = Modifier.height(spacerHeight))
                        }

                        if (countryUiData.languages.isNotEmpty()) {
                            val languages = countryUiData.languages.mapNotNull { it.name }.joinToString(", ")
                            ExploreDetailEntry(title = stringResource(id = R.string.language), value = languages)
                            Spacer(modifier = Modifier.height(spacerHeight))
                        }

                        if (countryUiData.currencies.isNotEmpty()) {
                            val currencies = countryUiData.currencies.joinToString(", ") { "${it.name} (${it.code}/${it.symbol})" }
                            ExploreDetailEntry(title = stringResource(id = R.string.currency), value = currencies)
                            Spacer(modifier = Modifier.height(spacerHeight))
                        }

                        countryUiData.areaSquareKilometers?.let {
                            val area = NumberFormat.getNumberInstance().format(it)
                            val km = stringResource(id = R.string.km2)
                            val areaString = "$area $km"
                            ExploreDetailEntry(title = stringResource(id = R.string.area), value = areaString)
                            Spacer(modifier = Modifier.height(spacerHeight))
                        }

                        countryUiData.population?.let {
                            val population = NumberFormat.getNumberInstance().format(it)
                            ExploreDetailEntry(title = stringResource(id = R.string.population), value = population)
                            Spacer(modifier = Modifier.height(spacerHeight))
                        }

                        if (countryUiData.timezones.isNotEmpty()) {
                            val timezones = countryUiData.timezones.joinToString(", ")
                            ExploreDetailEntry(title = stringResource(id = R.string.timezones), value = timezones)
                            Spacer(modifier = Modifier.height(spacerHeight))
                        }

                        countryUiData.region?.let {
                            ExploreDetailEntry(title = stringResource(id = R.string.region), value = it)
                            Spacer(modifier = Modifier.height(spacerHeight))
                        }


                        countryUiData.isoCode?.let {
                            ExploreDetailEntry(title = stringResource(id = R.string.isoCode), value = it)
                            Spacer(modifier = Modifier.height(spacerHeight))
                        }

                        if (countryUiData.callingCodes.isNotEmpty()) {
                            val callingCodes = countryUiData.callingCodes.joinToString(", ")
                            ExploreDetailEntry(title = stringResource(id = R.string.callingCodes), value = callingCodes)
                            Spacer(modifier = Modifier.height(spacerHeight))
                        }

                        if (countryUiData.domains.isNotEmpty()) {
                            val domains = countryUiData.domains.joinToString(", ")
                            ExploreDetailEntry(title = stringResource(id = R.string.domains), value = domains)
                            Spacer(modifier = Modifier.height(spacerHeight))
                        }

                        countryUiData.nativeName?.let {
                            ExploreDetailEntry(title = stringResource(id = R.string.native_name), value = it)
                            Spacer(modifier = Modifier.height(spacerHeight))
                        }

                        if (countryUiData.regionalBlocks.isNotEmpty()) {
                            val regionalBlocks = countryUiData.regionalBlocks.joinToString(", ") { "${it.name} (${it.acronym})" }
                            ExploreDetailEntry(title = stringResource(id = R.string.blocks), value = regionalBlocks)
                            Spacer(modifier = Modifier.height(spacerHeight))
                        }
                    }
                }

                is ExploreDetailViewModel.State.Error -> {
                    val error = (state as ExploreDetailViewModel.State.Error)

                    val errorMessage = error.message ?: stringResource(id = (error.stringRes ?: R.string.couldNotRetrieveData))

                    Text(
                        text = errorMessage,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        textAlign = TextAlign.Center,
                        fontSize = 20.sp,
                    )
                }
            }
        }
    }
}

@Composable
fun ExploreDetailEntry(modifier: Modifier = Modifier, title: String? = null, value: String? = null) {
    TravelCompanionTheme {
        Column(modifier = modifier) {
            title?.let {
                Text(
                    text = it,
                    fontWeight = FontWeight.Bold,
                    color = colors.primary,
                    textAlign = TextAlign.Start,
                    fontSize = 20.sp
                )

                Spacer(modifier = Modifier.height(8.dp))
            }

            value?.let {
                Text(
                    text = it,
                    color = colors.primary,
                    textAlign = TextAlign.Start,
                    fontSize = 16.sp
                )
            }
        }
    }
}

@Preview
@Composable
fun ExploreDetailEntryPreview() {
    ExploreDetailEntry(title = "Capital", value = "Roma")
}

@Preview
@Composable
fun ExploreDetailHomeScreenPreview() {
    ExploreDetailHomeScreen(pinId = 1)
}