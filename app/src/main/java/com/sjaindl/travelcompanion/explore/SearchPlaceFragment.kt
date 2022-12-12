package com.sjaindl.travelcompanion.explore

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.sjaindl.travelcompanion.R
import com.sjaindl.travelcompanion.api.google.GoogleClient
import com.sjaindl.travelcompanion.api.google.GooglePlaceType
import com.sjaindl.travelcompanion.databinding.FragmentSearchPlaceBinding
import com.sjaindl.travelcompanion.util.CustomDividerItemDecoration
import com.sjaindl.travelcompanion.util.randomStringByKotlinRandom
import kotlinx.coroutines.launch
import kotlin.random.Random

class SearchPlaceFragment : Fragment() {
    private var binding: FragmentSearchPlaceBinding? = null

    private val latitude: Float?
        get() = arguments?.getFloat(ExploreFragment.LATITUDE)

    private val longitude: Float?
        get() = arguments?.getFloat(ExploreFragment.LONGITUDE)

    private val radius: Float?
        get() = arguments?.getFloat(ExploreFragment.RADIUS)

    private val sessionToken = randomStringByKotlinRandom(32)

    private val searchPlaceAdapter by lazy {
        SearchPlaceAdapter {
            onClickItem(it)
        }
    }

    companion object {
        const val LATITUDE = "latitude"
        const val LONGITUDE = "longitude"
        const val RADIUS = "radius"

        fun newInstance(mapLocationData: MapLocationData): SearchPlaceFragment {
            return SearchPlaceFragment().apply {
                arguments = Bundle().apply {
                    putDouble(LATITUDE, mapLocationData.latitude)
                    putDouble(LONGITUDE, mapLocationData.longitude)
                    putDouble(RADIUS, mapLocationData.radius)
                }
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        binding = FragmentSearchPlaceBinding.inflate(inflater, container, false)

        setupAutocompletion()
        binding?.placeSuggestions?.let {
            setupRecyclerView(it)
        }

        return binding?.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    private fun setupAutocompletion() {
        val adapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_dropdown_item, emptyArray())
        binding?.autocompleteCountry?.setAdapter(adapter)

        binding?.autocompleteCountry?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // update places
                val latitude = latitude?.toDouble() ?: return
                val longitude = longitude?.toDouble() ?: return
                val radius = radius?.toDouble() ?: return

                lifecycleScope.launch {
                    val result = GoogleClient().searchPlaces(
                        s.toString(), latitude, longitude, GooglePlaceType.PointOfInterest.key, radius.toString()
                    )

                    val names = result.results.forEach { place ->
                        place.name
                    }

                    val autocompleteResult = GoogleClient().autocomplete(
                        s.toString(), sessionToken
                    )

                    val suggestions = autocompleteResult?.predictions?.map {
                        it.description
                    } ?: emptyList()

                    requireActivity().runOnUiThread {
                        println(suggestions)

                        val viewHolders = suggestions.map {
                            SearchPlaceViewHolderType.Item(it)
                        }
                        searchPlaceAdapter.submitList(viewHolders)
                    }
                }
            }
        })
    }

    private fun onClickItem(item: SearchPlaceViewHolderType.Item) {
        Snackbar.make(requireView(), "Add ${item.description}", Snackbar.LENGTH_LONG).show()
    }

    private fun setupRecyclerView(recyclerView: RecyclerView) {
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.addItemDecoration(CustomDividerItemDecoration(requireContext(), R.drawable.divider))
        recyclerView.adapter = searchPlaceAdapter
    }
}
