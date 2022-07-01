package com.armutyus.ninova.ui.search

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.armutyus.ninova.R
import com.armutyus.ninova.databinding.FragmentSearchArchiveBinding
import com.armutyus.ninova.ui.search.adapters.SearchArchiveRecyclerViewAdapter
import com.armutyus.ninova.ui.search.viewmodels.MainSearchViewModel
import javax.inject.Inject

class SearchArchiveFragment @Inject constructor(
    private val searchArchiveAdapter: SearchArchiveRecyclerViewAdapter
) : Fragment(R.layout.fragment_search_archive) {

    private var fragmentBinding: FragmentSearchArchiveBinding? = null
    private lateinit var searchArchiveViewModel: MainSearchViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentSearchArchiveBinding.bind(view)
        fragmentBinding = binding
        searchArchiveViewModel =
            ViewModelProvider(requireActivity())[MainSearchViewModel::class.java]

        binding.searchArchiveRecyclerView.adapter = searchArchiveAdapter
        binding.searchArchiveRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.searchArchiveRecyclerView.visibility = View.VISIBLE

        observeBookList()

    }

    private fun observeBookList() {

        searchArchiveViewModel.fakeBooksArchiveList.observe(viewLifecycleOwner) {
            val searchedList = it?.toList()
            searchArchiveAdapter.searchArchiveBooksList = searchedList!!

            if (searchedList.isEmpty()) {
                fragmentBinding?.linearLayoutSearchError?.visibility = View.VISIBLE
            } else {
                fragmentBinding?.linearLayoutSearchError?.visibility = View.GONE
            }

        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        fragmentBinding = null
    }
}