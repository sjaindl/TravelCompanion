package com.sjaindl.travelcompanion.explore.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.os.bundleOf
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sjaindl.travelcompanion.R
import com.sjaindl.travelcompanion.databinding.FragmentSearchPlaceBinding
import com.sjaindl.travelcompanion.di.TCInjector
import com.sjaindl.travelcompanion.util.CustomDividerItemDecoration
import com.sjaindl.travelcompanion.util.randomStringByKotlinRandom
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import timber.log.Timber

class SearchPlaceFragment : Fragment() {
    private var binding: FragmentSearchPlaceBinding? = null

    private val sessionToken = randomStringByKotlinRandom(32)

    private val searchPlaceAdapter by lazy {
        SearchPlaceAdapter { viewHolderType ->
            (viewHolderType as? SearchPlaceViewHolderType.PlacesPredictionItem)?.let {
                onClickItem(it)
            }
        }
    }

    private val googleClient by lazy {
        TCInjector.googleClient
    }

    companion object {
        const val PLACE_RESULT = "place"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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
        val adapter = ArrayAdapter<String>(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            emptyArray()
        )
        binding?.autocompleteCountry?.setAdapter(adapter)

        binding?.autocompleteCountry?.doOnTextChanged { text, start, before, count ->
            lifecycleScope.launch {
                val autocompleteResult = googleClient.autocomplete(
                    text.toString(), sessionToken
                ).onSuccess { autocompleteResult ->
                    val suggestions = autocompleteResult?.suggestions ?: emptyList()

                    requireActivity().runOnUiThread {
                        println(suggestions)

                        val viewHolders = suggestions.map {
                            SearchPlaceViewHolderType.PlacesPredictionItem(placePrediction = it.placePrediction)
                        }
                        searchPlaceAdapter.submitList(viewHolders)
                    }
                }.onFailure {
                    Timber.e(it)
                    searchPlaceAdapter.submitList(emptyList())
                }
            }
        }
    }

    private fun onClickItem(item: SearchPlaceViewHolderType.PlacesPredictionItem) {
        val encodedResult = Json.encodeToString(item.placePrediction)
        setFragmentResult(PLACE_RESULT, bundleOf(PLACE_RESULT to encodedResult))
        // Alternative using nav graph backstack:
        // findNavController().currentBackStackEntry?.savedStateHandle?.set("place", item.description)

        findNavController().popBackStack()
    }

    private fun setupRecyclerView(recyclerView: RecyclerView) {
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.addItemDecoration(
            CustomDividerItemDecoration(
                requireContext(),
                R.drawable.divider
            )
        )
        recyclerView.adapter = searchPlaceAdapter
    }
}
