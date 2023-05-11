package com.sjaindl.travelcompanion.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.sjaindl.travelcompanion.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        val binding = FragmentProfileBinding.inflate(inflater, container, false)

        binding.composeView.setContent {
            ProfileContainer(
                onClose = {
                    requireActivity().finish()
                }
            )
        }

        return binding.root
    }
}
