package com.armutyus.ninova.ui.shelves.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.armutyus.ninova.R
import com.armutyus.ninova.constants.Cache.currentBookIdExtra
import com.armutyus.ninova.constants.Response
import com.armutyus.ninova.roomdb.entities.BookShelfCrossRef
import com.armutyus.ninova.roomdb.entities.LocalShelf
import com.armutyus.ninova.ui.books.BooksViewModel
import com.armutyus.ninova.ui.shelves.ShelvesViewModel
import javax.inject.Inject

class BookToShelfRecyclerViewAdapter @Inject constructor(
) : RecyclerView.Adapter<BookToShelfRecyclerViewAdapter.BookToShelfViewHolder>() {

    private lateinit var shelvesViewModel: ShelvesViewModel
    private lateinit var booksViewModel: BooksViewModel

    class BookToShelfViewHolder(view: View) : RecyclerView.ViewHolder(view)

    private val diffUtil = object : DiffUtil.ItemCallback<LocalShelf>() {
        override fun areItemsTheSame(oldItem: LocalShelf, newItem: LocalShelf): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: LocalShelf, newItem: LocalShelf): Boolean {
            return oldItem == newItem
        }
    }

    private val recyclerListDiffer = AsyncListDiffer(this, diffUtil)

    var bookToShelfList: List<LocalShelf>
        get() = recyclerListDiffer.currentList
        set(value) = recyclerListDiffer.submitList(value)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookToShelfViewHolder {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.add_book_to_shelf_row, parent, false)

        return BookToShelfViewHolder(view)
    }

    override fun onBindViewHolder(holder: BookToShelfViewHolder, position: Int) {
        val shelfRow = holder.itemView.findViewById<CheckBox>(R.id.bottomSheetShelfCheckBox)
        val shelf = bookToShelfList[position]

        holder.itemView.apply {
            shelfRow.text = shelf.shelfTitle
            val checkedShelfList =
                booksViewModel.bookWithShelvesList.value?.firstOrNull { it.shelfList.contains(shelf) }?.shelfList
            shelfRow.isChecked = checkedShelfList != null && checkedShelfList.isNotEmpty()
        }

        shelfRow.setOnCheckedChangeListener { _, isChecked ->
            val crossRef = BookShelfCrossRef(currentBookIdExtra!!, shelf.shelfId)
            if (isChecked) {
                shelvesViewModel.insertBookShelfCrossRef(crossRef).invokeOnCompletion {
                    uploadCrossRefToFirestore(crossRef)
                    booksViewModel.loadBookWithShelves(currentBookIdExtra!!)
                    shelvesViewModel.loadShelfWithBookList()
                }
            } else {
                shelvesViewModel.deleteBookShelfCrossRef(crossRef).invokeOnCompletion {
                    deleteCrossRefFromFirestore(crossRef.bookId + crossRef.shelfId)
                    booksViewModel.loadBookWithShelves(currentBookIdExtra!!)
                    shelvesViewModel.loadShelfWithBookList()
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return bookToShelfList.size
    }

    private fun deleteCrossRefFromFirestore(crossRefId: String) {
        shelvesViewModel.deleteCrossRefFromFirestore(crossRefId) { response ->
            when (response) {
                is Response.Loading ->
                    Log.i("crossRefDelete", "Deleting from firestore")
                is Response.Success ->
                    Log.i("crossRefDelete", "Deleted from firestore")
                is Response.Failure ->
                    Log.e("crossRefDelete", response.errorMessage)
            }
        }
    }

    private fun uploadCrossRefToFirestore(crossRef: BookShelfCrossRef) {
        shelvesViewModel.uploadCrossRefToFirestore(crossRef) { response ->
            when (response) {
                is Response.Loading ->
                    Log.i("crossRefUpload", "Uploading to firestore")
                is Response.Success ->
                    Log.i("crossRefUpload", "Uploaded to firestore")
                is Response.Failure ->
                    Log.e("crossRefUpload", response.errorMessage)
            }
        }
    }

    fun setViewModels(shelvesViewModel: ShelvesViewModel, booksViewModel: BooksViewModel) {
        this.shelvesViewModel = shelvesViewModel
        this.booksViewModel = booksViewModel
    }

}