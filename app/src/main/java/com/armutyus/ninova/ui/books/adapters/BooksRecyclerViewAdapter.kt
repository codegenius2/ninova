package com.armutyus.ninova.ui.books.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.armutyus.ninova.R
import com.armutyus.ninova.constants.Cache.currentBook
import com.armutyus.ninova.constants.Cache.currentLocalBook
import com.armutyus.ninova.constants.Constants.BOOK_DETAILS_INTENT
import com.armutyus.ninova.constants.Constants.BOOK_TYPE_FOR_DETAILS
import com.armutyus.ninova.constants.Constants.LOCAL_BOOK_TYPE
import com.armutyus.ninova.model.DataModel
import com.bumptech.glide.RequestManager
import javax.inject.Inject
import javax.inject.Named

class BooksRecyclerViewAdapter @Inject constructor(
    private val glide: RequestManager
) : RecyclerView.Adapter<BooksRecyclerViewAdapter.MainBooksViewHolder>() {

    @Named(BOOK_DETAILS_INTENT)
    @Inject
    lateinit var bookDetailsIntent: Intent

    class MainBooksViewHolder(view: View) : RecyclerView.ViewHolder(view)

    private val diffUtil = object : DiffUtil.ItemCallback<DataModel.LocalBook>() {
        override fun areItemsTheSame(
            oldItem: DataModel.LocalBook,
            newItem: DataModel.LocalBook
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: DataModel.LocalBook,
            newItem: DataModel.LocalBook
        ): Boolean {
            return oldItem == newItem
        }
    }

    private val recyclerListDiffer = AsyncListDiffer(this, diffUtil)

    var mainBooksList: List<DataModel.LocalBook>
        get() = recyclerListDiffer.currentList
        set(value) = recyclerListDiffer.submitList(value)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainBooksViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.books_main_row, parent, false)

        return MainBooksViewHolder(view)
    }

    override fun onBindViewHolder(holder: MainBooksViewHolder, position: Int) {
        val bookCover = holder.itemView.findViewById<ImageView>(R.id.bookImage)
        val bookTitle = holder.itemView.findViewById<TextView>(R.id.bookTitleText)
        val bookAuthor = holder.itemView.findViewById<TextView>(R.id.bookAuthorText)
        val bookPages = holder.itemView.findViewById<TextView>(R.id.bookPageText)
        val bookReleaseDate = holder.itemView.findViewById<TextView>(R.id.bookReleaseDateText)
        val book = mainBooksList[position]

        holder.itemView.setOnClickListener {
            bookDetailsIntent.putExtra(BOOK_TYPE_FOR_DETAILS, LOCAL_BOOK_TYPE)
            currentLocalBook = book
            currentBook = null
            holder.itemView.context.startActivity(bookDetailsIntent)
        }

        holder.itemView.apply {
            glide.load(book.bookCoverSmallThumbnail).centerCrop().into(bookCover)
            bookTitle.text = book.bookTitle
            bookAuthor.text = book.bookAuthors?.joinToString(", ")
            bookPages.text = book.bookPages
            bookReleaseDate.text = book.bookPublishedDate
        }

    }

    override fun getItemCount(): Int {
        return mainBooksList.size
    }

}