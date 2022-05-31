package com.armutyus.ninova.ui.books

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.armutyus.ninova.R
import com.armutyus.ninova.databinding.FragmentBooksBinding

class BooksFragment : Fragment(R.layout.fragment_books) {

    private var fragmentBinding: FragmentBooksBinding? = null
    private lateinit var booksViewModel: BooksViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentBooksBinding.bind(view)
        fragmentBinding = binding
        booksViewModel = ViewModelProvider(requireActivity())[BooksViewModel::class.java]

        binding.animationView.visibility = View.VISIBLE

    }

    override fun onDestroyView() {
        super.onDestroyView()
        fragmentBinding = null
    }
}