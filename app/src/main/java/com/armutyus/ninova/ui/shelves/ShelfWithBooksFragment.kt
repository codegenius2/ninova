package com.armutyus.ninova.ui.shelves

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.armutyus.ninova.R
import com.armutyus.ninova.constants.Constants.currentShelf
import com.armutyus.ninova.databinding.FragmentShelfWithBooksBinding
import com.armutyus.ninova.roomdb.entities.BookShelfCrossRef
import com.armutyus.ninova.ui.books.adapters.BooksRecyclerViewAdapter
import com.google.android.material.snackbar.Snackbar
import javax.inject.Inject

class ShelfWithBooksFragment @Inject constructor(
    private val booksAdapter: BooksRecyclerViewAdapter
) : Fragment(R.layout.fragment_shelf_with_books) {

    private var fragmentBinding: FragmentShelfWithBooksBinding? = null
    private lateinit var shelvesViewModel: ShelvesViewModel
    private var currentShelfId = 0
    private val args: ShelfWithBooksFragmentArgs by navArgs()
    private val swipeCallBack = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            return true
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val layoutPosition = viewHolder.layoutPosition
            val swipedBook = booksAdapter.mainBooksList[layoutPosition]
            val crossRef = BookShelfCrossRef(swipedBook.bookId, currentShelfId)
            shelvesViewModel.deleteBookShelfCrossRef(crossRef).invokeOnCompletion {
                Snackbar.make(requireView(), "Book deleted from this shelf", Snackbar.LENGTH_LONG)
                    .setAction("UNDO") {
                        shelvesViewModel.insertBookShelfCrossRef(crossRef)
                    }.show()
            }
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentShelfWithBooksBinding.bind(view)
        fragmentBinding = binding
        shelvesViewModel = ViewModelProvider(requireActivity())[ShelvesViewModel::class.java]

        val recyclerView = binding.shelfWithBooksRecyclerView
        recyclerView.adapter = booksAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        ItemTouchHelper(swipeCallBack).attachToRecyclerView(recyclerView)

        currentShelfId = args.currentShelfId

        observeBookList()

    }

    override fun onResume() {
        super.onResume()
        shelvesViewModel.getShelfWithBookList()
    }

    private fun observeBookList() {
        shelvesViewModel.shelfWithBooksList.observe(viewLifecycleOwner) { booksOfShelfList ->
            val currentBookList = booksOfShelfList.find { it.shelf.shelfId == currentShelfId }?.book
            currentBookList?.let {
                if (it.isEmpty()) {
                    fragmentBinding?.shelfWithBooksRecyclerView?.visibility = View.GONE
                    fragmentBinding?.linearLayoutShelfWithBooksError?.visibility = View.VISIBLE

                } else {
                    fragmentBinding?.linearLayoutShelfWithBooksError?.visibility = View.GONE
                    fragmentBinding?.shelfWithBooksRecyclerView?.visibility = View.VISIBLE
                    booksAdapter.mainBooksList = it
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        fragmentBinding = null
    }

}