package com.armutyus.ninova.ui.shelves.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.Navigation
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.armutyus.ninova.R
import com.armutyus.ninova.constants.Cache.currentShelf
import com.armutyus.ninova.roomdb.entities.LocalShelf
import com.armutyus.ninova.ui.shelves.ShelvesFragment
import com.armutyus.ninova.ui.shelves.ShelvesFragmentDirections
import com.armutyus.ninova.ui.shelves.ShelvesViewModel
import com.bumptech.glide.RequestManager
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

class ShelvesRecyclerViewAdapter @Inject constructor(
    private val glide: RequestManager
) :
    RecyclerView.Adapter<ShelvesRecyclerViewAdapter.ShelvesViewHolder>() {

    class ShelvesViewHolder(view: View) : RecyclerView.ViewHolder(view)

    private lateinit var viewModel: ShelvesViewModel
    private lateinit var shelvesFragment: ShelvesFragment

    private val diffUtil = object : DiffUtil.ItemCallback<LocalShelf>() {
        override fun areItemsTheSame(oldItem: LocalShelf, newItem: LocalShelf): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: LocalShelf, newItem: LocalShelf): Boolean {
            return oldItem == newItem
        }
    }

    private val recyclerListDiffer = AsyncListDiffer(this, diffUtil)

    var mainShelfList: List<LocalShelf>
        get() = recyclerListDiffer.currentList
        set(value) = recyclerListDiffer.submitList(value)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShelvesViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.shelves_main_row, parent, false)

        return ShelvesViewHolder(view)
    }

    override fun onBindViewHolder(holder: ShelvesViewHolder, position: Int) {
        val shelfCover = holder.itemView.findViewById<ImageView>(R.id.shelfCoverImage)
        val shelfTitle = holder.itemView.findViewById<TextView>(R.id.shelfTitleText)
        val booksInShelf = holder.itemView.findViewById<TextView>(R.id.booksInShelfText)
        val shelfCreatedDate = holder.itemView.findViewById<TextView>(R.id.shelfCreatedDateText)
        val shelf = mainShelfList[position]

        holder.itemView.setOnClickListener {
            currentShelf = shelf
            val action =
                ShelvesFragmentDirections.actionNavigationShelvesToShelfWithBooksFragment(shelf.shelfId)
            Navigation.findNavController(it).navigate(action)
        }

        holder.itemView.apply {
            glide.load(shelf.shelfCover).centerCrop().into(shelfCover)
            shelfTitle.text = shelf.shelfTitle
            shelfCreatedDate.text = if (shelf.createdAt!!.length > 10) {
                val shelfCreatedDateText = shelf.createdAt?.substring(0..9)
                val inputFormat = SimpleDateFormat(
                    "yyyy-MM-dd",
                    Locale.getDefault()
                ).parse(shelfCreatedDateText!!)
                val outputFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                outputFormat.format(inputFormat!!)
            } else {
                shelf.createdAt
            }
            booksInShelf.text = shelf.getBookCount(viewModel).toString()
        }

        shelfCover.setOnClickListener {
            currentShelf = shelf
            shelvesFragment.onClick()
        }

    }

    override fun getItemCount(): Int {
        return mainShelfList.size
    }

    fun setFragment(fragment: ShelvesFragment) {
        this.shelvesFragment = fragment
    }

    fun setViewModel(shelvesViewModel: ShelvesViewModel) {
        this.viewModel = shelvesViewModel
    }
}