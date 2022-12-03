package com.sjaindl.travelcompanion

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import com.sjaindl.travelcompanion.databinding.FragmentMainBinding

class MainFragment : Fragment() {
    private var binding: FragmentMainBinding? = null

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
            getString(R.string.plan), getString(R.string.planDetail), AppCompatResources.getDrawable(requireContext(), R.drawable.plan)
        )

        fragmentBinding.rememberItem = MainMenuItem(
            getString(R.string.remember),
            getString(R.string.rememberDetail),
            AppCompatResources.getDrawable(requireContext(), R.drawable.remember)
        )

        binding = fragmentBinding
        return fragmentBinding.root

    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}
