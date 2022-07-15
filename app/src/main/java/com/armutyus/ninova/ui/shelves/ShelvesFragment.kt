package com.armutyus.ninova.ui.shelves

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.armutyus.ninova.R
import com.armutyus.ninova.databinding.AddNewShelfBottomSheetBinding
import com.armutyus.ninova.databinding.FragmentShelvesBinding
import com.armutyus.ninova.roomdb.entities.LocalShelf
import com.armutyus.ninova.ui.shelves.adapters.ShelvesRecyclerViewAdapter
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class ShelvesFragment @Inject constructor(
    private val shelvesAdapter: ShelvesRecyclerViewAdapter
) : Fragment(R.layout.fragment_shelves), SearchView.OnQueryTextListener {

    private var fragmentBinding: FragmentShelvesBinding? = null
    private lateinit var shelvesViewModel: ShelvesViewModel
    private lateinit var bottomSheetBinding: AddNewShelfBottomSheetBinding
    private val swipeCallBack = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            return true
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val layoutPosition = viewHolder.layoutPosition
            val swipedShelf = shelvesAdapter.mainShelfList[layoutPosition]
            shelvesViewModel.deleteShelf(swipedShelf).invokeOnCompletion {
                Snackbar.make(
                    requireView(),
                    "Shelf deleted from your library",
                    Snackbar.LENGTH_LONG
                )
                    .setAction("UNDO") {
                        shelvesViewModel.insertShelf(swipedShelf)
                    }.show()
            }
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentShelvesBinding.bind(view)
        fragmentBinding = binding
        shelvesViewModel = ViewModelProvider(requireActivity())[ShelvesViewModel::class.java]

        val recyclerView = binding.mainShelvesRecyclerView
        recyclerView.adapter = shelvesAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        shelvesAdapter.setViewModel(shelvesViewModel)
        ItemTouchHelper(swipeCallBack).attachToRecyclerView(recyclerView)

        val searchView = binding.shelvesSearch
        searchView.setOnQueryTextListener(this)
        searchView.setIconifiedByDefault(false)

        binding.mainShelvesAddButton.setOnClickListener {
            showAddShelfDialog()
        }

        observeShelfList()

    }

    private fun showAddShelfDialog() {
        val dialog = BottomSheetDialog(requireContext())
        bottomSheetBinding = AddNewShelfBottomSheetBinding.inflate(layoutInflater)
        dialog.setContentView(bottomSheetBinding.root)
        dialog.show()

        val addShelfButton = dialog.findViewById<MaterialButton>(R.id.addShelfButton)
        addShelfButton?.setOnClickListener {
            val shelfTitle = bottomSheetBinding.shelfTitleText.text.toString()

            if (shelfTitle.isEmpty()) {
                Toast.makeText(requireContext(), "Title cannot be empty!", Toast.LENGTH_LONG).show()
            } else {
                val timeStamp = Date().time
                val formattedDate = SimpleDateFormat("dd-MM-yyyy").format(timeStamp)
                shelvesViewModel.insertShelf(
                    LocalShelf(
                        0,
                        shelfTitle,
                        formattedDate,
                        "",
                    )
                )
                dialog.hide()
            }

        }
    }

    override fun onResume() {
        super.onResume()
        shelvesViewModel.getShelfList()
    }

    private fun observeShelfList() {
        shelvesViewModel.currentShelfList.observe(viewLifecycleOwner) { currentShelfList ->
            shelvesAdapter.mainShelfList = currentShelfList
            setVisibilities(currentShelfList)
        }

        shelvesViewModel.shelfList.observe(viewLifecycleOwner) {
            shelvesViewModel.setCurrentList(it?.toList() ?: listOf())
        }

        shelvesViewModel.searchShelvesList.observe(viewLifecycleOwner) {
            shelvesViewModel.setCurrentList(it?.toList() ?: listOf())
        }

    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }

    override fun onQueryTextChange(searchQuery: String?): Boolean {
        if (searchQuery?.length!! > 0) {
            fragmentBinding?.progressBar?.visibility = View.VISIBLE
            fragmentBinding?.mainShelvesRecyclerView?.visibility = View.GONE
            shelvesViewModel.searchShelves("%$searchQuery%")

        } else if (searchQuery.isNullOrBlank()) {
            shelvesViewModel.getShelfList()
        }

        return true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        fragmentBinding = null
    }

    private fun setVisibilities(shelfList: List<LocalShelf>) {
        if (shelfList.isEmpty()) {
            fragmentBinding?.linearLayoutShelvesError?.visibility = View.VISIBLE
            fragmentBinding?.progressBar?.visibility = View.GONE
            fragmentBinding?.mainShelvesRecyclerView?.visibility = View.GONE
        } else {
            fragmentBinding?.linearLayoutShelvesError?.visibility = View.GONE
            fragmentBinding?.progressBar?.visibility = View.GONE
            fragmentBinding?.mainShelvesRecyclerView?.visibility = View.VISIBLE
        }
    }
}