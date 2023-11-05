package com.armutyus.ninova.ui.books

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.res.Resources
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.Html
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.armutyus.ninova.R
import com.armutyus.ninova.constants.Cache.currentBookIdExtra
import com.armutyus.ninova.constants.Cache.currentGoogleBook
import com.armutyus.ninova.constants.Cache.currentLocalBook
import com.armutyus.ninova.constants.Cache.currentOpenLibBook
import com.armutyus.ninova.constants.Cache.currentOpenLibBookCategory
import com.armutyus.ninova.constants.Constants.BOOK_TYPE_FOR_DETAILS
import com.armutyus.ninova.constants.Constants.DELETED_FIRESTORE
import com.armutyus.ninova.constants.Constants.DELETING_FIRESTORE
import com.armutyus.ninova.constants.Constants.GOOGLE_BOOK_TYPE
import com.armutyus.ninova.constants.Constants.LOCAL_BOOK_TYPE
import com.armutyus.ninova.constants.Constants.MAIN_INTENT
import com.armutyus.ninova.constants.Constants.OPEN_LIB_BOOK_TYPE
import com.armutyus.ninova.constants.Constants.UPLOADED_FIRESTORE
import com.armutyus.ninova.constants.Constants.UPLOADING_FIRESTORE
import com.armutyus.ninova.constants.Response
import com.armutyus.ninova.constants.Util.Companion.checkAndApplyTheme
import com.armutyus.ninova.databinding.ActivityBookDetailsBinding
import com.armutyus.ninova.databinding.AddBookToShelfBottomSheetBinding
import com.armutyus.ninova.databinding.CustomDialogEditTextLayoutBinding
import com.armutyus.ninova.model.googlebooksmodel.BookDetailsInfo
import com.armutyus.ninova.model.googlebooksmodel.DataModel
import com.armutyus.ninova.model.openlibrarymodel.BookDetailsResponse
import com.armutyus.ninova.roomdb.entities.BookShelfCrossRef
import com.armutyus.ninova.roomdb.entities.LocalShelf
import com.armutyus.ninova.ui.discover.DiscoverViewModel
import com.armutyus.ninova.ui.shelves.ShelvesViewModel
import com.armutyus.ninova.ui.shelves.adapters.BookToShelfRecyclerViewAdapter
import com.bumptech.glide.RequestManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import javax.inject.Inject
import javax.inject.Named

@AndroidEntryPoint
class BookDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBookDetailsBinding
    private lateinit var bookToShelfBottomSheetBinding: AddBookToShelfBottomSheetBinding
    private lateinit var customDialogEditTextLayoutBinding: CustomDialogEditTextLayoutBinding
    private lateinit var googleBookDetails: BookDetailsInfo
    private lateinit var openLibBookDetails: BookDetailsResponse.CombinedResponse
    private var notesTabDisabled = true
    private lateinit var tabLayout: TabLayout
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    private lateinit var permissionResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var pickMedia: ActivityResultLauncher<PickVisualMediaRequest>
    private val booksViewModel by viewModels<BooksViewModel>()
    private val discoverViewModel by viewModels<DiscoverViewModel>()
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

    private val themePreferences: SharedPreferences
        get() = PreferenceManager.getDefaultSharedPreferences(this)

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
                /*
                 No need to implement this method.
                 */
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                /*
                 No need to implement this method.
                 */
            }
        })

        shelvesViewModel.loadShelfList()
        observeShelfListChanges()

        when (type) {
            LOCAL_BOOK_TYPE -> {
                supportActionBar?.title = currentLocalBook?.bookTitle
                if (currentLocalBook?.bookId?.startsWith("OL") == true) {
                    observeOpenLibBookDetailsResponse()
                } else {
                    observeGoogleBookDetailsResponse()
                }
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
                    currentLocalBook?.let { localBook ->
                        booksViewModel.deleteBook(localBook).invokeOnCompletion {
                            deleteBookFromFirestore(localBook.bookId)
                            setVisibilitiesForBookRemoved()
                        }
                    }
                }

                binding.shelvesOfBooks.setOnClickListener {
                    currentBookIdExtra = currentLocalBook?.bookId!!
                    showAddShelfDialog()
                }
            }

            GOOGLE_BOOK_TYPE -> {
                supportActionBar?.title = currentGoogleBook?.volumeInfo?.title
                observeGoogleBookDetailsResponse()
                setupGoogleBookInfo()
                isGoogleBookAddedCheck()
                setVisibilitiesForBookRemoved()

                binding.addBookToLibraryButton.setOnClickListener {
                    if (this::googleBookDetails.isInitialized) {
                        val book =
                            DataModel.LocalBook(
                                currentGoogleBook?.id!!,
                                googleBookDetails.authors ?: listOf(),
                                googleBookDetails.categories ?: listOf(),
                                googleBookDetails.imageLinks?.smallThumbnail,
                                googleBookDetails.imageLinks?.thumbnail,
                                Html.fromHtml(
                                    googleBookDetails.description ?: "",
                                    Html.FROM_HTML_OPTION_USE_CSS_COLORS
                                ).toString(),
                                "",
                                googleBookDetails.pageCount.toString(),
                                googleBookDetails.publishedDate,
                                googleBookDetails.publisher,
                                googleBookDetails.subtitle,
                                googleBookDetails.title
                            )
                        booksViewModel.insertBook(book).invokeOnCompletion {
                            uploadBookToFirestore(book)
                            setVisibilitiesForBookAdded()
                            booksViewModel.loadBookList()
                        }
                    } else {
                        Log.i("googleBookDetails", "googleBookDetails not initialized.")
                    }
                }

                binding.removeBookFromLibraryButton.setOnClickListener {
                    booksViewModel.deleteBookById(currentGoogleBook?.id!!).invokeOnCompletion {
                        deleteBookFromFirestore(currentGoogleBook?.id!!)
                        setVisibilitiesForBookRemoved()
                    }
                }

                binding.shelvesOfBooks.setOnClickListener {
                    currentBookIdExtra = currentGoogleBook?.id!!
                    showAddShelfDialog()
                }
            }

            OPEN_LIB_BOOK_TYPE -> {
                supportActionBar?.title = currentOpenLibBook?.title
                observeOpenLibBookDetailsResponse()
                setupOpenLibBookInfo()
                isOpenLibBookAddedCheck()
                setVisibilitiesForBookRemoved()

                val bookKey = currentOpenLibBook?.key!!.substringAfterLast("/")

                binding.addBookToLibraryButton.setOnClickListener {
                    if (this::openLibBookDetails.isInitialized) {
                        val authorList = currentOpenLibBook?.authors?.map { it.name }
                        val bookId = bookKey + currentOpenLibBook?.lending_edition
                        val bookCoverUrl =
                            "https://covers.openlibrary.org/b/id/${currentOpenLibBook?.cover_id}-M.jpg"
                        val bookLargeCoverUrl =
                            "https://covers.openlibrary.org/b/id/${currentOpenLibBook?.cover_id}-L.jpg"
                        val book =
                            DataModel.LocalBook(
                                bookId,
                                authorList ?: listOf(),
                                currentOpenLibBookCategory ?: listOf(),
                                bookCoverUrl,
                                bookLargeCoverUrl,
                                Html.fromHtml(
                                    openLibBookDetails.description ?: "",
                                    Html.FROM_HTML_OPTION_USE_CSS_COLORS
                                ).toString(),
                                "",
                                openLibBookDetails.number_of_pages,
                                currentOpenLibBook?.first_publish_year?.toString(),
                                openLibBookDetails.publishers?.joinToString(", "),
                                "",
                                currentOpenLibBook?.title
                            )
                        booksViewModel.insertBook(book).invokeOnCompletion {
                            uploadBookToFirestore(book)
                            setVisibilitiesForBookAdded()
                            booksViewModel.loadBookList()
                        }
                    } else {
                        Log.i("openLibBookDetails", "openLibBookDetails not initialized.")
                    }
                }

                binding.removeBookFromLibraryButton.setOnClickListener {
                    booksViewModel.deleteBookById(bookKey)
                        .invokeOnCompletion {
                            deleteBookFromFirestore(bookKey)
                            setVisibilitiesForBookRemoved()
                        }
                }

                binding.shelvesOfBooks.setOnClickListener {
                    currentBookIdExtra = bookKey + currentOpenLibBook?.lending_edition
                    showAddShelfDialog()
                }
            }
        }

    }

    override fun onResume() {
        super.onResume()
        currentLocalBook?.let {
            booksViewModel.loadBookWithShelves(it.bookId)
            binding.userBookNotesEditText.setText(it.bookNotes)
        }
        currentGoogleBook?.let { googleBookItem ->
            booksViewModel.loadBookWithShelves(googleBookItem.id!!)
            booksViewModel.loadBookList()
            val userNotesFromLocal =
                booksViewModel.localBookList.value?.firstOrNull { it.bookId == googleBookItem.id }?.bookNotes
            binding.userBookNotesEditText.setText(userNotesFromLocal)
        }
        currentOpenLibBook?.let { openLibBookItem ->
            val bookId = openLibBookItem.key + openLibBookItem.lending_edition
            booksViewModel.loadBookWithShelves(bookId)
            booksViewModel.loadBookList()
            val userNotesFromLocal =
                booksViewModel.localBookList.value?.firstOrNull { it.bookId == bookId }?.bookNotes
            binding.userBookNotesEditText.setText(userNotesFromLocal)
        }
    }

    override fun onPause() {
        super.onPause()
        when (type) {
            LOCAL_BOOK_TYPE -> saveUserNotes()
            GOOGLE_BOOK_TYPE -> {
                currentLocalBook =
                    booksViewModel.localBookList.value?.firstOrNull { it.bookId == currentGoogleBook?.id }
                if (currentLocalBook != null) {
                    saveUserNotes()
                }
            }

            OPEN_LIB_BOOK_TYPE -> {
                val bookId =
                    currentOpenLibBook?.key?.substringAfterLast("/") + currentOpenLibBook?.lending_edition
                currentLocalBook =
                    booksViewModel.localBookList.value?.firstOrNull { it.bookId == bookId }
                if (currentLocalBook != null) {
                    saveUserNotes()
                }
            }
        }
    }

    override fun getTheme(): Resources.Theme {
        val theme = super.getTheme()
        checkAndApplyTheme(themePreferences, theme)
        return theme
    }

    private fun saveUserNotes() {
        currentLocalBook?.let {
            it.bookNotes = binding.userBookNotesEditText.text.toString()
            booksViewModel.updateBook(it)
            uploadBookToFirestore(it)
        }
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
                showNotesTab()
            }

            getString(R.string.info) -> {
                showInfoTab()
            }

            else -> {
                showInfoTab()
            }
        }
    }

    private fun showInfoTab() {
        binding.bookDetailNotesLinearLayout.visibility = View.GONE
        binding.bookDetailInfoLinearLayout.visibility = View.VISIBLE
        binding.linearLayoutDetailsError.visibility = View.GONE
    }

    private fun showNotesTab() {
        binding.bookDetailNotesLinearLayout.visibility = View.VISIBLE
        binding.bookDetailInfoLinearLayout.visibility = View.GONE
        binding.linearLayoutDetailsError.visibility = View.GONE
    }

    private fun setTabVisibilitiesForBookRemoved(tab: TabLayout.Tab?) {
        when (tab?.text) {
            getString(R.string.notes) -> {
                Toast.makeText(this, R.string.book_edit_warning, Toast.LENGTH_LONG).show()
            }

            getString(R.string.info) -> {
                showInfoTab()
            }

            else -> {
                showInfoTab()
            }
        }
    }

    private fun isGoogleBookAddedCheck() {
        booksViewModel.loadBookList().invokeOnCompletion {
            if (currentGoogleBook?.isBookAddedCheck(booksViewModel) == true) {
                setVisibilitiesForBookAdded()
            } else {
                setVisibilitiesForBookRemoved()
            }
        }
    }

    private fun isOpenLibBookAddedCheck() {
        booksViewModel.loadBookList().invokeOnCompletion {
            if (currentOpenLibBook?.isBookAddedCheck(booksViewModel) == true) {
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

    private fun setupGoogleBookInfo() {
        if (currentGoogleBook == null) {
            setVisibilitiesForBookNull()
        } else {
            booksViewModel.getBookDetailsById(currentGoogleBook?.id!!)
        }
    }

    private fun setupLocalBookInfo() {
        if (currentLocalBook == null) {
            setVisibilitiesForBookNull()
        } else {
            if (currentLocalBook?.bookId?.startsWith("OL") == true) {
                val bookKey = currentLocalBook?.bookId!!.substringBeforeLast("OL")
                val lendingKey = currentLocalBook?.bookId!!.substringAfter("W")
                discoverViewModel.getBookDetails(bookKey, lendingKey)
            } else {
                booksViewModel.getBookDetailsById(currentLocalBook?.bookId!!)
            }
        }
    }

    private fun setupOpenLibBookInfo() {
        if (currentOpenLibBook == null) {
            setVisibilitiesForBookNull()
        } else {
            val bookKey = currentOpenLibBook?.key!!.substringAfterLast("/")
            discoverViewModel.getBookDetails(
                bookKey,
                currentOpenLibBook?.lending_edition!!
            )
        }
    }

    private fun observeGoogleBookDetailsResponse() {
        booksViewModel.bookDetails.observe(this) { response ->
            when (response) {
                is Response.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }

                is Response.Success -> {
                    binding.progressBar.visibility = View.GONE
                    googleBookDetails = response.data.volumeInfo
                    if (type == LOCAL_BOOK_TYPE && currentLocalBook?.bookId?.startsWith("OL") == false) {
                        applyGoogleBookDetailChangesToLocalBook(googleBookDetails)
                    } else {
                        applyGoogleBookDetailChanges(googleBookDetails)
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

    private fun observeOpenLibBookDetailsResponse() {
        discoverViewModel.combinedResponse.observe(this) { combinedResponseData ->
            if (combinedResponseData.loading) {
                binding.progressBar.visibility = View.VISIBLE
            } else if (combinedResponseData.keyError?.isNotBlank() == true && combinedResponseData.lendingKeyError?.isNotBlank() == true) {
                binding.progressBar.visibility = View.GONE
                if (type == LOCAL_BOOK_TYPE) {
                    Toast.makeText(
                        this,
                        R.string.details_activity_load_error,
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        this,
                        getString(R.string.some_details_not_loaded), Toast.LENGTH_SHORT
                    )
                        .show()
                }
            } else {
                binding.progressBar.visibility = View.GONE
                openLibBookDetails = combinedResponseData
                if (type == LOCAL_BOOK_TYPE && currentLocalBook?.bookId?.startsWith("OL") == true) {
                    applyOpenLibDetailChangesToLocalBook(openLibBookDetails)
                } else {
                    applyOpenLibBookDetailChanges(openLibBookDetails)
                }
            }
        }
    }

    private fun deleteBookFromFirestore(bookId: String) {
        booksViewModel.deleteBookFromFirestore(bookId) { response ->
            when (response) {
                is Response.Loading ->
                    Log.i("bookDelete", DELETING_FIRESTORE)

                is Response.Success ->
                    Log.i("bookDelete", DELETED_FIRESTORE)

                is Response.Failure ->
                    Log.e("bookDelete", response.errorMessage)
            }
        }
    }

    private fun uploadCustomBookCoverToFirestore(uri: Uri) {
        booksViewModel.uploadCustomBookCoverToFirestore(uri) { response ->
            when (response) {
                is Response.Loading ->
                    Log.i("bookCoverUpload", UPLOADING_FIRESTORE)

                is Response.Success -> {
                    val downloadUrl = response.data.toString()
                    currentLocalBook?.bookCoverSmallThumbnail = downloadUrl
                    booksViewModel.updateBook(currentLocalBook!!)
                    uploadBookToFirestore(currentLocalBook!!)
                    Log.i("bookCoverUpload", UPLOADED_FIRESTORE)
                }

                is Response.Failure ->
                    Log.e("bookCoverUpload", response.errorMessage)
            }
        }
    }

    private fun uploadBookToFirestore(localBook: DataModel.LocalBook) {
        booksViewModel.uploadBookToFirestore(localBook) { response ->
            when (response) {
                is Response.Loading ->
                    Log.i("bookUpload", UPLOADING_FIRESTORE)

                is Response.Success ->
                    Log.i("bookUpload", UPLOADED_FIRESTORE)

                is Response.Failure ->
                    Log.e("bookUpload", response.errorMessage)
            }
        }
    }

    private fun uploadShelfToFirestore(localShelf: LocalShelf) {
        shelvesViewModel.uploadShelfToFirestore(localShelf) { response ->
            when (response) {
                is Response.Loading ->
                    Log.i("shelfUpload", UPLOADING_FIRESTORE)

                is Response.Success ->
                    Log.i("shelfUpload", UPLOADED_FIRESTORE)

                is Response.Failure ->
                    Log.e("shelfUpload", response.errorMessage)
            }
        }
    }

    private fun uploadCrossRefToFirestore(crossRef: BookShelfCrossRef) {
        shelvesViewModel.uploadCrossRefToFirestore(crossRef) { response ->
            when (response) {
                is Response.Loading ->
                    Log.i("crossRefUpload", UPLOADING_FIRESTORE)

                is Response.Success ->
                    Log.i("crossRefUpload", UPLOADED_FIRESTORE)

                is Response.Failure ->
                    Log.e("crossRefUpload", response.errorMessage)
            }
        }
    }

    private fun launchCreateShelfDialog(shelfTitle: String, shelfDialog: DialogInterface) {
        val timeStamp = Date().time
        val formattedDate =
            SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS", Locale.getDefault()).format(timeStamp)
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
                shelvesViewModel.loadShelfWithBookList()
                shelfDialog.dismiss()
            }
        }
    }

    private fun applyGoogleBookDetailChanges(bookDetails: BookDetailsInfo) {
        glide
            .load(
                currentGoogleBook?.volumeInfo?.imageLinks?.thumbnail
                    ?: bookDetails.imageLinks?.smallThumbnail
            )
            .centerCrop()
            .into(binding.bookCoverImageView)
        binding.bookDetailTitleText.text = currentGoogleBook?.volumeInfo?.title ?: bookDetails.title
        binding.bookDetailSubTitleText.text =
            currentGoogleBook?.volumeInfo?.subtitle ?: bookDetails.subtitle
        binding.bookDetailAuthorsText.text =
            currentGoogleBook?.volumeInfo?.authors?.joinToString(", ")
                ?: bookDetails.authors?.joinToString(", ")
        binding.bookDetailPagesNumber.text =
            currentGoogleBook?.volumeInfo?.pageCount?.toString()
                ?: bookDetails.pageCount?.toString()
        binding.bookDetailCategories.text =
            currentGoogleBook?.volumeInfo?.categories?.joinToString(", ")
                ?: bookDetails.categories?.joinToString(", ")
        binding.bookDetailPublisher.text =
            currentGoogleBook?.volumeInfo?.publisher ?: bookDetails.publisher
        binding.bookDetailPublishDate.text =
            currentGoogleBook?.volumeInfo?.publishedDate ?: bookDetails.publishedDate

        val formattedBookDescription = if (bookDetails.description == null) {
            currentGoogleBook?.volumeInfo?.description
        } else {
            Html.fromHtml(
                bookDetails.description,
                Html.FROM_HTML_OPTION_USE_CSS_COLORS
            ).toString()
        }
        binding.bookDetailDescription.text = formattedBookDescription
    }

    private fun applyGoogleBookDetailChangesToLocalBook(bookDetails: BookDetailsInfo) {
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

        updateLocalBookWithGoogleBook(bookDetails)
    }

    private fun applyOpenLibBookDetailChanges(bookDetails: BookDetailsResponse.CombinedResponse) {
        val authorList = currentOpenLibBook?.authors?.map { it.name }
        val bookCoverUrl =
            "https://covers.openlibrary.org/b/id/${currentOpenLibBook?.cover_id}-M.jpg"
        glide
            .load(bookCoverUrl)
            .centerCrop()
            .into(binding.bookCoverImageView)
        binding.bookDetailTitleText.text = currentOpenLibBook?.title
        binding.bookDetailAuthorsText.text = authorList?.joinToString(", ")
        binding.bookDetailPagesNumber.text = bookDetails.number_of_pages
        binding.bookDetailCategories.text =
            currentOpenLibBookCategory?.joinToString(", ")
        binding.bookDetailPublisher.text =
            bookDetails.publishers?.joinToString(", ")
        binding.bookDetailPublishDate.text =
            currentOpenLibBook?.first_publish_year?.toString()

        val formattedBookDescription = if (bookDetails.description.isNullOrBlank()) {
            currentLocalBook?.bookDescription
        } else {
            Html.fromHtml(
                bookDetails.description,
                Html.FROM_HTML_OPTION_USE_CSS_COLORS
            ).toString()
        }
        binding.bookDetailDescription.text = formattedBookDescription
    }

    private fun applyOpenLibDetailChangesToLocalBook(bookDetails: BookDetailsResponse.CombinedResponse) {
        val bookPages = if (bookDetails.number_of_pages.isNullOrBlank()) {
            currentLocalBook?.bookPages
        } else {
            bookDetails.number_of_pages
        }
        val bookPublishers = if (bookDetails.publishers.isNullOrEmpty()) {
            currentLocalBook?.bookPublisher
        } else {
            bookDetails.publishers.joinToString(", ")
        }
        glide
            .load(
                currentLocalBook?.bookCoverSmallThumbnail
            )
            .centerCrop()
            .into(binding.bookCoverImageView)
        binding.bookDetailTitleText.text = currentLocalBook?.bookTitle
        binding.bookDetailSubTitleText.text = currentLocalBook?.bookSubtitle
        binding.bookDetailAuthorsText.text = currentLocalBook?.bookAuthors?.joinToString(", ")
        binding.bookDetailPagesNumber.text = bookPages
        binding.bookDetailCategories.text =
            currentOpenLibBookCategory?.joinToString(", ")
                ?: currentLocalBook?.bookCategories?.joinToString(", ")
        binding.bookDetailPublisher.text = bookPublishers
        binding.bookDetailPublishDate.text = currentLocalBook?.bookPublishedDate

        val formattedBookDescription = if (bookDetails.description.isNullOrBlank()) {
            currentLocalBook?.bookDescription
        } else {
            Html.fromHtml(
                bookDetails.description,
                Html.FROM_HTML_OPTION_USE_CSS_COLORS
            ).toString()
        }
        binding.bookDetailDescription.text = formattedBookDescription

        updateLocalBookWithOpenLibBook(bookDetails)
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

    private fun updateLocalBookWithGoogleBook(bookDetails: BookDetailsInfo) {
        currentLocalBook?.let {
            it.bookCoverSmallThumbnail?.let { coverUrl ->
                if (bookDetails.imageLinks?.smallThumbnail != null && coverUrl.startsWith(
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

    private fun updateLocalBookWithOpenLibBook(bookDetails: BookDetailsResponse.CombinedResponse) {
        currentLocalBook?.let {
            if (bookDetails.number_of_pages != null && it.bookPages != bookDetails.number_of_pages) {
                it.bookPages = binding.bookDetailPagesNumber.text.toString()
            }

            if (bookDetails.publishers != null && it.bookPublisher != bookDetails.publishers.joinToString(
                    ", "
                )
            ) {
                it.bookPublisher = binding.bookDetailPublisher.text.toString()
            }

            if (bookDetails.description != null && it.bookDescription != bookDetails.description) {
                it.bookDescription = binding.bookDetailDescription.text.toString()
            }
        }

        booksViewModel.updateBook(currentLocalBook!!)
        uploadBookToFirestore(currentLocalBook!!)
    }

    private fun onBookCoverClicked(view: View) {
        if (isPhotoPickerAvailable()) {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        } else {
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
                permissionResultLauncher.launch(galleryIntent)
            }
        }
    }

    private fun isPhotoPickerAvailable(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
    }

    private fun registerLauncher() {
        permissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { result ->
            if (result) {
                val galleryIntent =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                permissionResultLauncher.launch(galleryIntent)
            } else {
                Toast.makeText(this, R.string.permission_needed, Toast.LENGTH_LONG).show()
            }
        }
        permissionResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                val uri = result.data?.data
                if (uri != null) {
                    glide.load(uri).centerCrop().into(binding.bookCoverImageView)
                    uploadCustomBookCoverToFirestore(uri)
                }
            }
        }
        pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                Log.d("PhotoPicker", "Selected URI: $uri")
                val flag = Intent.FLAG_GRANT_READ_URI_PERMISSION
                this.contentResolver.takePersistableUriPermission(uri, flag)
                glide.load(uri).centerCrop().into(binding.bookCoverImageView)
                uploadCustomBookCoverToFirestore(uri)
            } else {
                Log.d("PhotoPicker", "No media selected")
            }
        }
    }

}