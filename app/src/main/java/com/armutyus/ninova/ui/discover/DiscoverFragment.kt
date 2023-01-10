package com.armutyus.ninova.ui.discover

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.armutyus.ninova.R
import com.armutyus.ninova.constants.Util.Companion.fadeIn
import com.armutyus.ninova.databinding.FragmentDiscoverBinding
import javax.inject.Inject

class DiscoverFragment @Inject constructor(

) : Fragment(R.layout.fragment_discover) {
    private var fragmentBinding: FragmentDiscoverBinding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentDiscoverBinding.bind(view)
        fragmentBinding = binding

        binding.appNameTextView.fadeIn(1000)
        binding.discoverSearchButton.setOnClickListener {
            findNavController().navigate(R.id.action_main_to_search)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        fragmentBinding = null
    }
}