package com.armutyus.ninova.ui.search

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.armutyus.ninova.R
import com.armutyus.ninova.databinding.FragmentSearchApiBinding
import com.armutyus.ninova.ui.search.adapters.SearchApiRecyclerViewAdapter
import com.armutyus.ninova.ui.search.viewmodels.MainSearchViewModel
import javax.inject.Inject

class SearchApiFragment @Inject constructor(
    private val apiSearchAdapter: SearchApiRecyclerViewAdapter
) : Fragment(R.layout.fragment_search_api) {

    private var fragmentBinding: FragmentSearchApiBinding? = null
    private lateinit var searchApiViewModel: MainSearchViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentSearchApiBinding.bind(view)
        fragmentBinding = binding
        searchApiViewModel = ViewModelProvider(requireActivity())[MainSearchViewModel::class.java]

        binding.searchApiRecyclerView.adapter = apiSearchAdapter
        binding.searchApiRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.searchApiRecyclerView.visibility = View.VISIBLE

        observeBookList()

    }

    private fun observeBookList() {

        searchApiViewModel.fakeBooksApiList.observe(viewLifecycleOwner) {
            val searchedList = it?.toList()
            apiSearchAdapter.searchApiBooksList = searchedList!!

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