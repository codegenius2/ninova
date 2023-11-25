package com.armutyus.ninova.ui.search.adapters

import android.content.Intent
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.armutyus.ninova.R
import com.armutyus.ninova.constants.Cache.currentGoogleBook
import com.armutyus.ninova.constants.Cache.currentLocalBook
import com.armutyus.ninova.constants.Cache.currentOpenLibBook
import com.armutyus.ninova.constants.Constants
import com.armutyus.ninova.model.googlebooksmodel.DataModel
import com.bumptech.glide.RequestManager

class LocalBookRowViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    fun bindLocalBook(book: DataModel.LocalBook, glide: RequestManager, bookDetailsIntent: Intent) {
        val bookCover = itemView.findViewById<ImageView>(R.id.bookImage)
        val bookTitle = itemView.findViewById<TextView>(R.id.bookTitleText)
        val bookAuthor = itemView.findViewById<TextView>(R.id.bookAuthorText)
        val bookPages = itemView.findViewById<TextView>(R.id.bookPageText)
        val bookReleaseDate = itemView.findViewById<TextView>(R.id.bookReleaseDateText)

        itemView.apply {
            glide.load(book.bookCoverSmallThumbnail).centerCrop().into(bookCover)
            bookTitle.text = book.bookTitle
            bookAuthor.text = book.bookAuthors?.joinToString(", ")
            bookPages.text = book.bookPages
            bookReleaseDate.text = book.bookPublishedDate
            setOnClickListener {
                bookDetailsIntent.putExtra(
                    Constants.BOOK_TYPE_FOR_DETAILS,
                    Constants.LOCAL_BOOK_TYPE
                )
                currentLocalBook = book
                currentGoogleBook = null
                currentOpenLibBook = null
                itemView.context.startActivity(bookDetailsIntent)
            }
        }
    }

}