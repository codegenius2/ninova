package com.armutyus.ninova.ui.books

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.armutyus.ninova.R
import com.armutyus.ninova.constants.Response
import com.armutyus.ninova.databinding.FragmentBooksBinding
import com.armutyus.ninova.model.DataModel
import com.armutyus.ninova.ui.books.adapters.BooksRecyclerViewAdapter
import com.armutyus.ninova.ui.shelves.ShelvesViewModel
import com.google.android.material.snackbar.Snackbar
import javax.inject.Inject

class BooksFragment @Inject constructor(
    private val booksAdapter: BooksRecyclerViewAdapter
) : Fragment(R.layout.fragment_books) {

    private var fragmentBinding: FragmentBooksBinding? = null

    private val booksViewModel by activityViewModels<BooksViewModel>()
    private val shelvesViewModel by activityViewModels<ShelvesViewModel>()

    private val swipeCallBack = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            return true
        }

        override fun onChildDraw(
            c: Canvas,
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            dX: Float,
            dY: Float,
            actionState: Int,
            isCurrentlyActive: Boolean
        ) {

            val deleteIcon =
                AppCompatResources.getDrawable(requireContext(), R.drawable.ic_delete_account)
            val intrinsicWidth = deleteIcon!!.intrinsicWidth
            val intrinsicHeight = deleteIcon.intrinsicHeight
            val swipeBackground = ColorDrawable()
            val swipeBackgroundColor = R.color.md_theme_dark_errorContainer
            val deleteIconColor = R.color.md_theme_dark_onErrorContainer
            val clearPaint = Paint().apply { xfermode = PorterDuffXfermode(PorterDuff.Mode.DARKEN) }
            val itemView = viewHolder.itemView
            val itemHeight = itemView.bottom - itemView.top
            val isCanceled = dX == 0f || !isCurrentlyActive

            if (isCanceled) {
                c.drawRect(
                    itemView.right + dX,
                    itemView.top.toFloat() - 44,
                    itemView.right.toFloat(),
                    itemView.bottom.toFloat() - 44,
                    clearPaint
                )
                super.onChildDraw(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
                return
            }

            swipeBackground.color = resources.getColor(swipeBackgroundColor, context!!.theme)
            swipeBackground.setBounds(
                itemView.right + dX.toInt(),
                itemView.top - 44,
                itemView.right,
                itemView.bottom - 44
            )
            swipeBackground.draw(c)

            val iconMargin = (itemHeight - intrinsicHeight) / 2
            val iconTop = itemView.top - ((itemHeight / 2.5) - (intrinsicHeight * 3))
            val iconLeft = itemView.right - intrinsicWidth - (iconMargin / 2)
            val iconRight = itemView.right - (iconMargin / 3)
            val iconBottom = itemView.bottom - ((itemHeight * 0.7) - (intrinsicHeight))

            deleteIcon.setBounds(iconLeft, iconTop.toInt(), iconRight, iconBottom.toInt())
            deleteIcon.setTint(resources.getColor(deleteIconColor, context!!.theme))
            deleteIcon.draw(c)
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)

        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val position = viewHolder.layoutPosition
            val swipedBook = booksAdapter.mainBooksList[position]
            booksViewModel.deleteBook(swipedBook).invokeOnCompletion {
                Snackbar.make(requireView(), R.string.book_deleted, Snackbar.LENGTH_LONG)
                    .setAction(R.string.undo) {
                        booksViewModel.insertBook(swipedBook).invokeOnCompletion {
                            uploadBookToFirestore(swipedBook)
                            booksViewModel.loadBookList()
                        }
                    }.show()
                deleteBookFromFirestore(swipedBook.bookId)
                booksViewModel.loadBookList()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentBooksBinding.bind(view)
        fragmentBinding = binding

        val recyclerView = binding.mainBooksRecyclerView
        recyclerView.adapter = booksAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        ItemTouchHelper(swipeCallBack).attachToRecyclerView(recyclerView)

        shelvesViewModel.loadShelfWithBookList()
        observeBookList()
    }

    override fun onResume() {
        super.onResume()
        booksViewModel.loadBookList()
    }

    private fun observeBookList() {
        booksViewModel.localBookList.observe(viewLifecycleOwner) { localBookList ->
            if (localBookList.isEmpty()) {
                fragmentBinding?.linearLayoutBooksError?.visibility = View.VISIBLE
                fragmentBinding?.mainBooksRecyclerView?.visibility = View.GONE
            } else {
                fragmentBinding?.linearLayoutBooksError?.visibility = View.GONE
                fragmentBinding?.mainBooksRecyclerView?.visibility = View.VISIBLE
                booksAdapter.mainBooksList = localBookList
            }
        }
    }

    private fun deleteBookFromFirestore(bookId: String) {
        booksViewModel.deleteBookFromFirestore(bookId) { response ->
            when (response) {
                is Response.Loading ->
                    Log.i("bookDelete", "Deleting from firestore")
                is Response.Success ->
                    Log.i("bookDelete", "Deleted from firestore")
                is Response.Failure ->
                    Log.e("bookDelete", response.errorMessage)
            }
        }
    }

    private fun uploadBookToFirestore(localBook: DataModel.LocalBook) {
        booksViewModel.uploadBookToFirestore(localBook) { response ->
            when (response) {
                is Response.Loading ->
                    Log.i("bookUpload", "Uploading to firestore")
                is Response.Success ->
                    Log.i("bookUpload", "Uploaded to firestore")
                is Response.Failure ->
                    Log.e("bookUpload", response.errorMessage)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        fragmentBinding = null
    }
}