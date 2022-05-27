package com.armutyus.ninova.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.armutyus.ninova.R
import com.armutyus.ninova.databinding.FragmentBooksBinding

class BooksFragment : Fragment() {

    private var _binding: FragmentBooksBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this)[BooksViewModel::class.java]

        _binding = FragmentBooksBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.homeScreenShelvesButton.setOnClickListener {
            findNavController().navigate(R.id.action_books_to_shelves)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}