package com.armutyus.ninova.ui.shelves

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.armutyus.ninova.R
import com.armutyus.ninova.constants.Constants.DETAILS_STRING_EXTRA
import com.armutyus.ninova.constants.Constants.FROM_DETAILS_ACTIVITY
import com.armutyus.ninova.databinding.AddNewShelfBottomSheetBinding
import com.armutyus.ninova.databinding.FragmentBookToShelfBinding
import com.armutyus.ninova.roomdb.entities.BookShelfCrossRef
import com.armutyus.ninova.roomdb.entities.LocalShelf
import com.armutyus.ninova.ui.shelves.adapters.BookToShelfRecyclerViewAdapter
import com.armutyus.ninova.ui.shelves.listeners.OnShelfItemClickListener
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class BookToShelfFragment @Inject constructor(
    private val bookToShelfAdapter: BookToShelfRecyclerViewAdapter
) : Fragment(R.layout.fragment_book_to_shelf), OnShelfItemClickListener,
    SearchView.OnQueryTextListener {

    private var fragmentBinding: FragmentBookToShelfBinding? = null
    private lateinit var shelvesViewModel: ShelvesViewModel
    private lateinit var bottomSheetBinding: AddNewShelfBottomSheetBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentBookToShelfBinding.bind(view)
        fragmentBinding = binding
        shelvesViewModel = ViewModelProvider(requireActivity())[ShelvesViewModel::class.java]

        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menu.clear()
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return false
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        val searchView = binding.bookToShelfSearch
        searchView.setOnQueryTextListener(this)
        searchView.setIconifiedByDefault(false)

        val recyclerView = binding.addShelvesRecyclerView
        recyclerView.adapter = bookToShelfAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        bookToShelfAdapter.setFragment(this)
        bookToShelfAdapter.setViewModel(shelvesViewModel)

        binding.addShelfButton.setOnClickListener {
            showAddShelfDialog()
        }

        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner) {
            when (activity?.intent?.getStringExtra(DETAILS_STRING_EXTRA)) {
                FROM_DETAILS_ACTIVITY -> {
                    activity?.finish()
                }
            }
        }

        shelvesViewModel.getShelfWithBookList()
        observeShelfList()
    }

    override fun onResume() {
        super.onResume()
        shelvesViewModel.getShelfList()
    }

    private fun showAddShelfDialog() {
        val dialog = BottomSheetDialog(requireContext())
        bottomSheetBinding = AddNewShelfBottomSheetBinding.inflate(layoutInflater)
        dialog.setContentView(bottomSheetBinding.root)
        dialog.show()

        val addShelfButton = dialog.findViewById<MaterialButton>(R.id.addShelfButton)
        addShelfButton?.setOnClickListener {
            val shelfTitle = bottomSheetBinding.shelfTitleText.text.toString()

            if (shelfTitle.isEmpty()) {
                Toast.makeText(requireContext(), "Title cannot be empty!", Toast.LENGTH_LONG).show()
            } else {
                val timeStamp = Date().time
                val formattedDate = SimpleDateFormat("dd-MM-yyyy").format(timeStamp)
                shelvesViewModel.insertShelf(
                    LocalShelf(
                        0,
                        shelfTitle,
                        formattedDate,
                        "",
                    )
                )
                dialog.hide()
            }

        }

    }

    private fun observeShelfList() {
        shelvesViewModel.currentShelfList.observe(viewLifecycleOwner) {
            bookToShelfAdapter.bookToShelfList = it
            setVisibilities(it)
        }

        shelvesViewModel.shelfList.observe(viewLifecycleOwner) {
            shelvesViewModel.setCurrentList(it?.toList() ?: listOf())
        }

        shelvesViewModel.searchShelvesList.observe(viewLifecycleOwner) {
            shelvesViewModel.setCurrentList(it?.toList() ?: listOf())
        }

    }

    override fun onClick(localShelf: LocalShelf) {
        val args: BookToShelfFragmentArgs by navArgs()
        val bookId = args.currentBookId
        val shelfId = localShelf.shelfId
        val crossRef = BookShelfCrossRef(bookId, shelfId)
        shelvesViewModel.insertBookShelfCrossRef(crossRef)

        if (activity?.intent?.getStringExtra(DETAILS_STRING_EXTRA) == FROM_DETAILS_ACTIVITY) {
            activity?.finish()
        } else {
            findNavController().popBackStack()
        }
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }

    override fun onQueryTextChange(searchQuery: String?): Boolean {
        if (searchQuery?.length!! > 0) {
            fragmentBinding?.progressBar?.visibility = View.VISIBLE
            fragmentBinding?.addShelvesRecyclerView?.visibility = View.GONE
            shelvesViewModel.searchShelves("%$searchQuery%")

        } else if (searchQuery.isNullOrBlank()) {
            shelvesViewModel.getShelfList()
        }

        return true
    }

    private fun setVisibilities(shelfList: List<LocalShelf>) {
        if (shelfList.isEmpty()) {
            fragmentBinding?.linearLayoutShelvesError?.visibility = View.VISIBLE
            fragmentBinding?.progressBar?.visibility = View.GONE
            fragmentBinding?.addShelvesRecyclerView?.visibility = View.GONE
        } else {
            fragmentBinding?.linearLayoutShelvesError?.visibility = View.GONE
            fragmentBinding?.progressBar?.visibility = View.GONE
            fragmentBinding?.addShelvesRecyclerView?.visibility = View.VISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        fragmentBinding = null
    }

}