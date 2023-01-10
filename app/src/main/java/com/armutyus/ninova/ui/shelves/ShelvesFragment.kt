package com.armutyus.ninova.ui.shelves

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.armutyus.ninova.R
import com.armutyus.ninova.constants.Cache.currentShelf
import com.armutyus.ninova.constants.Response
import com.armutyus.ninova.databinding.AddNewShelfBottomSheetBinding
import com.armutyus.ninova.databinding.FragmentShelvesBinding
import com.armutyus.ninova.roomdb.entities.LocalShelf
import com.armutyus.ninova.ui.shelves.adapters.ShelvesRecyclerViewAdapter
import com.armutyus.ninova.ui.shelves.listeners.OnShelfCoverClickListener
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class ShelvesFragment @Inject constructor(
    private val shelvesAdapter: ShelvesRecyclerViewAdapter
) : Fragment(R.layout.fragment_shelves), SearchView.OnQueryTextListener, OnShelfCoverClickListener {

    private var fragmentBinding: FragmentShelvesBinding? = null
    private val shelvesViewModel by activityViewModels<ShelvesViewModel>()
    private lateinit var bottomSheetBinding: AddNewShelfBottomSheetBinding
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
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
                        shelvesViewModel.insertShelf(swipedShelf).invokeOnCompletion {
                            uploadShelfToFirestore(swipedShelf)
                            shelvesViewModel.loadShelfList()
                        }
                    }.show()
                deleteShelfFromFirestore(swipedShelf.shelfId)
                shelvesViewModel.loadShelfList()
            }
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerLauncher()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentShelvesBinding.bind(view)
        fragmentBinding = binding

        val recyclerView = binding.mainShelvesRecyclerView
        recyclerView.adapter = shelvesAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        shelvesAdapter.setViewModel(shelvesViewModel)
        shelvesAdapter.setFragment(this)
        ItemTouchHelper(swipeCallBack).attachToRecyclerView(recyclerView)

        val searchView = binding.shelvesSearch
        searchView.setOnQueryTextListener(this)
        searchView.setIconifiedByDefault(false)

        binding.mainShelvesFab.setOnClickListener {
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
                    uploadShelfToFirestore(shelf)
                    shelvesViewModel.loadShelfList()
                }
                dialog.dismiss()
            }

        }
    }

    override fun onResume() {
        super.onResume()
        shelvesViewModel.loadShelfList()
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

        } else if (searchQuery.isBlank()) {
            shelvesViewModel.loadShelfList()
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

    private fun deleteShelfFromFirestore(shelfId: String) {
        shelvesViewModel.deleteShelfFromFirestore(shelfId) { response ->
            when (response) {
                is Response.Loading ->
                    Log.i("shelfDelete", "Deleting from firestore")
                is Response.Success ->
                    Log.i("shelfDelete", "Deleted from firestore")
                is Response.Failure ->
                    Log.e("shelfDelete", response.errorMessage)
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

    override fun onClick() {
        onBookCoverClicked()
    }

    private fun onBookCoverClicked() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    requireActivity(),
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            ) {
                Snackbar.make(requireView(), "Permission needed!", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Give Permission") {
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
            if (result.resultCode == AppCompatActivity.RESULT_OK) {
                val intentFromResult = result.data?.data
                if (intentFromResult != null) {
                    currentShelf?.shelfCover = intentFromResult.toString()
                    shelvesViewModel.updateShelf(currentShelf!!)
                    shelvesAdapter.notifyDataSetChanged()
                    uploadShelfToFirestore(currentShelf!!)
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
                Toast.makeText(requireContext(), "Permission needed!", Toast.LENGTH_LONG).show()
            }
        }
    }
}