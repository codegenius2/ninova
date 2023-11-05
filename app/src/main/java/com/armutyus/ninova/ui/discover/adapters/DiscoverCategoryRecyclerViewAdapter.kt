package com.armutyus.ninova.ui.discover.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.armutyus.ninova.R
import com.armutyus.ninova.constants.Cache.currentOpenLibBook
import com.armutyus.ninova.constants.Constants
import com.armutyus.ninova.constants.Constants.BOOK_TYPE_FOR_DETAILS
import com.armutyus.ninova.constants.Constants.OPEN_LIB_BOOK_TYPE
import com.armutyus.ninova.model.openlibrarymodel.OpenLibraryWork
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import javax.inject.Inject
import javax.inject.Named

class DiscoverCategoryRecyclerViewAdapter @Inject constructor(
    private val glide: RequestManager
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    @Named(Constants.BOOK_DETAILS_INTENT)
    @Inject
    lateinit var bookDetailsIntent: Intent

    private val adapterData = mutableListOf<OpenLibraryWork>()

    class DiscoverCategoryViewHolder(view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.discover_category_book_item, parent, false)

        return DiscoverCategoryViewHolder(view)
    }

    override fun getItemCount(): Int {
        return adapterData.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val book = adapterData[position]
        val bookAuthor = holder.itemView.findViewById<TextView>(R.id.bookAuthorText)
        val bookCover = holder.itemView.findViewById<ImageView>(R.id.bookImage)
        val bookTitle = holder.itemView.findViewById<TextView>(R.id.bookTitleText)
        val bookPublishedYear = holder.itemView.findViewById<TextView>(R.id.bookReleaseDateText)
        val bookCoverUrl = "https://covers.openlibrary.org/b/id/${book.cover_id}-M.jpg"
        val bookAuthors = book.authors.map { it.name }

        bookTitle.isSelected = true

        holder.itemView.apply {
            glide.load(bookCoverUrl).centerCrop()
                .transition(DrawableTransitionOptions.withCrossFade()).into(bookCover)
            bookAuthor.text = bookAuthors.joinToString(", ")
            bookTitle.text = book.title
            bookPublishedYear.text = book.first_publish_year.toString()

            setOnClickListener {
                bookDetailsIntent.putExtra(
                    BOOK_TYPE_FOR_DETAILS,
                    OPEN_LIB_BOOK_TYPE
                )
                currentOpenLibBook = book
                holder.itemView.context.startActivity(bookDetailsIntent)
            }
        }
    }

    fun setData(data: List<OpenLibraryWork>) {
        adapterData.addAll(data)
        notifyDataSetChanged()
    }

    fun clearData() {
        adapterData.clear()
        notifyDataSetChanged()
    }
}