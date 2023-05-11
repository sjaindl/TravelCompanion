package com.sjaindl.travelcompanion.plan

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.sjaindl.travelcompanion.databinding.FragmentPlanBinding
import com.sjaindl.travelcompanion.explore.details.ExploreDetailActivity

class PlanFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        val binding = FragmentPlanBinding.inflate(inflater, container, false)

        binding.composeView.setContent {
            PlanHomeScreen(
                onShowDetails = { pinId ->
                    val intent = Intent(requireActivity(), ExploreDetailActivity::class.java).apply {
                        putExtra(ExploreDetailActivity.PIN_ID, pinId)
                    }

                    requireActivity().startActivity(intent)
                }
            )
        }

        return binding.root
    }
}
