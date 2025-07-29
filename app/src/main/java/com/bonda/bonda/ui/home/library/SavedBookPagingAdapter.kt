package com.bonda.bonda.ui.home.library

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil3.load
import com.bonda.bonda.databinding.ItemSavedBookBinding
import com.bonda.bonda.network.model.book.SavedBooksResponse

class SavedBookPagingAdapter(
    private val onClick: (SavedBooksResponse.Book) -> Unit
) : PagingDataAdapter<SavedBooksResponse.Book, SavedBookPagingAdapter.VH>(DIFF) {

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<SavedBooksResponse.Book>() {
            override fun areItemsTheSame(
                oldItem: SavedBooksResponse.Book,
                newItem: SavedBooksResponse.Book
            ) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(
                oldItem: SavedBooksResponse.Book,
                newItem: SavedBooksResponse.Book
            ) =
                oldItem == newItem
        }
    }

    inner class VH(private val binding: ItemSavedBookBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: SavedBooksResponse.Book) {
            binding.image.load(item.imageUrl)
            binding.root.setOnClickListener { onClick(item) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemSavedBookBinding.inflate(inflater, parent, false)
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }
}
