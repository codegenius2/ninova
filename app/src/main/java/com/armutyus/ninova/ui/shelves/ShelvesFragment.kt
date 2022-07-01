package com.armutyus.ninova.ui.shelves

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.armutyus.ninova.R
import com.armutyus.ninova.databinding.AddNewShelfBottomSheetBinding
import com.armutyus.ninova.databinding.FragmentShelvesBinding
import com.armutyus.ninova.databinding.RegisterUserBottomSheetBinding
import com.armutyus.ninova.roomdb.LocalShelf
import com.armutyus.ninova.ui.shelves.adapters.ShelvesRecyclerViewAdapter
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import javax.inject.Inject

class ShelvesFragment @Inject constructor(
    private val shelvesAdapter: ShelvesRecyclerViewAdapter
) : Fragment(R.layout.fragment_shelves) {

    private var fragmentBinding: FragmentShelvesBinding? = null
    private lateinit var shelvesViewModel: ShelvesViewModel
    private lateinit var bottomSheetBinding: AddNewShelfBottomSheetBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentShelvesBinding.bind(view)
        fragmentBinding = binding
        shelvesViewModel = ViewModelProvider(requireActivity())[ShelvesViewModel::class.java]

        val recyclerView = binding.mainShelvesRecyclerView
        recyclerView.adapter = shelvesAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        binding.mainShelvesAddButton.setOnClickListener {
            showAddShelfDialog()
        }

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
                Toast.makeText(requireContext(),"Title cannot be empty!", Toast.LENGTH_LONG).show()
            } else {
                shelvesViewModel.insertShelf(LocalShelf(0,shelfTitle))
                dialog.hide()
            }

        }
    }

    override fun onResume() {
        super.onResume()

        shelvesViewModel.getShelfList()
        observeShelfList()

    }

    private fun observeShelfList() {
        shelvesViewModel.currentShelfList.observe(this) { localShelfList ->
            if (localShelfList.isEmpty()) {
                fragmentBinding?.linearLayoutShelvesError?.visibility = View.VISIBLE
            } else {
                fragmentBinding?.linearLayoutShelvesError?.visibility = View.GONE
                fragmentBinding?.mainShelvesRecyclerView?.visibility = View.VISIBLE
                shelvesAdapter.mainShelfList = localShelfList
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        fragmentBinding = null
    }
}