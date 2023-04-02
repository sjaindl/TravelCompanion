package com.sjaindl.travelcompanion.explore

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.collectAsState
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import com.sjaindl.travelcompanion.R
import com.sjaindl.travelcompanion.api.geonames.GeoNamesClient
import com.sjaindl.travelcompanion.api.google.GoogleClient
import com.sjaindl.travelcompanion.api.google.PlacesPredictions
import com.sjaindl.travelcompanion.databinding.FragmentExploreBinding
import com.sjaindl.travelcompanion.explore.details.ExploreDetailActivity
import com.sjaindl.travelcompanion.explore.details.ExploreDetailActivity.Companion.PIN_ID
import com.sjaindl.travelcompanion.explore.search.SearchPlaceFragment
import com.sjaindl.travelcompanion.explore.search.PlaceActionDialog
import com.sjaindl.travelcompanion.repository.DataRepositoryImpl
import com.sjaindl.travelcompanion.sqldelight.DatabaseDriverFactory
import com.sjaindl.travelcompanion.sqldelight.DatabaseWrapper
import com.sjaindl.travelcompanion.util.GoogleMapsUtil
import com.sjaindl.travelcompanion.util.randomStringByKotlinRandom
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.serialization.json.Json

data class MapLocationData(
    val latitude: Double,
    val longitude: Double,
    val radius: Double
)

class ExploreFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    private var binding: FragmentExploreBinding? = null
    private var googleMap: GoogleMap? = null

    private val latitude: Float?
        get() = arguments?.getFloat(LATITUDE)

    private val longitude: Float?
        get() = arguments?.getFloat(LONGITUDE)

    private val radius: Float?
        get() = arguments?.getFloat(RADIUS)

    private val sessionToken = randomStringByKotlinRandom(32)

    private val consumeDatabase by lazy {
        DatabaseWrapper(DatabaseDriverFactory(requireContext().applicationContext))
    }

    private val dataRepository by lazy {
        DataRepositoryImpl(consumeDatabase.dbQueries)
    }

    private val viewModel by viewModels<ExploreViewModel>(factoryProducer = { ExploreViewModelFactory(dataRepository) })

    companion object {
        const val LATITUDE = "latitude"
        const val LONGITUDE = "longitude"
        const val RADIUS = "radius"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        binding = FragmentExploreBinding.inflate(inflater, container, false)

        val mapFragment = childFragmentManager.findFragmentByTag("tag_map") as? SupportMapFragment
        mapFragment?.getMapAsync(this)

        binding?.composeView?.setContent {
            val showDialogState = viewModel.showDialog.collectAsState()
            val title = viewModel.dialogTitle.collectAsState()

            PlaceActionDialog(
                show = showDialogState.value,
                title = title.value,
                onShowDetails = viewModel::onShowDetails,
                onPlanTrip = viewModel::onPlanTrip,
                onDelete = viewModel::onDelete,
                onCancel = viewModel::onDismiss,
            )
        }

        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.fab?.setOnClickListener {
            Snackbar.make(view, "Add place", Snackbar.LENGTH_LONG)
                .setAnchorView(R.id.fab)
                .setAction("Action", null)
                .show()

            val action = ExploreFragmentDirections.actionExploreFragmentToSearchPlaceFragment(
                15.4f, 10000.0f, 47.0f
            )

            findNavController().navigate(action)
        }

        setFragmentResultListener(SearchPlaceFragment.PLACE_RESULT) { key, bundle ->
            val encodedPlaces = bundle.getString(SearchPlaceFragment.PLACE_RESULT) ?: return@setFragmentResultListener
            val placesPredictions = Json.decodeFromString(PlacesPredictions.serializer(), encodedPlaces)
            val placeId = placesPredictions.placeId ?: return@setFragmentResultListener

            Snackbar.make(
                requireView(),
                "Picked ${placesPredictions.description} via fragment result of key $key",
                Snackbar.LENGTH_LONG
            ).show()

            fetchPlaceDetails(placeId = placeId)
        }

        // Alternative using nav graph backstack:
        //findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<String>("place")?.observe(viewLifecycleOwner) {
        setObservers()
    }

    private fun setObservers() {
        lifecycleScope.launch {
            //repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.onShowDetails.collect { pinId ->
                if (pinId > 0) {
                    viewModel.clickedOnDetails()

                    val intent = Intent(requireActivity(), ExploreDetailActivity::class.java).apply {
                        putExtra(PIN_ID, pinId)
                    }

                    requireActivity().startActivity(intent)
                }
            }
            // }
        }
    }

    private fun fetchPlaceDetails(placeId: String) = lifecycleScope.launch {
        val details = GoogleClient().placeDetail(placeId, sessionToken)
        val location = details.result.geometry.location
        googleMap?.addMarker(
            MarkerOptions()
                .position(LatLng(location.lat, location.lng))
                .title(details.result.name)
        )

        googleMap?.moveCamera(CameraUpdateFactory.newLatLng(LatLng(location.lat, location.lng)))

        try {
            val countryCode = GeoNamesClient().fetchCountryCode(latitude = location.lat, longitude = location.lng)

            val component = details.result.addressComponents?.firstOrNull {
                it.types.contains("country")
            }

            dataRepository.insertPin(
                id = 0,
                address = details.result.formattedAddress,
                country = component?.longName,
                countryCode = countryCode,
                creationDate = Clock.System.now(),
                latitude = details.result.geometry.location.lat,
                longitude = details.result.geometry.location.lng,
                name = details.result.name,
                phoneNumber = null,
                placeId = placeId,
                rating = null,
                url = details.result.url,
            )

        } catch (exception: Exception) {
            Snackbar.make(
                requireView(),
                exception.localizedMessage ?: "Could not fetch country code",
                Snackbar.LENGTH_LONG
            ).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap

        val latitude = latitude?.toDouble() ?: return
        val longitude = longitude?.toDouble() ?: return
        val radius = radius?.toDouble() ?: return

        val cameraUpdate = GoogleMapsUtil.getCameraUpdate(latitude, longitude, radius)
        googleMap.moveCamera(cameraUpdate)

        googleMap.addMarker(
            MarkerOptions()
                .position(LatLng(latitude, longitude))
                .title("Marker")
        )

        addPersistedPinsToMap()

        googleMap.setOnMarkerClickListener(this)
    }

    private fun addPersistedPinsToMap() {
        dataRepository.allPins().forEach {
            val lat = it.latitude ?: return@forEach
            val lng = it.longitude ?: return@forEach
            googleMap?.addMarker(
                MarkerOptions()
                    .position(LatLng(lat, lng))
                    .title(it.name)
            )
        }
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        viewModel.clickedOnPlace(marker.title)
        return true
    }
}
