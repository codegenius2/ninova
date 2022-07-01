package com.armutyus.ninova.ui.shelves.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.armutyus.ninova.R
import com.armutyus.ninova.roomdb.entities.LocalShelf
import com.armutyus.ninova.ui.shelves.BookToShelfFragment
import com.bumptech.glide.RequestManager
import javax.inject.Inject

class BookToShelfRecyclerViewAdapter @Inject constructor(
    private val glide: RequestManager
) :
    RecyclerView.Adapter<BookToShelfRecyclerViewAdapter.BookToShelfViewHolder>() {

    private lateinit var bookToShelfFragment: BookToShelfFragment

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
            LayoutInflater.from(parent.context).inflate(R.layout.shelves_main_row, parent, false)

        return BookToShelfViewHolder(view)
    }

    override fun onBindViewHolder(holder: BookToShelfViewHolder, position: Int) {
        val shelfCover = holder.itemView.findViewById<ImageView>(R.id.shelfCoverImage)
        val shelfTitle = holder.itemView.findViewById<TextView>(R.id.shelfTitleText)
        val booksInShelf = holder.itemView.findViewById<TextView>(R.id.booksInShelfText)
        val shelfCreatedDate = holder.itemView.findViewById<TextView>(R.id.shelfCreatedDateText)
        val shelf = bookToShelfList[position]

        holder.itemView.apply {
            shelfTitle.text = shelf.shelfTitle
            shelfCreatedDate.text = shelf.createdAt
            booksInShelf.text = shelf.booksInShelf.toString()
        }

        holder.itemView.setOnClickListener {
            bookToShelfFragment.onClick(shelf)
        }
    }

    override fun getItemCount(): Int {
        return bookToShelfList.size
    }

    fun setFragment(fragment: BookToShelfFragment) {
        this.bookToShelfFragment = fragment
    }

}