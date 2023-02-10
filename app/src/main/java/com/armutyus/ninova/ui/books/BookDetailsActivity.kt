package com.armutyus.ninova.ui.books

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import android.text.Html
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import com.armutyus.ninova.R
import com.armutyus.ninova.constants.Cache.currentBook
import com.armutyus.ninova.constants.Cache.currentBookIdExtra
import com.armutyus.ninova.constants.Cache.currentLocalBook
import com.armutyus.ninova.constants.Constants.BOOK_TYPE_FOR_DETAILS
import com.armutyus.ninova.constants.Constants.GOOGLE_BOOK_TYPE
import com.armutyus.ninova.constants.Constants.LOCAL_BOOK_TYPE
import com.armutyus.ninova.constants.Constants.MAIN_INTENT
import com.armutyus.ninova.constants.Response
import com.armutyus.ninova.databinding.ActivityBookDetailsBinding
import com.armutyus.ninova.databinding.AddBookToShelfBottomSheetBinding
import com.armutyus.ninova.databinding.CustomDialogEditTextLayoutBinding
import com.armutyus.ninova.model.BookDetailsInfo
import com.armutyus.ninova.model.DataModel
import com.armutyus.ninova.roomdb.entities.BookShelfCrossRef
import com.armutyus.ninova.roomdb.entities.LocalShelf
import com.armutyus.ninova.ui.shelves.ShelvesViewModel
import com.armutyus.ninova.ui.shelves.adapters.BookToShelfRecyclerViewAdapter
import com.bumptech.glide.RequestManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Named

@AndroidEntryPoint
class BookDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBookDetailsBinding
    private lateinit var bookToShelfBottomSheetBinding: AddBookToShelfBottomSheetBinding
    private lateinit var customDialogEditTextLayoutBinding: CustomDialogEditTextLayoutBinding
    private lateinit var bookDetails: BookDetailsInfo
    private var notesTabDisabled = true
    private lateinit var tabLayout: TabLayout
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    private val booksViewModel by viewModels<BooksViewModel>()
    private val shelvesViewModel by viewModels<ShelvesViewModel>()


    @Inject
    lateinit var glide: RequestManager

    @Inject
    lateinit var bookToShelfAdapter: BookToShelfRecyclerViewAdapter


    @Named(MAIN_INTENT)
    @Inject
    lateinit var mainIntent: Intent

    private val type: Int
        get() = intent?.getIntExtra(BOOK_TYPE_FOR_DETAILS, -1) ?: -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tabLayout = binding.bookDetailTabLayout
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {

            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (notesTabDisabled) {
                    tabLayout.getTabAt(0)?.select()
                    tabLayout.getTabAt(1)?.view?.isFocusable = false
                    setTabVisibilitiesForBookRemoved(tab)
                } else {
                    tabLayout.getTabAt(1)?.view?.isFocusable = true
                    setTabVisibilitiesForBookAdded(tab)
                }
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }
        })

        when (type) {
            LOCAL_BOOK_TYPE -> {
                supportActionBar?.title = currentLocalBook?.bookTitle
                setVisibilitiesForBookAdded()
                registerLauncher()
                setupLocalBookInfo()
                showLocalBookDetails()

                binding.addBookToLibraryButton.setOnClickListener {
                    booksViewModel.insertBook(currentLocalBook!!).invokeOnCompletion {
                        uploadBookToFirestore(currentLocalBook!!)
                        setVisibilitiesForBookAdded()
                        booksViewModel.loadBookList()
                    }
                }

                binding.bookCoverImageView.setOnClickListener {
                    onBookCoverClicked(it)
                }

                binding.removeBookFromLibraryButton.setOnClickListener {
                    booksViewModel.deleteBook(currentLocalBook!!).invokeOnCompletion {
                        deleteBookFromFirestore(currentLocalBook?.bookId!!)
                        setVisibilitiesForBookRemoved()
                    }
                }

                binding.shelvesOfBooks.setOnClickListener {
                    currentBookIdExtra = currentLocalBook?.bookId!!
                    showAddShelfDialog()
                }
            }

            GOOGLE_BOOK_TYPE -> {
                supportActionBar?.title = currentBook?.volumeInfo?.title
                setupBookInfo()
                isBookAddedCheck()
                setVisibilitiesForBookRemoved()

                binding.addBookToLibraryButton.setOnClickListener {
                    val book =
                        DataModel.LocalBook(
                            currentBook?.id!!,
                            bookDetails.authors ?: listOf(),
                            bookDetails.categories ?: listOf(),
                            bookDetails.imageLinks?.smallThumbnail,
                            bookDetails.imageLinks?.thumbnail,
                            Html.fromHtml(
                                bookDetails.description ?: "",
                                Html.FROM_HTML_OPTION_USE_CSS_COLORS
                            ).toString(),
                            "",
                            bookDetails.pageCount.toString(),
                            bookDetails.publishedDate,
                            bookDetails.publisher,
                            bookDetails.subtitle,
                            bookDetails.title
                        )
                    booksViewModel.insertBook(book).invokeOnCompletion {
                        uploadBookToFirestore(book)
                        setVisibilitiesForBookAdded()
                        booksViewModel.loadBookList()
                    }
                }

                binding.removeBookFromLibraryButton.setOnClickListener {
                    booksViewModel.deleteBookById(currentBook?.id!!).invokeOnCompletion {
                        deleteBookFromFirestore(currentBook?.id!!)
                        setVisibilitiesForBookRemoved()
                    }
                }

                binding.shelvesOfBooks.setOnClickListener {
                    currentBookIdExtra = currentBook?.id!!
                    showAddShelfDialog()
                }
            }
            else -> {}
        }

        shelvesViewModel.loadShelfList()
        observeShelfListChanges()
        observeBookDetailsResponse()
    }

    override fun onResume() {
        super.onResume()
        currentLocalBook?.let {
            booksViewModel.loadBookWithShelves(it.bookId)
            binding.userBookNotesEditText.setText(it.bookNotes)
        }
        currentBook?.let { googleBookItem ->
            booksViewModel.loadBookWithShelves(googleBookItem.id!!)
            booksViewModel.loadBookList()
            val userNotesFromLocal =
                booksViewModel.localBookList.value?.firstOrNull { it.bookId == googleBookItem.id }?.bookNotes
            binding.userBookNotesEditText.setText(userNotesFromLocal)
        }
    }

    override fun onPause() {
        super.onPause()
        when (type) {
            LOCAL_BOOK_TYPE -> saveUserNotes()
            GOOGLE_BOOK_TYPE -> {
                currentLocalBook =
                    booksViewModel.localBookList.value?.firstOrNull { it.bookId == currentBook?.id }
                if (currentLocalBook != null) {
                    saveUserNotes()
                }
            }
        }
    }

    private fun saveUserNotes() {
        currentLocalBook!!.bookNotes = binding.userBookNotesEditText.text.toString()
        booksViewModel.updateBook(currentLocalBook!!)
        uploadBookToFirestore(currentLocalBook!!)
    }

    private var currentShelvesList = mutableListOf<String?>()

    private fun observeShelfListChanges() {
        booksViewModel.bookWithShelvesList.observe(this) { shelvesOfBook ->
            shelvesOfBook.forEach { bookWithShelves ->
                val shelfTitleList = bookWithShelves.shelfList.map { it.shelfTitle }.toList()
                currentShelvesList.clear()
                currentShelvesList.addAll(shelfTitleList)
            }
            binding.shelvesOfBooks.text = currentShelvesList.joinToString(", ")
        }

        shelvesViewModel.currentShelfList.observe(this) {
            bookToShelfAdapter.bookToShelfList = it
        }

        shelvesViewModel.shelfList.observe(this) {
            shelvesViewModel.setCurrentList(it?.toList() ?: listOf())
        }
    }

    private fun setTabVisibilitiesForBookAdded(tab: TabLayout.Tab?) {
        when (tab?.text) {
            getString(R.string.notes) -> {
                binding.bookDetailNotesLinearLayout.visibility = View.VISIBLE
                binding.bookDetailInfoLinearLayout.visibility = View.GONE
                binding.linearLayoutDetailsError.visibility = View.GONE
            }
            getString(R.string.info) -> {
                binding.bookDetailNotesLinearLayout.visibility = View.GONE
                binding.bookDetailInfoLinearLayout.visibility = View.VISIBLE
                binding.linearLayoutDetailsError.visibility = View.GONE
            }
            else -> {
                binding.bookDetailNotesLinearLayout.visibility = View.GONE
                binding.bookDetailInfoLinearLayout.visibility = View.VISIBLE
                binding.linearLayoutDetailsError.visibility = View.GONE
            }
        }
    }

    private fun setTabVisibilitiesForBookRemoved(tab: TabLayout.Tab?) {
        when (tab?.text) {
            getString(R.string.notes) -> {
                Toast.makeText(this, R.string.book_edit_warning, Toast.LENGTH_LONG).show()
            }
            getString(R.string.info) -> {
                binding.bookDetailNotesLinearLayout.visibility = View.GONE
                binding.bookDetailInfoLinearLayout.visibility = View.VISIBLE
                binding.linearLayoutDetailsError.visibility = View.GONE
            }
            else -> {
                binding.bookDetailNotesLinearLayout.visibility = View.GONE
                binding.bookDetailInfoLinearLayout.visibility = View.VISIBLE
                binding.linearLayoutDetailsError.visibility = View.GONE
            }
        }
    }

    private fun isBookAddedCheck() {
        booksViewModel.loadBookList().invokeOnCompletion {
            if (currentBook?.isBookAddedCheck(booksViewModel) == true) {
                setVisibilitiesForBookAdded()
            } else {
                setVisibilitiesForBookRemoved()
            }
        }
    }

    private fun setVisibilitiesForBookAdded() {
        binding.addBookToLibraryButton.visibility = View.GONE
        binding.bookDetailShelvesTextViews.visibility = View.VISIBLE
        binding.removeBookFromLibraryButton.visibility = View.VISIBLE
        notesTabDisabled = false
    }

    private fun setVisibilitiesForBookNull() {
        binding.linearLayoutDetailsError.visibility = View.VISIBLE
        binding.bookDetailGeneralLayout.visibility = View.GONE
        binding.progressBar.visibility = View.GONE
        binding.addBookToLibraryButton.visibility = View.GONE
        binding.bookDetailNotesLinearLayout.visibility = View.GONE
        binding.bookDetailInfoLinearLayout.visibility = View.GONE
    }

    private fun setVisibilitiesForBookRemoved() {
        binding.addBookToLibraryButton.visibility = View.VISIBLE
        binding.bookDetailShelvesTextViews.visibility = View.GONE
        binding.removeBookFromLibraryButton.visibility = View.GONE
        notesTabDisabled = true
        tabLayout.getTabAt(0)?.select()
    }

    private fun showAddShelfDialog() {
        val dialog = BottomSheetDialog(this)
        bookToShelfBottomSheetBinding = AddBookToShelfBottomSheetBinding.inflate(layoutInflater)
        dialog.setContentView(bookToShelfBottomSheetBinding.root)

        val recyclerView = bookToShelfBottomSheetBinding.addBookToShelfRecyclerView
        recyclerView.adapter = bookToShelfAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        bookToShelfAdapter.setViewModels(shelvesViewModel, booksViewModel)


        val addToShelfButton = bookToShelfBottomSheetBinding.addShelfButton
        addToShelfButton.setOnClickListener {
            var shelfTitle: String
            customDialogEditTextLayoutBinding =
                CustomDialogEditTextLayoutBinding.inflate(layoutInflater)
            val editTextInputField = customDialogEditTextLayoutBinding.customDialogShelfTitleText
            shelfTitle = editTextInputField.text.toString()
            val builder =
                MaterialAlertDialogBuilder(this)
                    .setTitle(title)
                    .setMessage(R.string.create_shelf_and_add_book)
                    .setView(customDialogEditTextLayoutBinding.root)
                    .setPositiveButton(R.string.create) { shelfDialog, _ ->
                        launchCreateShelfDialog(shelfTitle, shelfDialog)
                    }
                    .setNegativeButton(R.string.cancelCaps, null)
            val createShelfDialog = builder.create()
            editTextInputField.doAfterTextChanged {
                shelfTitle = editTextInputField.text.toString()
                createShelfDialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled =
                    shelfTitle.isNotEmpty()
            }
            createShelfDialog.setCanceledOnTouchOutside(false)
            createShelfDialog.show()
            createShelfDialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = false
        }

        dialog.show()
    }

    private fun setupBookInfo() {
        if (currentBook == null) {
            setVisibilitiesForBookNull()
        } else {
            booksViewModel.getBookDetailsById(currentBook?.id!!)
        }
    }

    private fun setupLocalBookInfo() {
        if (currentLocalBook == null) {
            setVisibilitiesForBookNull()
        } else {
            booksViewModel.getBookDetailsById(currentLocalBook?.bookId!!)
        }
    }

    private fun observeBookDetailsResponse() {
        booksViewModel.bookDetails.observe(this) { response ->
            when (response) {
                is Response.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                is Response.Success -> {
                    binding.progressBar.visibility = View.GONE
                    bookDetails = response.data.volumeInfo
                    if (type == LOCAL_BOOK_TYPE) {
                        applyLocalBookDetailChanges(bookDetails)
                    } else {
                        applyBookDetailChanges(bookDetails)
                    }

                }
                is Response.Failure -> {
                    binding.progressBar.visibility = View.GONE
                    if (type == LOCAL_BOOK_TYPE) {
                        Toast.makeText(
                            this,
                            R.string.details_activity_load_error,
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        setVisibilitiesForBookNull()
                        Toast.makeText(this, R.string.something_went_wrong, Toast.LENGTH_SHORT)
                            .show()
                    }
                }
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

    private fun uploadShelfToFirestore(localShelf: LocalShelf) {
        shelvesViewModel.uploadShelfToFirestore(localShelf) { response ->
            when (response) {
                is Response.Loading ->
                    Log.i("shelfUpload", "Uploading to firestore")
                is Response.Success ->
                    Log.i("shelfUpload", "Uploaded to firestore")
                is Response.Failure ->
                    Log.e("shelfUpload", response.errorMessage)
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

    private fun launchCreateShelfDialog(shelfTitle: String, shelfDialog: DialogInterface) {
        val timeStamp = Date().time
        val formattedDate =
            SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(timeStamp)
        val shelf =
            LocalShelf(
                UUID.randomUUID().toString(),
                shelfTitle,
                formattedDate,
                "",
            )
        shelvesViewModel.insertShelf(shelf).invokeOnCompletion {
            val crossRef = BookShelfCrossRef(currentBookIdExtra!!, shelf.shelfId)
            uploadShelfToFirestore(shelf)
            shelvesViewModel.loadShelfList()
            shelvesViewModel.insertBookShelfCrossRef(crossRef).invokeOnCompletion {
                uploadCrossRefToFirestore(crossRef)
                booksViewModel.loadBookWithShelves(currentBookIdExtra!!)
                shelfDialog.dismiss()
            }
        }
    }

    private fun applyBookDetailChanges(bookDetails: BookDetailsInfo) {
        glide
            .load(
                currentBook?.volumeInfo?.imageLinks?.thumbnail
                    ?: bookDetails.imageLinks?.smallThumbnail
            )
            .centerCrop()
            .into(binding.bookCoverImageView)
        binding.bookDetailTitleText.text = currentBook?.volumeInfo?.title ?: bookDetails.title
        binding.bookDetailSubTitleText.text =
            currentBook?.volumeInfo?.subtitle ?: bookDetails.subtitle
        binding.bookDetailAuthorsText.text =
            currentBook?.volumeInfo?.authors?.joinToString(", ")
                ?: bookDetails.authors?.joinToString(", ")
        binding.bookDetailPagesNumber.text =
            currentBook?.volumeInfo?.pageCount?.toString() ?: bookDetails.pageCount?.toString()
        binding.bookDetailCategories.text =
            currentBook?.volumeInfo?.categories?.joinToString(", ")
                ?: bookDetails.categories?.joinToString(", ")
        binding.bookDetailPublisher.text =
            currentBook?.volumeInfo?.publisher ?: bookDetails.publisher
        binding.bookDetailPublishDate.text =
            currentBook?.volumeInfo?.publishedDate ?: bookDetails.publishedDate

        val formattedBookDescription = if (bookDetails.description == null) {
            currentBook?.volumeInfo?.description
        } else {
            Html.fromHtml(
                bookDetails.description,
                Html.FROM_HTML_OPTION_USE_CSS_COLORS
            ).toString()
        }
        binding.bookDetailDescription.text = formattedBookDescription

    }

    private fun applyLocalBookDetailChanges(bookDetails: BookDetailsInfo) {
        glide
            .load(
                currentLocalBook?.bookCoverSmallThumbnail
                    ?: bookDetails.imageLinks?.smallThumbnail
            )
            .centerCrop()
            .into(binding.bookCoverImageView)
        binding.bookDetailTitleText.text = bookDetails.title ?: currentLocalBook?.bookTitle
        binding.bookDetailSubTitleText.text =
            bookDetails.subtitle ?: currentLocalBook?.bookSubtitle
        binding.bookDetailAuthorsText.text =
            bookDetails.authors?.joinToString(", ")
                ?: currentLocalBook?.bookAuthors?.joinToString(", ")
        binding.bookDetailPagesNumber.text =
            bookDetails.pageCount?.toString() ?: currentLocalBook?.bookPages
        binding.bookDetailCategories.text =
            bookDetails.categories?.joinToString(", ")
                ?: currentLocalBook?.bookCategories?.joinToString(", ")
        binding.bookDetailPublisher.text =
            bookDetails.publisher ?: currentLocalBook?.bookPublisher
        binding.bookDetailPublishDate.text =
            bookDetails.publishedDate ?: currentLocalBook?.bookPublishedDate

        val formattedBookDescription = if (bookDetails.description == null) {
            currentLocalBook?.bookDescription
        } else {
            Html.fromHtml(
                bookDetails.description,
                Html.FROM_HTML_OPTION_USE_CSS_COLORS
            ).toString()
        }
        binding.bookDetailDescription.text = formattedBookDescription

        updateLocalBook(bookDetails)
    }

    private fun showLocalBookDetails() {
        glide.load(currentLocalBook?.bookCoverSmallThumbnail).centerCrop()
            .into(binding.bookCoverImageView)
        binding.bookDetailTitleText.text = currentLocalBook?.bookTitle
        binding.bookDetailSubTitleText.text = currentLocalBook?.bookSubtitle
        binding.bookDetailAuthorsText.text = currentLocalBook?.bookAuthors?.joinToString(", ")
        binding.bookDetailPagesNumber.text = currentLocalBook?.bookPages
        binding.bookDetailCategories.text = currentLocalBook?.bookCategories?.joinToString(", ")
        binding.bookDetailPublisher.text = currentLocalBook?.bookPublisher
        binding.bookDetailPublishDate.text = currentLocalBook?.bookPublishedDate
        binding.bookDetailDescription.text = currentLocalBook?.bookDescription
    }

    private fun updateLocalBook(bookDetails: BookDetailsInfo) {

        currentLocalBook?.let {
            if (it.bookCoverSmallThumbnail != null) {
                if (bookDetails.imageLinks?.smallThumbnail != null && it.bookCoverSmallThumbnail!!.startsWith(
                        "http://"
                    )
                ) {
                    it.bookCoverSmallThumbnail =
                        bookDetails.imageLinks.smallThumbnail
                }
            }

            if (bookDetails.title != null && it.bookTitle != bookDetails.title) {
                it.bookTitle = binding.bookDetailTitleText.text.toString()
            }

            if (bookDetails.subtitle != null && it.bookSubtitle != bookDetails.subtitle) {
                it.bookSubtitle = binding.bookDetailSubTitleText.text.toString()
            }

            if (bookDetails.authors != null && it.bookAuthors != bookDetails.authors) {
                it.bookAuthors = bookDetails.authors
            }

            if (bookDetails.pageCount != null && it.bookPages != bookDetails.pageCount.toString()) {
                it.bookPages = binding.bookDetailPagesNumber.text.toString()
            }

            if (bookDetails.categories != null && it.bookCategories != bookDetails.categories) {
                it.bookCategories = bookDetails.categories
            }

            if (bookDetails.publisher != null && it.bookPublisher != bookDetails.publisher) {
                it.bookPublisher = binding.bookDetailPublisher.text.toString()
            }

            if (bookDetails.publishedDate != null && it.bookPublishedDate != bookDetails.publishedDate) {
                currentLocalBook?.bookPublishedDate = binding.bookDetailPublishDate.text.toString()
            }

            if (bookDetails.description != null && it.bookDescription != bookDetails.description) {
                it.bookDescription = binding.bookDetailDescription.text.toString()
            }
        }

        booksViewModel.updateBook(currentLocalBook!!)
        uploadBookToFirestore(currentLocalBook!!)
    }

    private fun onBookCoverClicked(view: View) {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            ) {
                Snackbar.make(view, R.string.permission_needed, Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.give_permission) {
                        permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                    }.show()
            } else {
                permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        } else {
            val galleryIntent =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            activityResultLauncher.launch(galleryIntent)
        }
    }

    private fun registerLauncher() {
        activityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                val intentFromResult = result.data?.data
                if (intentFromResult != null) {
                    glide.load(intentFromResult).centerCrop().into(binding.bookCoverImageView)
                    currentLocalBook?.bookCoverSmallThumbnail = intentFromResult.toString()
                    booksViewModel.updateBook(currentLocalBook!!)
                    uploadBookToFirestore(currentLocalBook!!)
                }
            }
        }
        permissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { result ->
            if (result) {
                val galleryIntent =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(galleryIntent)
            } else {
                Toast.makeText(this, R.string.permission_needed, Toast.LENGTH_LONG).show()
            }
        }
    }

}