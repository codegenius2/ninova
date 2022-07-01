package com.armutyus.ninova.ui.books.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.armutyus.ninova.R
import com.armutyus.ninova.roomdb.LocalBook
import com.bumptech.glide.RequestManager
import javax.inject.Inject

class BooksRecyclerViewAdapter @Inject constructor(
    private val glide: RequestManager
) : RecyclerView.Adapter<BooksRecyclerViewAdapter.MainBooksViewHolder>() {

    class MainBooksViewHolder(view: View) : RecyclerView.ViewHolder(view)

    private val diffUtil = object : DiffUtil.ItemCallback<LocalBook>() {
        override fun areItemsTheSame(oldItem: LocalBook, newItem: LocalBook): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: LocalBook, newItem: LocalBook): Boolean {
            return oldItem == newItem
        }
    }

    private val recyclerListDiffer = AsyncListDiffer(this, diffUtil)

    var mainBooksList: List<LocalBook>
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

        holder.itemView.apply {
            bookTitle.text = book.bookTitle
            bookAuthor.text = book.bookAuthor
            bookPages.text = book.bookPages
            bookReleaseDate.text = book.releaseDate
        }

    }

    override fun getItemCount(): Int {
        return mainBooksList.size
    }

}