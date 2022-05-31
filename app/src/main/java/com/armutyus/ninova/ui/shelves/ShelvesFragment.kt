package com.armutyus.ninova.ui.shelves

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.armutyus.ninova.R
import com.armutyus.ninova.databinding.FragmentShelvesBinding


class ShelvesFragment : Fragment(R.layout.fragment_shelves) {

    private var fragmentBinding: FragmentShelvesBinding? = null
    private lateinit var shelvesViewModel: ShelvesViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentShelvesBinding.bind(view)
        fragmentBinding = binding
        shelvesViewModel = ViewModelProvider(requireActivity())[ShelvesViewModel::class.java]

        binding.animationView.visibility = View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        fragmentBinding = null
    }
}