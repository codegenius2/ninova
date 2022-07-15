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
import com.armutyus.ninova.constants.Constants.currentShelf
import com.armutyus.ninova.roomdb.entities.LocalShelf
import com.armutyus.ninova.ui.shelves.ShelvesFragmentDirections
import com.armutyus.ninova.ui.shelves.ShelvesViewModel
import com.bumptech.glide.RequestManager
import javax.inject.Inject

class ShelvesRecyclerViewAdapter @Inject constructor(
    private val glide: RequestManager
) :
    RecyclerView.Adapter<ShelvesRecyclerViewAdapter.ShelvesViewHolder>() {

    class ShelvesViewHolder(view: View) : RecyclerView.ViewHolder(view)

    private lateinit var viewModel: ShelvesViewModel

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
            shelfTitle.text = shelf.shelfTitle
            shelfCreatedDate.text = shelf.createdAt
            booksInShelf.text = shelf.getBookCount(viewModel).toString()
        }

    }

    override fun getItemCount(): Int {
        return mainShelfList.size
    }

    fun setViewModel(shelvesViewModel: ShelvesViewModel) {
        this.viewModel = shelvesViewModel
    }
}