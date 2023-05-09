package com.sjaindl.travelcompanion

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.sjaindl.travelcompanion.databinding.FragmentMainBinding
import com.sjaindl.travelcompanion.prefs.MapLocationDataPrefs
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class MainMenuItem(
    val title: String,
    val subtitle: String,
    val drawable: Drawable?,
)

class MainFragment : Fragment() {
    private var binding: FragmentMainBinding? = null

    private val prefs by lazy {
        MapLocationDataPrefs(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val fragmentBinding = FragmentMainBinding.inflate(inflater, container, false)

        fragmentBinding.exploreItem = MainMenuItem(
            getString(R.string.explore),
            getString(R.string.exploreDetail),
            AppCompatResources.getDrawable(requireContext(), R.drawable.explore)
        )

        fragmentBinding.planItem = MainMenuItem(
            getString(R.string.plan),
            getString(R.string.planDetail),
            AppCompatResources.getDrawable(requireContext(), R.drawable.plan)
        )

        fragmentBinding.rememberItem = MainMenuItem(
            getString(R.string.remember),
            getString(R.string.rememberDetail),
            AppCompatResources.getDrawable(requireContext(), R.drawable.remember)
        )

        fragmentBinding.lifecycleOwner = this

        binding = fragmentBinding

        setListeners()

        return fragmentBinding.root
    }

    private fun setListeners() {
        val binding = binding ?: return

        val exploreViews = listOf(
            binding.explore.mainTitle,
            binding.explore.mainSubtitle,
            binding.explore.imageView
        )
        val planViews =
            listOf(binding.plan.mainTitle, binding.plan.mainSubtitle, binding.plan.imageView)
        val rememberViews = listOf(
            binding.remember.mainTitle,
            binding.remember.mainSubtitle,
            binding.remember.imageView
        )

        exploreViews.forEach {
            it.setOnClickListener {
                navigateToExplore()
            }
        }

        planViews.forEach {
            it.setOnClickListener {
                navigateToPlan()
            }
        }

        rememberViews.forEach {
            it.setOnClickListener {
                navigateToRemember()
            }
        }
    }

    private fun navigateToExplore() {
        lifecycleScope.launch {
            val location = prefs.lastLocationFlow.first()

            val action = MainFragmentDirections.actionMainFragmentToExploreActivity(
                latitude = location.latitude,
                longitude = location.longitude,
                radius = location.radius,
            )

            findNavController().navigate(action)
        }
    }

    private fun navigateToPlan() {
        findNavController().navigate(MainFragmentDirections.actionMainFragmentToPlanActivity())
    }

    private fun navigateToRemember() {
        // TODO
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}
