package com.armutyus.ninova.ui.search

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.armutyus.ninova.R
import com.armutyus.ninova.databinding.FragmentMainSearchBinding
import com.armutyus.ninova.ui.search.adapters.MainSearchRecyclerViewAdapter
import com.armutyus.ninova.ui.search.adapters.MainSearchViewPagerAdapter
import com.armutyus.ninova.ui.search.adapters.SearchApiRecyclerViewAdapter
import com.armutyus.ninova.ui.search.adapters.SearchArchiveRecyclerViewAdapter
import com.armutyus.ninova.ui.search.viewmodels.MainSearchViewModel
import com.google.android.material.tabs.TabLayoutMediator
import javax.inject.Inject

class MainSearchFragment @Inject constructor(
    private val recyclerViewAdapter: MainSearchRecyclerViewAdapter,
    private val archiveAdapter: SearchArchiveRecyclerViewAdapter,
    private val apiAdapter: SearchApiRecyclerViewAdapter
) : Fragment(R.layout.fragment_main_search), SearchView.OnQueryTextListener {

    private var fragmentBinding: FragmentMainSearchBinding? = null
    private val binding get() = fragmentBinding
    private lateinit var isSearchActive: SharedPreferences
    private lateinit var mainSearchViewModel: MainSearchViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isSearchActive = requireActivity().getPreferences(Context.MODE_PRIVATE) ?: return
        mainSearchViewModel = ViewModelProvider(requireActivity())[MainSearchViewModel::class.java]

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentBinding = FragmentMainSearchBinding.inflate(inflater, container, false)
        val searchView = binding?.mainSearch
        searchView?.setOnQueryTextListener(this)
        searchView?.setIconifiedByDefault(false)

        val recyclerView = binding?.mainSearchRecyclerView
        recyclerView?.adapter = recyclerViewAdapter
        recyclerView?.layoutManager = LinearLayoutManager(requireContext())
        recyclerView?.visibility = View.VISIBLE

        return binding?.root
    }

    override fun onResume() {
        super.onResume()
        mainSearchViewModel.getBooksList()
        getFakeBooksList()
    }

    private fun showChildSearchFragments() {
        binding?.mainSearchRecyclerView?.visibility = View.GONE
        binding?.mainSearchBooksTitle?.visibility = View.GONE
        binding?.itemDivider?.visibility = View.GONE

        val fragments: ArrayList<Fragment> = arrayListOf(
            SearchArchiveFragment(archiveAdapter),
            SearchApiFragment(apiAdapter)
        )

        val tabLayout = binding?.mainSearchTabLayout
        val viewPager = binding?.mainSearchViewPager
        tabLayout?.visibility = View.VISIBLE
        viewPager?.visibility = View.VISIBLE
        val vpAdapter = MainSearchViewPagerAdapter(childFragmentManager, lifecycle, fragments)
        viewPager?.adapter = vpAdapter

        if (tabLayout != null && viewPager != null) {
            TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                when (position) {
                    0 -> {
                        tab.text = "YOUR LIBRARY"
                    }
                    1 -> {
                        tab.text = "FROM NINOVA"
                    }
                }
            }.attach()
        }
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }

    override fun onQueryTextChange(searchQuery: String?): Boolean {
        if (searchQuery?.length!! > 0) {

            mainSearchViewModel.getBooksArchiveList(searchQuery)
            mainSearchViewModel.getBooksApiList(searchQuery)

            showChildSearchFragments()

        } else if (searchQuery.isNullOrBlank()) {

            binding?.mainSearchRecyclerView?.visibility = View.VISIBLE
            binding?.mainSearchBooksTitle?.visibility = View.VISIBLE
            binding?.itemDivider?.visibility = View.VISIBLE
            binding?.mainSearchTabLayout?.visibility = View.GONE
            binding?.mainSearchViewPager?.visibility = View.GONE

        }

        return true
    }

    private fun getFakeBooksList() {

        mainSearchViewModel.fakeBooksList.observe(viewLifecycleOwner) {
            val newBooksList = it.toList()
            recyclerViewAdapter.mainSearchBooksList = newBooksList
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        fragmentBinding = null
    }

}