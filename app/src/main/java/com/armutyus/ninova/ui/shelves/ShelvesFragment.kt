package com.armutyus.ninova.ui.shelves

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
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
    private val binding get() = fragmentBinding
    private val shelvesViewModel by activityViewModels<ShelvesViewModel>()
    private lateinit var bottomSheetBinding: AddNewShelfBottomSheetBinding
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    private lateinit var pickMedia: ActivityResultLauncher<PickVisualMediaRequest>

    private val swipeCallBack = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            return true
        }

        override fun onChildDraw(
            c: Canvas,
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            dX: Float,
            dY: Float,
            actionState: Int,
            isCurrentlyActive: Boolean
        ) {

            val deleteIcon =
                AppCompatResources.getDrawable(requireContext(), R.drawable.ic_delete_account)
            val intrinsicWidth = deleteIcon!!.intrinsicWidth
            val intrinsicHeight = deleteIcon.intrinsicHeight
            val swipeBackground = ColorDrawable()
            val swipeBackgroundColor = R.color.md_theme_dark_errorContainer
            val deleteIconColor = R.color.md_theme_dark_onErrorContainer
            val clearPaint = Paint().apply { xfermode = PorterDuffXfermode(PorterDuff.Mode.DARKEN) }
            val itemView = viewHolder.itemView
            val itemHeight = itemView.bottom - itemView.top
            val isCanceled = dX == 0f || !isCurrentlyActive

            if (isCanceled) {
                c.drawRect(
                    itemView.right + dX,
                    itemView.top.toFloat() - 44,
                    itemView.right.toFloat(),
                    itemView.bottom.toFloat() - 44,
                    clearPaint
                )
                super.onChildDraw(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
                return
            }

            swipeBackground.color = resources.getColor(swipeBackgroundColor, context!!.theme)
            swipeBackground.setBounds(
                itemView.right + dX.toInt(),
                itemView.top - 44,
                itemView.right,
                itemView.bottom - 44
            )
            swipeBackground.draw(c)

            val iconMargin = (itemHeight - intrinsicHeight) / 2
            val iconTop = itemView.top - ((itemHeight / 2.2) - (intrinsicHeight * 2.5))
            val iconLeft = itemView.right - intrinsicWidth - (iconMargin / 2)
            val iconRight = itemView.right - (iconMargin / 3)
            val iconBottom = itemView.bottom - ((itemHeight * 0.7) - (intrinsicHeight))

            deleteIcon.setBounds(iconLeft, iconTop.toInt(), iconRight, iconBottom.toInt())
            deleteIcon.setTint(resources.getColor(deleteIconColor, context!!.theme))
            deleteIcon.draw(c)
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)

        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val layoutPosition = viewHolder.layoutPosition
            val swipedShelf = shelvesAdapter.mainShelfList[layoutPosition]
            shelvesViewModel.deleteShelf(swipedShelf).invokeOnCompletion {
                Snackbar.make(
                    requireView(),
                    R.string.shelf_deleted,
                    Snackbar.LENGTH_LONG
                )
                    .setAction(R.string.undo) {
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentBinding = FragmentShelvesBinding.inflate(inflater, container, false)

        val searchView = binding?.shelvesSearch
        searchView?.setOnQueryTextListener(this)
        searchView?.setIconifiedByDefault(false)

        val recyclerView = binding?.mainShelvesRecyclerView
        recyclerView?.adapter = shelvesAdapter
        recyclerView?.layoutManager = LinearLayoutManager(requireContext())
        shelvesAdapter.setViewModel(shelvesViewModel)
        shelvesAdapter.setFragment(this)
        ItemTouchHelper(swipeCallBack).attachToRecyclerView(recyclerView)

        binding?.mainShelvesFab?.setOnClickListener {
            showAddShelfDialog()
        }
        shelvesViewModel.loadShelfWithBookList()
        observeShelfList()

        return binding?.root
    }

    private fun showAddShelfDialog() {
        val dialog = BottomSheetDialog(requireContext())
        bottomSheetBinding = AddNewShelfBottomSheetBinding.inflate(layoutInflater)
        dialog.setContentView(bottomSheetBinding.root)
        dialog.show()

        val createShelfButton = dialog.findViewById<MaterialButton>(R.id.createShelfButton)
        createShelfButton?.setOnClickListener {
            val shelfTitle = bottomSheetBinding.shelfTitleText.text.toString()

            if (shelfTitle.isEmpty()) {
                Toast.makeText(requireContext(), R.string.title_cannot_empty, Toast.LENGTH_LONG)
                    .show()
            } else {
                val timeStamp = Date().time
                val formattedDate =
                    SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS", Locale.getDefault()).format(
                        timeStamp
                    )
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
            binding?.linearLayoutShelvesError?.visibility = View.VISIBLE
            binding?.progressBar?.visibility = View.GONE
            binding?.mainShelvesRecyclerView?.visibility = View.GONE
        } else {
            binding?.linearLayoutShelvesError?.visibility = View.GONE
            binding?.progressBar?.visibility = View.GONE
            binding?.mainShelvesRecyclerView?.visibility = View.VISIBLE
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
        onShelfCoverClicked()
    }

    private fun onShelfCoverClicked() {
        if (isPhotoPickerAvailable()) {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        } else {
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
                    Snackbar.make(
                        requireView(),
                        R.string.permission_needed,
                        Snackbar.LENGTH_INDEFINITE
                    )
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
    }

    private fun isPhotoPickerAvailable(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
    }

    private fun registerLauncher() {
        activityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == AppCompatActivity.RESULT_OK) {
                val uri = result.data?.data
                if (uri != null) {
                    currentShelf?.shelfCover = uri.toString()
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
                Toast.makeText(requireContext(), R.string.permission_needed, Toast.LENGTH_LONG)
                    .show()
            }
        }
        pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                Log.d("PhotoPicker", "Selected URI: $uri")
                val flag = Intent.FLAG_GRANT_READ_URI_PERMISSION
                requireContext().contentResolver.takePersistableUriPermission(uri, flag)
                currentShelf?.shelfCover = uri.toString()
                shelvesViewModel.updateShelf(currentShelf!!)
                shelvesAdapter.notifyDataSetChanged()
                uploadShelfToFirestore(currentShelf!!)
            } else {
                Log.d("PhotoPicker", "No media selected")
            }
        }
    }
}