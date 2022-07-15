package com.armutyus.ninova.ui.books

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.armutyus.ninova.constants.Constants.DETAILS_INT_EXTRA
import com.armutyus.ninova.constants.Constants.DETAILS_STRING_EXTRA
import com.armutyus.ninova.constants.Constants.FROM_DETAILS_ACTIVITY
import com.armutyus.ninova.constants.Constants.FROM_DETAILS_TO_NOTES_EXTRA
import com.armutyus.ninova.constants.Constants.MAIN_INTENT
import com.armutyus.ninova.constants.Constants.currentBook
import com.armutyus.ninova.databinding.ActivityBookDetailsBinding
import com.bumptech.glide.RequestManager
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import javax.inject.Named

@AndroidEntryPoint
class BookDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBookDetailsBinding
    private lateinit var tabLayout: TabLayout
    private val viewModel by viewModels<BooksViewModel>()

    @Inject
    lateinit var glide: RequestManager

    @Named(MAIN_INTENT)
    @Inject
    lateinit var mainIntent: Intent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = currentBook?.bookTitle

        tabLayout = binding.bookDetailTabLayout
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {

            override fun onTabSelected(tab: TabLayout.Tab?) {
                setVisibilities(tab)
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                setVisibilities(tab)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                setVisibilities(tab)
            }
        })

        binding.shelvesOfBooks.setOnClickListener {
            goToBookToShelfFragment()
        }

        binding.bookDetailUserNotes.setOnClickListener {
            goToUserBookNotesFragment()
        }

        setupBookInfo()

    }

    override fun onResume() {
        super.onResume()
        viewModel.getBookWithShelves(currentBook!!.bookId)
        observeBookDetailsChanges()
    }

    private fun goToBookToShelfFragment() {
        val currentBookId = currentBook!!.bookId
        mainIntent.putExtra(DETAILS_INT_EXTRA, currentBookId)
        mainIntent.putExtra(DETAILS_STRING_EXTRA, FROM_DETAILS_ACTIVITY)
        startActivity(mainIntent)
    }

    private fun goToUserBookNotesFragment() {
        mainIntent.putExtra(DETAILS_INT_EXTRA, FROM_DETAILS_TO_NOTES_EXTRA)
        mainIntent.putExtra(DETAILS_STRING_EXTRA, FROM_DETAILS_ACTIVITY)
        startActivity(mainIntent)
    }

    private var currentShelvesList = mutableListOf<String?>()

    private fun observeBookDetailsChanges() {
        viewModel.bookWithShelvesList.observe(this) { shelvesOfBook ->
            shelvesOfBook.forEach { bookWithShelves ->
                val shelfTitleList = bookWithShelves.shelf.map { it.shelfTitle }.toList()
                currentShelvesList.removeAll(shelfTitleList)
                currentShelvesList.addAll(shelfTitleList)
            }
            binding.shelvesOfBooks.text = currentShelvesList.joinToString(", ")
        }
        binding.bookDetailUserNotes.text = currentBook!!.bookNotes
    }

    private fun setVisibilities(tab: TabLayout.Tab?) {
        when (tab?.text) {
            "NOTES" -> {
                binding.bookDetailNotesLinearLayout.visibility = View.VISIBLE
                binding.bookDetailInfoLinearLayout.visibility = View.GONE
                binding.linearLayoutDetailsError.visibility = View.GONE
            }
            "INFO" -> {
                binding.bookDetailNotesLinearLayout.visibility = View.GONE
                binding.bookDetailInfoLinearLayout.visibility = View.VISIBLE
                binding.linearLayoutDetailsError.visibility = View.GONE
            }
            else -> {
                binding.bookDetailNotesLinearLayout.visibility = View.VISIBLE
                binding.bookDetailInfoLinearLayout.visibility = View.GONE
                binding.linearLayoutDetailsError.visibility = View.GONE
            }

        }

    }

    private fun setupBookInfo() {
        if (currentBook == null) {
            binding.linearLayoutDetailsError.visibility = View.VISIBLE
            binding.bookDetailNotesLinearLayout.visibility = View.GONE
            binding.bookDetailInfoLinearLayout.visibility = View.GONE
        } else {
            val bookImage = binding.bookCoverImageView
            glide.load(currentBook!!.bookCoverUrl).centerCrop().into(bookImage)
            binding.bookDetailTitleText.text = currentBook!!.bookTitle
            binding.bookDetailSubTitleText.text = currentBook!!.bookSubtitle
            binding.bookDetailAuthorsText.text = currentBook!!.bookAuthors!!.joinToString(", ")
            binding.bookDetailPagesNumber.text = currentBook!!.bookPages
            binding.bookDetailCategories.text = currentBook!!.bookCategories!!.joinToString(", ")
            binding.bookDetailPublisher.text = currentBook!!.bookPublisher
            binding.bookDetailPublishDate.text = currentBook!!.bookPublishedDate
            binding.bookDetailDescription.text = currentBook!!.bookDescription
        }
    }

}