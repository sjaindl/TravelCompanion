package com.sjaindl.travelcompanion.explore

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import com.sjaindl.travelcompanion.R
import com.sjaindl.travelcompanion.databinding.FragmentExploreBinding
import com.sjaindl.travelcompanion.util.GoogleMapsUtil

data class MapLocationData(
    val latitude: Double,
    val longitude: Double,
    val radius: Double
)

class ExploreFragment : Fragment(), OnMapReadyCallback {
    private var binding: FragmentExploreBinding? = null

    private val latitude: Float?
        get() = arguments?.getFloat(LATITUDE)

    private val longitude: Float?
        get() = arguments?.getFloat(LONGITUDE)

    private val radius: Float?
        get() = arguments?.getFloat(RADIUS)

    companion object {
        const val LATITUDE = "latitude"
        const val LONGITUDE = "longitude"
        const val RADIUS = "radius"

        fun newInstance(mapLocationData: MapLocationData): ExploreFragment {
            return ExploreFragment().apply {
                arguments = Bundle().apply {
                    putDouble(LATITUDE, mapLocationData.latitude)
                    putDouble(LONGITUDE, mapLocationData.longitude)
                    putDouble(RADIUS, mapLocationData.radius)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        binding = FragmentExploreBinding.inflate(inflater, container, false)

        val mapFragment = childFragmentManager.findFragmentByTag("tag_map") as? SupportMapFragment
        mapFragment?.getMapAsync(this)

        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.fab?.setOnClickListener { view ->
            Snackbar.make(view, "Add place", Snackbar.LENGTH_LONG)
                .setAnchorView(R.id.fab)
                .setAction("Action", null)
                .show()

            val action = ExploreFragmentDirections.actionExploreFragmentToSearchPlaceFragment(
                15.4f, 10000.0f, 47.0f
            )

            findNavController().navigate(action)
        }

        setFragmentResultListener("place") { key, bundle ->
            Snackbar.make(requireView(), "Picked $key via fragment result", Snackbar.LENGTH_LONG).show()
        }

        // Alternative using nav graph backstack:
        //findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<String>("place")?.observe(viewLifecycleOwner) {
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    override fun onMapReady(googleMap: GoogleMap) {
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
    }
}
