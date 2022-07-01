package com.armutyus.ninova.ui.search.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.armutyus.ninova.R
import com.armutyus.ninova.model.Book
import com.armutyus.ninova.roomdb.LocalBook
import com.armutyus.ninova.ui.search.MainSearchFragment
import com.bumptech.glide.RequestManager
import javax.inject.Inject

class MainSearchRecyclerViewAdapter @Inject constructor(
    private val glide: RequestManager
) : RecyclerView.Adapter<MainSearchRecyclerViewAdapter.MainSearchViewHolder>() {

    private lateinit var searchFragment: MainSearchFragment

    class MainSearchViewHolder(view: View) : RecyclerView.ViewHolder(view)

    private val diffUtil = object : DiffUtil.ItemCallback<Books>() {
        override fun areItemsTheSame(oldItem: Books, newItem: Books): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Books, newItem: Books): Boolean {
            return oldItem == newItem
        }
    }

    private val recyclerListDiffer = AsyncListDiffer(this, diffUtil)

    var mainSearchBooksList: List<Book>
        get() = recyclerListDiffer.currentList
        set(value) = recyclerListDiffer.submitList(value)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainSearchViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.search_main_row, parent, false)

        return MainSearchViewHolder(view)
    }

    override fun onBindViewHolder(holder: MainSearchViewHolder, position: Int) {
        val bookCover = holder.itemView.findViewById<ImageView>(R.id.bookImage)
        val bookTitle = holder.itemView.findViewById<TextView>(R.id.bookTitleText)
        val bookAuthor = holder.itemView.findViewById<TextView>(R.id.bookAuthorText)
        val bookPages = holder.itemView.findViewById<TextView>(R.id.bookPageText)
        val bookReleaseDate = holder.itemView.findViewById<TextView>(R.id.bookReleaseDateText)
        val book = mainSearchBooksList[position]

        val addButton = holder.itemView.findViewById<ImageButton>(R.id.main_search_add_button)

        addButton?.setOnClickListener {
            searchFragment.onClick(
                LocalBook(
                    0,
                    book.bookTitle,
                    book.bookAuthor,
                    book.bookPages,
                    "",
                    "",
                    book.releaseDate
                )
            )
        }

        holder.itemView.apply {
            bookTitle.text = book.bookTitle
            bookAuthor.text = book.bookAuthor
            bookPages.text = book.bookPages
            bookReleaseDate.text = book.releaseDate
        }

    }

    override fun getItemCount(): Int {
        return mainSearchBooksList.size
    }

    fun setFragment(fragment: MainSearchFragment) {
        this.searchFragment = fragment
}