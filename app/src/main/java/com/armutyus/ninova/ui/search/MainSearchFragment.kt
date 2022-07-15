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
import com.armutyus.ninova.model.Book
import com.armutyus.ninova.roomdb.entities.LocalBook
import com.armutyus.ninova.ui.books.BooksViewModel
import com.armutyus.ninova.ui.search.adapters.MainSearchRecyclerViewAdapter
import com.armutyus.ninova.ui.search.listeners.OnBookAddButtonClickListener
import javax.inject.Inject

class MainSearchFragment @Inject constructor(
    private val searchFragmentAdapter: MainSearchRecyclerViewAdapter
) : Fragment(R.layout.fragment_main_search), SearchView.OnQueryTextListener,
    OnBookAddButtonClickListener {

    private var fragmentBinding: FragmentMainSearchBinding? = null
    private val binding get() = fragmentBinding
    private lateinit var isSearchActive: SharedPreferences
    private lateinit var mainSearchViewModel: MainSearchViewModel
    private lateinit var booksViewModel: BooksViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isSearchActive = requireActivity().getPreferences(Context.MODE_PRIVATE) ?: return
        mainSearchViewModel = ViewModelProvider(requireActivity())[MainSearchViewModel::class.java]
        booksViewModel = ViewModelProvider(requireActivity())[BooksViewModel::class.java]
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
        recyclerView?.adapter = searchFragmentAdapter
        recyclerView?.layoutManager = LinearLayoutManager(requireContext())
        recyclerView?.visibility = View.VISIBLE
        searchFragmentAdapter.setFragment(this)
        searchFragmentAdapter.setViewModel(booksViewModel)

        val toggleButtonGroup = binding?.searchButtonToggleGroup
        toggleButtonGroup?.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                when (checkedId) {
                    R.id.localSearchButton -> {
                        /*val list = mainSearchViewModel.fakeBooksArchiveList.value ?: listOf()
                        mainSearchViewModel.setCurrentList(list)*/
                    }
                    R.id.apiSearchButton -> {
                        val list = mainSearchViewModel.fakeBooksApiList.value ?: listOf()
                        mainSearchViewModel.setCurrentList(list)
                    }
                }
            }
        }

        runObservers()

        return binding?.root
    }

    override fun onResume() {
        super.onResume()
        booksViewModel.getBookList()
        mainSearchViewModel.getBooksList()
        setVisibilitiesForSearchQueryNull()
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }

    override fun onQueryTextChange(searchQuery: String?): Boolean {
        if (searchQuery?.length!! > 0) {
            binding?.progressBar?.visibility = View.VISIBLE
            binding?.mainSearchRecyclerView?.visibility = View.GONE
            binding?.mainSearchBooksTitle?.visibility = View.GONE

            mainSearchViewModel.getBooksApiList(searchQuery)
            //mainSearchViewModel.searchLocalBooks(searchQuery)

            val toggleButtonGroup = binding?.searchButtonToggleGroup
            toggleButtonGroup?.visibility = View.VISIBLE

        } else if (searchQuery.isNullOrBlank()) {
            mainSearchViewModel.getBooksList()
            setVisibilitiesForSearchQueryNull()
        }

        return true
    }

    private fun runObservers() {
        val toggleButtonGroup = binding?.searchButtonToggleGroup

        mainSearchViewModel.currentList.observe(viewLifecycleOwner) {
            searchFragmentAdapter.mainSearchBooksList = it
            setVisibilities(it)
        }

        /*mainSearchViewModel.searchLocalBookList.observe(viewLifecycleOwner) {
            if (toggleButtonGroup?.checkedButtonId != R.id.localSearchButton) return@observe
            mainSearchViewModel.setCurrentList(it?.toList() ?: listOf())
        }*/

        mainSearchViewModel.fakeBooksApiList.observe(viewLifecycleOwner) {
            if (toggleButtonGroup?.checkedButtonId != R.id.apiSearchButton) return@observe
            mainSearchViewModel.setCurrentList(it?.toList() ?: listOf())
        }

        mainSearchViewModel.fakeBooksList.observe(viewLifecycleOwner) {
            mainSearchViewModel.setCurrentList(it?.toList() ?: listOf())
        }
    }

    private fun setVisibilities(bookList: List<Book>) {
        if (bookList.isEmpty()) {
            binding?.linearLayoutSearchError?.visibility = View.VISIBLE
            binding?.progressBar?.visibility = View.GONE
            binding?.mainSearchRecyclerView?.visibility = View.GONE
            binding?.mainSearchBooksTitle?.visibility = View.GONE
        } else {
            binding?.linearLayoutSearchError?.visibility = View.GONE
            binding?.progressBar?.visibility = View.GONE
            binding?.mainSearchBooksTitle?.visibility = View.GONE
            binding?.mainSearchRecyclerView?.visibility = View.VISIBLE
        }
    }

    private fun setVisibilitiesForSearchQueryNull() {
        binding?.mainSearchRecyclerView?.visibility = View.VISIBLE
        binding?.mainSearchBooksTitle?.visibility = View.VISIBLE
        binding?.searchButtonToggleGroup?.visibility = View.GONE
        binding?.linearLayoutSearchError?.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        fragmentBinding = null
    }

    override fun onClick(localBook: LocalBook) {
        mainSearchViewModel.insertBook(localBook)
    }

}