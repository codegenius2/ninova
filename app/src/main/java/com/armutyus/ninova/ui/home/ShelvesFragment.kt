package com.armutyus.ninova.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.armutyus.ninova.R
import com.armutyus.ninova.databinding.FragmentShelvesBinding

class ShelvesFragment : Fragment() {

    private var _binding: FragmentShelvesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val shelvesViewModel =
            ViewModelProvider(this)[ShelvesViewModel::class.java]

        _binding = FragmentShelvesBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.homeScreenBooksButton.setOnClickListener {
            findNavController().navigate(R.id.action_shelves_to_books)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}