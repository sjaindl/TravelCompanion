package com.sjaindl.travelcompanion.explore

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.collectAsState
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import com.sjaindl.travelcompanion.R
import com.sjaindl.travelcompanion.api.google.PlacePredictions
import com.sjaindl.travelcompanion.com.sjaindl.travelcompanion.di.AndroidPersistenceInjector
import com.sjaindl.travelcompanion.databinding.FragmentExploreBinding
import com.sjaindl.travelcompanion.di.TCInjector
import com.sjaindl.travelcompanion.explore.search.PlaceActionBottomSheet
import com.sjaindl.travelcompanion.explore.search.SearchPlaceFragment
import com.sjaindl.travelcompanion.prefs.MapLocationDataPreferences
import com.sjaindl.travelcompanion.util.GoogleMapsUtil
import com.sjaindl.travelcompanion.util.randomStringByKotlinRandom
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.serialization.json.Json

class ExploreFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnCameraIdleListener {
    private var binding: FragmentExploreBinding? = null
    private var googleMap: GoogleMap? = null

    private val latitude: Float?
        get() = arguments?.getFloat(LATITUDE)

    private val longitude: Float?
        get() = arguments?.getFloat(LONGITUDE)

    private val radius: Float?
        get() = arguments?.getFloat(RADIUS)

    private val sessionToken = randomStringByKotlinRandom(size = 32)

    private val dataRepository by lazy {
        AndroidPersistenceInjector(requireContext().applicationContext).shared.dataRepository
    }

    private val geoNamesClient by lazy {
        TCInjector.geoNamesClient
    }

    private val googleClient by lazy {
        TCInjector.googleClient
    }

    private val viewModel by viewModels<ExploreViewModel>(factoryProducer = { ExploreViewModel.ExploreViewModelFactory(dataRepository) })

    private val prefs by lazy {
        MapLocationDataPreferences(requireContext())
    }

    private var cameraMoveInProgress = false

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
            val showDialogState = viewModel.showBottomSheet.collectAsState()
            val title = viewModel.dialogTitle.collectAsState()

            binding?.fab?.visibility = if (showDialogState.value) View.GONE else View.VISIBLE

            if (showDialogState.value) {
                PlaceActionBottomSheet(
                    title = title.value,
                    onShowDetails = viewModel::onShowDetails,
                    onPlanTrip = viewModel::onDismiss,
                    onDelete = viewModel::onDelete,
                    onCancel = viewModel::onDismiss,
                )
            }

            /*
            PlaceActionDialog(
                show = showDialogState.value,
                title = title.value,
                onShowDetails = viewModel::onShowDetails,
                onPlanTrip = viewModel::onPlanTrip,
                onDelete = viewModel::onDelete,
                onCancel = viewModel::onDismiss,
            )
             */
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

            // val action = ExploreFragmentDirections.actionExploreFragmentToSearchPlaceFragment()
            // findNavController().navigate(action)
        }

        setFragmentResultListener(SearchPlaceFragment.PLACE_RESULT) { key, bundle ->
            val encodedPlaces = bundle.getString(SearchPlaceFragment.PLACE_RESULT) ?: return@setFragmentResultListener
            val placesPredictions = Json.decodeFromString(PlacePredictions.serializer(), encodedPlaces)
            val placeId = placesPredictions.placePrediction.placeId ?: return@setFragmentResultListener

            Snackbar.make(
                requireView(),
                "Picked ${placesPredictions.placePrediction.description} via fragment result of key $key",
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
            viewModel.showDetails.collect { pinId ->
                if (pinId > 0) {
                    viewModel.clickedOnDetails()

                    // val intent = Intent(requireActivity(), ExploreDetailActivity::class.java).apply {
                    //  putExtra(PIN_ID, pinId)
                    // }

                    // requireActivity().startActivity(intent)
                }
            }
            // }
        }
    }

    private fun fetchPlaceDetails(placeId: String) = lifecycleScope.launch {
        googleClient.placeDetail(placeId = placeId, token = sessionToken)
            .onSuccess { details ->
                val location = details.location
                val marker = googleMap?.addMarker(
                    MarkerOptions()
                        .position(LatLng(location.latitude, location.longitude))
                        .title(details.name)
                )

                googleMap?.moveCamera(CameraUpdateFactory.newLatLng(LatLng(location.latitude, location.longitude)))

                try {
                    val countryCode = geoNamesClient.fetchCountryCode(latitude = location.latitude, longitude = location.longitude)

                    val component = details.addressComponents?.firstOrNull {
                        it.types.contains("country")
                    }
                    val name = details.name

                    dataRepository.insertPin(
                        id = 0,
                        address = details.formattedAddress,
                        country = component?.longName,
                        countryCode = countryCode,
                        creationDate = Clock.System.now(),
                        latitude = details.location.latitude,
                        longitude = details.location.longitude,
                        name = name,
                        phoneNumber = null,
                        placeId = placeId,
                        rating = null,
                        url = details.websiteUri,
                    )

                    if (name != null && marker != null) {
                        dataRepository.singlePin(name = name)?.let { pin ->
                            // viewModel.markers[pin.id] = marker
                        }
                    }
                } catch (exception: Exception) {
                    Snackbar.make(
                        requireView(),
                        exception.localizedMessage ?: "Could not fetch country code",
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            }.onFailure {
                Snackbar.make(
                    requireView(),
                    it.localizedMessage ?: "Could not fetch country code",
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

        addPersistedPinsToMap()

        googleMap.setOnMarkerClickListener(this)
        googleMap.setOnCameraIdleListener(this)
    }

    private fun addPersistedPinsToMap() {
        dataRepository.allPins().forEach { pin ->
            val lat = pin.latitude ?: return@forEach
            val lng = pin.longitude ?: return@forEach

            val marker = googleMap?.addMarker(
                MarkerOptions()
                    .position(LatLng(lat, lng))
                    .title(pin.name)
            )

            marker?.let {
                // viewModel.markers[pin.id] = it
            }
        }
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        viewModel.clickedOnPlace(marker.title)
        return true
    }

    override fun onCameraIdle() {
        val cameraPosition = googleMap?.cameraPosition ?: return

        lifecycleScope.launch {
            cameraMoveInProgress = false
            prefs.updateLastLocation(
                latitude = cameraPosition.target.latitude.toFloat(),
                longitude = cameraPosition.target.longitude.toFloat(),
                radius = cameraPosition.zoom,
            )
        }
    }
}
