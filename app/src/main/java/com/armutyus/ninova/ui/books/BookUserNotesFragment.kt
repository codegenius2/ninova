package com.armutyus.ninova.ui.books

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import com.armutyus.ninova.R
import com.armutyus.ninova.constants.Constants
import com.armutyus.ninova.constants.Constants.currentBook
import com.armutyus.ninova.databinding.FragmentBookUserNotesBinding
import javax.inject.Inject


class BookUserNotesFragment @Inject constructor(
) : Fragment(R.layout.fragment_book_user_notes) {

    private var fragmentBinding: FragmentBookUserNotesBinding? = null
    private lateinit var booksViewModel: BooksViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentBookUserNotesBinding.bind(view)
        fragmentBinding = binding
        booksViewModel = ViewModelProvider(requireActivity())[BooksViewModel::class.java]

        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menu.clear()
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return false
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner) {
            when (activity?.intent?.getStringExtra(Constants.DETAILS_STRING_EXTRA)) {
                Constants.FROM_DETAILS_ACTIVITY -> {
                    activity?.finish()
                }
            }
        }

        if (currentBook!!.bookNotes != null) {
            binding.userBookNotesEditText.setText(currentBook!!.bookNotes)
        }

        binding.saveUserNotes.setOnClickListener {
            currentBook!!.bookNotes = binding.userBookNotesEditText.text.toString()
            booksViewModel.updateBook(currentBook!!)
            activity?.finish()
        }

    }


    override fun onDestroyView() {
        super.onDestroyView()
        fragmentBinding = null
    }
}