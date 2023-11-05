package com.armutyus.ninova.ui.discover

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.armutyus.ninova.R
import com.armutyus.ninova.constants.Response
import com.armutyus.ninova.constants.Util.Companion.fadeIn
import com.armutyus.ninova.databinding.FragmentDiscoverBinding
import com.armutyus.ninova.ui.discover.adapters.DiscoverRecyclerViewAdapter
import javax.inject.Inject

class DiscoverFragment @Inject constructor(
    private val discoverAdapter: DiscoverRecyclerViewAdapter
) : Fragment(R.layout.fragment_discover) {

    private var fragmentBinding: FragmentDiscoverBinding? = null

    private val discoverViewModel by activityViewModels<DiscoverViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentDiscoverBinding.bind(view)
        fragmentBinding = binding

        binding.appNameTextView.fadeIn(1000)

        val recyclerView = binding.discoverRecyclerView
        recyclerView.adapter = discoverAdapter
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 4)
        recyclerView.visibility = View.VISIBLE

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (!recyclerView.canScrollVertically(-1)) {
                    (requireActivity() as AppCompatActivity).supportActionBar?.show()
                } else {
                    (requireActivity() as AppCompatActivity).supportActionBar?.hide()
                }
            }
        })

        runObservers()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        fragmentBinding = null
    }

    private fun runObservers() {
        discoverViewModel.bookCoverFromApiResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Response.Loading -> {
                    fragmentBinding?.progressBar?.visibility = View.VISIBLE
                }

                is Response.Failure -> {
                    Log.i("OpenLibraryError", response.errorMessage)
                }

                is Response.Success -> {
                    fragmentBinding?.progressBar?.visibility = View.GONE
                }
            }
        }
        discoverViewModel.categoryCoverId.observe(viewLifecycleOwner) {
            it?.let {
                discoverAdapter.updateData(it)
            }
        }
    }

}