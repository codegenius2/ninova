package com.armutyus.ninova.ui.discover.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.armutyus.ninova.R
import com.armutyus.ninova.constants.Constants.discoverScreenCategories
import com.armutyus.ninova.constants.Util.Companion.setImageTint
import com.armutyus.ninova.ui.discover.DiscoverFragmentDirections
import com.bumptech.glide.RequestManager
import javax.inject.Inject

class DiscoverRecyclerViewAdapter @Inject constructor(
    private val glide: RequestManager
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    class DiscoverScreenViewHolder(view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.book_category_item, parent, false)

        return DiscoverScreenViewHolder(view)
    }

    override fun getItemCount(): Int {
        return discoverScreenCategories.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val categoryCover = holder.itemView.findViewById<ImageView>(R.id.bookImage)
        val categoryTitle = holder.itemView.findViewById<TextView>(R.id.bookCategory)

        val categoryCoverSrc = discoverScreenCategories.values.elementAt(position)

        categoryTitle.isSelected = true

        holder.itemView.apply {
            glide.load(categoryCoverSrc).centerInside().into(categoryCover)
            categoryTitle.text = discoverScreenCategories.keys.elementAt(position)
            categoryCover.setImageTint(holder.itemView.context)
        }

        holder.itemView.setOnClickListener {
            val action =
                DiscoverFragmentDirections.actionNavigationDiscoveryToNavigationDiscoveryCategory(
                    discoverScreenCategories.keys.elementAt(position)
                )
            Navigation.findNavController(it).navigate(action)
        }
    }

}