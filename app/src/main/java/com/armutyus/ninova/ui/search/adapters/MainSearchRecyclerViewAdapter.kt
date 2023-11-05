package com.armutyus.ninova.ui.search.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.armutyus.ninova.R
import com.armutyus.ninova.constants.Constants
import com.armutyus.ninova.constants.Constants.GOOGLE_BOOK_TYPE
import com.armutyus.ninova.constants.Constants.LOCAL_BOOK_TYPE
import com.armutyus.ninova.model.googlebooksmodel.DataModel
import com.armutyus.ninova.ui.books.BooksViewModel
import com.armutyus.ninova.ui.search.MainSearchFragment
import com.bumptech.glide.RequestManager
import javax.inject.Inject
import javax.inject.Named

class MainSearchRecyclerViewAdapter @Inject constructor(
    private val glide: RequestManager
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    @Named(Constants.BOOK_DETAILS_INTENT)
    @Inject
    lateinit var bookDetailsIntent: Intent

    private lateinit var searchFragment: MainSearchFragment
    private lateinit var booksViewModel: BooksViewModel
    private val adapterData = mutableListOf<DataModel>()

    init {
        try {
            setHasStableIds(true)
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)

        return when (viewType) {
            GOOGLE_BOOK_TYPE -> GoogleBookRowViewHolder(
                layoutInflater.inflate(
                    R.layout.search_main_row,
                    parent,
                    false
                )
            )

            LOCAL_BOOK_TYPE -> LocalBookRowViewHolder(
                layoutInflater.inflate(
                    R.layout.search_local_book_row,
                    parent,
                    false
                )
            )

            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        return when (holder.itemViewType) {
            GOOGLE_BOOK_TYPE -> {
                holder as GoogleBookRowViewHolder
                holder.bindGoogleBook(
                    adapterData[position] as DataModel.GoogleBookItem,
                    glide,
                    searchFragment,
                    booksViewModel,
                    bookDetailsIntent
                )
            }

            LOCAL_BOOK_TYPE -> {
                holder as LocalBookRowViewHolder
                holder.bindLocalBook(
                    adapterData[position] as DataModel.LocalBook,
                    glide,
                    bookDetailsIntent
                )
            }

            else -> throw IllegalArgumentException("Unsupported type")
        }
    }

    override fun getItemViewType(position: Int) = when (adapterData[position]) {
        is DataModel.GoogleBookItem -> GOOGLE_BOOK_TYPE
        is DataModel.LocalBook -> LOCAL_BOOK_TYPE
    }

    override fun getItemId(position: Int): Long = adapterData[position].hashCode().toLong()

    override fun getItemCount(): Int {
        return adapterData.size
    }

    fun setFragment(fragment: MainSearchFragment) {
        this.searchFragment = fragment
    }

    fun setViewModel(booksViewModel: BooksViewModel) {
        this.booksViewModel = booksViewModel
    }

    fun setDataType(data: List<DataModel>) {
        adapterData.apply {
            clear()
            addAll(data)
        }
        notifyDataSetChanged()
    }
}